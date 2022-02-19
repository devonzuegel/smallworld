bin/make-jar.sh &&
git add target/smallworld.jar -f &&  # the rest of the /target file is in .gitignore
git commit -m "built target/smallworld.jar" &&
bin/deploy.sh &&
bin/heroku-logs.sh