--
-- GRANT-Scripte, um die Benutzer fuer die Datenbank 'TTS' zu definieren.
--

-- User: tt_tools
-- Hosts: 10.1.% und 10.10.%
GRANT USAGE ON *.* TO "tt_tools"@"10.1.%" IDENTIFIED BY "az25er";
GRANT SELECT, INSERT, UPDATE, DELETE ON `TTS`.* TO "tt_tools"@"10.1.%";
GRANT USAGE ON *.* TO "tool"@"10.1.%" IDENTIFIED BY "er234q";
GRANT SELECT ON `hurrican`.* TO "tool"@"10.1.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_int_account` TO "tool"@"10.1.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_verlauf_abteilung` TO "tool"@"10.1.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_carrierbestellung` TO "tool"@"10.1.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_hvt_standort` TO "tool"@"10.1.%";
GRANT USAGE ON *.* TO "tool"@"10.10.%" IDENTIFIED BY "er234q";
GRANT SELECT ON `hurrican`.* TO "tool"@"10.10.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_int_account` TO "tool"@"10.10.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_verlauf_abteilung` TO "tool"@"10.10.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_carrierbestellung` TO "tool"@"10.10.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_hvt_standort` TO "tool"@"10.10.%";
GRANT USAGE ON *.* TO "tt_tools"@"10.10.%" IDENTIFIED BY "az25er";
GRANT SELECT, INSERT, UPDATE, DELETE ON `TTS`.* TO "tt_tools"@"10.10.%";
GRANT USAGE ON *.* TO "tt_tools"@"10.10.%" IDENTIFIED BY "az25er";
GRANT SELECT, INSERT, UPDATE, DELETE ON `TTS`.* TO "tt_tools"@"10.10.%";
GRANT USAGE ON *.* TO "tt_tools"@"10.4.%" IDENTIFIED BY "az25er";
GRANT SELECT, INSERT, UPDATE, DELETE ON `TTS`.* TO "tt_tools"@"10.4.%";
GRANT USAGE ON *.* TO "tt_tools"@"10.20.%" IDENTIFIED BY "az25er";
GRANT SELECT, INSERT, UPDATE, DELETE ON `TTS`.* TO "tt_tools"@"10.20.%";
GRANT USAGE ON *.* TO "tt_tools"@"10.5.%" IDENTIFIED BY "az25er";
GRANT SELECT, INSERT, UPDATE, DELETE ON `TTS`.* TO "tt_tools"@"10.5.%";
GRANT USAGE ON *.* TO "tool"@"10.50.%" IDENTIFIED BY  "er234q";
GRANT SELECT, INSERT, UPDATE, DELETE ON `TTS`.* TO "tt_tools"@"10.50.%";
GRANT USAGE ON *.* TO "tool"@"10.1.%" IDENTIFIED BY "er234q";
GRANT SELECT ON `hurrican`.* TO "tool"@"10.1.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_int_account` TO "tool"@"10.1.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_dslam` TO "tool"@"10.1.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_verlauf_abteilung` TO "tool"@"10.1.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_dialer` TO "tool"@"10.1.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_rangierungs_auftrag` TO "tool"@"10.1.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_rangierungs_material` TO "tool"@"10.1.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_ia` TO "tool"@"10.1.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_ia_budget` TO "tool"@"10.1.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_ia_mat_entnahme` TO "tool"@"10.1.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_ia_mat_entnahme_artikel` TO "tool"@"10.1.%";
GRANT USAGE ON *.* TO "tool"@"10.10.%" IDENTIFIED BY  "er234q";
GRANT SELECT ON `hurrican`.* TO "tool"@"10.10.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_int_account` TO "tool"@"10.10.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_dslam` TO "tool"@"10.10.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_verlauf_abteilung` TO "tool"@"10.10.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_dialer` TO "tool"@"10.10.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_rangierungs_auftrag` TO "tool"@"10.10.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_rangierungs_material` TO "tool"@"10.10.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_ia` TO "tool"@"10.10.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_ia_budget` TO "tool"@"10.10.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_ia_mat_entnahme` TO "tool"@"10.10.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_ia_mat_entnahme_artikel` TO "tool"@"10.10.%";
GRANT USAGE ON *.* TO "tool"@"10.4.%" IDENTIFIED BY  "er234q";
GRANT SELECT ON `hurrican`.* TO "tool"@"10.4.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_int_account` TO "tool"@"10.4.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_verlauf_abteilung` TO "tool"@"10.4.%";
GRANT USAGE ON *.* TO "tool"@"10.20.%" IDENTIFIED BY  "er234q";
GRANT SELECT ON `hurrican`.* TO "tool"@"10.20.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_int_account` TO "tool"@"10.20.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_verlauf_abteilung` TO "tool"@"10.20.%";
GRANT USAGE ON *.* TO "tool"@"10.5.%" IDENTIFIED BY  "er234q";
GRANT SELECT ON `hurrican`.* TO "tool"@"10.5.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_int_account` TO "tool"@"10.5.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_verlauf_abteilung` TO "tool"@"10.5.%";
GRANT USAGE ON *.* TO "tool"@"10.50.%" IDENTIFIED BY  "er234q";
GRANT SELECT ON `hurrican`.* TO "tool"@"10.50.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_int_account` TO "tool"@"10.50.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_verlauf_abteilung` TO "tool"@"10.50.%";

GRANT USAGE ON *.* TO "tt_tools"@"192.168.%" IDENTIFIED BY "az25er";
GRANT SELECT, INSERT, UPDATE, DELETE ON `TTS`.* TO "tt_tools"@"192.168.%";
GRANT USAGE ON *.* TO "tool"@"192.168.%" IDENTIFIED BY "er234q";
GRANT SELECT ON `hurrican`.* TO "tool"@"192.168.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_int_account` TO "tool"@"192.168.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_verlauf_abteilung` TO "tool"@"192.168.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_carrierbestellung` TO "tool"@"192.168.%";
GRANT USAGE ON *.* TO "tool"@"192.168.%" IDENTIFIED BY "er234q";
GRANT SELECT ON `hurrican`.* TO "tool"@"192.168.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_int_account` TO "tool"@"192.168.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_dslam` TO "tool"@"192.168.%";
GRANT SELECT, UPDATE ON `hurrican`.`t_verlauf_abteilung` TO "tool"@"192.168.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_dialer` TO "tool"@"192.168.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_rangierungs_auftrag` TO "tool"@"192.168.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_rangierungs_material` TO "tool"@"192.168.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_ia` TO "tool"@"192.168.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_ia_budget` TO "tool"@"192.168.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_ia_mat_entnahme` TO "tool"@"192.168.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_ia_mat_entnahme_artikel` TO "tool"@"192.168.%";

GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_equipment` TO "tool"@"10.10.%";
GRANT SELECT, INSERT, UPDATE ON `hurrican`.`t_rangierung` TO "tool"@"10.10.%";

GRANT SELECT , UPDATE (`NAME`,`RANGIER_ID`,`RANGIER_ID_ADDITIONAL`,`HVT_ID_STANDORT`) ON `hurrican`.`t_endstelle` TO "tool"@"10.1.%";
GRANT SELECT , UPDATE (`NAME`,`RANGIER_ID`,`RANGIER_ID_ADDITIONAL`,`HVT_ID_STANDORT`) ON `hurrican`.`t_endstelle` TO "tool"@"10.10.%";
GRANT SELECT , UPDATE (`NAME`,`RANGIER_ID`,`RANGIER_ID_ADDITIONAL`,`HVT_ID_STANDORT`) ON `hurrican`.`t_endstelle` TO "tool"@"10.4.%";
GRANT SELECT , UPDATE (`NAME`,`RANGIER_ID`,`RANGIER_ID_ADDITIONAL`,`HVT_ID_STANDORT`) ON `hurrican`.`t_endstelle` TO "tool"@"10.20.%";
GRANT SELECT , UPDATE (`NAME`,`RANGIER_ID`,`RANGIER_ID_ADDITIONAL`,`HVT_ID_STANDORT`) ON `hurrican`.`t_endstelle` TO "tool"@"10.5.%";
GRANT SELECT , UPDATE (`NAME`,`RANGIER_ID`,`RANGIER_ID_ADDITIONAL`,`HVT_ID_STANDORT`) ON `hurrican`.`t_endstelle` TO "tool"@"10.50.%";

SET PASSWORD FOR "tt_tools"@"10.1.%" = OLD_PASSWORD("az25er");
SET PASSWORD FOR "tt_tools"@"10.10.%" = OLD_PASSWORD("az25er");
SET PASSWORD FOR "tt_tools"@"10.4.%" = OLD_PASSWORD("az25er");
SET PASSWORD FOR "tt_tools"@"10.20.%" = OLD_PASSWORD("az25er");
SET PASSWORD FOR "tt_tools"@"10.5.%" = OLD_PASSWORD("az25er");
SET PASSWORD FOR "tt_tools"@"10.50.%" = OLD_PASSWORD("az25er");
SET PASSWORD FOR "tt_tools"@"192.168.%" = OLD_PASSWORD("az25er");

SET PASSWORD FOR "tool"@"10.1.%" = OLD_PASSWORD("er234q");
SET PASSWORD FOR "tool"@"10.10.%" = OLD_PASSWORD("er234q");
SET PASSWORD FOR "tool"@"10.4.%" = OLD_PASSWORD("er234q");
SET PASSWORD FOR "tool"@"10.20.%" = OLD_PASSWORD("er234q");
SET PASSWORD FOR "tool"@"10.5.%" = OLD_PASSWORD("er234q");
SET PASSWORD FOR "tool"@"10.50.%" = OLD_PASSWORD("er234q");
SET PASSWORD FOR "tool"@"192.168.%" = OLD_PASSWORD("er234q");

-- Leseberechtigungen fuer User 'hurrican-reader'
GRANT USAGE ON `TTS`.* TO "hurrican-reader"@"10.1.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-reader"@"10.10.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-reader"@"10.4.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-reader"@"10.20.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-reader"@"10.5.%" IDENTIFIED BY "nacirruh" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;;
GRANT USAGE ON `TTS`.* TO "hurrican-reader"@"10.50.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-reader"@"192.168.250.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-reader"@"192.168.251.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-reader"@"172.22.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-reader"@"172.16.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-reader"@"10.203.1.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-reader"@"10.206.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-reader"@"10.207.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-reader"@"10.208.%" IDENTIFIED BY "nacirruh";
GRANT SELECT ON `TTS`.* TO "hurrican-reader"@"10.207.%" IDENTIFIED BY "nacirruh";
GRANT SELECT ON `TTS`.* TO "hurrican-reader"@"10.208.%" IDENTIFIED BY "nacirruh";

-- Lese- und Schreibberechtigungen fuer User 'hurrican-writer'
GRANT USAGE ON `TTS`.* TO "hurrican-writer"@"10.1.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-writer"@"10.10.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-writer"@"10.4.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-writer"@"10.20.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-writer"@"10.5.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-writer"@"10.50.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-writer"@"192.168.250.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-writer"@"192.168.251.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-writer"@"172.22.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-writer"@"172.16.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-writer"@"10.203.1.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-writer"@"10.206.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-writer"@"10.207.%" IDENTIFIED BY "nacirruh";
GRANT USAGE ON `TTS`.* TO "hurrican-writer"@"10.208.%" IDENTIFIED BY "nacirruh";
GRANT SELECT ON `TTS`.* TO "hurrican-writer"@"172.22.%" IDENTIFIED BY "nacirruh";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_troubleticket` TO "hurrican-writer"@"10.1.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_arbeiten` TO "hurrican-writer"@"10.1.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_troubleticket` TO "hurrican-writer"@"10.10.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_arbeiten` TO "hurrican-writer"@"10.10.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_troubleticket` TO "hurrican-writer"@"10.4.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_arbeiten` TO "hurrican-writer"@"10.4.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_troubleticket` TO "hurrican-writer"@"10.20.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_arbeiten` TO "hurrican-writer"@"10.20.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_troubleticket` TO "hurrican-writer"@"10.5.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_arbeiten` TO "hurrican-writer"@"10.5.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_troubleticket` TO "tool"@"10.50.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_arbeiten` TO "hurrican-writer"@"10.50.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_troubleticket` TO "hurrican-writer"@"192.168.250.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_arbeiten` TO "hurrican-writer"@"192.168.250.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_troubleticket` TO "hurrican-writer"@"192.168.251.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_arbeiten` TO "hurrican-writer"@"192.168.251.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_troubleticket` TO "hurrican-writer"@"172.22.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_arbeiten` TO "hurrican-writer"@"172.22.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_troubleticket` TO "hurrican-writer"@"10.203.1.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_arbeiten` TO "hurrican-writer"@"10.203.1.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_troubleticket` TO "hurrican-writer"@"10.206.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_arbeiten` TO "hurrican-writer"@"10.206.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_troubleticket` TO "hurrican-writer"@"10.207.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_arbeiten` TO "hurrican-writer"@"10.207.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_troubleticket` TO "hurrican-writer"@"10.208.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_arbeiten` TO "hurrican-writer"@"10.208.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_troubleticket` TO "hurrican-writer"@"172.16.%";
GRANT SELECT, INSERT, UPDATE ON `TTS`.`tt_arbeiten` TO "hurrican-writer"@"172.16.%";

-- Benutzer TT_EWSD mit Schreib- und Leserechten auf DB 'EWSD'
GRANT USAGE ON *.* TO "TT_EWSD"@"10.%" IDENTIFIED BY "DSWE"  WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0;
GRANT USAGE ON *.* TO "TT_EWSD"@"192.168.%" IDENTIFIED BY "DSWE"  WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0;
GRANT SELECT, INSERT, UPDATE ON `EWSD`.* TO "TT_EWSD"@"10.%";
GRANT SELECT, INSERT, UPDATE ON `EWSD`.* TO "TT_EWSD"@"192.168.%";

SET PASSWORD FOR "TT_EWSD"@"10.%" = OLD_PASSWORD("DSWE");
SET PASSWORD FOR "TT_EWSD"@"192.168.%" = OLD_PASSWORD("DSWE");

