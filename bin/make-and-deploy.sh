bin/make-jar.sh &&

echo "" &&
echo "running command:" &&
echo "    source bin/set-env-variables.sh" &&
git add target/smallworld.jar -f &&  # the rest of the /target file is in .gitignore
git commit -m "build target/smallworld.jar" &&

bin/deploy.sh &&
bin/heroku-logs.sh
