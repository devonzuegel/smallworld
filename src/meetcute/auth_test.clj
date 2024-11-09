(ns meetcute.auth-test
  (:require [clojure.test :refer :all]
            [meetcute.auth :as auth])
  (:import (java.util Date)))

(deftest auth-sessions
  (testing "successful authentication with correct code"
    (let [email "test@test.com"
          code (auth/random-code)
          initial-auth-sessions (auth/add-new-code {} email code)
          approved-auth-sessions (auth/new-attempt initial-auth-sessions email code)]
      (is (nil? (get-in initial-auth-sessions [email :success?])))
      (is (true? (get-in approved-auth-sessions [email :success?])))))

  (testing "failed authentication with incorrect code"
    (let [email "test@test.com"
          correct-code (auth/random-code)
          wrong-code "000000"
          initial-auth-sessions (auth/add-new-code {} email correct-code)
          failed-auth-sessions (auth/new-attempt initial-auth-sessions email wrong-code)]
      (is (= :error (get-in failed-auth-sessions [email :attempts 0 :result])))
      (is (false? (get-in failed-auth-sessions [email :success?])))))

  (testing "rate limiting - exceeding maximum attempts per hour"
    (let [email "test@test.com"
          code (auth/random-code)
          initial-auth-sessions (auth/add-new-code {} email code)
          ; Create 6 failed attempts
          attempts-sessions (reduce
                             (fn [sessions _]
                               (auth/new-attempt sessions email "wrong-code"))
                             initial-auth-sessions
                             (range 6))
          ; Try one more time with correct code
          final-attempt (auth/new-attempt attempts-sessions email code)]
      (is (= 6 (count (get-in attempts-sessions [email :attempts]))))
      (is (= :error (get-in final-attempt [email :attempts 6 :result])))
      (is (false? (get-in final-attempt [email :success?])))))

  (testing "nil code attempt"
    (let [email "test@test.com"
          code (auth/random-code)
          initial-auth-sessions (auth/add-new-code {} email code)
          failed-auth-sessions (auth/new-attempt initial-auth-sessions email nil)]
      (is (= :error (get-in failed-auth-sessions [email :attempts 0 :result])))
      (is (false? (get-in failed-auth-sessions [email :success?])))))

  (testing "multiple sessions for different emails"
    (let [email1 "test1@test.com"
          email2 "test2@test.com"
          code1 (auth/random-code)
          code2 (auth/random-code)
          sessions (-> {}
                       (auth/add-new-code email1 code1)
                       (auth/add-new-code email2 code2)
                       (auth/new-attempt email1 code1))]
      (is (true? (get-in sessions [email1 :success?])))
      (is (nil? (get-in sessions [email2 :success?])))))

  (testing "updating existing session with new code"
    (let [email "test@test.com"
          code1 (auth/random-code)
          code2 (auth/random-code)
          initial-sessions (auth/add-new-code {} email code1)
          updated-sessions (auth/add-new-code initial-sessions email code2)]
      (is (= code2 (get-in updated-sessions [email :code])))
      (is (= (get-in initial-sessions [email :started_at])
             (get-in updated-sessions [email :started_at])))))

  (testing "attempts tracking and last attempt retrieval"
    (let [email "test@test.com"
          code (auth/random-code)
          sessions (-> {}
                       (auth/add-new-code email code)
                       (auth/new-attempt email "wrong1")
                       (auth/new-attempt email "wrong2")
                       (auth/new-attempt email code))
          attempts (get-in sessions [email :attempts])
          last-try (auth/last-attempt attempts)]
      (is (= 3 (count attempts)))
      (is (= code (:code last-try)))
      (is (= :success (:result last-try)))))

  (testing "time-based attempt filtering"
    (let [email "test@test.com"
          code (auth/random-code)
          old-date #inst "2023-01-01"
          recent-date (auth/now)
          attempts [{:time old-date :code "wrong" :result :error}
                    {:time recent-date :code "wrong" :result :error}]]
      (is (= 1 (->> attempts
                    (map :time)
                    (filter (partial auth/in-last-hour? recent-date))
                    count)))))

  (testing "reset functionality"
    (let [email "test@test.com"
          code (auth/random-code)]
      (swap! auth/auth-sessions-state auth/add-new-code email code)
      (auth/reset-sms-sessions!)
      (is (empty? @auth/auth-sessions-state)))))