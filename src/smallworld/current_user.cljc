(ns smallworld.current-user)

(def default-state {})

;; use this during development when you don't want to fetch from Twitter for whatever reason
(def dummy-data {:profile_image_url_large
                 "https://pbs.twimg.com/profile_images/1410680490949058566/lIlsTIH6.jpg"
                 :main-coords {:lat 25.792236328125
                               :lng -80.13484954833984}
                 :name-location nil
                 :name "Devon ☀️ (HARDCODED)"
                 :user-id "TODO"
                 :screen-name "devonzuegel"
                 :main-location "Miami Beach"
                 :name-coords nil
                 :distance
                 {:name-main nil
                  :name-name nil
                  :main-main nil
                  :main-name nil}})