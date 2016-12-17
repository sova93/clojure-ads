(ns ads.logging-service
  (:require [clojure.java.io :as io]
            [ads.models :as m]
            )
  (:import (java.util Date)))

(defn now [] (.. (Date.) (toString)))

(def log-agent (agent (clojure.java.io/writer "user-log.txt" :append true)))

(defn write-out [writer message]
  (.write writer message)
  writer)

(defn flush-and-return [writer]
  (.flush writer)
  writer)

(defn log-message [logger message]
  (send logger write-out message))

(defn close [writer]
  (send writer #(.close %)))

(defn close-log [id]
    (send log-agent close))

(defn flush [writer]
  (send writer flush-and-return)
  writer)

(defn log-message-to-service [cmd]

      (log-message log-agent (str cmd "\r\n"))
        (flush log-agent)
  )



