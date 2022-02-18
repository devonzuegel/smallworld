bin/make-jar.sh &&
git add git add target/smallworld.jar &&
git commit -m "built target/smallworld.jar" &&
bin/deploy.sh &&
bin/heroku-logs.sh