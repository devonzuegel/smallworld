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
- [sql cheatsheet](#sql-cheatsheet)
- [misc commands cheatsheet](#misc-commands-cheatsheet)
- [how the matchmaking algorithm works](#how-the-matchmaking-algorithm-works)
- [branding ideas](#branding-ideas)
- [TODO before launch](#todo-before-launch)

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

## sql cheatsheet

- open Heroku Postgres instance in terminal:

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
- orange favicon
- "media naranja" (spanish for "half orange", their word for "soulmate")

## TODO before launch
- [ ] change the Airtable DB to the real one, instead of fake data
- [ ] change the Signup form to the correct link (real data instead of fake data): https://airtable.com/embed/appF2K8ThWvtrC6Hs/shrdeJxeDgrYtcEe8
- [ ] implement the matchmaking algorithm
- [ ] implement the daily email
- [ ] get Twilio account unsuspended
- [ ] add a button for the user to make their account public/private
- [ ] add an orange favicon
- [ ] for valentine's day, we can do corny citrus puns like:
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


