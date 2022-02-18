bin/make-jar.sh &&
git add target/smallworld.jar -f &&
git commit -m "built target/smallworld.jar" &&
bin/deploy.sh &&
bin/heroku-logs.sh