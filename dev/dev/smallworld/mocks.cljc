(ns smallworld.mocks)

; TODO: find out how to only load this in development, not in production
; use during development when you don't want to fetch from Twitter

(def current-user
  {:profile_image_url_large
   "https://pbs.twimg.com/profile_images/1410680490949058566/lIlsTIH6.jpg"
   :main-coords {:lat 25.792236328125
                 :lng -80.13484954833984}
   :name-location nil
   :name "Hardcoded ☀️"
   :user-id "TODO"
   :screen-name "hardcoded"
   :main-location "Miami Beach"
   :name-coords nil
   :distance
   {:name-main nil
    :name-name nil
    :main-main nil
    :main-name nil}})

(def friend
  {:profile_image_url_large
   "https://pbs.twimg.com/profile_images/1421550750426140672/FrfugU7f.png"
   :main-coords nil
   :name-location nil
   :name "friend"
   :user-id "TODO"
   :screen-name "_nakst"
   :main-location "Miami"
   :name-coords nil
   :distance
   {:name-main nil
    :name-name 2
    :main-main nil
    :main-name nil}})

(def friends [friend
              friend
              friend
              friend])