(ns llm.tokenization
  (:require [clojure.string :as string]))

;; Chapter 2 page 31
;;
;; Python 
;; import urllib.request
;; url = ("https://raw.githubusercontent.com/rasbt/"
;;        "LLMs-from-scratch/main/ch02/01_main-chapter-code/"
;;        "the-verdict.txt")
;; file_path = "the-verdict.txt"
;; urllib.request.urlretrieve (url, file_path)
;; 
;; with open("the-verdict.txt", "r", encoding="utf-8") as f:
;;     raw_text = f.read()

(def verdict-txt
  (slurp
   (str "https://raw.githubusercontent.com/rasbt/"
        "LLMs-from-scratch/main/ch02/01_main-chapter-code/"
        "the-verdict.txt")))

;; preprocessed = re.split(r'([,.:;?_!"()\']|--|\s)', raw_text)
;; preprocessed = [item.strip() for item in preprocessed if item.strip()]
;; print(len(preprocessed))

(defn tokenizer [txt]
  (string/split ))







(comment
  verdict-txt

  ;; print("Total number of character:", len(raw_text))
  ;; print(raw_text[:99])
  (format "Total number of character: %d" (.length verdict-txt))
  (subs verdict-txt 0 99)

  ;; Chapter 2 page 32
  ;; import re
  ;;  text = "Hello, world. This, is a test."
  ;; result = re.split (r'(\s)', text)
  ;; print (result)

  (def text "Hello, world. This, is a test.")
    (string/split text #"(\s)")


  ;; result = [item for item in result if item.strip()]
  ;; print (result)

  ;; “text = "Hello, world. Is this-- a test?"
  ;; result = re.split(r'([,.:;?_!"()\']|--|\s)', text)
  ;; result = [item.strip() for item in result if item.strip()]
  ;; print(result)”
  (def text2 "Hello, world. Is this-- a test?")
  (string/split text2 #"([,.:;?_!\"()\']|--|\\s)") 

  
  (take 3 (seq "abcdefghijk"))
  (str (take 3 "abcdefghijk"))
  
  ;;
  )