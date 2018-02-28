# Workflow Sample Files

These files go with YouTube videos.

There are two tags on this repository.

- workflow-part-1 is the state of the code at the end of [this video](https://youtu.be/XdLIKOJ4rKg).
- workflow-part-2 is for [this video](https://youtu.be/xaM7sqXk32U).

## Some basics:

Building the client and cards:

```
$ lein run -m clojure.main script/figwheel.clj
cljs=> (switch-to-build "dev" "cards")
```

Running the server:

```
$ lein repl
user=> (go)
```

Browse to http://localhost:3000.

Running server tests:

```
$ lein repl
user=> (start-server-tests)
```

and browse to http://localhost:8888/fulcro-spec-server-tests.html. Use the menu in the upper
left corner to turn on integration tests. Requires your computer to understand and have a
`/tmp` directory.