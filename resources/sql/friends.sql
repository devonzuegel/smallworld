-- name: ys-friends-by-screen-name
with friends_rows as (
  select json_array_elements(data->'friends') as f
  from friends
  where request_key = :screen_name
)
select
  f->'id' as id,
  f->'description' as description,
  f->'screen-name' as "screen-name",
  f->'profile-image-url-https' as "profile-image-url-https",
  f->'email' as email,
  f->'location' as location,
  f->'name' as name
from friends_rows;

-- name: ys-friends-by-screen-name-debug
select json_array_elements(data->'friends') as f
from friends
where request_key = :screen_name