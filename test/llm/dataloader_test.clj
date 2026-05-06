(ns llm.dataloader-test
  (:require [clojure.test :refer [deftest is testing run-tests]] 
            [llm.dataloader :refer [shuffle-buffer batcher]]))

(deftest shuffle-buffer-test
  (testing "preserves elements in small dataset"
    (let [data [1 2 3 4 5]
          result (shuffle-buffer data 10)]
      (is (= (count (vec result)) 5))
      (is (= (set result) #{1 2 3 4 5}))))

  (testing "shuffles data within buffer"
    (let [data (range 10)
          result (shuffle-buffer data 10)]
      (is (= (count (vec result)) 10))
      (is (= (set result) (set data)))))

  (testing "handles empty dataset"
    (let [data []
          result (shuffle-buffer data 5)]
      (is (empty? (vec result))))))

(deftest batcher-test
  (testing "batches items correctly"
    (let [data [1 2 3 4 5 6]
          xf (batcher 2)
          batches (vec (sequence xf data))]
      (is (= batches [[1 2] [3 4] [5 6]]))))

  (testing "handles remainder items"
    (let [data [1 2 3 4 5]
          xf (batcher 2)
          batches (vec (sequence xf data))]
      (is (= batches [[1 2] [3 4] [5]]))))

  (testing "single batch"
    (let [data [1 2 3]
          xf (batcher 5)
          batches (vec (sequence xf data))]
      (is (= batches [[1 2 3]])))))

(comment 
  (run-tests)
  ;;
  )