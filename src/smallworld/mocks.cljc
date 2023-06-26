(ns smallworld.mocks)

; TODO: find out how to only load this in development, not in production
; use during development when you don't want to fetch from Twitter

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
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

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(def friends [friend
              friend
              friend
              friend])

(def settings {:friends_refresh nil,
               :twitter_last_fetched #inst "2023-06-24T21:11:52.374651000-00:00",
               :name "myscreenname",
               :screen_name "myscreenname",
               :main_location_corrected nil,
               :updated_at #inst "2023-06-24T21:11:52.374651000-00:00",
               :locations [{:name "South Florida ☀️",
                            :coords {:lat 25.719772338867188, :lng -80.4179916381836},
                            :distances nil,
                            :special-status "twitter-location",
                            :name-initial-value "South Florida ☀️"}
                           {:name "Sonoma County",
                            :coords {:lat 38.43969727, :lng -122.71564484},
                            :loading false,
                            :special-status "added-manually"}
                           {:name "Santa Rosa",
                            :coords {:lat 38.43969727, :lng -122.71564484},
                            :loading false,
                            :special-status "added-manually"}
                           {:name "Windsor, CA",
                            :coords {:lat 38.54728317, :lng -122.81663513},
                            :loading false,
                            :special-status "added-manually"}]
               :id 217,
               :welcome_flow_complete true,
               :name_location_corrected nil,
               :email_address "example@gmail.com",
               :email_notifications "daily",
               :twitter_avatar
               "https://pbs.twimg.com/profile_images/1117495906335485952/waXUR3aO.jpg",
               :created_at #inst "2023-06-24T02:39:56.647212000-00:00"})

(def raw-twitter-friend
  {:screen-name "_kasita"
   :location "Austin, TX"
   :description "Hospitality for the Modern Traveler"
   :default-profile-image false
   :follow-request-sent false
   :friends-count 68
   :withheld-in-countries []
   :profile-background-image-url
   "http://abs.twimg.com/images/themes/theme1/bg.png"
   :is-translator false
   :utc-offset nil
   :name "Kasita"
   :profile-sidebar-fill-color "DDEEF6"
   :profile-sidebar-border-color "C0DEED"
   :statuses-count 912
   :entities
   {:url
    {:urls
     [{:url "https://t.co/m86n6JOpCR"
       :expanded-url "http://www.kasita.com"
       :display-url "kasita.com"
       :indices [0 23]}]}
    :description {:urls []}}
   :id-str "3661383132"
   :following true
   :profile-background-color "C0DEED"
   :lang nil
   :live-following false
   :profile-background-image-url-https
   "https://abs.twimg.com/images/themes/theme1/bg.png"
   :followers-count 2054
   :profile-background-tile false
   :notifications false
   :is-translation-enabled false
   :translator-type "none"
   :status
   {:retweet-count 0
    :in-reply-to-user-id nil
    :coordinates nil
    :in-reply-to-user-id-str nil
    :place nil
    :geo nil
    :entities
    {:hashtags []
     :symbols []
     :user-mentions []
     :urls []
     :media
     [{:sizes
       {:medium {:w 1200, :h 1200, :resize "fit"}
        :thumb {:w 150, :h 150, :resize "crop"}
        :large {:w 1800, :h 1800, :resize "fit"}
        :small {:w 680, :h 680, :resize "fit"}}
       :display-url "pic.twitter.com/NZgHvAQyJv"
       :expanded-url
       "https://twitter.com/_kasita/status/1376323397513269255/photo/1"
       :type "photo"
       :id-str "1376323237597024260"
       :id 1376323237597024260
       :url "https://t.co/NZgHvAQyJv"
       :indices [39 62]
       :media-url-https
       "https://pbs.twimg.com/media/ExmusUkWEAQKACD.jpg"
       :media-url "http://pbs.twimg.com/media/ExmusUkWEAQKACD.jpg"}]}
    :id-str "1376323397513269255"
    :in-reply-to-status-id nil
    :source "<a href=\"https://www.later.com\" rel=\"nofollow\">LaterMedia</a>"
    :extended-entities
    {:media
     [{:sizes
       {:medium {:w 1200, :h 1200, :resize "fit"}
        :thumb {:w 150, :h 150, :resize "crop"}
        :large {:w 1800, :h 1800, :resize "fit"}
        :small {:w 680, :h 680, :resize "fit"}}
       :display-url "pic.twitter.com/NZgHvAQyJv"
       :expanded-url
       "https://twitter.com/_kasita/status/1376323397513269255/photo/1"
       :type "photo"
       :id-str "1376323237597024260"
       :id 1376323237597024260
       :url "https://t.co/NZgHvAQyJv"
       :indices [39 62]
       :media-url-https
       "https://pbs.twimg.com/media/ExmusUkWEAQKACD.jpg"
       :media-url "http://pbs.twimg.com/media/ExmusUkWEAQKACD.jpg"}]}
    :lang "en"
    :possibly-sensitive false
    :id 1376323397513269255
    :contributors nil
    :truncated false
    :retweeted false
    :in-reply-to-screen-name nil
    :is-quote-status false
    :in-reply-to-status-id-str nil
    :favorited false
    :created-at "Mon Mar 29 00:00:43 +0000 2021"
    :favorite-count 1
    :text
    "Hospitality Redefined. Coming in 2021. https://t.co/NZgHvAQyJv"}
   :id 3661383132
   :url "https://t.co/m86n6JOpCR"
   :profile-use-background-image true
   :protected false
   :listed-count 60
   :muting false
   :profile-link-color "1DA1F2"
   :geo-enabled true
   :has-extended-profile false
   :favourites-count 847
   :profile-banner-url
   "https://pbs.twimg.com/profile_banners/3661383132/1563572069"
   :created-at "Wed Sep 23 16:47:58 +0000 2015"
   :profile-image-url-https
   "https://pbs.twimg.com/profile_images/1348709965150748672/FcD5yO9__normal.jpg"
   :contributors-enabled false
   :profile-image-url
   "http://pbs.twimg.com/profile_images/1348709965150748672/FcD5yO9__normal.jpg"
   :profile-text-color "333333"
   :blocking false
   :default-profile true
   :verified false
   :time-zone nil
   :blocked-by false})