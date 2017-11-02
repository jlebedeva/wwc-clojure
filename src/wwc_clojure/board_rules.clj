(ns wwc-clojure.board-rules)

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

(def adjacent-spaces
  "A lookup table of possible moves for each space on the board.
  It is a vector of 16 maps, one for each position.
  [{:right [0 1], :down [0 4]}
   ...
   {:left [15 14], :up [15 11]}]"
  (let [spaces             (range 16)
        ;; (set) takes a vector and returns a hash-set with its distinct items
        space-on-board?    (set spaces)
        ;; 'space->moves' is a function that exists temporarily. It takes an index and returns a hash-map with 4 keys.
        ;; Keys can be anything, but here they are keywords :left, :right, :up and :down.
        ;; Values are vectors with two values (just like the ones 'move-tiles' would accept).
        space->moves       (fn [idx] {:left [idx (- idx 1)]
                                      :right [idx (+ idx 1)]
                                      :up [idx (- idx 4)]
                                      :down [idx (+ idx 4)]})
        unfiltered-moves   (map space->moves spaces)
        leftmost-space?    (fn [index] (zero? (mod index 4)))
        move-allowed?      (fn [map-entry]
                             (let [[direction-keyword [from-space to-space]] map-entry]
                               (and (space-on-board? to-space)
                                    (case direction-keyword
                                      :left (not (leftmost-space? from-space))
                                      :right (not (leftmost-space? to-space))
                                      true))))]
    (mapv (fn [moves-from-space]
            ;; Hash map can be represented as a vector of pairs, as 'move-allowed?' does.
            ;; (into) will just convert it back to a map after that.
            (into {} (filter move-allowed? moves-from-space)))
          unfiltered-moves)))