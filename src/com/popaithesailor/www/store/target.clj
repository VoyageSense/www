(ns com.popaithesailor.www.store.target)

(def configs
  (let [dummy {:boats     {:sun-odyssey-410 "Jeanneau Sun Odyssey 410"
                           :oceanis-42.3    "Beneteau Oceanis 42.3"
                           :dufour-41       "Dufour 41"
                           :dufour-44       "Dufour 44"
                           :oceanis-46.1    "Beneteau Oceanis 46.1"}
               :locations {"Carribean"
                           {:usvi-bvi        "Virgin Islands (British and United States)"
                            :leeward-islands "Leeward Islands"
                            :turks-caicos    "Turks and Caicos Islands"}
                           "South Pacific"
                           {:tahiti          "Tahiti"}}
               :price     300}
        mediterranean {:boats     {:sun-odyssey-319 "Jeanneau Sun Odyssey 319"
                                   :sun-odyssey-410 "Jeanneau Sun Odyssey 410"
                                   :sun-odyssey-419 "Jeanneau Sun Odyssey 419"
                                   :sun-odyssey-519 "Jeanneau Sun Odyssey 519"
                                   :bavaria-41      "Bavaria 41"
                                   :bavaria-46      "Bavaria 46"
                                   :bavaria-50      "Bavaria 50"
                                   :bavaria-50c     "Bavaria 50C"
                                   :bavaria-50s     "Bavaria 50S"
                                   :bavaria-50h     "Bavaria 50H"
                                   :bavaria-56      "Bavaria 56"
                                   :bavaria-57      "Bavaria 57"
                                   :oceanis-30.1    "Beneteau Oceanis 30.1"
                                   :oceanis-34.1    "Beneteau Oceanis 34.1"
                                   :oceanis-38.1    "Beneteau Oceanis 38.1"
                                   :oceanis-42.3    "Beneteau Oceanis 42.3"
                                   :oceanis-46.1    "Beneteau Oceanis 46.1"
                                   :oceanis-51.1    "Beneteau Oceanis 51.1"
                                   :oceanis-55.1    "Beneteau Oceanis 55.1"
                                   :oceanis-62.1    "Beneteau Oceanis 62.1"
                                   :dufour-32       "Dufour 32"
                                   :dufour-36       "Dufour 36"
                                   :dufour-40       "Dufour 40"
                                   :dufour-41       "Dufour 41"
                                   :dufour-44       "Dufour 44"
                                   :dufour-45       "Dufour 45"
                                   :dufour-48       "Dufour 48"}
                       :locations {"Carribean"
                                   {:usvi-bvi        "Virgin Islands (British and United States)"
                                    :leeward-islands "Leeward Islands"
                                    :turks-caicos    "Turks and Caicos Islands"}
                                   "Mediterranean"
                                   {:croatia         "Croatia"
                                    :france          "France"
                                    :greece          "Greece"
                                    :italy           "Italy"
                                    :spain           "Spain"
                                    :turkey          "Turkey"}}
                       :price     200}
        carribean {:boats     {:sun-odyssey-410 "Jeanneau Sun Odyssey 410"
                               :sun-odyssey-419 "Jeanneau Sun Odyssey 419"
                               :sun-odyssey-519 "Jeanneau Sun Odyssey 519"
                               :bavaria-41      "Bavaria 41"
                               :bavaria-46      "Bavaria 46"
                               :bavaria-50      "Bavaria 50"
                               :bavaria-56      "Bavaria 56"
                               :bavaria-57      "Bavaria 57"
                               :oceanis-42.3    "Beneteau Oceanis 42.3"
                               :oceanis-46.1    "Beneteau Oceanis 46.1"
                               :oceanis-51.1    "Beneteau Oceanis 51.1"
                               :oceanis-55.1    "Beneteau Oceanis 55.1"
                               :oceanis-62.1    "Beneteau Oceanis 62.1"
                               :dufour-40       "Dufour 40"
                               :dufour-44       "Dufour 44"
                               :dufour-45       "Dufour 45"
                               :dufour-48       "Dufour 48"
                               :sunsail-424     "Sunsail Lagoon 424 Classic"
                               :sunsail-454     "Sunsail 454 Classic"
                               :sunsail-45      "Sunsail 454L"
                               :sunsail-454W    "Sunsail 454W"
                               :sunsail-465     "Sunsail 465"
                               :lagoon-42       "Lagoon 42"
                               :lagoon-45       "Lagoon 450"
                               :lagoon-50       "Lagoon 50"
                               :lagoon-51       "Lagoon 51"
                               :lagoon-52       "Lagoon 52"
                               :lagoon-55       "Lagoon 55"}
                   :locations {"Carribean"
                               {:bahamas         "Bahamas"
                                :exumas          "Exuma"
                                :st-martin-anguilla-st-barth "St Martin/Anguilla/St Barthélemy"
                                :turks-caicos    "Turks and Caicos Islands"
                                :usvi-bvi        "Virgin Islands (British and United States)"}
                               "Mediterranean"
                               {:croatia         "Croatia"}}
                   :price     200}
        italy {:boats     {:sun-odyssey-410 "Jeanneau Sun Odyssey 410"
                           :sun-odyssey-419 "Jeanneau Sun Odyssey 419"
                           :sun-odyssey-519 "Jeanneau Sun Odyssey 519"
                           :bavaria-41      "Bavaria 41"
                           :bavaria-46      "Bavaria 46"
                           :bavaria-50      "Bavaria 50"
                           :bavaria-56      "Bavaria 56"
                           :bavaria-57      "Bavaria 57"
                           :oceanis-42.3    "Beneteau Oceanis 42.3"
                           :oceanis-46.1    "Beneteau Oceanis 46.1"
                           :oceanis-51.1    "Beneteau Oceanis 51.1"
                           :oceanis-55.1    "Beneteau Oceanis 55.1"
                           :oceanis-62.1    "Beneteau Oceanis 62.1"
                           :dufour-41       "Dufour 41"
                           :dufour-44       "Dufour 44"
                           :dufour-45       "Dufour 45"
                           :dufour-48       "Dufour 48"
                           :fp-aura-51      "Fountaine Pajot Aura 51"
                           :fp-saba-50      "Fountaine Pajot Saba 50"
                           :fp-saona-47     "Fountaine Pajot Saona 47"
                           :bali-4.5        "Bali 4.5"
                           :bali-4.6        "Bali 4.6"
                           :bali-4.8        "Bali 4.8"
                           :lagoon-42       "Lagoon 42"
                           :lagoon-45       "Lagoon 450"
                           :lagoon-51       "Lagoon 51"
                           :lagoon-52       "Lagoon 52"
                           :lagoon-55       "Lagoon 55"}
               :locations {"Carribean"
                           {:bahamas         "Bahamas"
                            :usvi-bvi        "Virgin Islands (British and United States)"}
                           "Mediterranean"
                           {:croatia         "Croatia"
                            :amalfi          "Amalfi Coast & Gulf of Naples"
                            :sardinia        "Sardinia, Italy"
                            :sicily          "Sicily, Italy"
                            :malta           "Malta"}}
               :price     200}]
    {:watermellon dummy
     ;; :mtklk dummy
     ;; :ulrbt dummy
     ;; :pwhom dummy
     ;; :ocbto dummy
     ;; :aocbq dummy
     ;; :bsfqn dummy
     ;; :ysquo dummy
     ;; :lhztx dummy
     ;; :xqccm dummy
     ;; :plpgm dummy
     :mango {}
     ;; :1ayjd {}
     ;; :1vhji {}
     ;; :1nzxl {}
     ;; :1rxah {}
     ;; :1ojpw {}
     ;; :1uvac {}
     ;; :1xppc {}
     ;; :1hhey {}
     ;; :1ruxy {}
     ;; :1hxpd {}
     ;; :1djii {}
     ;; :1pdqs {}
     ;; :1jxop {}
     ;; :1ovwc {}
     ;; :1eblo {}
     ;; :1psbx {}
     ;; :1bgpt {}
     ;; :1xjjv {}
     ;; :1kksd {}
     ;; :1nbnj {}
     ;; :1mkrj {}
     ;; :1khbf {}
     ;; :1fnji {}
     ;; :1jamh {}
     ;; :1smob {}
     ;; :1pajn {}
     ;; :1dqhp {}
     ;; :1kcnz {}
     ;; :1hqqo {}
     ;; :1fovf {}
     ;; :1rvxb {}
     ;; :1lpzj {}
     ;; :1urcs {}
     ;; :1gupz {}
     ;; :1terr {}
     ;; :1ddor {}
     ;; :1bnps {}
     ;; :1rsrs {}
     ;; :1sobx {}
     ;; :1pflq {}
     ;; :1ajyk {}
     ;; :1zevq {}
     ;; :1eyey {}
     ;; :1hlis {}
     ;; :1afqs {}
     ;; :1emdb {}
     ;; :1bncy {}
     ;; :1yhvz {}
     ;; :1donk {}
     ;; :1wcnc {}
     ;; :1beyc {}
     ;; :1knjj {}
     ;; :1wlda {}
     ;; :1boie {}
     ;; :1wzir {}
     ;; :1utcg {}
     ;; :1zexj {}
     ;; :1kfny {}
     ;; :1yypb {}
     ;; :1jrto {}
     ;; :1hwza {}
     ;; :1yunx {}
     ;; :1gglp {}
     ;; :1jnog {}
     ;; :1dopu {}
     ;; :1smaj {}
     ;; :1seja {}
     ;; :1xzuy {}
     ;; :1rdyf {}
     ;; :1obwb {}
     ;; :1rpsd {}
     ;; :1uokj {}
     ;; :1brka {}
     ;; :1wmef {}
     ;; :1cddg {}
     ;; :1rzsg {}
     ;; :1zxsc {}
     ;; :1sphl {}
     ;; :1lvql {}
     ;; :1mcpd {}
     ;; :1amcw {}
     ;; :1dnvv {}
     ;; :1gmvw {}
     ;; :1tpfp {}
     ;; :1cfjg {}
     ;; :1fcml {}
     ;; :1envx {}
     ;; :1xlel {}
     ;; :1rjza {}
     ;; :1rbrw {}
     ;; :1ixeo {}
     ;; :1fqap {}
     ;; :1aixh {}
     ;; :1drkr {}
     ;; :1ofoh {}
     ;; :1uscz {}
     ;; :1slxl {}
     ;; :1dijp {}
     ;; :1bdvq {}
     ;; :1mvyh {}
     ;; :1jbch {}
     ;; :1wdti {}
     ;; :1qaag {}
     ;; :1ttxc {}
     ;; :1wpai {}
     ;; :1ohvg {}
     ;; :1wabw {}
     ;; :1jatp {}
     ;; :1cxmm {}
     ;; :1kbsu {}
     :2dlhi {}
     :2brip {}
     :2zuta mediterranean
     :2mnnt {}
     :2bjii mediterranean
     :2pugv carribean
     :2pjem {}
     :2ftzv italy
     :2rwdf {}
     :2vuvo {}
     :2xknh {}
     :2vznh {}
     :2afsc {}
     :2xmcs {}
     :2pxlv {}
     :2tvsf {}
     :2oxqc {}
     :2zojo {}
     :2qskj {}
     :2evie {}}))
