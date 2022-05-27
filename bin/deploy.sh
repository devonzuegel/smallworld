echo ""
echo "--------------------------------------------------"
echo "          deploying smallworld to heroku"
echo "--------------------------------------------------"

echo ""
echo "running command:"
echo "    git push heroku HEAD:master   # push the current branch to Heroku, whatever it’s called"
echo ""
git push heroku HEAD:master # push the current branch to Heroku, whatever it’s called

bin/heroku-logs.sh
