--
-- Update-Script fuer die Aufnahme des TroubleTicket-Systems
-- in Hurrican.
--

--
-- SQL-Statements fuer die DB 'AUTHENTICATION'
--
-- DataSource in Authentication einbinden
INSERT INTO DB (ID, NAME, DRIVER, URL) 
	VALUES (8, 'tt', 'com.mysql.jdbc.Driver', 'jdbc:mysql://10.1.20.12:3306/tts');

INSERT INTO ACCOUNT (DB_ID, APP_ID, ACC_NAME, ACCOUNTUSER, ACCOUNTPASSWORD)
	VALUES (8, 1, 'tt.writer', 'hurrican-writer', 'dyMRupr3P1Nl1OkoxnPWiQ==');

INSERT INTO ROLE (NAME, DESCRIPTION, APP_ID) VALUES ('tt.worker', 'Bearbeiter fuer TroubleTickets', 1);
INSERT INTO ROLE (NAME, DESCRIPTION, APP_ID) VALUES ('tt.viewer', 'View-Rechte fuer TroubleTickets', 1);

GRANT SELECT , INSERT , UPDATE ON `tts`.* TO "hurrican-writer"@"10.1.%";
GRANT SELECT , INSERT , UPDATE ON `tts`.* TO "hurrican-writer"@"10.2.%";
GRANT SELECT , INSERT , UPDATE ON `tts`.* TO "hurrican-writer"@"10.3.%";
GRANT SELECT , INSERT , UPDATE ON `tts`.* TO "hurrican-writer"@"10.10.%";
GRANT SELECT , INSERT , UPDATE ON `tts`.* TO "hurrican-writer"@"10.20.%";
GRANT SELECT , INSERT , UPDATE ON `tts`.* TO "hurrican-writer"@"10.30.%";


-- SQL-Statements fuer die DB 'HURRICAN'
-- Definition des Panels fuer die Billing-Leistungen
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)				
   VALUES (210, "de.augustakom.hurrican.gui.tt.TTOverviewPanel", "PANEL", 
   "tt.overview.panel", "TroubleTickets", "Panel fuer die Anzeige der TroubleTickets", null, 95, 1);	
-- durch Angabe von ReferenzID '-99' wird das Panel von allen ProduktGruppen angezogen.
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (210, -99, "de.augustakom.hurrican.model.cc.ProduktGruppe");   

-- Action im Kunden-Menu, um die TTs des Kunden zu oeffnen
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ADD_SEPARATOR, ACTIVE)				
   VALUES (109, "de.augustakom.hurrican.gui.tt.actions.OpenKundenTroubleTicketsAction", "ACTION", 
   "show.tts4kunde", "TroubleTickets", "Zeigt alle TroubleTickets des Kunden an", null, 90, 1, 1);	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (109, 100, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");   
	
-- Aenderungen an der TTS-Datenbank
alter table tt_arbeiten change column Arbeiten_RegieBericht Arbeiten_RegieBericht tinyint(1);
alter table tt_arbeiten change column Arbeiten_aRAZ Arbeiten_aRAZ tinyint(1);


-- GRANTs
-- ausgefuehrt am 28.03.2006
GRANT SELECT ON `tts`.* to "hurrican-writer"@"10.1.%";
GRANT SELECT ON `tts`.* to "hurrican-writer"@"10.10.%";
GRANT SELECT ON `tts`.* to "hurrican-writer"@"10.2.%";
GRANT SELECT ON `tts`.* to "hurrican-writer"@"10.20.%";
GRANT SELECT ON `tts`.* to "hurrican-writer"@"10.3.%";
GRANT SELECT ON `tts`.* to "hurrican-writer"@"10.30.%";
GRANT SELECT ON `tts`.* to "hurrican-reader"@"10.1.%";
GRANT SELECT ON `tts`.* to "hurrican-reader"@"10.10.%";
GRANT SELECT ON `tts`.* to "hurrican-reader"@"10.2.%";
GRANT SELECT ON `tts`.* to "hurrican-reader"@"10.20.%";
GRANT SELECT ON `tts`.* to "hurrican-reader"@"10.3.%";
GRANT SELECT ON `tts`.* to "hurrican-reader"@"10.30.%";
flush privileges;