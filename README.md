# Reagent Phonecat

The official AngularJS 'Phonecat' tutorial ported to a ClojureScript + Reagent stack.

This project is based on the Reagent Leiningen template, which has the following workflow:

```
lein deps # installing the dependencies

lein figwheel # compiling clojurescript and serving the files with a development server

## Optional: starting a ClojureScript REPL
lein repl # starting a Clojure REPL
>reagent-phonecat.handler: (browser-repl) # starting the ClojureScript REPL
```
See [here]() for more information.

You can see the code for each step in the tutorial by `git`-checking out the corresponding branches (`step-1` to `step-12`). Please note that:
* `step-11` has not been implemented, since I have not found an equivalent of `ngResource` for ClojureScript
* `step-12` (animations) is currently in a quite experimental state, suggestions appreciated.

## Contributing

This is my first try at Reagent, and many of the choices I made are questionable. Comments, critics and suggestions for improvement are appreciated and encouraged.
