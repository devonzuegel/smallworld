while true
do
  now=$(date +"%T")
  echo "$now - sleeping for 1 minutes before next restart"
  sleep 60  # seconds
  heroku restart
done
