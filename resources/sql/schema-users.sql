-- TODO: in the future, may want to add an index on `phone` and `screen_name`

create table if not exists users (
  id                  integer primary key generated always as identity,
  phone               varchar(255) not null unique,
  last_ping           timestamp not null default current_timestamp,
  status              varchar(255) not null default 'offline',
  screen_name         varchar(255) not null unique,
  name                varchar(255),
  email_address       varchar(255),
  email_notifications varchar(255),
  created_at          timestamp not null default current_timestamp,
  updated_at          timestamp not null default current_timestamp
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
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION set_current_timestamp_updated_at();
