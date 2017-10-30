;; This is a namespace ("ns") — a structural unit of Clojure codebase.
(ns wwc-clojure.core
  "This is a small 15 puzzle game created to show Clojure programming language in its natural habitat.
  Instructions: if new to Clojure, please read bottom to top to follow the comments.
  Try loading it to REPL and follow the instructions being printed.
  REPL allows to inspect the documentation associated with the names, for example:
  (doc wwc-clojure.core)")

;; The form (as stated between parentheses) below will be evaluated when this namespace (file) is loaded in REPL.
;; The first "word" is the name of the function being called: "println" is just the usual System.out.println of Java.
;; The result of calling the function is two-fold:
;; 1) System.out.println will display the string in the double quotes (a side-effect)
;; 2) The function call itself will return 'nil'. All functions have return values, but not all of them return
;; something meaningful.
;; The function being invoked is always the name after the opening parenthesis, known as "prefix notation".
;; For example a sum would look like: (+ 1 2), and an expression that checks for equality: (= 1 "1").
(println "To play: call (def play! (wwc-clojure.core/new-game)), then (play! :right) or (play! :up) etc.")

;; (def) form binds a value to a name (called "symbol") in the scope of this namespace.
;; Backslash followed by a character is a literal character value.
(def empty-space \*)

(def solved-board
  ;; (let) form binds values to symbols as well: the major difference is the scope where the name is defined.
  ;; Once parentheses are closed the name bound by (let) is gone.
  (let [tiles (vec (range 1 16))
        ;; 'tiles' is a vector of 15 elements: numbers 1 to 15, the tiles used on the board.
        ;; (assoc) creates a new vector: this one has one more "tile" at the very end, the empty space that allows
        ;; to move tiles on the board.
        ;; 'tiles' remains unchanged - it has one item less than 'tiles-and-space' have.
        tiles-and-space (assoc (vec tiles) 15 empty-space)]
    ;; (mapv) will eagerly iterate over 'tiles-and-space', calling the anonymous function (fn [tile] ...) on each item
    ;; in it. The results of calling it would be stored in a new vector, one value for each item of the original vector.
    ;; (mapv) is the last expression, since after it only closing parentheses follow. It will be the value returned by
    ;; whole (let) expression, and the value that 'solved-board' will refer to.
    (mapv (fn [tile]
            ;; Here we pad the tile names with whitespaces to make them format nicely. It is unfolded starting with the
            ;; inner calls:
            ;; Take 'tile', convert it to String with (str), get its length with (count), check if it is equal to 1
            ;; and if it so, calculate the first argument to the outer invocation of (str) to be a whitespace (a mouthful!).
            ;; Then have the tile as the second argument to (str) - and now a new String is complete.
            ;; Tile 1 will be transformed to " 1", and 11 to "11".
            (str (when (= 1 (count (str tile)))
                   \space)
                 tile))
          tiles-and-space)))

;; (defn) combines (def) and (fn [] )
;; (solved?) is a function that will accept 1 argument it refers to as 'board'.
;; the name 'solved-board' is defined in the scope of the namespace, above.
;; The return value is a boolean, either true or false.
(defn solved?
  "Return true if the board is solved: numbers are ordered from 1 to 15 with the empty space in bottom right corner;
  false otherwise."
  [board]
  (= solved-board board))

(defn new-board
  "Build a new game board."
  []
  ;; The value of 'solved-board' is not mutated by calling 'shuffle' and giving it as an argument here.
  ;; Thi is not specific to 'shuffle'. Data structures are immutable.
  (shuffle solved-board))

(defn move-tiles
  "Exchange titles in two positions on the board."
  ;; This function accepts two arguments. The first one is named 'board', but the second one does not have a name.
  ;; Instead it is "destructured": the second argument is vector of length=2 representing a pair of positions on the
  ;; board, and both of the positions are bound to their names right away using destructuring with [].
  ;; Destructuring mirrors creation: vector containing 4 and 2 would be created as [4 2].
  [board [first-position second-position]]
  ;; board is a vector, and position is a number, so (get) will access this index in the vector, returning the value.
  (let [first-tile (get board first-position)
        second-tile (get board second-position)]
    ;; (assoc), or "associate", changes values associated with the keys (and also vector indices).
    (assoc board
      first-position second-tile
      second-position first-tile)))

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

(defn moves
  "Given the board return the map of moves available."
  [board]
  ;; Clojure-Java interop is as easy as this: to call 'indexOf' on an Array here we name the method
  ;; instead of the function we call, and give the object on which to call it as the first argument.
  ;; In Java it would be 'board.indexOf(emptySpace)'.
  (let [empty-space-index (.indexOf board empty-space)]
    (get adjacent-spaces empty-space-index)))

(defn tile->str
  "Format a tile as a string to be printed as part of the board."
  [tile]
  (format "[ %s ]" tile))

(defn print-board
  "Print the board."
  [board]
  (println "Is board solved?:" (solved? board))
  ;; Threading macro (->>) offers another way to write (last-call (another-call (call value))): instead simply
  ;; (->> value (call) (another-call) (last-call)). No need to read it from inside out with it, just follow the order
  ;; inside the macro.
  (let [rows (->> board
                  (map tile->str)
                  (partition 4)
                  (map #(clojure.string/join %)))]
    ;; (doseq) iterates over the 4 rows one by one, discarding return values of (println) calls.
    ;; Its goal lies entirely in the side-effects.
    (doseq [row rows]
      (println row))))

(defn play
  "Given the board and the direction where the tile is moving from into the empty space,
  return the board after the move is done."
  [board direction]
  (when-let [move (get (moves board) direction)]
    (move-tiles board move)))

(defn new-game
  "Create a new 15-puzzle game."
  []
  ;; (atom) creates a special container for a value, see https://clojure.org/reference/atoms
  ;; It is one of the nice concurrency primitives Clojure offers.
  ;; 'moves' now refers to an Atom that currently holds an empty vector. This vector will always be empty. But the
  ;; value inside the atom can be exchanged for another one with (swap!).
  ;; This atom will hold the whole history of the game, all the moves that were made by the player.
  (let [moves (atom [])
        board (new-board)]
    (print-board board)
    (println "To make a move call this function with direction :left, :right, :up or :down.")
    (fn [direction]
      ;; (swap!) makes changes to the atom: after calling it 'moves' will contain one more direction keyword.
      ;; The new value is the roughly the same as: (conj @moves direction) but assigned atomically.
      (swap! moves conj direction)
      ;; (reduce) takes 3 arguments: a reducer function, the accumulator and the sequence to be reduce, and returns the
      ;; accumulator. Here the accumulator is the board, and the sequence is all the moves that were played.
      ;; Reducer here can be any function that takes accumulator and a single element of the sequence being reduced.
      ;; The call to this function will reply it all from scratch.
      ;; The current board is not saved anywhere (not that we cannot, but chose not to).
      ;; It is printed and forgotten. Only 'moves' atom will contain the log of all the events that happened since the
      ;; initial board was generated.
      (reduce (fn [board move]
                ;; Can also be stated more concisely: (if-let [moved-board ...] ...)
                (let [moved-board (play board move)]
                  ;; (if) always has two branches to choose between: when first argument evaluates to false or nil,
                  ;; the third argument ("else" branch) is executed, in all other cases it will be the second one.
                  (if moved-board
                    (do (print-board moved-board)
                        moved-board)
                    (do (println "There is no tile there.")
                        board))))
              board
              @moves)
      nil)))