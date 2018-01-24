(ns workflow.client-test-main
  (:require workflow.tests-to-run
            [fulcro-spec.selectors :as sel]
            [fulcro-spec.suite :as suite]))

(enable-console-print!)

(suite/def-test-suite client-tests {:ns-regex #"workflow..*-spec"}
  {:default   #{::sel/none :focused}
   :available #{:focused}})

