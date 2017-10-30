# wwc-clojure
Simple interactive 15-puzzle game in Clojure for presentation at [Women Who Code](https://www.meetup.com/Women-Who-Code-Toronto/) Toronto event.

This project was intended as a demonstration of Clojure programming language in action. It is a learning tool.

## Install IDE

Please install [IntelliJ IDEA](http://www.jetbrains.com/idea/download/index.html) and [Cursive](https://cursive-ide.com/userguide/) plugin for it. Both are available under free license.

## Import project

In IntelliJ IDEA import this project: **File → New → Project from Version Control → GitHub**.

Use the repository URL:

    git@github.com:jlebedeva/wwc-clojure.git

Select **Leiningen** as the option to import the project and accept defaults for all other choices.

## Code

The project has a single namespace **wwc-clojure.core**. The implementation allows to play the 15-puzzle game in REPL.

Code contains detailed comments.

## Run the game

1. Right-click **project.clj** file in the **Project** panel on the left and select **Run 'REPL for wwc-clojure'**. When the message "nREPL server started..." appears in the REPL output area it is ready to interact with. At the bottom of REPL panel input area will be accepting new statements.
2. Open file **src → wwc_clojure → core.clj**.
3. Right-click anywhere inside the editor panel and select **REPL → Load file in REPL**. Note the instructions being printed: "To play: call...".
4. In REPL input area, type or paste in:

       (def play! (wwc-clojure.core/new-game))

5. Check your board and make a move using another expression, for example to "move" the "empty" tile displayed as **'\*'** (star) one position up:

       (play! :up)