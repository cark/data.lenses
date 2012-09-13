(ns cark.data.lenses
  "Provides data type and functions to work with lenses,
 also known as functional references."
  (:use [clojure.algo.monads]))

(defprotocol PLens
  "All lenses answer to this protocol."
  (lget [self s] [self]
    "returns the value pointed by the lens inside s.")
  (lset [self value s] [self value] [self]
    "sets the value pointed by the lens inside s, returning a new s.")
  (lupd [self func s] [self func] [self]
    "updates the value pointed by the lens by applying func on its current value,
returning a new s."))

;; The Lens data type probably should not be used by the library user
(deftype ^:private Lens [getter setter] 
  PLens
  (lget [self s] (getter s))
  (lget [self] getter)
  (lset [self value s] (setter value s))
  (lset [self value]
    #(setter value %))
  (lset [self]
    (fn
      ([value s]
         (setter value s))
      ([value]
         #(setter value %))))
  (lupd [self func s]
    (setter (func (getter s)) s))
  (lupd [self func]
    #(setter (func (getter %)) %))
  (lupd [self]
    (fn
      ([func s]
         (setter (func (getter s)) s))
      ([func]
         #(setter (func (getter %)) %))))) 

(alter-meta! #'->Lens assoc :no-doc true)

(defn lens
  "Creates and returns new lens.

getter-func : a function which given a single state parameter will return the value pointed by this lens
setter-func : a function which given a new value and a state will return the new state where the value pointed by this lens is now the new value

example :
 (def point {:x 10 :y 20})
 (def x-lens (lens (fn [s] (get s :x))
                   (fn [new-x s] (assoc s :x new-x))))"
  [getter-func setter-func]
  (Lens. getter-func setter-func))

(defn lcomp
  "Composes 2 lenses."
  [outer inner]
  (lens (fn [s]
          (lget inner (lget outer s)))
        (fn [v s]
          (lset outer (lset inner v (lget outer s)) s))))

(defn map-val-lens
  "creates a lens on a map, pointing to the value accessed with the provided key.
lset'ing the value to nil dissociates the key"
  [key]
  (lens (fn [s]
          (get s key)) 
        (fn [value s]
          (if value
            (assoc s key value)
            (dissoc s key)))))

(defn set-lens
  "A lens over a set. lset'ing it to true conj'es the value, to false disj'es it"
  [val]
  (lens (fn [s]
          (s val))
        (fn [value s]
          (if value
            (conj s val)
            (disj s val)))))

(defn slget
  "gets the value pointed by the lens parameter inside the state monad"
  [lens]
  (fn [s]
    [(lget lens s) s]))

(defn slset
  "sets the value pointed by the lens parameter inside the state monad,
 returning the monad value of the old state"
  [lens val]
  (fn [s]
    [s (lset lens val s)]))

(defn slupd
    "updates the value pointed by the lens parameter inside the state monad,
 returning the monad value of the old state"
    [lens func]
    (fn [s]
      [s (lupd lens func s)]))

