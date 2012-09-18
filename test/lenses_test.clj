(ns lenses_test
  (:use [cark.data.lenses]
        [midje.sweet]
        [clojure.algo.monads]))

(def point {:x 10 :y 20})
(def x (lens :x (fn [val s] (assoc s :x val))))
(def y (lens :y (fn [val s] (assoc s :y val))))

(facts "Basic functionality"
  (fact "gets the value from a map"
    (lget x point) => 10)  
  (fact "sets the value in the map"
    (lset x 1 point) => {:x 1 :y 20})  
  (fact "updates the pointed value"
    (lupd x inc point) => {:x 11 :y 20}))

(facts "Currying"
  (facts "lget"
    (fact "works with a single parameter"
      ((lget x) point) => 10))
  
  (facts "lset"
    (fact "works with 2 parameters"
      ((lset x 1) point) => {:x 1 :y 20})
    (facts "with 1 parameter"
      (fact "returned function works with two parameters"
        ((lset x) 1 point) => {:x 1 :y 20})      
      (fact "returned function works with one parameter"
        (((lset x) 1) point) => {:x 1 :y 20})))
  
  (facts "lupd"
    (fact "works with two parameters"
      ((lupd x inc) point) => {:x 11 :y 20})
    (facts "with one parameter"
      (fact "returned function works with two parameters"
        ((lupd x) inc point) => {:x 11 :y 20})
      (fact "returned function works with one parameter"
        (((lupd x) inc) point) => {:x 11 :y 20}))))

(def circle {:center point})
(def center (lens :center (fn [val s] (assoc s :center val))))
(def center-x (lcomp center x))

(facts "composition"
  (fact "lget center works"
    (lget center circle) => point)
  (fact "lget x still works"
    (lget x point) => 10)
  (fact "lget center-x works"
    (lget center-x circle) => 10)
  (fact "lset works"
    (lset center-x 1 circle) => {:center {:x 1 :y 20}})
  (fact "lupd works"
    (lupd center-x inc circle) => {:center {:x 11 :y 20}}))

(def mvx (map-val-lens :x))

(facts "map-val-lens"
  (fact "lget works"
    (lget mvx point) => 10)
  (fact "lset works"
    (lset mvx 1 point) => {:x 1 :y 20})
  (fact "lset to nil dissociates"
    (lset mvx nil point) => {:y 20})
  (fact "lupd works"
    (lupd mvx inc point) => {:x 11 :y 20}))

(facts "set-lens"
  (fact "lget returns the value it found."
    (lget (set-lens 2) #{1 2}) => 2)
  (fact "lget returns nil when the value is not in the set"
    (lget (set-lens 5) #{1 2}) => nil)
  (fact "lset true adds the value to the set"
    (lset (set-lens 2) true #{1}) => #{1 2})
  (fact "lset false removes the value from the set"
    (lset (set-lens 2) false #{1 2}) => #{1}))

(defn eval-state [mv state]
  (get (mv state) 0))

(defn exec-state [mv state]
  (get (mv state) 1))

(facts "state-m lens functions"
  (fact "get's the pointed value"
    (eval-state (with-monad state-m
                     (slget x))
                point)
    => 10)
  (fact "sets the pointed value"
    (exec-state (with-monad state-m
                               (slset x 1))
                point)
    => {:x 1 :y 20})
  (fact "updates the pointed value"
    (exec-state (with-monad state-m
                                (slupd x inc))
                point)
    => {:x 11 :y 20}))
