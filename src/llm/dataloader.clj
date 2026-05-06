(ns llm.dataloader
  (:require [clojure.core.async :as a])
  (:import [java.util Iterator]))


;; ------------------------------------------------------------
;; Utilities
;; ------------------------------------------------------------

(defn shuffle-buffer
  "Approximate streaming shuffle using a bounded buffer.
   Similar to PyTorch's shuffle with buffer_size."
  [coll buffer-size]
  (lazy-seq
    (let [buf (object-array buffer-size)
          it  (iterator-seq (.iterator coll))]
      (loop [buf buf
             i 0
             xs it]
        (cond
          ;; fill buffer
          (< i buffer-size)
          (if-let [x (first xs)]
            (do
              (aset buf i x)
              (recur buf (inc i) (rest xs)))
            ;; emit remainder shuffled
            (let [remaining (take i buf)]
              (shuffle remaining)))

          ;; normal streaming
          :else
          (if-let [x (first xs)]
            (let [j (rand-int buffer-size)
                  out (aget buf j)]
              (aset buf j x)
              (cons out
                    (shuffle-buffer (rest xs) buffer-size)))
            ;; drain buffer
            (shuffle (vec buf))))))))

(defn batcher
  [n]
  (partition-all n))

;; ------------------------------------------------------------
;; Worker pool
;; ------------------------------------------------------------

(defn start-worker-pool
  [in-ch out-ch f n-workers]
  (dotimes [_ n-workers]
    (a/go-loop []
      (if-let [x (a/<! in-ch)]
        (do
          (a/>! out-ch (f x))
          (recur))
        (a/close! out-ch)))))

;; ------------------------------------------------------------
;; DataLoader
;; ------------------------------------------------------------

(defn dataloader
  [{:keys [dataset
           batch-size
           shuffle?
           shuffle-buffer-size
           num-workers
           prefetch
           xf
           collate-fn]
    :or {batch-size 32
         shuffle? false
         shuffle-buffer-size 1024
         num-workers 4
         prefetch 128
         xf nil
         collate-fn identity}}]
  (let [in-ch (a/chan prefetch)
        work-ch (a/chan prefetch)
        batch-ch (a/chan prefetch)

        ;; dataset prep
        data (cond-> dataset
               shuffle? (shuffle-buffer shuffle-buffer-size))

        ;; apply transform pipeline
        data (if xf
               (sequence xf data)
               data)]

    ;; producer
    (a/go
      (doseq [x data]
        (a/>! in-ch x))
      (a/close! in-ch))

    ;; worker pool
    (start-worker-pool in-ch work-ch (or collate-fn identity) num-workers)

    ;; batching stage
    (a/go-loop [batch []]
      (if-let [x (a/<! work-ch)]
        (if (= (count batch) batch-size)
          (do
            (a/>! batch-ch batch)
            (recur [x]))
          (recur (conj batch x)))
        (do
          (when (seq batch)
            (a/>! batch-ch batch))
          (a/close! batch-ch))))

    ;; return as lazy sequence
    (letfn [(channel-seq [ch]
              (lazy-seq
                (let [batch (a/<!! ch)]
                  (when batch
                    (cons batch (channel-seq ch))))))]
      (channel-seq batch-ch))))

