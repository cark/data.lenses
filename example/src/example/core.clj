(ns example.core
  (:use [cark.data.lenses]
        [clojure.algo.monads]
        [clojure.pprint :only [pprint]]))

;; This file is more a tutorial than a working example.
;; You are supposed to evaluate each expression in turn and see the result in your repl

;; first we'll see how lenses work at the basic level
;; *************************************************
;;
;; we define a point, and the lenses to access point-like maps

(def point {:x 10 :y 20})

;; The lens function creates a new lens, it needs to be passed a getter and a setter function.
;; - The getter function returns x when called with a point parameter.
;; - The setter function returns a new point when being called with a new value for x and a point.
(def point-x (lens (fn [point]
                     (get point :x)) 
                   (fn [val point]
                     (assoc point :x val))))

;; I expect this type of lens to be pretty common, so there is a function that creates these.
;; The map-val-lens creates a lens to the value at the specified key in a map
(def point-y (map-val-lens :y))

;; let's use our lenses
;; the lget function fetches the value pointed at by a lens inside the passed point
(= (lget point-x point)
   10)

;; and for point-y
(= (lget point-y point)
   20)

;; The lget function is curried
;; So (lget point-x) might be seen as the function that retreives the x value from a provided
;; point-like value
(= ((lget point-x) point)
   10)

;; the lset function updates the value pointed by a lens, returning the new point
(= (lset point-x 1 point)
   {:x 1 :y 20})

;; lset is also curried
(= ((lset point-x 1) point)
   {:x 1 :y 20})

;; and doubly so
(= ((lset point-x) 1 point)
   {:x 1 :y 20})

(= (((lset point-x) 1) point)
   {:x 1 :y 20})

;; the lupd function  will update the value of the pointed value, returning a new point
(= (lupd point-x inc point)
   {:x 11 :y 20})

;; the lupd function is also curried
(= ((lupd point-x inc) point)
   {:x 11 :y 20})

;; currying makes it easy to define actions on a structure
(let [go-right (lupd point-x inc)]
  (= (go-right point)
     {:x 11 :y 20}))


;; So what is all that buying me ?
;; *******************************
;;
;; It turns out that lenses are easy to compose.
;; Let's define a circle data structure and the lenses that go with it.

(def circle {:center point :radius 5})
(def center (map-val-lens :center))
(def radius (map-val-lens :radius))

(= (lget center circle)
   {:x 10 :y 20})

;; the lcomp function composes two lenses
(def center-x (lcomp center point-x))

(= (lget center-x circle)
   10)

(= (lset center-x 1 circle)
   {:center {:x 1 :y 20}
    :radius 5})

(= (lupd center-x inc circle)
   {:center {:x 11 :y 20}
    :radius 5})

(let [go-right (lupd center-x inc)]
  (= (go-right circle)
     {:center {:x 11 :y 20}
      :radius 5}))

(let [go-right (fn [some-lens-to-x]
                 (lupd some-lens-to-x inc))]
  [(= ((go-right point-x) point)
      {:x 11 :y 20})
   
   (= ((go-right center-x) circle)
      {:center {:x 11 :y 20}
       :radius 5})])


;; Lenses and the state monad
;; **************************
;;
;; Lenses do work well with the state monad.
;;
;; First, let's define a couple helper functions (that really should be in the monad library)

(defn eval-state
  "runs the statefull computation with the provided initial state,
 returning the last result"
  [computation initial-state]
  (get (computation initial-state) 0))

(defn exec-state
  "runs the statefull computation with the provided initial state,
 returning the state at the end of the computation"
  [computation initial-state]
  (get (computation initial-state) 1))

;; data.lens introduces the slget slset and slupd functions for statefull computations
(= (eval-state (domonad state-m
                        [x (slget point-x)]
                        x)
               point)
   10)

(= (exec-state (domonad state-m
                        [_ (slset point-x 1)]
                        :dont-care)
               point)
   {:x 1 :y 20})

(= (exec-state (domonad state-m
                        [_ (slupd point-x inc)]
                        :dont-care)
               point)
   {:x 11 :y 20})

;; so we can go fully imperative now, while retaining immutability.
(let [+= (fn [some-lens value]
           (slupd some-lens (partial + value)))]
  [(= (exec-state (domonad state-m
                           [_ (+= point-x 1)]
                           :dont-care)
                  point)
      {:x 11 :y 20})
   (= (exec-state (domonad state-m
                           [_ (+= center-x 1)]
                           :dont-care)
                  circle)
      {:center {:x 11 :y 20}
       :radius 5})])


;; A somewhat practical example
;; ****************************
;;
;; we want to have a database with the following data
;; - a list of people
;; - a list of dwellings
;; - a person has a social security number (ssn), a dwelling, and a name
;; - a dwelling has an address and a list of persons that live there
;;
;; the problem :
;; Since we have a bidirectional link between persons and dwelling, we cannot really express
;; it in a purely functional way. Also even if we could (there are ways), who's the owner of the other ?
;;
;; solutions :
;; - We could use clojure's reference types, but then we loose immutablility.
;; - We could use an id field from our person and dwelling items, then use that as the links.
;;   This works well, but what if we change the access method ? and also that requires to know
;;   the access path to the linked items.
;; - We could use lenses !


;; we'll have a map form social security number to person
(def initial-people {1 {:ssn 1
                        :name "Harry Potter"
                        :dwelling nil}
                     2 {:ssn 2
                        :name "Sherlock Holmes"
                        :dwelling nil}
                     3 {:ssn 3
                        :name "Dr Watson"
                        :dwelling nil}})

;; and a map from address to dwelling
(def initial-dwellings
  {"221B, Baker Street" {:address "221B, Baker Street"
                         :people []}
   "4 Privet Drive, under the Stairs" {:address "4 Privet Drive, under the Stairs"
                                       :people []}})


;; dwellings and people of england
(def initial-england {:people initial-people
                     :dwellings initial-dwellings})


;; now let's define a few lenses
(defn lperson [ssn]
  (lcomp (map-val-lens :people) (map-val-lens ssn)))

(defn ldwelling [address]
  (lcomp (map-val-lens :dwellings) (map-val-lens address)))

;; a linking function
(defmonadfn link-person-to-dwelling [ssn address]
  (let [person (lperson ssn)
        dwelling (ldwelling address)]
    (domonad
     [_ (slupd person #(assoc % :dwelling dwelling))
      _ (slupd dwelling #(update-in % [:people] conj person))]
     :dont-care)))

;; and do the linking
(def england1 (exec-state
               (domonad state-m
                        [_ (link-person-to-dwelling 1 "4 Privet Drive, under the Stairs")
                         _ (link-person-to-dwelling 2 "221B, Baker Street")
                         _ (link-person-to-dwelling 3 "221B, Baker Street")]
                        :dont-care)
               initial-england))

(comment
  (pprint england1))

;; who's living at baker street ?

(defn dwelling-at [address]
  (slget (ldwelling address)))

(comment
  (pprint (eval-state (dwelling-at "221B, Baker Street")
                      england1)))

(defn people-at [address]
  (domonad state-m
           [{people :people} (dwelling-at address)
            result (m-seq (map slget people))]
           result))

(comment
  (pprint (eval-state (people-at "221B, Baker Street")
                      initial-england))
  (pprint (eval-state (people-at "221B, Baker Street")
                      england1)))

;; those people at 221B Baker Street are on a new case
;; we need to update them

(defn update-people-at [address update-func]
  (domonad state-m
           [{people :people} (dwelling-at address)
            result (m-seq (map #(slupd % update-func) people))]
           :dont-care))

(comment
  (pprint (exec-state (update-people-at "221B, Baker Street" #(assoc % :case "The Hound of the Baskervilles"))
                      england1)))


;; In conclusion
;; *************
;;
;; We have shown how to describe cyclic dependencies inside our database by using lenses.
;; We've also shown how, with lenses, one can write imperative style code while
;; retaining referencial transparency and immutability.
;;
