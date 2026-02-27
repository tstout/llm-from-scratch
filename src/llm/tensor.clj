(ns llm.tensor
  "Note: the book uses PyTorch tensors. Let's see how far we can get without needing
   a dependency on a python tensor library.")
;;
;; Tensor Representation
;;
;; {:data    double-array
;;  :shape   [d0 d1 ... dn]
;;  :strides [s0 s1 ... sn]
;;  :offset  0}
;; 
;; data → flat primitive storage
;; shape → logical dimensions of the tensor
;; strides → jumps in the flat array
;; offset → starting position (for views later)
;;

(defn row-major-strides [shape]
  (let [rev-shape (reverse shape)]
    (->> rev-shape
         (reductions * 1)
         butlast
         reverse
         vec)))

(defn total-size [shape]
  (reduce * shape))

(defn tensor
  "Tensor Constructor"
  ([shape]
   (tensor shape (double-array (total-size shape))))
  ([shape data]
   {:data data
    :shape shape
    :strides (row-major-strides shape)
    :offset 0}))

(defn linear-index [{:keys [strides offset]} indices]
  (+ offset
     (reduce + (map * strides indices))))

(defn tget [t indices]
  (let [idx (linear-index t indices)]
    (aget ^doubles (:data t) idx)))

(defn tset! [t indices value]
  (let [idx (linear-index t indices)]
    (aset-double ^doubles (:data t) idx value)
    t))

(defn reshape [t new-shape]
  (assert (= (total-size (:shape t))
             (total-size new-shape)))
  (assoc t
         :shape new-shape
         :strides (row-major-strides new-shape)))

(defn transpose [t]
  (-> t
      (update :shape reverse)
      (update :strides reverse)))


(comment
  ;; REPL evaluations
  
  ;; Create a 2x3 tensor
  (def t (tensor [2 3]))
  t ;; => {:data [0.0, 0.0, 0.0, 0.0, 0.0, 0.0], :shape [2 3], :strides [3 1], :offset 0}
  
  ;; Set some values
  (tset! t [0 0] 1.0)
  (tset! t [0 1] 2.0)
  (tset! t [0 2] 3.0)
  (tset! t [1 0] 4.0)
  (tset! t [1 1] 5.0)
  (tset! t [1 2] 6.0)
  t ;; => {:data [1.0, 2.0, 3.0, 4.0, 5.0, 6.0], :shape [2 3], :strides [3 1], :offset 0}
  

  ;; Get some values
  (tget t [0 0]) ;; => 1.0 
  (tget t [1 2]) ;; => 6.0
  
  ;; Reshape to a flat vector
  (def t-flat (reshape t [6]))
  t-flat

  ;; Transpose the original
  (def t-transposed (transpose t))
  t-transposed
  ;; Get values from the transposed
  (tget t-transposed [0 0]) ;; => 1.0
  (tget t-transposed [0 1]) ;; => 4.0
  (tget t-transposed [1 0]) ;; => 2.0
  (tget t-transposed [1 1]) ;; => 5.0
  (tget t-transposed [2 0]) ;; => 3.0
  (tget t-transposed [2 1]) ;; => 6.0
  )