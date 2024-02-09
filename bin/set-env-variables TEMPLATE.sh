export AIRTABLE_BASE_API_KEY=get from https://airtable.com
export BING_MAPS_API_KEY=get from Bing Developer
export COOKIE_STORE_SECRET_KEY=generate a 16-char random string
export DATABASE_URL=set up in Heroku
export ENVIRONMENT=e.g. dev-m1-macbook
export EXPO_PUSH_TOKEN=
export JAVA_OPTS="-Xmx300m -Xss512k -XX:CICompilerCount=2 -Dfile.encoding=UTF-8" # match the max heap/stack size set by Heroku
export JWT_SECRET_KEY=generate a 5-word random string
export LEIN_JVM_OPTS="-XX:TieredStopAtLevel=1" # suppresses OpenJDK 64-Bit Server VM warning: https://stackoverflow.com/a/67695691/2639250
export SENDGRID_API_KEY=get from https://sendgrid.com
export SMS_CODE=generate a random code here
export TWILIO_AUTH_TOKEN=get from twilio.com
export TWILIO_PHONE_NUMBER=+16502295016
export TWILIO_SID=get from twilio.com
export TWILIO_VERIFY_SERVICE=VAc2b8caa89134e9b10b31e67d4468a637
export TWITTER_ACCESS_TOKEN_SECRET=get from https://developer.twitter.com
export TWITTER_ACCESS_TOKEN=get from https://developer.twitter.com
export TWITTER_CONSUMER_KEY=get from https://developer.twitter.com
export TWITTER_CONSUMER_SECRET=get from https://developer.twitter.com

# when you add a new environment variable to to this file, make
# sure to also add it to `bin/set-env-variables.sh`