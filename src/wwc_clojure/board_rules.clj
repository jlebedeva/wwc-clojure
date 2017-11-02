(ns wwc-clojure.board-rules
  "A helper namespace for wwc-clojure.core")

;; (def) form binds a value to a name (called "symbol") in the scope of this namespace.
(def empty-space "[  * ]")

(def solved-board
  ;; 'tiles' is a vector of 15 elements: numbers 1 to 15, the tiles used on the board.
  (let [tiles (vec (range 1 16))]
    ;; (mapv) will eagerly iterate over 'tiles-and-space', calling the anonymous function (fn [tile] ...) on each item
    ;; in it. The results of calling it would be stored in a new vector, one value for each item of the original vector.
    ;; (mapv) is the last expression, since after it only closing parentheses follow. It will be the value returned by
    ;; whole (let) expression, and the value that 'solved-board' will refer to.
    (conj
      (mapv (fn [tile]
              ;; Here we pad the tile names with whitespaces to make them format nicely. It is unfolded starting with the
              ;; inner calls:
              ;; Take 'tile', convert it to String with (str), get its length with (count), check if it is equal to 1
              ;; and if it so, calculate the first argument to the outer invocation of (str) to be a whitespace (a mouthful!).
              ;; Then have the tile as the second argument to (str) - and now a new String is complete.
              ;; Tile 1 will be transformed to " 1", and 11 to "11".
              (format (if (= 1 (count (str tile)))
                        "[  %s ]"
                        "[ %s ]")
                      tile))
            tiles)
      empty-space)))

(defn position-on-board?
  [index]
  (<= 0 index 15))

(defn leftmost-position?
  [index]
  (zero? (mod index 4)))

(defn rightmost-position?
  [index]
  (= 3 (mod index 4)))

(defn position-moves
  "Function that takes a position index and returns a map of possible moves."
  [idx]
  ;; Keys can be anything, but here they are keywords :left, :right, :up and :down.
  ;; Values are vectors with two values (just like the ones 'move-tiles' would accept).
  (let [left (- idx 1)
        right (+ idx 1)
        up (- idx 4)
        down (+ idx 4)]
    (cond-> {}
            (position-on-board? up) (assoc :up [idx up])
            (position-on-board? down) (assoc :down [idx down])
            (and (position-on-board? left)
                 (not (leftmost-position? idx))) (assoc :left [idx left])
            (and (position-on-board? right)
                 (not (rightmost-position? idx))) (assoc :right [idx right]))))

(def adjacent-positions
  "A vector of maps to look up possible moves for each position on the board.
  It is a vector of 16 maps, one for each position.
  [{:right [0 1], :down [0 4]}
   ...
   {:left [15 14], :up [15 11]}]"
  (mapv position-moves (range 16)))