(ns example.core
  (:use [cark.data.lenses]))

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

;; I expect this type of lens to be pretty common, so there is a function that creates these
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

;; currying makes it easy to define actions on a structur
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

