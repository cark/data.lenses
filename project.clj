(defproject cark/data.lenses "0.0.1-SNAPSHOT"
  :description "Implements lenses, also known as functional references"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/algo.monads "0.1.3-SNAPSHOT"]]
  :profiles {:dev {:plugins [[codox "0.6.1"]
                             [lein-midje "2.0.0-SNAPSHOT"]]
                   :dependencies [[com.stuartsierra/lazytest "2.0.0-SNAPSHOT"]
                                  [midje "1.4.0"]]}}
  :repositories {"stuartsierra-releases" "http://stuartsierra.com/maven2"
		 "stuartsierra-snapshots" "http://stuartsierra.com/m2snapshots"})