while true
do
  now=$(date +"%T")
  n_seconds=100
  n_iterations=10
  echo "$now - sleeping for $n_seconds seconds before next restart"
  for i in {1..10}; do
    sleep 10 # seconds
    now=$(date +"%T")
    echo "$now - still sleeping ($i/$n_iterations)"
  done
  heroku restart
done
