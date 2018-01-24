(ns workflow.client-main
  (:require [workflow.client :as client]
            [fulcro.client :as core]
            [workflow.ui.root :as root]))

; This is the production entry point. In dev mode, we do not require this file at all, and instead mount (and
; hot code reload refresh) from cljs/user.cljs
(reset! client/app (core/mount @client/app root/Root "app"))
