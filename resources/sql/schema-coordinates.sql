create table if not exists coordinates (
  id integer primary key generated always as identity,
  request_key varchar(255) not null unique,
  data json,
  created_at timestamp not null default current_timestamp,
  updated_at timestamp not null default current_timestamp
)

--- split here ---

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
BEFORE UPDATE ON coordinates
FOR EACH ROW
EXECUTE FUNCTION set_current_timestamp_updated_at();