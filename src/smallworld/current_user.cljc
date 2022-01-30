(ns smallworld.current-user)

(def default-state {})

;; use this during development when you don't want to fetch from Twitter for whatever reason
(def dummy-data {:profile_image_url_large
                 "https://pbs.twimg.com/profile_images/1410680490949058566/lIlsTIH6.jpg"
                 :main-coords {:lat 25.792236328125
                               :lng -80.13484954833984}
                 :name-location nil
                 :name "Current User [HARDCODED]"
                 :user-id "TODO"
                 :screen-name "111111"
                 :main-location "Miami Beach"
                 :name-coords nil
                 :distance
                 {:name-main nil
                  :name-name nil
                  :main-main nil
                  :main-name nil}})

(def dummy-data-friends
  [{:profile_image_url_large "https://pbs.twimg.com/profile_images/1421550750426140672/FrfugU7f.png"
    :main-coords nil
    :name-location nil
    :name "22222222"
    :user-id "TODO"
    :screen-name "000000"
    :main-location "they/them"
    :name-coords nil
    :distance {:name-main nil
               :name-name nil
               :main-main nil
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1477167544800849922/Nr2CEkUs.jpg"
    :main-coords nil
    :name-location nil
    :name "33333333"
    :user-id "TODO"
    :screen-name "222222"
    :main-location "follow on ig for bonus content"
    :name-coords nil
    :distance {:name-main nil
               :name-name nil
               :main-main nil
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1316474332659539970/eweGtE9t.jpg"
    :name-location nil
    :name "44444444"
    :user-id "TODO"
    :screen-name "333333"
    :name-coords nil
    :main-coords {:lat 25.775083541870117 :lng -80.1947021484375}
    :main-location "miami"
    :distance {:name-main 1865.8631795571262
               :name-name nil
               :main-main 6.295965728349408
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1347199344638914560/jOL3tB-V.jpg"
    :name-location nil
    :name "55555555"
    :user-id "TODO"
    :screen-name "444444"
    :name-coords nil
    :main-coords {:lat 25.775083541870117 :lng -80.1947021484375}
    :main-location "miami"
    :distance {:name-main 1865.8631795571262
               :name-name nil
               :main-main 6.295965728349408
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1470797764699295748/-5ebrvAy.jpg"
    :main-coords nil
    :name-location nil
    :name "00000000"
    :user-id "TODO"
    :screen-name "555555"
    :main-location "est. 080121 "
    :name-coords nil
    :distance {:name-main nil
               :name-name nil
               :main-main nil
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1469718039155249156/vVJSxXMR.jpg"
    :name-location nil
    :name "66666666"
    :user-id "TODO"
    :screen-name "666666"
    :name-coords nil
    :main-coords {:lat 25.775083541870117 :lng -80.1947021484375}
    :main-location "miami"
    :distance {:name-main 1865.8631795571262
               :name-name nil
               :main-main 6.295965728349408
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/568912568/nb.twitter.png"
    :main-coords nil
    :name-location nil
    :name "77777777"
    :user-id "TODO"
    :screen-name "777777"
    :main-location "272 Capp Street, SF, CA"
    :name-coords nil
    :distance {:name-main nil
               :name-name nil
               :main-main nil
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1458804832551247877/z8CMVDb8.jpg"
    :main-coords nil
    :name-location nil
    :name "88888888"
    :user-id "TODO"
    :screen-name "888888"
    :main-location "Discord + IRL"
    :name-coords nil
    :distance {:name-main nil
               :name-name nil
               :main-main nil
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1454845040510050313/XOYab7Jp.jpg"
    :main-coords nil
    :name-location nil
    :name "99999999"
    :user-id "TODO"
    :screen-name "999999"
    :main-location "Aventura, FL"
    :name-coords nil
    :distance {:name-main nil
               :name-name nil
               :main-main nil
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1476593718090248196/O_o_mTWV.jpg"
    :main-coords nil
    :name-location nil
    :name "00000000"
    :user-id "TODO"
    :screen-name "101010101010"
    :main-location "watchfaces.eth"
    :name-coords nil
    :distance {:name-main nil
               :name-name nil
               :main-main nil
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1462484610315554817/XkSguVNh.jpg"
    :main-coords {:lat 37.78007888793945 :lng -122.42015838623047}
    :name-location nil
    :name "11111111"
    :user-id "TODO"
    :screen-name "111111111111"
    :main-location "San Francisco, CA"
    :name-coords nil
    :distance {:name-main 5354.115213311319
               :name-name nil
               :main-main 4177.8862233830505
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1473938218169081859/XTpgZAAR.jpg"
    :name-location nil
    :name "22222222"
    :user-id "TODO"
    :screen-name "121212121212"
    :name-coords nil
    :main-coords {:lat 25.775083541870117 :lng -80.1947021484375}
    :main-location "miami"
    :distance {:name-main 1865.8631795571262
               :name-name nil
               :main-main 6.295965728349408
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1467402905510723586/8cIcESev.jpg"
    :name-location nil
    :name "33333333"
    :user-id "TODO"
    :screen-name "131313131313"
    :name-coords nil
    :main-coords {:lat 25.775083541870117 :lng -80.1947021484375}
    :main-location "miami"
    :distance {:name-main 1865.8631795571262
               :name-name nil
               :main-main 6.295965728349408
               :main-name nil}}

   ; miami
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1470384861290635270/cP_An-Xi.jpg"
    :name-location nil
    :name "444444441"
    :user-id "TODO"
    :screen-name "141414141414"
    :name-coords nil
    :main-coords {:lat 25.775083541870117 :lng -80.1947021484375}
    :main-location "miami"
    :distance {:name-main 1865.8631795571262
               :name-name nil
               :main-main 6.295965728349408
               :main-name nil}}

    ; others
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1463619836223778817/eqq7i0O-.jpg"
    :main-coords nil
    :name-location nil
    :name "55555555"
    :user-id "TODO"
    :screen-name "151515151515"
    :main-location "SF / LA / NY"
    :name-coords nil
    :distance {:name-main nil
               :name-name nil
               :main-main nil
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1380666942302744577/9cqbM5hL.jpg"
    :main-coords nil
    :name-location nil
    :name "66666666"
    :user-id "TODO"
    :screen-name "161616161616"
    :main-location "paradave.eth"
    :name-coords nil
    :distance {:name-main nil
               :name-name nil
               :main-main nil
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1458052553225105409/ZzT_JnAG.jpg"
    :main-coords {:lat 37.78007888793945 :lng -122.42015838623047}
    :name-location nil
    :name "77777777"
    :user-id "TODO"
    :screen-name "171717171717"
    :main-location "San Francisco, CA"
    :name-coords nil
    :distance {:name-main 5354.115213311319
               :name-name nil
               :main-main 4177.8862233830505
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1406056719633981441/x2S4m8ya.jpg"
    :main-coords {:lat 37.75 :lng -122.28333282470703}
    :name-location nil
    :name "88888888"
    :user-id "TODO"
    :screen-name "181818181818"
    :main-location "Bay Area, CA"
    :name-coords nil
    :distance {:name-main 5341.803788364867
               :name-name nil
               :main-main 4165.558575892986
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1445209235483250692/nbUOruMW.jpg"
    :name-location nil
    :name "99999999"
    :user-id "TODO"
    :screen-name "191919191919"
    :name-coords nil
    :main-coords {:lat 25.775083541870117 :lng -80.1947021484375}
    :main-location "miami"
    :distance {:name-main 1865.8631795571262
               :name-name nil
               :main-main 6.295965728349408
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1477799826590994435/VFz0t4G-.jpg"
    :name-location nil
    :name "200000000"
    :user-id "TODO"
    :screen-name "202020202020"
    :name-coords nil
    :main-coords {:lat 25.775083541870117 :lng -80.1947021484375}
    :main-location "miami"
    :distance {:name-main 1865.8631795571262
               :name-name nil
               :main-main 6.295965728349408
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1425546209679138820/lkoAz-xF.png"
    :main-coords nil
    :name-location nil
    :name "211111111"
    :user-id "TODO"
    :screen-name "212121212121"
    :main-location "oakland & the internet"
    :name-coords nil
    :distance {:name-main nil
               :name-name nil
               :main-main nil
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1457737146383613952/koFsGI91.jpg"
    :main-coords nil
    :name-location nil
    :name "222222222"
    :user-id "TODO"
    :screen-name "222222222222"
    :main-location "San Juan, PR"
    :name-coords nil
    :distance {:name-main nil
               :name-name nil
               :main-main nil
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/816408696687239168/StdRWHF8.jpg"
    :main-coords {:lat 31.463794708251953 :lng -99.3332748413086}
    :name-location nil
    :name "233333333"
    :user-id "TODO"
    :screen-name "232323232323"
    :main-location "Texas"
    :name-coords nil
    :distance {:name-main 3231.131067356933
               :name-name nil
               :main-main 1975.9794859754986
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1222724334843768839/hBxA_FjR.jpg"
    :main-coords nil
    :name-location nil
    :name "244444444"
    :user-id "TODO"
    :screen-name "242424242424"
    :main-location "Washington, D.C."
    :name-coords nil
    :distance {:name-main nil
               :name-name nil
               :main-main nil
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1365150653849378819/uX13vT49.jpg"
    :main-coords {:lat 37.75 :lng -122.28333282470703}
    :name-location nil
    :name "255555555"
    :user-id "TODO"
    :screen-name "252525252525"
    :main-location "Bay Area, CA"
    :name-coords nil
    :distance {:name-main 5341.803788364867
               :name-name nil
               :main-main 4165.558575892986
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1082093593840349184/P2B7Qiyn.jpg"
    :main-coords nil
    :name-location nil
    :name "266666666"
    :user-id "TODO"
    :screen-name "262626262626"
    :main-location "🇨🇦"
    :name-coords nil
    :distance {:name-main nil
               :name-name nil
               :main-main nil
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1375497226093535234/H_E5gOzG.jpg"
    :main-coords {:lat nil :lng nil}
    :name-location nil
    :name "277777777"
    :user-id "TODO"
    :screen-name "272727272727"
    :main-location "Multiverse"
    :name-coords nil
    :distance {:name-main nil
               :name-name nil
               :main-main nil
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1313562155803971584/0u10P3f6.jpg"
    :main-coords {:lat 39.50355529785156 :lng -99.01830291748047}
    :name-location nil
    :name "288888888"
    :user-id "TODO"
    :screen-name "282828282828"
    :main-location "United states"
    :name-coords nil
    :distance {:name-main 3907.8029027101725
               :name-name nil
               :main-main 2328.2337558809722
               :main-name nil}}
   {:profile_image_url_large "https://pbs.twimg.com/profile_images/1476275357753958400/oFjWNNTt.jpg"
    :name-location nil
    :name "299999999"
    :user-id "TODO"
    :screen-name "292929292929"
    :name-coords nil
    :main-coords {:lat 25.775083541870117 :lng -80.1947021484375}
    :main-location "miami"
    :distance {:name-main 1865.8631795571262
               :name-name nil
               :main-main 6.295965728349408
               :main-name nil}}])