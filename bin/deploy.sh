echo ""
echo "--------------------------------------------------"
echo "          deploying smallworld to heroku"
echo "--------------------------------------------------"

echo ""
echo "running command:"
echo "    git push heroku master"
echo ""
echo "note: this will deploy the *existing* jar to the heroku app."
echo "      run bin/make-jar.sh if you want to create a new jar."
echo ""
git push heroku master