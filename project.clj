(defproject cark/data.lenses "0.0.1-SNAPSHOT"
  :description "Implements lenses, also known as functional references"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/algo.monads "0.1.3-SNAPSHOT"]]
  :dev-dependencies [#_[com.stuartsierra/lazytest "2.0.0-SNAPSHOT"]
                     #_[codox "0.6.1"]]
  :profiles {:dev {:plugins [[codox "0.6.1"]]
                   :dependencies [[com.stuartsierra/lazytest "2.0.0-SNAPSHOT"]]}}
  :repositories {"stuartsierra-releases" "http://stuartsierra.com/maven2"
		 "stuartsierra-snapshots" "http://stuartsierra.com/m2snapshots"})