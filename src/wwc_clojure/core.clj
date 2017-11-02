;; This is a namespace ("ns") — a structural unit of Clojure code.
(ns wwc-clojure.core
  "This is a small 15 puzzle game created to show Clojure programming language in its natural habitat.
  Instructions: if new to Clojure, please read bottom to top to follow the comments.
  Try loading it to REPL and follow the instructions being printed.
  REPL allows to inspect the documentation associated with the names, for example:
  (doc wwc-clojure.core)"
  ;;
  (:require [wwc-clojure.board-rules :as rules]))

;; The expression (as stated between parentheses) below will be evaluated when this namespace (file) is loaded in REPL.
;; The first "word" is the name of the function being called: "println" is  "System.out.println" in Java.
;; The result of calling the function is two-fold:
;; 1) The argument of the function, string in the double quotes, is printed (a side-effect)
;; 2) The function call itself returns 'nil', Clojure name for 'null'.
;; All functions have return values, but not all of them return something meaningful.
;; The function being invoked is always the name after the opening parenthesis, the way is known as "prefix notation".
;; For example a sum would look like: (+ 1 2), and an expression that checks for equality: (= 1 "1").
(println "To play: call (def play! (wwc-clojure.core/new-game)), then (play! :right) or (play! :up) etc.")

;; Examples that can be easily send to REPL to try will start with (comment)
(comment
  :1
  (println "Hello, WWC Toronto!")
  (+ 1 2)
  (= 1 "1"))

;; (defn) makes new functions. Each function has a number of arguments it will accept, and the 'body', or what it
;; does when called. The last expression in the body will be returned back.
;; Here is how to do it: (defn function-name [function-argument(s)] function-body)
(defn solved?
  "Check if the board is solved.

  Function that accepts one argument it refers to as 'board'.
  'Board' is a vector of 16 string 'tiles': [  1 ], [  2 ], [  3 ] ... [ 14 ], [ 15 ], [  * ]
  It represents a 4 by 4 board, row 1, then row 2 etc.
  During the game tiles are moved on the board using one empty space, displayed as [  * ]

  The return value is boolean: either true or false.
  True if the board is solved: tiles are ordered from [  1 ] to [  15 ] with the empty space [  * ] in bottom right
  corner, and false otherwise."
  [board]
  ;; The symbol 'board-rules/solved-board' is defined in the namespace nick-named 'board-rules', but its full name is
  ;; 'wwc-clojure.board-rules'.
  (= rules/solved-board board))

(comment
  :2
  [1 2 3]
  (type [1 2 3])
  (= [1 2 3] [1 2 3]))

(comment
  :3
  (defn my-function []
    (= [1 2 3] [1 2 3])
    "Evaluated, but not returned."
    "Returned by the function!")
  (my-function))

(defn new-board
  "Build a new game board.

  Function that accepts no arguments and returns the board, the same 'board' as in solved? function above."
  []
  ;; The value of 'board-rules/solved-board' is not mutated by calling 'shuffle' and giving it as an argument here.
  ;; This is not specific to 'shuffle' only. Data structures in Clojure are immutable in general.
  (shuffle rules/solved-board))

(comment
  :4
  rules/solved-board
  (shuffle rules/solved-board))

(defn move-tiles
  "Exchange tiles in two positions on the board.

  Function that accepts two arguments.
  The first argument is named 'board', the same 'board' as in solved? and new-board functions above.
  The second argument is a vector of length=2 representing a pair of positions on the board. Position is an index,
  number from 0 to 15.

  The return value is 'board' again."
  ;; The second argument does not have a name. Instead, names are given to what it contains inside: the first element
  ;; in it is called 'first-position' and the second one is called 'second-position'. It is called destructuring.
  ;; Destructuring mirrors creation: vector containing 4 and 2 would be created as [4 2].
  [board [first-position second-position]]
  ;; (get) is used to access an index 'first-position' in the 'board' vector, returning the value at the index.
  ;; (let) binds values to symbols: once parentheses are closed though, the name bound by (let) is gone.
  (let [first-tile (get board first-position)
        second-tile (get board second-position)]
    ;; (assoc), short for "associate", changes values associated with the key or keys in the map (and also, more
    ;; importantly, it does so for vectors and vector indices).
    (assoc board
      first-position second-tile
      second-position first-tile)))

(comment
  :5
  (let [x 7]
    (* x x)
    x)
  (assoc {"key" "value"} "another-key" "another-value")
  (let [my-name-map {:first "Yana"
                     :middle nil}]
    (assoc my-name-map :last "Lebedeva")))

(defn moves
  "Get all available moves for this board.

  Function that accepts one argument it refers to as 'board', the same 'board' as in solved?,  new-board and move-tiles
  functions above.
  The return value is a map of moves having 2, 3 or 4 keys and their corresponding values:
  {:right [0 1], :down [0 4]}
  or
  {:left [1 0], :right [1 2], :down [1 5]}
  and so on.

  The number of key-value pairs depends on how many different tiles can be played when empty space is in one particular
  position on the board. A key in the map can be one of 4 keywords, :up, :down, :left or :right.
  Each value is a vector of length=2 representing a pair of positions on the board that should be exchanged to make
  the move, the same value as move-tiles function accepts and destructures."
  [board]
  ;; Clojure-Java interoperability is as easy as this: to call 'indexOf' on an Array here we name the method
  ;; instead of the function we call, and give the object on which to call it as the first argument.
  ;; In Java it is 'board.indexOf(emptySpace), in Clojure (.indexOf board board-rules/empty-space).'.
  (let [empty-space-index (.indexOf board rules/empty-space)]
    (get rules/adjacent-positions empty-space-index)))

(comment
  :6
  (first rules/adjacent-positions)
  (last rules/adjacent-positions)
  rules/adjacent-positions
  rules/solved-board
  (.indexOf rules/solved-board rules/empty-space)
  (get rules/adjacent-positions 15)
  (moves rules/solved-board))

(defn print-board
  "Print the board.

  Function that accepts one argument it refers to as 'board', the same 'board' as in solved?, new-board, move-tiles and
  moves functions above.
  The return value is nil."
  [board]
  (println "Is board solved?:" (solved? board))
  ;; Threading macro (->>) offers another way to write (last-call (another-call (call value)))
  ;; It can be written as (->> value (call) (another-call) (last-call)).
  ;; That way there is no need to read it from inside out, instead it is done in the order of the functions inside the
  ;; macro.
  (let [rows (->> board
                  (partition 4)
                  (map clojure.string/join))]
    ;; (doseq) iterates over the 4 rows one by one, discarding return values of (println) calls.
    ;; Its goal is entirely in the side-effects it produces.
    (doseq [row rows]
      (println row))))

(comment
  :7
  (partition 4 rules/solved-board)
  (clojure.string/join (list \W \W \C \space \T \o \r \o \n \t \o))
  (doseq [i ["line-A" "line-B" "line-C" "line-D"]] (println i)))

(defn play
  "Given the board and the direction where the tile is moving from into the empty space,
  return the board after the move is done.

  Function that accepts two arguments.
  The first argument is named 'board', the same 'board' as in all the functions above.
  The second argument is named 'direction', a keyword that can be :up, :down, :left or :right.
  The return value is: if the move can be done, then the new board, or nil otherwise."
  [board direction]
  (let [move (get (moves board) direction)]
    ;; There are two values in Clojure that not true: false and nil
    ;; When the move is possible, then the 'moves' map has the direction key, (get) returns the pair of indices, and
    ;; move is not a nil.
    (when move
      (move-tiles board move))))

(comment
  :8
  (when (or false nil)
    "Never returned.")
  (if (and true (not (nil? "String, not a nil")))
    "Is a true value"
    "Is a false value")
  (get "Yana" 2)
  (get [1 2 3 5 8] 4)
  (get {:first "Yana" :middle nil :last "Lebedeva"} :first))

;; Functions are "first-class citizens" in Clojure.
;; It means they can be arguments to other functions, and can be returned from other function just like anything else.
;; The name Clojure is play on words: closure is a way to create a function and capture names with their values that it
;; can see.
;; And J is just for Java, right?
(defn new-game
  "Create a new 15-puzzle game.

  Function that accepts no arguments and returns another function."
  []
  ;; (atom) creates a special container for a value, see https://clojure.org/reference/atoms
  ;; It is one of the nice concurrency primitives Clojure offers.
  ;; 'moves' now refers to an Atom that currently holds an empty vector. This vector will always be empty (it is
  ;; immutable!). But the value inside the atom can be exchanged for another one with (swap!) function.
  ;; This atom will hold the whole history of the game, the vector of all the moves that were made by the player.
  ;; Both 'moves' and 'board' are closed over (as in 'closure') when the function to be returned is created.
  (let [moves (atom [])
        board (new-board)]
    (print-board board)
    (println "To make a move call this function with direction :left, :right, :up or :down.")
    (fn [direction]
      ;; (swap!) makes changes to the atom: after calling it 'moves' will contain one more direction keyword in the end.
      ;; The new value is the roughly the same as: (conj @moves direction) but assigned atomically.
      (swap! moves conj direction)
      ;; (reduce) takes 3 arguments: a reducer function, the accumulator and the sequence to be reduced, and returns the
      ;; accumulator. Here the accumulator is the board, and the sequence is all the moves that were played.
      ;; Reducer here can be any function that takes accumulator and a single element of the sequence being reduced.
      ;; The call to this function will replay the board from very beginning.
      ;; The current board is not saved anywhere (not that we cannot, just chose not to).
      ;; The current board is printed and forgotten. Only 'moves' atom will contain the log of all the events that
      ;; happened since the initial board was generated.
      (reduce (fn [board move]
                (let [moved-board (play board move)]
                  ;; (if) always has two branches to choose between: when first argument evaluates to false (or nil),
                  ;; the third argument ("else" branch) is executed, in all other cases the second one is.
                  (if moved-board
                    ;; (do) evaluates every expression, but returns only the result of the last one.
                    (do (print-board moved-board)
                        moved-board)
                    (do (println "There is no tile there.")
                        board))))
              board
              @moves)
      nil)))

(comment
  :9
  (range 10)
  (reduce + 0 (range 10))
  (let [initial-value 0
        an-atom (atom initial-value)]
    (println "Atom is" an-atom "and it's value is" @an-atom)
    (swap! an-atom inc)
    (println "Atom contains the new value" @an-atom)))