while true
do
  now=$(date +"%T")
  n_seconds=150
  n_iterations=15
  echo "$now - sleeping for $n_seconds seconds before next restart"
  for i in {1..15}; do
    sleep 15 # seconds
    now=$(date +"%T")
    echo "$now - still sleeping ($i/$n_iterations)"
  done
  heroku restart
done
