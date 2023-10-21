(ns smallworld.anki-pkg
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(import '[java.util.zip ZipFile])
(import '[java.io File])
(import '[java.sql DriverManager])

(defn extract-zip [zip-file-path dest-dir-path]
  (let [zip-file (ZipFile. (File. zip-file-path))]
    (doseq [entry (enumeration-seq (.entries zip-file))]
      (let [entry-name (.getName entry)
            entry-file (File. dest-dir-path entry-name)]
        (if (.isDirectory entry)
          (.mkdirs entry-file)
          (with-open [input-stream (.getInputStream zip-file entry)
                      output-stream (io/output-stream entry-file)]
            (io/copy input-stream output-stream)))))))

(defn get-connection [db-file-path]
  (let [connection (DriverManager/getConnection (str "jdbc:sqlite:" db-file-path))]
    connection))

#_(defn query-card-count [connection]
    (let [statement (.createStatement connection)
          resultSet (.executeQuery statement "SELECT count(*) AS card_count FROM cards")]
      (when (.next resultSet)
        (.getInt resultSet "card_count"))))

#_(defn print-notes [connection]
    (let [query "SELECT * FROM notes LIMIT 2"]
      (with-open [stmt (.prepareStatement connection query)
                  rs (.executeQuery stmt)]
      ; iterate through the result rs and print each row
        (let [metadata (.getMetaData rs)
              column-count (.getColumnCount metadata)]
          (loop []
            (when (.next rs)
              (doseq [i (range 1 (inc column-count))]
                (let [column-name (.getColumnName metadata i)
                      value (.getString rs i)]
                  (println (str column-name ": " value))))
            ; pretty print fields
              (println "\nFields: " (.getString rs "flds"))
              (println)
              (recur))))
        #_(loop []
            (when (.next rs)

              #_(let [fields (.getString rs "flds")
                      [front back] (str/split fields #"\x1f")
                      media-filename (.getString rs "fname")]
                  (println (str "Front: " front))
                  (println (str "Back: " back))
                  (when media-filename
                    (println (str "Media: " media-filename)))
                  (recur)))))))

#_(defn get-flashcards [connection]
    (let [query "SELECT * FROM cards LIMIT 10"]
      (with-open [stmt (.prepareStatement connection query)
                  rs (.executeQuery stmt)]
        (loop []
          (when (.next rs)
            (println rs)
            #_(let [fields (.getString rs "flds")
                    [front back] (str/split fields #"\x1f")
                    media-filename (.getString rs "fname")]
                (println (str "Front: " front))
                (println (str "Back: " back))
                (when media-filename
                  (println (str "Media: " media-filename)))
                (recur)))))))

(defn print-table-names [connection]
  (let [query "SELECT name FROM sqlite_master WHERE type='table';"]
    (with-open [stmt (.prepareStatement connection query)
                rs (.executeQuery stmt)]
      (println "tables:")
      (loop []
        (when (.next rs)
          (println (str "  " (.getString rs "name")))
          (recur))))))

(defn print-notes [connection]
  (println "print-notes:")
  (let [query "SELECT flds FROM notes LIMIT 2"]
    (with-open [stmt (.prepareStatement connection query)
                rs (.executeQuery stmt)]
      (loop []
        (when (.next rs)
          (let [fields (.getString rs "flds")
                [front back] (clojure.string/split fields #"\x1f")]
            (println (str "  Front: " front))
            (println (str "  Back: " back))
            (println))
          (recur))))))

(defn -main []
  (let [file-name "Essential Spanish Vocabulary Top 5000"
        parent-dir "/Users/devonzuegel/Downloads/"
        zip-file (str parent-dir file-name ".apkg")
        dest-dir (str parent-dir file-name)]
    (extract-zip zip-file dest-dir)

    (when-not (.exists (File. (str dest-dir "/collection.anki21")))
      (throw ".anki21 file does not exist — sorry, this app currently only supports .anki21 files"))

    (let [connection (get-connection (str dest-dir "/collection.anki21"))]
      (println "\n")
      (print-notes connection)
      (println)
      (print-table-names connection)
      (println "\n")

      (.close connection))))
