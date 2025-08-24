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
  "Chapter 2 - read the chapter text from a URL. Note in the book
   the text is assigned to the variable raw_text. Here it is assigned
   to a var named verdict-txt"
  (slurp
   (str "https://raw.githubusercontent.com/rasbt/"
        "LLMs-from-scratch/main/ch02/01_main-chapter-code/"
        "the-verdict.txt")))

;; Python
;; preprocessed = re.split(r'([,.:;?_!"()\']|--|\s)', raw_text)
;; preprocessed = [item.strip() for item in preprocessed if item.strip()]
;; print(len(preprocessed))

(defn tokenizer 
  "Chapter 2 tokenizer. Note: The python regex in simpler than
  the java regex used here to achieve the same result. Interesting that there is
  such a large difference between python and java regex behavior."
  [txt]
  (->> 
   (.split txt "(?<=(?:--)|[,.:;?_!\"()'\\s])|(?=(?:--)|[,.:;?_!\"()'\\s])") 
   (map string/trim)
   (filter not-empty)))

(def preprocessed
  "As in the book, this is a complete collection of the tokens from the 
   short story."
  (tokenizer verdict-txt))

;;
;; Building the Vocabulary
;;
(def vocabulary 
  "Create integer token IDs for the tokens. This is simply enumerating the sorted distinct
   tokens. The set fn is used to create the sequence of distinct tokens.
   This defines a var bound to the symbol vocabulary as a sequence of [index token] pairs."
  (->> preprocessed
       set
       sort
       (keep-indexed (fn [index token] [index token]))))

(comment
  ;; REPL evaluations
  
  ;; Entire Text
  verdict-txt

  ;; test the tokeinizer fn
  (tokenizer "Hello, world. This--, is a test.")


  ;; How many tokens are in the short story?
  (-> verdict-txt
      tokenizer
      count)

  ;; Fist 30 tokens from the short story
  (take 30 (tokenizer verdict-txt))

  ;; An equivalent form to take the first 30 (cleaner IMHO)
  (->> verdict-txt
       tokenizer
       (take 30))

  ;; The Vocubulary...token IDs
  ;;
  ;; How many distinct tokens?
  (count (set preprocessed))


  ;;
  )