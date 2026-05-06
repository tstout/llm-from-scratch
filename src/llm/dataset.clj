(ns llm.dataset
  (:require [llm.bpe :refer [mk-bpe-tokenizer]]))

;;
;; The equivalent in the book of class GPTDatasetV1(Dataset)
;;
(defn gpt-dataset-v1
  [{:keys [txt max-length stride]}]
  ;; TODO - some range checks here? 
  ;; max-length should be > 0, stride should be > 0 and < max-length, txt should not be empty
  (let [tokenizer (mk-bpe-tokenizer)
        token-ids (tokenizer :encode txt)
        n         (count token-ids)
        ;; Uses a sliding window to chunk tokenization of txt into 
        ;; overlapping sequences of max_length
        samples   (for [i (range 0 (- n max-length) stride)]
                    {:input-ids  (subvec token-ids i (+ i max-length))
                     :target-ids (subvec token-ids (inc i) (+ i max-length 1))})]
    {:input-ids  (mapv :input-ids samples)
     :target-ids (mapv :target-ids samples) 
     :len        (count samples)
     :token-count n
     :tokens token-ids}))





(comment 
  (def ds
    (gpt-dataset-v1
     {:txt        "fo-bar baz qux quux corge grault garply waldo fred plugh xyzzy thud"
      :max-length 5
      :stride     5}))
  
  ds
  
  (:len ds)
  (:token-ids ds)
  (- 128 22)

  ;; like __len__
  
  ((:input-ids ds) 0)
  ((:target-ids ds) 0)
  ;; like __getitem__
  
  ;;
  )
