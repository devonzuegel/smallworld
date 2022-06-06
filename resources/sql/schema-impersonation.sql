create table if not exists impersonation (
  screen_name varchar(255), -- if null, then admin isn't impersonating
  created_at timestamp not null default current_timestamp,
  updated_at timestamp not null default current_timestamp
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
BEFORE UPDATE ON impersonation
FOR EACH ROW
EXECUTE FUNCTION set_current_timestamp_updated_at();