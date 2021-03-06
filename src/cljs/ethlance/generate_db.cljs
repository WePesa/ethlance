(ns ethlance.generate-db
  (:require [re-frame.core :refer [reg-event-db reg-event-fx trim-v after reg-fx console dispatch]]
            [ethlance.utils :as u]
            [cljs-web3.eth :as web3-eth]
            [clojure.data :as data]
            [clojure.string :as string]
            [ethlance.constants :as constants]
            [cljs-web3.core :as web3]))

(defn rand-id [n]
  (inc (rand-int n)))

(defn rand-uint-coll [max-n max-int]
  (repeatedly (rand-id max-n) #(rand-id max-int)))

(defn rand-text [max-chars]
  (let [c (inc (rand-int (/ max-chars (inc (rand-int 8)))))]
    (u/truncate (string/join " " (repeatedly c #(u/rand-str (inc (rand-int 12))))) max-chars "")))

(defn get-instance [key]
  (get-in @re-frame.db/app-db [:eth/contracts key :instance]))

(defn gen-freelancer []
  {:user/name (rand-text 40)
   :user/gravatar "a"
   :user/country (rand-id (count constants/countries))
   :user/languages (set (rand-uint-coll 6 (count constants/languages)))
   :freelancer/available? true
   :freelancer/job-title (rand-text 40)
   :freelancer/hourly-rate (web3/to-wei (rand-int 100) :ether)
   :freelancer/categories (set (rand-uint-coll 6 (count constants/categories)))
   :freelancer/skills (set (rand-uint-coll 9 29))
   :freelancer/description (rand-text 300)})

(def freelancer1
  (merge (gen-freelancer)
         {:user/name "Matúš Lešťan"
          :user/gravatar "bfdb252fe9d0ab9759f41e3c26d7700e"
          :freelancer/job-title "Clojure(script), Ethereum developer"}))

(defn gen-job []
  {:job/title (rand-text 90)
   :job/description (rand-text 300)
   :job/skills (set (rand-uint-coll 6 29))
   :job/budget (web3/to-wei (rand-int 100) :ether)
   :job/language (rand-id (count constants/languages))
   :job/category (rand-id (count constants/categories))
   :job/payment-type (rand-id (count constants/payment-types))
   :job/experience-level (rand-id (count constants/experience-levels))
   :job/estimated-duration (rand-id (count constants/estimated-durations))
   :job/hours-per-week (rand-id (count constants/hours-per-weeks))
   :job/freelancers-needed (inc (rand-int 10))})

(def employer1
  {:user/name "SomeCorp."
   :user/gravatar "bfdb252fe9d0ab9759f41e3c26d7700f"
   :user/country 21
   :user/languages [1]
   :employer/description "hahaha"})

(def job1
  {:job/title "Ionic/AngularJs Mobile App Developer"
   :job/description "We are porting our jQueryMobile-based app to ionic, and have another ionic app, but have periodic development needs.\n\nPlease read these and respond in your application.\n\nYou MUST be available during US hours using Google Chat for video-conferencing and screen sharing.\n\nYou MUST be the person doing the work, not subcontracting it out to someone else.\n\nYou need to  use the upwork timer."
   :job/skills [2 3 4]
   :job/budget 10
   :job/language 1
   :job/category 1
   :job/payment-type 1
   :job/experience-level 1
   :job/estimated-duration 1
   :job/hours-per-week 1
   :job/freelancers-needed 2})

(def job2
  {:job/title "Bitcoin and Blockchain content writer for 800 - 1000 word article"
   :job/description "New Blockchain startup is looking for content writers who understand the blockchain, bitcoin and cryptocurrency space.\n\nWe are looking for a mix of Technical but made simple, but delivered in a way anyone can understand. Our audience are investors, and entrepreneurs \n\n\nPlease provide us with previous work \n\nTopics to write about\n\nBlockchain, crypto, how to,  ethereum, \n\netc..."
   :job/skills [5 6 7]
   :job/budget 10
   :job/language 2
   :job/category 3
   :job/payment-type 2
   :job/experience-level 2
   :job/estimated-duration 2
   :job/hours-per-week 2
   :job/freelancers-needed 1})

(def invitation1
  {:contract/job 1
   :contract/freelancer 1
   :invitation/description (rand-text 100)})

(defn gen-invitation [& [job-id]]
  {:contract/job (or job-id (rand-id 10))
   :contract/freelancer (rand-id 10)
   :invitation/description (rand-text 200)})

(def proposal1
  {:contract/job 1
   :proposal/rate (rand-int 200)
   :proposal/description (rand-text 100)})

(defn gen-proposal [& [job-id]]
  {:contract/job (or job-id (rand-id 10))
   :proposal/rate (web3/to-wei (rand-int 100) :ether)
   :proposal/description (rand-text 200)})

(defn gen-contract [& [contract-id]]
  {:contract/id (or contract-id (rand-id 10))
   :contract/description (rand-text 200)
   :contract/hiring-done? false})

(defn gen-invoice [& [contract-id]]
  {:invoice/contract (or contract-id (rand-id 10))
   :invoice/description (rand-text 100)
   :invoice/amount (web3/to-wei (rand-int 10) :ether)
   :invoice/worked-hours (rand-int 200)
   :invoice/worked-from 1480407621
   :invoice/worked-to 1480407621})

(def feedback1
  {:contract/id 1
   :contract/feedback (rand-text 200)
   :contract/feedback-rating (rand-int 100)})

(def feedback2
  {:contract/id 1
   :contract/feedback (rand-text 200)
   :contract/feedback-rating (rand-int 100)})

(defn get-address [n]
  (nth (:my-addresses @re-frame.db/app-db) n))

(def skills1
  {:skill/names (set (repeatedly 30 #(rand-text 20)))})

(reg-event-fx
  :generate-db
  [trim-v]
  (fn [{:keys [db]}]
    #_{:dispatch [:contract.config/add-skills skills1 (get-address 0)]}
    {:dispatch-n [[:contract.user/register-freelancer freelancer1 (get-address 0)]]
     :dispatch-later (concat
                       [{:ms 10 :dispatch [:contract.user/register-employer employer1 (get-address 1)]}]
                       [{:ms 10 :dispatch [:contract.config/add-skills skills1 (get-address 0)]}]
                       (map #(hash-map :ms 25 :dispatch [:contract.job/add-job (gen-job) (get-address 1)]) (range 10))
                       [{:ms 20 :dispatch [:contract.contract/add-job-invitation invitation1 (get-address 1)]}
                        {:ms 30 :dispatch [:contract.contract/add-job-proposal proposal1 (get-address 0)]}
                        {:ms 40 :dispatch [:contract.contract/add-contract (gen-contract 1) (get-address 1)]}]
                       (map #(hash-map :ms 50 :dispatch [:contract.user/register-freelancer (gen-freelancer) (get-address %)]) (range 2 9))
                       (map #(hash-map :ms 60 :dispatch [:contract.contract/add-job-invitation (gen-invitation 1) (get-address 1)]) (range 5))
                       (map #(hash-map :ms 70 :dispatch [:contract.contract/add-job-proposal (gen-proposal 1) (get-address %)]) (range 2 5))
                       ;{:ms 60 :dispatch [:contract.invoice/add (gen-invoice 1) (get-address 0)]}
                       ;{:ms 70 :dispatch [:contract.invoice/pay {:invoice/id 1} (:invoice/amount invoice1) (get-address 1)]}
                       ;{:ms 80 :dispatch [:contract.invoice/add (gen-invoice 1) (get-address 0)]}
                       (map #(hash-map :ms 80 :dispatch [:contract.invoice/add-invoice (gen-invoice 1) (get-address 0)]) (range 10))
                       [{:ms 90 :dispatch [:contract.invoice/cancel-invoice {:invoice/id 2} (get-address 0)]}
                        {:ms 100 :dispatch [:contract.contract/add-feedback feedback1 (get-address 0)]}
                        {:ms 110 :dispatch [:contract.contract/add-feedback feedback2 (get-address 1)]}]
                       )

     }))


(comment
  (dispatch [:contract.contract/add-job-invitation invitation1 (get-address 1)])

  (let [coll1 (set (rand-uint-coll 50 100))
        coll2 (set (rand-uint-coll 50 100))
        [added removed] (web3-eth/contract-call (get-instance :ethlance-user) :diff coll1 coll2)
        intersection (u/big-nums->nums (web3-eth/contract-call (get-instance :ethlance-user) :intersect coll1 coll2))
        added (u/big-nums->nums added)
        removed (u/big-nums->nums removed)
        correct-removed (sort (vec (clojure.set/difference coll1 coll2)))
        correct-added (sort (vec (clojure.set/difference coll2 coll1)))
        corrent-intersection (sort (vec (clojure.set/intersection coll1 coll2)))]
    (.clear js/console)
    ;(print.foo/look coll1)
    ;(print.foo/look coll2)
    ;(print.foo/look correct-removed)
    ;(print.foo/look correct-added)
    [(= added (vec correct-added)) (= removed correct-removed)
     (= intersection corrent-intersection)]))