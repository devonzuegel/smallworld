echo ""
echo "--------------------------------------------------"
echo "          starting the smallworld server"
echo "--------------------------------------------------"

echo ""
echo "running command:"
echo "    source bin/set-env-variables.sh"
echo ""
source bin/set-env-variables.sh

echo "running command:"
echo "    java -jar target/smallworld.jar -m smallworld.web"
echo ""
java -jar target/smallworld.jar -m smallworld.web