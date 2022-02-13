lein clean &&
source bin/set-env-variables.sh  &&
bin/make-jar.sh &&
bin/deploy.sh &&
bin/heroku-logs.sh