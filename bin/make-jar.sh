echo ""
echo "--------------------------------------------------"
echo "          making the smallworld jar"
echo "--------------------------------------------------"

echo ""
echo "running command:"
echo "    source bin/set-env-variables.sh"
source bin/set-env-variables.sh

echo ""
echo "running command:"
echo "    lein clean"
lein clean

echo ""
echo "running command:"
echo "    lein uberjar"
echo ""
lein uberjar