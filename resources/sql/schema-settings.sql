-- This is for Small World, not for Ketchup Club

create table if not exists settings (
  id integer primary key generated always as identity,
  screen_name    varchar(255) not null unique,
  name           varchar(255),
  twitter_avatar varchar(255),
  welcome_flow_complete boolean not null default false,
  locations       json,
  friends_refresh json,
  email_address       varchar(255),
  email_notifications varchar(255),
  twitter_last_fetched timestamp not null default current_timestamp,
  created_at           timestamp not null default current_timestamp,
  updated_at           timestamp not null default current_timestamp
);

--- split here ---

--------------------------------------------------------------------------------
---- auto-update updated_at ----------------------------------------------------
--------------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION public.set_current_timestamp_updated_at()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
DECLARE
  _new record;
BEGIN
  _new := NEW;
  _new."updated_at" = NOW();
  RETURN _new;
END;
$function$;

--- split here ---

CREATE TRIGGER set_updated_at
BEFORE UPDATE ON settings
FOR EACH ROW
EXECUTE FUNCTION set_current_timestamp_updated_at();

--------------------------------------------------------------------------------
---- add an index on screen_name -----------------------------------------------
--------------------------------------------------------------------------------

--- split here ---

CREATE INDEX index__screen_name__settings
ON settings (screen_name);
