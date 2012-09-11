(ns lenses_test
  (:use [cara.data.lenses]
        [lazytest.describe :only (describe testing it do-it)]
        [lazytest.expect :only (expect)]
        [clojure.algo.monads]))

(def point {:x 10 :y 20})
(def x (lens :x (fn [val s] (assoc s :x val))))
(def y (lens :y (fn [val s] (assoc s :y val))))

(describe "basic functionality"
  
  (it "gets the value from the map"
    (= 10
       (lget x point)))  
  (it "sets the value in the map"
    (= {:x 1 :y 20}
       (lset x 1 point)))
  (it "updates the pointed value"
    (= {:x 11 :y 20}
       (lupd x inc point))))

(describe "currying"
  
  (testing "lget"    
    (it "works with a single parameter"
      (= 10
         ((lget x) point))))
  
  (testing "lset"    
    (it "works with two parameters"
      (= {:x 1 :y 20}
         ((lset x 1) point)))
    
    (testing "with one parameter"      
      (it "returned function works with two parameters"
        (= {:x 1 :y 20}
           ((lset x) 1 point)))      
      (it "returned function works with one parameter"
        (= {:x 1 :y 20}
           (((lset x) 1) point)))))
  
  (testing "lupd"
    (it "works with two parameters"
      (= {:x 11 :y 20}
         ((lupd x inc) point)))
    
    (testing "with one parameter"
      (it "returned function works with two parameters"
        (= {:x 11 :y 20} ((lupd x) inc point)))      
      (it "returned function works with one parameter"
        (= {:x 11 :y 20} (((lupd x) inc) point))))))

(def circle {:center point})
(def center (lens :center (fn [val s] (assoc s :center val))))
(def center-x (lcomp center x))

(describe "composition"
  (it "lget center works"
    (= point (lget center circle)))
  (it "lget x still works"
    (= 10 (lget x point)))
  (it "lget center-x works"
    (= 10 (lget center-x circle)))
  (it "lset works"
    (= {:center {:x 1 :y 20}}
       (lset center-x 1 circle)))
  (it "lupd works"
    (= {:center {:x 11 :y 20}}
       (lupd center-x inc circle))))

(def mvx (map-val-lens :x))

(describe "map-val-lens"
  (it "lget works"
    (= 10 (lget mvx point)))
  (it "lset works"
    (= {:x 1 :y 20} (lset mvx 1 point)))
  (it "lset to nil dissociates"
    (= {:y 20} (lset mvx nil point)))
  (it "lupd works"
    (= {:x 11 :y 20} (lupd mvx inc point))))

(describe "set-lens"
  (it "lget returns the value it found."
    (= 2 (lget (set-lens 2)
               #{1 2})))
  (it "lget returns nil when the value is not in the set"
    (= nil (lget (set-lens 5)
                 #{1 2})))
  (it "lset true adds the value to the set"
    (= #{1 2} (lset (set-lens 2) true #{1})))
  (it "lset false removes the value from the set"
    (= #{1} (lset (set-lens 2) false #{1 2}))))

(defn eval-state [mv state]
  (get (mv state) 0))

(defn exec-state [mv state]
  (get (mv state) 1))

(describe "state-m lens functions"
  (it "get's the pointed value"
    (= 10 (eval-state (with-monad state-m
                        (slget x))
                      point)))
  (it "sets the pointed value"
    (= {:x 1 :y 20} (exec-state (with-monad state-m
                                  (slset x 1))
                                point)))
  (it "updates the pointed value"
    (= {:x 11 :y 20} (exec-state (with-monad state-m
                                   (slupd x inc))
                                 point))))


