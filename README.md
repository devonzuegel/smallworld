# Small World

you can find the live version of Small World at:
https://smallworld.kiwi

##### table of contents:
- [run, build, \& deploy](#run-build--deploy)
  - [local setup](#local-setup)
  - [local development](#local-development)
  - [update code running in the repl](#update-code-running-in-the-repl)
  - [deploy to Heroku](#deploy-to-heroku)
- [initial designs](#initial-designs)
- [emails](#emails)
- [Monitoring](#monitoring)
- [sql cheatsheet](#sql-cheatsheet)
- [misc commands cheatsheet](#misc-commands-cheatsheet)
- [how the matchmaking algorithm works](#how-the-matchmaking-algorithm-works)
- [branding ideas](#branding-ideas)
- [TODO before launch](#todo-before-launch)
    - [features](#features)
    - [last-minute admin before it's usable](#last-minute-admin-before-its-usable)
    - [nice-to-haves, but not necessary for alpha launch to friends:](#nice-to-haves-but-not-necessary-for-alpha-launch-to-friends)
    - [before we launch the public beta:](#before-we-launch-the-public-beta)
    - [nice to have later on:](#nice-to-have-later-on)
    - [not that important](#not-that-important)
- [key design decisions:](#key-design-decisions)

## run, build, & deploy
### local setup

1. `lein install`
2. install postgres: https://postgresapp.com (database)
3. run `bin/setup` to create a local postgres db called `smallworld-local`
### local development
1. run `bin/start-dev.sh`
   - sets the environment variables
   - starts the server: http://localhost:3001
   - starts the frontend hot-reloading*
   - starts the repl

2. connect Calva repl in VSCode to the repl running in the terminal &nbsp; *(optional – the previous step starts a repl in your terminal, so this step is just for people who prefer to use the Calva repl instead of the terminal repl)*
   - command in VSCode: `Calva: Connect to a running REPL server in the project`
   - how to reload your code into the repl: `Calva: Load Current File and Dependencies`
   - how to reload the backend code in the running server: `(restart-server)`

> \* if you want to start <i>just</i> the frontend hot-reloading, without the server: `lein figwheel`.  you probably won't use this often, as `lein repl` starts the frontend hot-reloading as well as the server.

### update code running in the repl
you have two options:

1. reload the entire file into the repl
   - command in VSCode: `Calva: load current file and dependencies`
   - pros: simpler because it just reloads everything that file needs, so you don't need to worry about it
   - cons: slower
2. evaluate just the form that you want to update in the code
   - e.g. you can evaluate just the `(defroutes app ...) form if you updated code within
   - pros: faster
   - cons: more likely that you forget to evaluate a dependency that's needed and the whole thing doesn't actually update as you expect

### deploy to Heroku

```
bin/make-and-deploy.sh
```

<details><summary>here are the steps that script follows, broken down into separate subscripts:</summary>


1. build a production version
   ```sh
   bin/make-jar.sh
   ```

2. optional: run the jar locally to make sure it works, and open it at http://localhost:8080
   ```sh
   bin/run-jar.sh
   ```

3. deploy the jar to heroku
   ```sh
   bin/deploy
   ```

4. view heroku logs to check if deployment succeeded
   ```sh
   bin/heroku-logs.sh
   ```

</details>

## initial designs

[🎨 Figma workspace](https://www.figma.com/file/7fJoEke9aKGNg5uGE8BMCm/Small-World-mocks?node-id=0%3A1)

| ![](dev/design%20mocks/about.png) | ![](dev/design%20mocks/main%20screen%20map.jpg) | ![](dev/design%20mocks/main%20screen.jpg) |
| --------------------------------- | ----------------------------------------------- | ----------------------------------------- |
|                                   |                                                 |                                           |

## emails
- the server sends emails via SendGrid.  view/edit the templates here:
https://mc.sendgrid.com/dynamic-templates

## Monitoring
We use:
- https://analytics.google.com to view website traffic
- https://tryretool.com as a dashboard to view the database and other metrics
- https://my.papertrailapp.com to view server logs

## sql cheatsheet

- open Heroku Postgres instance in terminal (or you can view the data in a GUI with Postbird):

   ```bash
   bin/postgres-heroku.sh
   ```

- open local Postgres instance in terminal:

   ```bash
   bin/postgres-local.sh
   ```

- view all tables:

   ```sql
   select table_name from information_schema.tables
   where  table_schema = 'public';
   ```

- make a user go through welcome flow again:

   ```sql
   update settings set    welcome_flow_complete = false
   where  screen_name           = 'devon_dos';
   ```

- get column names of a table:

   ```sql
   select column_name, data_type
   from   information_schema.columns
   where  table_name = 'friends';
   ```

- reset a user: (BE CAREFUL, THIS IS VERY DESTRUCTIVE!)

   ```sql
   delete from twitter_profiles where request_key = 'devon_dos';
   delete from friends          where request_key = 'devon_dos';
   delete from settings         where screen_name = 'devon_dos';
   delete from access_tokens    where request_key = 'devon_dos';
   ```

- add a column:

   ```sql
   ALTER TABLE "settings" ADD COLUMN locations jsonb;
   ```

## misc commands cheatsheet

- run command line inside of Heroku:
   ```
   heroku ps:exec --app=small-world-friends
   ```
- view environment variables for process with pid 4:
   ```
   cat /proc/4/environ | tr '\0' '\n'
   ```
- run prod jar locally:
  ```sh
  lein uberjar # builds the jar
  java -Xmx300m -Xss512k -XX:CICompilerCount=2 -Dfile.encoding=UTF-8 -jar target/smallworld.jar -m smallworld.web
  ```

# MeetCute

## how the matchmaking algorithm works

1. each user has `N` lists of cuties:
   - `unseen-cuties`: the cuties the user hasn't seen yet
   - `todays-cutie`: the cutie the user is currently seeing
   - `selected-cuties`: the cuties the user has selected
   - `rejected-cuties`: the cuties the user has rejected
2. each night, there's a cron job that runs and does the following:
      1. pick a random* cutie from the `unseen` list
         - *the cutie has to meet the user's criteria (sexual orientation, etc)
         - in the future, the selection will be more sophisticated, but for now, it's just random
         - if the user has no `unseen` cuties, then leave it blank
      2. add it to the `todays-cutie` list
      3. remove that cutie from the `unseen` list

**invariants that should be true:**
- each cutie should be in exactly 1 of the 3 lists at a time (`unseen-`, `selected-`, or `rejected-`)
- the `todays-cutie` list should always have 0 or 1 cuties; that cutie will also be in one of `unseen-`, `selected-`, or `rejected-`
- if `unseen` is empty for any given user, email the admin to let them know that they need to add more cuties to the db

## branding ideas

- "cuties"
- "we'll have a fresh cutie for you tomorrow!"
- orange favicon
- "media naranja" (spanish for "half orange", their word for "soulmate")
- for valentine's day, we can do corny citrus puns like:
   - "squeeze the day!"
   - "you're the zest"
   - "will you be my clementine?"
   - "you're a cutie"
   - "you're my main squeeze"
   - "you're the zest"
   - "you're a-peeling"
   - "you're the apple of my eye"
   - "orange you glad we met?"
   - "you're berry special to me"
   - "you're the cherry on top"
   - "you're the pineapple of my eye"
   - "you're the lime to my coconut"
   - "I'm grapefruit-ful for your love!"
   - "you're one in a melon!"

## TODO before launch
#### features
- [x] add an orange favicon
- [x] implement the "today's cutie" screen
- [x] get Twilio account unsuspended
- [x] implement the matchmaking algorithm
  - [x] debug: the `unseen-cuties` order gets  messed up, not sure why... probably something to do with (a) memoization or (b) not fetching the profile frequently enough
  - [x] `refresh-todays-cutie` is still buggy... sometimes people end up in 2 lists, when they should only ever be in 1... consider adding assertions to check this
- [x] implement the email that gets sent to the user when their `todays-cutie` is refreshed
- [x] opengraph image
- [x] admin page: add button to refresh everyone's `todays-cutie` list

#### last-minute admin before it's usable
- [x] change the Airtable DB to the real one, instead of fake data
  - [x] make sure that the column names match
  - [x] fix everyone's phone numbers so that the have the +1 country code at the front
  - [x] change the signup iframe to the correct link (so that it points to the real data instead of fake data): https://airtable.com/embed/appF2K8ThWvtrC6Hs/shrdeJxeDgrYtcEe8
  - [x] when I signed up with my personal phone number and then tried to log in, it said there was no account with that number...
    - I figured out the issue: the airtable API has pagination, so my new account doesn't get returned in the first page of results, so the server thinks it doesn't exist
    - https://stackoverflow.com/questions/62096868/how-to-get-more-than-100-rows-using-airtable-api-using-offest
- [x] fix the header styles on the profile page
- [x] refresh-cuties on a per-person basis
- [x] implement `/meetcute/api/refresh-todays-cutie/all`
  - [x] start with not sending any emails, just to make sure it works
  - [x] randomize the order of the cuties, so that different cuties get shown first to each user
  - [x] then add the email sending
  - [x] replace the admin filter with the `include-in-nightly-job-TMP` filter (which I'll get rid of once I'm done testing)
- [x] run-through of the entire app to make sure it works
  - [x] test it with Erik to make sure it works
- [x] matches screen for admin to save Erik a time-consuming manual process
- [x] send the first emails to everybody MANUALLY
- [x] enable the cron job to refresh the `todays-cutie` list every night

#### nice-to-haves, but not necessary for alpha launch to friends:
- [x] rename `matchmaking` to `meetcute` throughout the codebase
- [x] only refresh-todays-cutie for people who are visible in the gallery (i.e. who haven't opted out)
- [x] figure out why emails didn't send to Milan (and maybe not to others?) – reason: I had set the "last-updated-at" field to Feb 1
- [x] test that Google Analytics is actually working: https://analytics.google.com/analytics/web/#/p305071962/reports/explorer?params=_u..nav%3Dmaui%26_u..insightCards%3D%5B%7B%22question%22:%22Top%20Page%20path%20%2B%20query%20string%20by%20unique%20Users%22%7D%5D&r=lifecycle-traffic-acquisition-v2&ruid=lifecycle-traffic-acquisition-v2,life-cycle,acquisition&collectionId=life-cycle
- [x] fix the "no cuties for you to see" bug that several people reported on 2024.02.02
  - [ ] I thought I fixed it, but on 2024.02.05 Haley reported she's still seeing it
- [x] Willy reported he hasn't been getting the emails
  - [x] appears to be because we don't trim trailing whitespace from the email address before sending. just fixed this
  - [x] tomorrow, check if he got the email (first by checking SendGrid, then by asking him directly)
- [x] make sure we're only sending the daily email to people who were not asked to be removed from the list. they probably don't want to get emails
- [x] add logging for whenever a user selects or rejects a cutie so we can see how much people are using the app
- [x] sort the cuties so that the ones who have selected you are at the front, to increase the likelihood of a match quicker
  - [x] in the short term, we'll just have Lei do this manually
- [x] send an email when two cuties have selected each other
  - [x] design the email
    - subject: Someone thinks you're cute! 🍊
    - body: "You and [cutie's name] both selected each other! Here's a refresher on their bio: [cutie's bio] And here's their contact info: [cutie's contact info] Don't forget to reach out soon!"
  - [x] send each cutie an email with the bio of the other cutie, including contact info
  - [x] backfill the emails for the people who matched on 2024.02.13, i.e. send them manually

#### before we launch the public beta:
- [x] security improvements
- [ ] allow users to filter cuties by city
  - [x] for adding the locations to a profile, you can use the `fetch-coordinates!` and Mapbox functions from Small World!
  - [ ] mobile styles for the profile editing page
  - [ ] backfill the locations for the people who signed up before this feature was added
  - [x] replace the airtable signup form with the custom signup form (important, because right now people can put in poorly-formed phone numbers, which will break the app! though luckily I ask them for the country code very insistently so hopefully that will prevent most issues)
    - [x] add the custom location field input to the signup form
    - [x] add fields for all of the data collected in the original form: https://airtable.com/appF2K8ThWvtrC6Hs/tbl0MIb6C4uOFmNAb/viwiUzsTUarShMVDN?blocks=hide
      - [x] first name
      - [x] last name
      - [x] who invited you? (skipped for now, because the following question captures it, albeit in an unstructured way)
      - [x] if other, who invited you?
      - [x] I'm interested in... [men/women]
      - [x] my gender is... [man/woman]
      - [x] social media links
      - [ ] pictures – add a way to upload (!!!!!!!!!!!!!!!!!!!!!!)
      - [x] home base city
      - [x] other cities
      - [x] birthday
      - [x] phone
      - [x] email
    - [x] add a way to mark the profile as "ready for review", but only once they've completed all the required fields
    - [x] give users a way to make their profile public/private
  - [ ] add option to filter by locations
  - [ ] once this is done, update these people:
    - [ ] Campbell: https://mail.superhuman.com/hello.at.smallworld@gmail.com/thread/18d5784c37614cf0#app
    - [ ] Meia: https://mail.superhuman.com/hello.at.smallworld@gmail.com/thread/18d75e336f838941#app
    - [ ] Asher: https://mail.superhuman.com/hello.at.smallworld@gmail.com/thread/18d8098a9ba42533#app
    - [ ] send an email to everyone to let them know about the new feature!
- [ ] add a way to filter by life stage
- [ ] landing page
- [ ] fix: when a user opens their cutie of the day from the email, there's a moment where the page says "no cutie today" before the cutie actually loads
- [ ] show past cuties that the user has missed
  - [ ] update Gillian when this is done (via SMS)
  - [ ] update mariah when this is done
- [ ] consider adding a way for users to include info about religion, politics, age, what they're looking for, etc

#### nice to have later on:
- [ ] track who has selected who and when, include in some sort of stats, so that we can see if people are using it
  - one way this will be used is so that Erik can see when the mutual selections happened so that he knows if it's a new connection
- [ ] profile page improvements:
  - [x] fix the profile editing feature – bug report with video in email on 2024.01.29
  - [ ] give users a way to turn off the daily emails
  - [ ] enable users to add/remove photos – then let Amit know that it's been updated
- [ ] if cutie A has selected cutie B, then cutie B should have cutie A at the front of their `selected-cuties` list
- [ ] don't expose the /bios endpoint to the public
  - [ ] validate that the phone number is unique
  - [ ] sign up page with validation – replace the Airtable iframe form with a custom form
- [ ] index user with the id, rather than the phone. indexing by the phone number is part of why Mariah's profile got out of whack, because she signed up for a second account with the same phone number and then the second one was never marked as updated
- [ ] in the daily emails, add a "someone has picked you!" to encourage them to go to the site – probably need to design this with a bit more thought though
- [ ] go through all `TODO:`s in the codebase and make sure they're all moved to issues / not critical before launch
- [ ] Idea: one of the questions for your data profile is a mood board with a few prompts like "what is your ideal morning?" or "how would you design your dream vacation?". Basic idea being that it might capture more about your aesthetic, vibe, and preferences than if you just try to describe it in words

#### not that important
- [ ] put admin? into the session so we don't have to hit the db every time we want to check if someone is an admin
      1. create an auth token that has a big string in it
      2. use ring to put it in a "session", which is backed by a cookie in our case (which is limited; doesn't require db so can't fit much data, line 817)
      3. when i parse the verify token, merge these default values in – only erik and i should have admin?=true anyways, so if someone doesn't have admin?=true, that's equivalent to admin?=false
- [ ] create webhook that stores all SendGrid events in db to (a) make it easier to query and (b) make us more resilient to SendGrid's data retention policy: https://docs.sendgrid.com/for-developers/tracking-events/getting-started-event-webhook
- [ ] add a way for admins to log in as any user for debugging purposes
- [ ] add a test to make sure the basic routes all work and do not 404 (especially the 2 signup pages!)
- [ ] make sure it looks nice on mobile (currently it does not look great, though it's funcitonal)
- [ ] consider adding a `undecided-cuties` list, to distinguish between `unseen` and `not-decided`
- [ ] in the email, link directly to the cutie's profile rather than the main page (maybe not necessary...?)
- [ ] rename `bios` to `cuties` throughout the codebase
- [ ] verify email addresses with Twilio Verify too
- [ ] style the SMS verification page with 4 digits, similar to Apple's SMS verification page

## key design decisions:
- not including age
- 1 cutie per day – compared to dating apps, which implicitly commodify people
Now
- friends' vouches
- friends of friends
- dinners
- small photos