(ns llm.bpe
  "Byte Pair Encoding (BPE) tokenizer implementation.
   This is a thin wrapper around https://github.com/knuddelsgmbh/jtokkit
   The book uses https://github.com/openai/tiktoken (Python)
   Attempting to avoid a python dependency for this effort." 
  (:import [com.knuddels.jtokkit Encodings]
           [com.knuddels.jtokkit.api ModelType IntArrayList]))

(defn vec-to-ints 
  "This is likely not very efficient, but the jtokkit API requires
   an IntArrayList for decoding, so we need to convert a Clojure vector
   of integers to an jtokkit-specific IntArrayList."
  [v]
  (let [arr (IntArrayList. (count v))]
    (doseq [i (range (count v))]
      (.add arr (nth v i)))
    arr))

(defn mk-bpe-tokenizer 
  "Create a BPE tokenizer using jtokkit.
   The returned function takes an operation keyword (:encode or :decode)
   and the corresponding argument(s)." 
  []
  (let [registry (Encodings/newLazyEncodingRegistry)
        encoding (.getEncodingForModel registry ModelType/GPT_4)
        ops      {:encode (fn [txt] (-> (.encodeOrdinary encoding txt)
                                        .toArray
                                        vec))
                  :decode (fn [ids] (->> ids 
                                         vec-to-ints 
                                         (.decode encoding)))}] 
    (fn [operation & args] (-> (ops operation) (apply args)))))


(comment
  ;; REPL evaluations

  ;; Create a BPE tokenizer
  (def bpe-tokenizer (mk-bpe-tokenizer))
  
  ;; encode the sample text from the book, binding result to the var 'encoded'
  (def encoded
    (bpe-tokenizer
     :encode
     "Hello, do you like tea? <|endoftext|> In the sunlit terraces of someunknownPlace."))
  
  ;; decode the encoded token ids back to text
  (bpe-tokenizer :decode encoded)
  
  ;; demonstrate encoding and decoding unknown words
  (def unknown-encoded
    (bpe-tokenizer
     :encode
     "Akwirw ier"))

  unknown-encoded 
  (bpe-tokenizer :decode unknown-encoded)

  ;;
  )
  
  


  