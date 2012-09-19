(defproject cark/data.lenses "0.0.1-SNAPSHOT"
  :description "Implements lenses, also known as functional references."
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :url "https://github.com/cark/data.lenses"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/algo.monads "0.1.3-SNAPSHOT"]]
  :profiles {:dev {:plugins [[codox "0.6.1"]
                             [lein-midje "2.0.0-SNAPSHOT"]
                             [lein-set-version "0.2.1"]]
                   :dependencies [[com.stuartsierra/lazytest "2.0.0-SNAPSHOT"]
                                  [midje "1.4.0"]]}}
  :repositories {"stuartsierra-releases" "http://stuartsierra.com/maven2"
		 "stuartsierra-snapshots" "http://stuartsierra.com/m2snapshots"}
  :set-version {:updates [{:path "README.md"
                           :search-regex #"cark/data.lenses \"\d+\.\d+\.\d+(-SNAPSHOT)?\""}]})