export TWITTER_CONSUMER_KEY=get from https://developer.twitter.com
export TWITTER_CONSUMER_SECRET=get from https://developer.twitter.com
export TWITTER_ACCESS_TOKEN=get from https://developer.twitter.com
export TWITTER_ACCESS_TOKEN_SECRET=get from https://developer.twitter.com
export BING_MAPS_API_KEY=get from Bing Developer
export COOKIE_STORE_SECRET_KEY=generate a 16-char random string
export DATABASE_URL=set up in Heroku
export LEIN_JVM_OPTS="-XX:TieredStopAtLevel=1" # suppresses OpenJDK 64-Bit Server VM warning: https://stackoverflow.com/a/67695691/2639250
export JAVA_OPTS="-Xmx300m -Xss512k -XX:CICompilerCount=2 -Dfile.encoding=UTF-8" # match the max heap/stack size set by Heroku
export SENDGRID_API_KEY=get from https://sendgrid.com