# Reagent Phonecat

The official [AngularJS 'Phonecat' tutorial](https://docs.angularjs.org/tutorial) ported to a ClojureScript + Reagent stack.

![](https://docs.angularjs.org/img/tutorial/catalog_screen.png)

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

## About the tutorial

The original tutorial consists of writing a small Single Page App that is a catalog of Android devices, featuring templating, data-binding, AJAX, routing, and some AngularJS-specific topics.

Here are some links to help you navigate efficiently through both implementations:
* **[Step 0](https://docs.angularjs.org/tutorial/step_00):** Initial app setup.
* **[Step 1](https://docs.angularjs.org/tutorial/step_01):** Static template - adding some static content to the HTML. ([Angular diff](https://github.com/angular/angular-phonecat/compare/step-0...step-1) | [Reagent diff](https://github.com/vvvvalvalval/reagent-phonecat/compare/step-0...step-1))
* **[Step 2](https://docs.angularjs.org/tutorial/step_02):** Making the same HTML dynamically generated from data. Note that in the Reagent case, there is no HTML code, and you can experiment with livecoding. ([Angular diff](https://github.com/angular/angular-phonecat/compare/step-1...step-2) | [Reagent diff](https://github.com/vvvvalvalval/reagent-phonecat/compare/step-1...step-2))
* **[Step 3](https://docs.angularjs.org/tutorial/step_03):** Adding text search capabilities, which introduces user input, state and DOM updates. Note that implementing the filtering logic took [some more work](https://github.com/vvvvalvalval/reagent-phonecat/compare/step-2...step-3#diff-2ea0213ccd74241f436b038b8f1ad421R10) in Reagent, as there is no such thing as the built-in [filter](https://docs.angularjs.org/api/ng/filter/filter) AngularJS filter in Reagent; however, you get to work with the full ClojureScript collection manipulation capabilities. ([Angular diff](https://github.com/angular/angular-phonecat/compare/step-2...step-3) | [Reagent diff](https://github.com/vvvvalvalval/reagent-phonecat/compare/step-2...step-3))
* **[Step 4](https://docs.angularjs.org/tutorial/step_04):** More user input with a sorting feature. This does not add much in Reagent, but was an occasion to introduce [Reagent cursors](https://github.com/reagent-project/reagent-cursor). ([Angular diff](https://github.com/angular/angular-phonecat/compare/step-3...step-4) | [Reagent diff](https://github.com/vvvvalvalval/reagent-phonecat/compare/step-3...step-4))
* **[Step 5](https://docs.angularjs.org/tutorial/step_05):** Instead of hardcoding the data, load it with AJAX. ([Angular diff](https://github.com/angular/angular-phonecat/compare/step-4...step-5) | [Reagent diff](https://github.com/vvvvalvalval/reagent-phonecat/compare/step-4...step-5))
* **[Step 6](https://docs.angularjs.org/tutorial/step_06):** Adding images, in Reagent this is trivial.  ([Angular diff](https://github.com/angular/angular-phonecat/compare/step-5...step-6) | [Reagent diff](https://github.com/vvvvalvalval/reagent-phonecat/compare/step-5...step-6))
* **[Step 7](https://docs.angularjs.org/tutorial/step_07):** Demonstrates routing, which takes [a bit of setup](https://github.com/vvvvalvalval/reagent-phonecat/compare/step-6...step-7#diff-2ea0213ccd74241f436b038b8f1ad421R111) in Reagent. Note that in Reagent, 'pages' are just than components.  ([Angular diff](https://github.com/angular/angular-phonecat/compare/step-6...step-7) | [Reagent diff](https://github.com/vvvvalvalval/reagent-phonecat/compare/step-6...step-7))
* **[Step 8](https://docs.angularjs.org/tutorial/step_08):** Implementing a detailed phone view. No new topic. The components-based approach of Reagent was [leveraged](https://github.com/vvvvalvalval/reagent-phonecat/compare/step-7...step-8#diff-2ea0213ccd74241f436b038b8f1ad421R138). ([Angular diff](https://github.com/angular/angular-phonecat/compare/step7...step-8) | [Reagent diff](https://github.com/vvvvalvalval/reagent-phonecat/compare/step-7...step-8#diff-2ea0213ccd74241f436b038b8f1ad421R27))
* **[Step 9](https://docs.angularjs.org/tutorial/step_09):** Displaying a boolean property with check marks. It takes a filter in Angular and a function in Reagent.  ([Angular diff](https://github.com/angular/angular-phonecat/compare/step-8...step-9) | [Reagent diff](https://github.com/vvvvalvalval/reagent-phonecat/compare/step-8...step-9))
* **[Step 10](https://docs.angularjs.org/tutorial/step_10):** More UI state with a changeable main image feature.  ([Angular diff](https://github.com/angular/angular-phonecat/compare/step-9...step-10) | [Reagent diff](https://github.com/vvvvalvalval/reagent-phonecat/compare/step-9...step-10))
* _[Step 11](https://docs.angularjs.org/tutorial/step_11):_ `$resource` service, no ClojureScript equivalent.
* _[Step 12](https://docs.angularjs.org/tutorial/step_12):_ Animations, still experimental for this project.

## Implementation overview

### Librairies

* [Reagent](http://holmsand.github.io/reagent/) for 'templating'
* [Reagent](http://holmsand.github.io/reagent/) and [reagent-cursor](https://github.com/reagent-project/reagent-cursor) for managing state
* A combination of [secretary](https://github.com/gf3/secretary) and [Google Closure's History](http://docs.closure-library.googlecode.com/git/class_goog_History.html) for client-side routing
* [cljs-ajax](https://github.com/JulianBirch/cljs-ajax) for AJAX

### Design choices

Except for UI state with limited scope, all the state of the application is [held in a global atom](https://github.com/vvvvalvalval/reagent-phonecat/blob/f66e515a0e33123999a4585cd8afefd694f1cc49/src/cljs/reagent_phonecat/core.cljs#L27), that acts notably as a partial copy of the database. [Ajax calls](https://github.com/vvvvalvalval/reagent-phonecat/blob/f66e515a0e33123999a4585cd8afefd694f1cc49/src/cljs/reagent_phonecat/core.cljs#L38) are used only to populate and update this atom. While the drawbacks of global state are well-known, I feel there are several things in this configuration that mitigate them:
* Combined with ClojureScript livecoding capabilities, having all the state in one accessible place makes it easy to debug and reason about your stateful app
* Reagent cursors allow us to [decouple view rendering/interactions from state storage](https://github.com/vvvvalvalval/reagent-phonecat/blob/f66e515a0e33123999a4585cd8afefd694f1cc49/src/cljs/reagent_phonecat/core.cljs#L55)
* React components enable you to build the view incrementally, with much finer control flow than the one-step controller->view relationship you typically find in Angular apps.

Navigation is done by keeping the current page component (i.e a function) and the associated route params in the state. This does not feel very neat, I'd rather have only the location hash in the state, but from what I've seen I don't think that's easily compatible with [secretary](https://github.com/gf3/secretary) currently.

Everything is in one namespace, splitting it felt like an overkill since there are only about 200 LoC at most.

## Contributing

This is my first try at Reagent, and many of the choices I made are questionable. Comments, critics and suggestions for improvement are appreciated and encouraged.
