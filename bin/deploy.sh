echo ""
echo "--------------------------------------------------"
echo "          deploying the smallworld jar"
echo "--------------------------------------------------"

echo ""
echo "running command:"
echo "    heroku deploy:jar target/smallworld.jar --app small-world-friends"
echo ""
echo "note: this will deploy the *existing* jar to the heroku app."
echo "      run bin/make-jar.sh if you want to create a new jar."
echo ""
heroku deploy:jar target/smallworld.jar --app small-world-friends