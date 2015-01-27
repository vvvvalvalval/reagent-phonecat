# Reagent Phonecat

The official AngularJS 'Phonecat' tutorial ported to a ClojureScript + Reagent stack.

This project is based on the Reagent Leiningen template, which has the following workflow:

```
lein deps # installing the dependencies

lein figwheel # compiling clojurescript and serving the files with a development server
## you can now visit the running app on [localhost:3449/#/](http.//localhost:3449/#/)

## Optional: starting a ClojureScript REPL
lein repl # starting a Clojure REPL
>reagent-phonecat.handler: (browser-repl) # starting the ClojureScript REPL
```
See [here](https://github.com/reagent-project/reagent-template) for more information.

You can see the code for each step in the tutorial by `git`-checking out the corresponding branches (`step-1` to `step-12`). Please note that:
* `step-11` has not been implemented, since I have not found an equivalent of `ngResource` for ClojureScript
* `step-12` (animations) is currently in a quite experimental state, suggestions appreciated.

## Implementation overview

### Librairies

* [Reagent](http://holmsand.github.io/reagent/) for 'templating'
* [Reagent](http://holmsand.github.io/reagent/) and [reagent-cursor](https://github.com/reagent-project/reagent-cursor) for managing state
* A combination of [secretary](https://github.com/gf3/secretary) and [Google Closure's History](http://docs.closure-library.googlecode.com/git/class_goog_History.html) for client-side routing
* [cljs-ajax](https://github.com/JulianBirch/cljs-ajax) for AJAX

### Design choices

Except for UI state with limited scope, all the state of the application is held in a global atom, that acts notably as a partial copy of the database. Ajax calls are used only to populate and update this atom. While the drawbacks of global state are well-known, I feel there are several things in this configuration that mitigate them:
* Combined with ClojureScript livecoding capabilities, having all the state in one accessible place makes it easy to debug and reason about your stateful app
* Reagent cursors allow us to decouple view rendering/interactions from state storage
* React components enable you to build the view incrementally, with much finer control flow than the one-step controller->view relationship you typically find in Angular apps.

Navigation is done by keeping the current page component (i.e a function) and the associated route params in the state. This does not feel very neat, I'd rather have only the location hash in the state, but from what I've seen I don't think that's easily compatible with [secretary](https://github.com/gf3/secretary) currently.

Everything is in one namespace, splitting it felt like an overkill since there are only about 200 LoC at most.

## Contributing

This is my first try at Reagent, and many of the choices I made are questionable. Comments, critics and suggestions for improvement are appreciated and encouraged.
