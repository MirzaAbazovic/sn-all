--
-- Ueber dieses SQL-Script werden die Benutzer fuer die Hurrican-DB angelegt.
--

-- User: hurrican-reader
-- Host: 10.1.%, 10.2.%, 10.3.%
GRANT USAGE ON * . * TO "hurrican-reader"@"10.1.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `hurrican` . * TO "hurrican-reader"@"10.1.%";
GRANT USAGE ON * . * TO "hurrican-reader"@"10.4.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `hurrican` . * TO "hurrican-reader"@"10.4.%";
GRANT USAGE ON * . * TO "hurrican-reader"@"10.5.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `hurrican` . * TO "hurrican-reader"@"10.5.%";
GRANT USAGE ON * . * TO "hurrican-reader"@"192.168.250.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `hurrican` . * TO "hurrican-reader"@"192.168.250.%";
GRANT USAGE ON * . * TO "hurrican-reader"@"192.168.251.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `hurrican` . * TO "hurrican-reader"@"192.168.251.%";
GRANT USAGE ON * . * TO "hurrican-reader"@"10.203.1.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `hurrican` . * TO "hurrican-reader"@"10.203.1.%";
GRANT USAGE ON * . * TO "hurrican-reader"@"172.22.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `hurrican` . * TO "hurrican-reader"@"172.22.%";
GRANT USAGE ON * . * TO "hurrican-reader"@"172.16.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `hurrican` . * TO "hurrican-reader"@"172.16.%";
-- Host: 10.10.%, 10.20.%, 10.30.%
GRANT USAGE ON * . * TO "hurrican-reader"@"10.10.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `hurrican` . * TO "hurrican-reader"@"10.10.%";
GRANT USAGE ON * . * TO "hurrican-reader"@"10.20.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `hurrican` . * TO "hurrican-reader"@"10.20.%";
GRANT USAGE ON * . * TO "hurrican-reader"@"10.50.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `hurrican` . * TO "hurrican-reader"@"10.50.%";

-- User: hurrican-writer
-- Host: 10.1.%, 10.4.%, 10.3.%
GRANT USAGE ON * . * TO "hurrican-writer"@"10.1.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE ON `hurrican`.* TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_uevt_2_ziel` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_schnittstelle` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_ba_verl_aend_prod_2_gruppe` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_physiktyp` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_sperre_verteilung` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_tdn` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_verlauf_abteilung` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_kubena_hvt` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_kubena_prod` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_kubena_tdn` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_hvt_standort_2_technik` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_COMMAND_MAPPING` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_CHAIN` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_prod_2_prod` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_prod_2_eg` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_report_2_prod` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_report_2_txt_baustein` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_rep2prod_stati` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_report_txt_bausteine` TO "hurrican-writer"@"10.1.%";
GRANT DELETE ON `hurrican`.`t_report_data` TO "hurrican-writer"@"10.1.%";
GRANT USAGE ON * . * TO "hurrican-writer"@"10.4.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE ON `hurrican`.* TO "hurrican-writer"@"10.4.%";
GRANT DELETE ON `hurrican`.`t_uevt_2_ziel` TO "hurrican-writer"@"10.4.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_schnittstelle` TO "hurrican-writer"@"10.4.%";
GRANT DELETE ON `hurrican`.`t_ba_verl_aend_prod_2_gruppe` TO "hurrican-writer"@"10.4.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_physiktyp` TO "hurrican-writer"@"10.4.%";
GRANT DELETE ON `hurrican`.`t_sperre_verteilung` TO "hurrican-writer"@"10.4.%";
GRANT DELETE ON `hurrican`.`t_tdn` TO "hurrican-writer"@"10.4.%";
GRANT DELETE ON `hurrican`.`t_verlauf_abteilung` TO "hurrican-writer"@"10.4.%";
GRANT DELETE ON `hurrican`.`t_kubena_hvt` TO "hurrican-writer"@"10.4.%";
GRANT DELETE ON `hurrican`.`t_kubena_prod` TO "hurrican-writer"@"10.4.%";
GRANT DELETE ON `hurrican`.`t_kubena_tdn` TO "hurrican-writer"@"10.4.%";
GRANT DELETE ON `hurrican`.`t_hvt_standort_2_technik` TO "hurrican-writer"@"10.4.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_COMMAND_MAPPING` TO "hurrican-writer"@"10.4.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_CHAIN` TO "hurrican-writer"@"10.4.%";
GRANT DELETE ON `hurrican`.`t_prod_2_prod` TO "hurrican-writer"@"10.4.%";
GRANT DELETE ON `hurrican`.`t_prod_2_eg` TO "hurrican-writer"@"10.4.%";
GRANT USAGE ON * . * TO "hurrican-writer"@"10.5.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE ON `hurrican`.* TO "hurrican-writer"@"10.5.%";
GRANT DELETE ON `hurrican`.`t_uevt_2_ziel` TO "hurrican-writer"@"10.5.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_schnittstelle` TO "hurrican-writer"@"10.5.%";
GRANT DELETE ON `hurrican`.`t_ba_verl_aend_prod_2_gruppe` TO "hurrican-writer"@"10.5.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_physiktyp` TO "hurrican-writer"@"10.5.%";
GRANT DELETE ON `hurrican`.`t_sperre_verteilung` TO "hurrican-writer"@"10.5.%";
GRANT DELETE ON `hurrican`.`t_tdn` TO "hurrican-writer"@"10.5.%";
GRANT DELETE ON `hurrican`.`t_verlauf_abteilung` TO "hurrican-writer"@"10.5.%";
GRANT DELETE ON `hurrican`.`t_kubena_hvt` TO "hurrican-writer"@"10.5.%";
GRANT DELETE ON `hurrican`.`t_kubena_prod` TO "hurrican-writer"@"10.5.%";
GRANT DELETE ON `hurrican`.`t_kubena_tdn` TO "hurrican-writer"@"10.5.%";
GRANT DELETE ON `hurrican`.`t_hvt_standort_2_technik` TO "hurrican-writer"@"10.5.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_COMMAND_MAPPING` TO "hurrican-writer"@"10.5.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_CHAIN` TO "hurrican-writer"@"10.5.%";
GRANT DELETE ON `hurrican`.`t_prod_2_prod` TO "hurrican-writer"@"10.5.%";
GRANT DELETE ON `hurrican`.`t_prod_2_eg` TO "hurrican-writer"@"10.5.%";
-- 192.168.250.% / 192.168.251.% / 172.22.% fuer Muenchen
GRANT USAGE ON * . * TO "hurrican-writer"@"192.168.250.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE ON `hurrican`.* TO "hurrican-writer"@"192.168.250.%";
GRANT DELETE ON `hurrican`.`t_uevt_2_ziel` TO "hurrican-writer"@"192.168.250.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_schnittstelle` TO "hurrican-writer"@"192.168.250.%";
GRANT DELETE ON `hurrican`.`t_ba_verl_aend_prod_2_gruppe` TO "hurrican-writer"@"192.168.250.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_physiktyp` TO "hurrican-writer"@"192.168.250.%";
GRANT DELETE ON `hurrican`.`t_sperre_verteilung` TO "hurrican-writer"@"192.168.250.%";
GRANT DELETE ON `hurrican`.`t_tdn` TO "hurrican-writer"@"192.168.250.%";
GRANT DELETE ON `hurrican`.`t_verlauf_abteilung` TO "hurrican-writer"@"192.168.250.%";
GRANT DELETE ON `hurrican`.`t_kubena_hvt` TO "hurrican-writer"@"192.168.250.%";
GRANT DELETE ON `hurrican`.`t_kubena_prod` TO "hurrican-writer"@"192.168.250.%";
GRANT DELETE ON `hurrican`.`t_kubena_tdn` TO "hurrican-writer"@"192.168.250.%";
GRANT DELETE ON `hurrican`.`t_hvt_standort_2_technik` TO "hurrican-writer"@"192.168.250.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_COMMAND_MAPPING` TO "hurrican-writer"@"192.168.250.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_CHAIN` TO "hurrican-writer"@"192.168.250.%";
GRANT DELETE ON `hurrican`.`t_prod_2_prod` TO "hurrican-writer"@"192.168.250.%";
GRANT USAGE ON * . * TO "hurrican-writer"@"192.168.251.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE ON `hurrican`.* TO "hurrican-writer"@"192.168.251.%";
GRANT DELETE ON `hurrican`.`t_uevt_2_ziel` TO "hurrican-writer"@"192.168.251.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_schnittstelle` TO "hurrican-writer"@"192.168.251.%";
GRANT DELETE ON `hurrican`.`t_ba_verl_aend_prod_2_gruppe` TO "hurrican-writer"@"192.168.251.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_physiktyp` TO "hurrican-writer"@"192.168.251.%";
GRANT DELETE ON `hurrican`.`t_sperre_verteilung` TO "hurrican-writer"@"192.168.251.%";
GRANT DELETE ON `hurrican`.`t_tdn` TO "hurrican-writer"@"192.168.251.%";
GRANT DELETE ON `hurrican`.`t_verlauf_abteilung` TO "hurrican-writer"@"192.168.251.%";
GRANT DELETE ON `hurrican`.`t_kubena_hvt` TO "hurrican-writer"@"192.168.251.%";
GRANT DELETE ON `hurrican`.`t_kubena_prod` TO "hurrican-writer"@"192.168.251.%";
GRANT DELETE ON `hurrican`.`t_kubena_tdn` TO "hurrican-writer"@"192.168.251.%";
GRANT DELETE ON `hurrican`.`t_hvt_standort_2_technik` TO "hurrican-writer"@"192.168.251.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_COMMAND_MAPPING` TO "hurrican-writer"@"192.168.251.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_CHAIN` TO "hurrican-writer"@"192.168.251.%";
GRANT DELETE ON `hurrican`.`t_prod_2_prod` TO "hurrican-writer"@"192.168.251.%";
GRANT USAGE ON * . * TO "hurrican-writer"@"172.22.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE ON `hurrican`.* TO "hurrican-writer"@"172.22.%";
GRANT DELETE ON `hurrican`.`t_uevt_2_ziel` TO "hurrican-writer"@"172.22.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_schnittstelle` TO "hurrican-writer"@"172.22.%";
GRANT DELETE ON `hurrican`.`t_ba_verl_aend_prod_2_gruppe` TO "hurrican-writer"@"172.22.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_physiktyp` TO "hurrican-writer"@"172.22.%";
GRANT DELETE ON `hurrican`.`t_sperre_verteilung` TO "hurrican-writer"@"172.22.%";
GRANT DELETE ON `hurrican`.`t_tdn` TO "hurrican-writer"@"172.22.%";
GRANT DELETE ON `hurrican`.`t_verlauf_abteilung` TO "hurrican-writer"@"172.22.%";
GRANT DELETE ON `hurrican`.`t_kubena_hvt` TO "hurrican-writer"@"172.22.%";
GRANT DELETE ON `hurrican`.`t_kubena_prod` TO "hurrican-writer"@"172.22.%";
GRANT DELETE ON `hurrican`.`t_kubena_tdn` TO "hurrican-writer"@"172.22.%";
GRANT DELETE ON `hurrican`.`t_hvt_standort_2_technik` TO "hurrican-writer"@"172.22.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_COMMAND_MAPPING` TO "hurrican-writer"@"172.22.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_CHAIN` TO "hurrican-writer"@"172.22.%";
GRANT DELETE ON `hurrican`.`t_prod_2_prod` TO "hurrican-writer"@"172.22.%";
-- 172.16.% fuer Shop
GRANT USAGE ON * . * TO "hurrican-writer"@"172.16.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE ON `hurrican`.* TO "hurrican-writer"@"172.16.%";
GRANT DELETE ON `hurrican`.`t_uevt_2_ziel` TO "hurrican-writer"@"172.16.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_schnittstelle` TO "hurrican-writer"@"172.16.%";
GRANT DELETE ON `hurrican`.`t_ba_verl_aend_prod_2_gruppe` TO "hurrican-writer"@"172.16.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_physiktyp` TO "hurrican-writer"@"172.16.%";
GRANT DELETE ON `hurrican`.`t_sperre_verteilung` TO "hurrican-writer"@"172.16.%";
GRANT DELETE ON `hurrican`.`t_tdn` TO "hurrican-writer"@"172.16.%";
GRANT DELETE ON `hurrican`.`t_verlauf_abteilung` TO "hurrican-writer"@"172.16.%";
GRANT DELETE ON `hurrican`.`t_kubena_hvt` TO "hurrican-writer"@"172.16.%";
GRANT DELETE ON `hurrican`.`t_kubena_prod` TO "hurrican-writer"@"172.16.%";
GRANT DELETE ON `hurrican`.`t_kubena_tdn` TO "hurrican-writer"@"172.16.%";
GRANT DELETE ON `hurrican`.`t_hvt_standort_2_technik` TO "hurrican-writer"@"172.16.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_COMMAND_MAPPING` TO "hurrican-writer"@"172.16.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_CHAIN` TO "hurrican-writer"@"172.16.%";
GRANT DELETE ON `hurrican`.`t_prod_2_prod` TO "hurrican-writer"@"172.16.%";
-- 10.203.1.% fuer Nuernberg
GRANT USAGE ON * . * TO "hurrican-writer"@"10.203.1.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE ON `hurrican`.* TO "hurrican-writer"@"10.203.1.%";
GRANT DELETE ON `hurrican`.`t_uevt_2_ziel` TO "hurrican-writer"@"10.203.1.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_schnittstelle` TO "hurrican-writer"@"10.203.1.%";
GRANT DELETE ON `hurrican`.`t_ba_verl_aend_prod_2_gruppe` TO "hurrican-writer"@"10.203.1.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_physiktyp` TO "hurrican-writer"@"10.203.1.%";
GRANT DELETE ON `hurrican`.`t_sperre_verteilung` TO "hurrican-writer"@"10.203.1.%";
GRANT DELETE ON `hurrican`.`t_tdn` TO "hurrican-writer"@"10.203.1.%";
GRANT DELETE ON `hurrican`.`t_verlauf_abteilung` TO "hurrican-writer"@"10.203.1.%";
GRANT DELETE ON `hurrican`.`t_kubena_hvt` TO "hurrican-writer"@"10.203.1.%";
GRANT DELETE ON `hurrican`.`t_kubena_prod` TO "hurrican-writer"@"10.203.1.%";
GRANT DELETE ON `hurrican`.`t_kubena_tdn` TO "hurrican-writer"@"10.203.1.%";
GRANT DELETE ON `hurrican`.`t_hvt_standort_2_technik` TO "hurrican-writer"@"10.203.1.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_COMMAND_MAPPING` TO "hurrican-writer"@"10.203.1.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_CHAIN` TO "hurrican-writer"@"10.203.1.%";
GRANT DELETE ON `hurrican`.`t_prod_2_prod` TO "hurrican-writer"@"10.203.1.%";
--10.207
GRANT USAGE ON * . * TO "hurrican-writer"@"10.207.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE ON `hurrican`.* TO "hurrican-writer"@"10.207.%";
GRANT DELETE ON `hurrican`.`t_uevt_2_ziel` TO "hurrican-writer"@"10.207.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_schnittstelle` TO "hurrican-writer"@"10.207.%";
GRANT DELETE ON `hurrican`.`t_ba_verl_aend_prod_2_gruppe` TO "hurrican-writer"@"10.207.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_physiktyp` TO "hurrican-writer"@"10.207.%";
GRANT DELETE ON `hurrican`.`t_sperre_verteilung` TO "hurrican-writer"@"10.207.%";
GRANT DELETE ON `hurrican`.`t_tdn` TO "hurrican-writer"@"10.207.%";
GRANT DELETE ON `hurrican`.`t_verlauf_abteilung` TO "hurrican-writer"@"10.207.%";
GRANT DELETE ON `hurrican`.`t_kubena_hvt` TO "hurrican-writer"@"10.207.%";
GRANT DELETE ON `hurrican`.`t_kubena_prod` TO "hurrican-writer"@"10.207.%";
GRANT DELETE ON `hurrican`.`t_kubena_tdn` TO "hurrican-writer"@"10.207.%";
GRANT DELETE ON `hurrican`.`t_hvt_standort_2_technik` TO "hurrican-writer"@"10.207.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_COMMAND_MAPPING` TO "hurrican-writer"@"10.207.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_CHAIN` TO "hurrican-writer"@"10.207.%";
GRANT DELETE ON `hurrican`.`t_prod_2_prod` TO "hurrican-writer"@"10.207.%";


-- Host: 10.10.%, 10.20.%, 10.30.%
GRANT USAGE ON * . * TO "hurrican-writer"@"10.10.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE ON `hurrican`.* TO "hurrican-writer"@"10.10.%";
GRANT DELETE ON `hurrican`.`t_uevt_2_ziel` TO "hurrican-writer"@"10.10.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_schnittstelle` TO "hurrican-writer"@"10.10.%";
GRANT DELETE ON `hurrican`.`t_ba_verl_aend_prod_2_gruppe` TO "hurrican-writer"@"10.10.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_physiktyp` TO "hurrican-writer"@"10.10.%";
GRANT DELETE ON `hurrican`.`t_sperre_verteilung` TO "hurrican-writer"@"10.10.%";
GRANT DELETE ON `hurrican`.`t_tdn` TO "hurrican-writer"@"10.10.%";
GRANT DELETE ON `hurrican`.`t_verlauf_abteilung` TO "hurrican-writer"@"10.10.%";
GRANT DELETE ON `hurrican`.`t_kubena_hvt` TO "hurrican-writer"@"10.10.%";
GRANT DELETE ON `hurrican`.`t_kubena_prod` TO "hurrican-writer"@"10.10.%";
GRANT DELETE ON `hurrican`.`t_kubena_tdn` TO "hurrican-writer"@"10.10.%";
GRANT DELETE ON `hurrican`.`t_hvt_standort_2_technik` TO "hurrican-writer"@"10.10.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_COMMAND_MAPPING` TO "hurrican-writer"@"10.10.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_CHAIN` TO "hurrican-writer"@"10.10.%";
GRANT DELETE ON `hurrican`.`t_prod_2_prod` TO "hurrican-writer"@"10.10.%";
GRANT DELETE ON `hurrican`.`t_prod_2_eg` TO "hurrican-writer"@"10.10.%";
GRANT USAGE ON * . * TO "hurrican-writer"@"10.20.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE ON `hurrican`.* TO "hurrican-writer"@"10.20.%";
GRANT DELETE ON `hurrican`.`t_uevt_2_ziel` TO "hurrican-writer"@"10.20.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_schnittstelle` TO "hurrican-writer"@"10.20.%";
GRANT DELETE ON `hurrican`.`t_ba_verl_aend_prod_2_gruppe` TO "hurrican-writer"@"10.20.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_physiktyp` TO "hurrican-writer"@"10.20.%";
GRANT DELETE ON `hurrican`.`t_sperre_verteilung` TO "hurrican-writer"@"10.20.%";
GRANT DELETE ON `hurrican`.`t_tdn` TO "hurrican-writer"@"10.20.%";
GRANT DELETE ON `hurrican`.`t_verlauf_abteilung` TO "hurrican-writer"@"10.20.%";
GRANT DELETE ON `hurrican`.`t_kubena_hvt` TO "hurrican-writer"@"10.20.%";
GRANT DELETE ON `hurrican`.`t_kubena_prod` TO "hurrican-writer"@"10.20.%";
GRANT DELETE ON `hurrican`.`t_kubena_tdn` TO "hurrican-writer"@"10.20.%";
GRANT DELETE ON `hurrican`.`t_hvt_standort_2_technik` TO "hurrican-writer"@"10.20.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_COMMAND_MAPPING` TO "hurrican-writer"@"10.20.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_CHAIN` TO "hurrican-writer"@"10.20.%";
GRANT DELETE ON `hurrican`.`t_prod_2_prod` TO "hurrican-writer"@"10.20.%";
GRANT DELETE ON `hurrican`.`t_prod_2_eg` TO "hurrican-writer"@"10.20.%";
GRANT USAGE ON * . * TO "hurrican-writer"@"10.50.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE ON `hurrican`.* TO "hurrican-writer"@"10.50.%";
GRANT DELETE ON `hurrican`.`t_uevt_2_ziel` TO "hurrican-writer"@"10.50.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_schnittstelle` TO "hurrican-writer"@"10.50.%";
GRANT DELETE ON `hurrican`.`t_ba_verl_aend_prod_2_gruppe` TO "hurrican-writer"@"10.50.%";
GRANT DELETE ON `hurrican`.`t_produkt_2_physiktyp` TO "hurrican-writer"@"10.50.%";
GRANT DELETE ON `hurrican`.`t_sperre_verteilung` TO "hurrican-writer"@"10.50.%";
GRANT DELETE ON `hurrican`.`t_tdn` TO "hurrican-writer"@"10.50.%";
GRANT DELETE ON `hurrican`.`t_verlauf_abteilung` TO "hurrican-writer"@"10.50.%";
GRANT DELETE ON `hurrican`.`t_kubena_hvt` TO "hurrican-writer"@"10.50.%";
GRANT DELETE ON `hurrican`.`t_kubena_prod` TO "hurrican-writer"@"10.50.%";
GRANT DELETE ON `hurrican`.`t_kubena_tdn` TO "hurrican-writer"@"10.50.%";
GRANT DELETE ON `hurrican`.`t_hvt_standort_2_technik` TO "hurrican-writer"@"10.50.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_COMMAND_MAPPING` TO "hurrican-writer"@"10.50.%";
GRANT DELETE ON `hurrican`.`T_SERVICE_CHAIN` TO "hurrican-writer"@"10.50.%";
GRANT DELETE ON `hurrican`.`t_prod_2_prod` TO "hurrican-writer"@"10.50.%";
GRANT DELETE ON `hurrican`.`t_prod_2_eg` TO "hurrican-writer"@"10.50.%";

-- User: fallback
-- Host: 10.1.%, 10.4.%, 10.3.%
GRANT USAGE ON * . * TO "fallback"@"10.1.%" IDENTIFIED BY "fallback" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON * . * TO "fallback"@"10.1.%" IDENTIFIED BY "fallback";
GRANT USAGE ON * . * TO "fallback"@"10.4.%" IDENTIFIED BY "fallback" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON * . * TO "fallback"@"10.4.%" IDENTIFIED BY "fallback";
GRANT USAGE ON * . * TO "fallback"@"10.5.%" IDENTIFIED BY "fallback" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON * . * TO "fallback"@"10.5.%" IDENTIFIED BY "fallback";
GRANT USAGE ON * . * TO "fallback"@"192.168.250.%" IDENTIFIED BY "fallback" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON * . * TO "fallback"@"192.168.250.%" IDENTIFIED BY "fallback";
GRANT USAGE ON * . * TO "fallback"@"192.168.251.%" IDENTIFIED BY "fallback" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON * . * TO "fallback"@"192.168.251.%" IDENTIFIED BY "fallback";
GRANT USAGE ON * . * TO "fallback"@"10.203.1.%" IDENTIFIED BY "fallback" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON * . * TO "fallback"@"10.203.1.%" IDENTIFIED BY "fallback";
GRANT USAGE ON * . * TO "fallback"@"172.22.%" IDENTIFIED BY "fallback" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON * . * TO "fallback"@"172.22.%" IDENTIFIED BY "fallback";
GRANT USAGE ON * . * TO "fallback"@"172.16.%" IDENTIFIED BY "fallback" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON * . * TO "fallback"@"172.16..%" IDENTIFIED BY "fallback";
-- Host: 10.10.%, 10.20.%, 10.30.%
GRANT USAGE ON * . * TO "fallback"@"10.10.%" IDENTIFIED BY "fallback" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON * . * TO "fallback"@"10.10.%" IDENTIFIED BY "fallback";
GRANT USAGE ON * . * TO "fallback"@"10.20.%" IDENTIFIED BY "fallback" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON * . * TO "fallback"@"10.20.%" IDENTIFIED BY "fallback";
GRANT USAGE ON * . * TO "fallback"@"10.50.%" IDENTIFIED BY "fallback" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON * . * TO "fallback"@"10.50.%" IDENTIFIED BY "fallback";

--User:IPS_SCRIPT
--Host:10.1.%
GRANT USAGE ON * . * TO "script_ips"@"10.1.%"IDENTIFIED BY "script_ips" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `hurrican` . * TO "script_ips"@"10.1.%";
GRANT SELECT , UPDATE (`STATUS_ID`,`LA_gesp`,`INBETRIEBNAHME`, `PROD_ID`) ON `hurrican`.`t_auftrag_daten` TO "script_ips"@"10.1.%";
GRANT SELECT , UPDATE ON `hurrican`.`t_verlauf` TO "script_ips"@"10.1.%"; 
GRANT SELECT , INSERT , UPDATE ON `hurrican`.`t_sdh_physik` TO "script_ips"@"10.1.%";
GRANT SELECT , UPDATE ON `hurrican`.`t_verlauf_abteilung` TO "script_ips"@"10.1.%";
GRANT SELECT , UPDATE ON `hurrican`.`t_int_account` TO "script_ips"@"10.1.%";
GRANT SELECT , UPDATE (`S_USER` , `S_DATUM`) ON `hurrican`.`t_sperre` TO "script_ips"@"10.1.%";

--User: SDH_SCRIPT
--Host:10.1.%
GRANT USAGE ON * . * TO "sdh_script"@"10.250.%"IDENTIFIED BY "tpircshds" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE ON `hurrican`.`T_DSLAM_PROFILE_CHANGE` TO "sdh_script"@"10.250.%";
GRANT SELECT ON `hurrican`.`T_DSLAM_PROFILE_CHANGE_REASON` TO "sdh_script"@"10.250.%";
GRANT SELECT ON `hurrican`.`T_DSLAM` TO "sdh_script"@"10.250.%";

--User: erechnung
GRANT USAGE ON *.* TO "erechnung"@"10.1.%" IDENTIFIED BY "erechnung";
GRANT SELECT ON `hurrican` . * TO "erechnung"@"10.1.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican` . `t_e_rechnung_druck` TO "erechnung"@"10.1.%";

