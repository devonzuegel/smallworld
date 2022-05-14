(defproject smallworld "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name ""
            :url ""}
  :clean-targets ^{:protect false} [:target-path "out" "resources/public/js"]
  :min-lein-version "2.5.3"
  :repl-options {:init-ns dev.repl
                 :timeout 380000}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/clojurescript "1.10.520"]
                 [reagent "0.9.0-rc1"]
                 [compojure "1.6.2"]
                 [twttr "3.2.3"]
                 [cheshire "5.10.1"]
                 [oauth-clj "0.1.16"]
                 [cljsjs/mapbox "0.46.0-0"]
                 [ring/ring-jetty-adapter "1.9.5"]
                 [ring/ring-ssl "0.3.0"]
                 [yesql "0.5.3"]
                 [org.clojure/data.json "2.4.0"]
                 [clj-fuzzy "0.4.1"]
                 [prismatic/schema "1.2.0"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [environ "1.1.0"]]
  :plugins [[lein-environ "1.1.0" :hooks false]
            [lein-cljsbuild "1.1.7"]
            [lein-figwheel "0.5.19"]]
  :figwheel {:css-dirs ["resources/public/css"]
             :server-port 3450
            ;;  :ring-handler smallworld.web/app ;; run the backend too
            ;;  :nrepl-port 7888
             }
  :uberjar-name "smallworld.jar"
  :profiles {:dev {:dependencies [[cider/piggieback "0.4.1"]
                                  [figwheel-sidecar "0.5.18"]
                                  ;; [binaryage/devtools "0.9.10"]
                                  ]
                   :source-paths ["src" "dev"]
                   :cljsbuild {:builds [{:id "dev"
                                         :source-paths ["src"]
                                         :figwheel true
                                         :compiler {:main "smallworld.frontend"
                                                    ;; :preloads [devtools.preload]
                                                    :asset-path "js/out"
                                                    :output-to "resources/public/js/main.js"
                                                    :output-dir "resources/public/js/out"
                                                    :optimizations :none
                                                    ;; :recompile-dependents true
                                                    :source-map true}}]}}
             :uberjar {:env {:production true}
                       :source-paths ["src"]
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]
                       :aot :all
                       :prep-tasks ["compile" ["cljsbuild" "once"]] ; can comment this out in dev to make it faster to build the serverside code
                       :cljsbuild {:builds [{:id "production"
                                             :source-paths ["src"]
                                             :jar true
                                             :compiler {:main "smallworld.frontend"
                                                        :asset-path "js/out"
                                                        :output-to "resources/public/js/main.js"
                                                        :output-dir "resources/public/js/out"
                                                        :optimizations :advanced
                                                        :pretty-print false}}]}}})
