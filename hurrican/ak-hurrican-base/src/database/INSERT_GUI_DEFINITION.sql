--
-- In diesem SQL-Script werden Daten in die Tabellen T_GUI_DEFINITION und
-- T_GUI_MAPPING eingetragen.
--

DELETE FROM T_GUI_MAPPING;
DELETE FROM T_GUI_DEFINITION;

-- Nummernkreis 100-199
-- GUI-Definitionen fuer das Kunden-Uebersichtsfenster
-- Action, um die Mistral-Auftragsuebersicht anzuzeigen
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ACTIVE)
	VALUES (100, "de.augustakom.hurrican.gui.auftrag.AuftragUebersichtFrame", "INTERNALFRAME",
	"Auftragsübersicht", null, null, null,1);
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (101, "de.augustakom.hurrican.gui.auftrag.actions.ShowBillingAuftragUebersichtAction", "ACTION",
	"uebersicht.mistral", "Übersicht Mistral...", "Erstellt eine Übersicht der Mistral-Aufträge", null, 3, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (101, 100, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");
-- Action, um eine Sperr-Historie anzuzeigen
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (102, "de.augustakom.hurrican.gui.auftrag.actions.ShowSpeereHistoryAction", "ACTION",
	"sperre.history", "Sperre Historie...", "Zeigt eine Übersicht über die bisherigen Sperren", null, 4, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (102, 100, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");
-- Action, um einen Kunden zu sperren
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (103, "de.augustakom.hurrican.gui.auftrag.actions.SperrenAction", "ACTION",
	"sperren", "Sperren - Entsperren...", "Sperrt bzw. entsperrt den aktuellen Kunden", null, 5, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (103, 100, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");
-- Action, um einen neuen Hurrican-Auftrag anzulegen
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (104, "de.augustakom.hurrican.gui.auftrag.actions.CreateAuftragAction", "ACTION",
	"create.auftrag", "Hurrican-Auftrag anlegen...", "Legt einen neuen Hurrican-Auftrag an", null, 1, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (104, 100, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");
-- Action, um einen Auftrag aus Mistral zu uebernehmen
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (105, "de.augustakom.hurrican.gui.auftrag.actions.ShowAuftragsMonitorAction", "ACTION",
	"show.auftragsmonitor", "Mistral-Auftrag übernehmen...", 
	"Zeigt eine Übersicht über die Hurrican- und Mistral-Aufträge an. Differenzen können abgeglichen werden.", null, 2, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (105, 100, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");
-- Action, um das ERechnungs-Anschreiben fuer den Kunden zu drucken
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (106, "de.augustakom.hurrican.gui.auftrag.actions.PrintERechnungAction", "ACTION",
	"print.erechnung", "ERechnungs-Anschreiben...", 
	"Erstellt das ERechnungs-Anschreiben fuer den Kunden.", null, 20, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (106, 100, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");
-- Action, um eine Übersicht über das ERechnungs-Anschreiben fuer den Kunden zu erhalten
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (107, "de.augustakom.hurrican.gui.erechnung.actions.ERechnungHistoryAction", "ACTION",
	"show.erechnung", "Übersicht ERechnungs-Anschreiben...", 
	"Übersicht über das ERechnungs-Anschreiben fuer den Kunden.", null, 21, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (107, 100, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");
-- Action, um fuer einen Kunden eine Auftrageingangs-Bestaetigung zu erstellen
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE, ADD_SEPARATOR) 
	VALUES (108, "de.augustakom.hurrican.gui.auftrag.actions.PrintAEAction", "ACTION",
	"print.ae4kunde", "Auftrags-Eingang drucken...", "Erstellt ein Auftrageingangs-Anschreiben für den Kunden", null, 50, 1, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (108, 100, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");

-- Nummernkreis 200-299
-- GUI-Definitionen fuer das Auftrags-Frame und die zugehoerigen Panels
-- Auftrags-Frame
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (200, "de.augustakom.hurrican.gui.auftrag.AuftragDataFrame", "INTERNALFRAME", 
	"auftrag.data.panel", "Auftragsdaten", "Internal-Frame fuer die Darstellung der Auftragsdaten", 
	"de/augustakom/hurrican/gui/images/auftrag.gif", 1, 1);
-- Definition des Panels fuer die Auftragsstammdaten.
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (201, "de.augustakom.hurrican.gui.auftrag.AuftragStammdatenPanel", "PANEL", 
	"auftrag.stammdaten.panel", "Stammdaten", "Panel für die Darstellung der Auftragsstammdaten", null, 1, 1);
-- Definition des Panels fuer die Rufnummern-Darstellung
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (202, "de.augustakom.hurrican.gui.auftrag.AuftragRufnummerPanel", "PANEL", 
	"auftrag.rufnummer.panel", "Rufnummern", "Panel für die Darstellung der Rufnummern", null, 2, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (202, 1, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (202, 3, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (202, 4, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (202, 7, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (202, 13, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (202, 16, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (202, 17, "de.augustakom.hurrican.model.cc.ProduktGruppe");
-- Definition des Panels fuer die Endstellen
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (203, "de.augustakom.hurrican.gui.auftrag.AuftragEndstellenPanel", "PANEL", 
	"auftrag.endstellen.panel", "Endstellen", "Panel für die Darstellung der Endstellen", null, 3, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (203, 1, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (203, 2, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (203, 3, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (203, 4, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (203, 6, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (203, 7, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (203, 8, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (203, 9, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (203, 10, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (203, 11, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (203, 12, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (203, 13, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (203, 14, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (203, 15, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (203, 16, "de.augustakom.hurrican.model.cc.ProduktGruppe");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (203, 17, "de.augustakom.hurrican.model.cc.ProduktGruppe");	
-- Definition des Panels fuer IN-Rufnummern-Daten
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)				
   VALUES (204, "de.augustakom.hurrican.gui.auftrag.AuftragINDienstePanel", "PANEL", 
   "auftrag.in.dienste.panel", "IN-Dienste", "Panel für die Darstellung der IN-Dienste zu einem Auftrag", null, 4, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (204, 1, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (204, 2, "de.augustakom.hurrican.model.cc.ProduktGruppe");
-- Definition des Panels fuer die VPN-Konfiguration
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)				
   VALUES (205, "de.augustakom.hurrican.gui.auftrag.vpn.VPNKonfigPanel", "PANEL", 
   "auftrag.vpn.konfig.panel", "VPN-Konfiguration", "Panel für die VPN-Konfiguration", null, 5, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (205, 1, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (205, 2, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (205, 3, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (205, 4, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (205, 5, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (205, 7, "de.augustakom.hurrican.model.cc.ProduktGruppe");		   
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (205, 8, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (205, 9, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (205, 10, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (205, 16, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (205, 17, "de.augustakom.hurrican.model.cc.ProduktGruppe");
-- Definition des Panels fuer die Internet-/Online-Daten
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)				
   VALUES (206, "de.augustakom.hurrican.gui.auftrag.internet.InternetPanel", "PANEL", 
   "auftrag.internet.panel", "Internet", "Panel für die Internet- und Online-Daten", null, 6, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (206, 2, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (206, 3, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (206, 4, "de.augustakom.hurrican.model.cc.ProduktGruppe");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (206, 5, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (206, 7, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (206, 8, "de.augustakom.hurrican.model.cc.ProduktGruppe");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (206, 10, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (206, 11, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (206, 12, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (206, 14, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (206, 16, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (206, 17, "de.augustakom.hurrican.model.cc.ProduktGruppe");
-- Definition des Panels fuer die Zugangsdaten zu Endgeraeten
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)				
   VALUES (207, "de.augustakom.hurrican.gui.auftrag.EGZugangPanel", "PANEL", 
   "auftrag.endgeraet.zugang.panel", "Endgeräte-Zugang", "Panel für die Zugangsdaten zu Endgeräten", null, 99, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (207, 8, "de.augustakom.hurrican.model.cc.ProduktGruppe");
-- Definition des Panels fuer die Connect- bzw. Faktura-Daten
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)				
   VALUES (208, "de.augustakom.hurrican.gui.auftrag.AuftragFakturaPanel", "PANEL", 
   "auftrag.faktura.panel", "AK-Connect", "Panel für die Faktura-Daten bei Connect-Aufträgen", null, 7, 1);	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (208, 1, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (208, 2, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (208, 9, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (208, 15, "de.augustakom.hurrican.model.cc.ProduktGruppe");   
-- Definition des Panels fuer die Billing-Leistungen
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)				
   VALUES (209, "de.augustakom.hurrican.gui.auftrag.billing.BillingAuftragLeistungenPanel", "PANEL", 
   "auftrag.leistungen.panel", "Leistungen", "Panel fuer die Anzeige der zugeordneten Leistungen", null, 90, 1);	
-- durch Angabe von ReferenzID '-99' wird das Panel von allen ProduktGruppen angezogen.
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (209, -99, "de.augustakom.hurrican.model.cc.ProduktGruppe");   
-- Definition vom VoIP-Panel
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)				
   VALUES (212, "de.augustakom.hurrican.gui.auftrag.AuftragVoIPPanel", "PANEL", 
   "auftrag.voip.panel", "VoIP", "Panel fuer die Anzeige der VoIP Daten", null, 50, 1);	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (212, 17, "de.augustakom.hurrican.model.cc.ProduktGruppe");  

-- Definition vom QoS-Panel
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)				
   VALUES (213, "de.augustakom.hurrican.gui.auftrag.AuftragQoSPanel", "PANEL", 
   "auftrag.qos.panel", "QoS", "Panel fuer die Anzeige der Quality-of-Service Daten", null, 60, 1);	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (213, 1, "de.augustakom.hurrican.model.cc.ProduktGruppe"),
	       (213, 2, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 3, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 4, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 5, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 6, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 7, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 8, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 9, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 12, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 16, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 17, "de.augustakom.hurrican.model.cc.ProduktGruppe"), 
	       (213, 18, "de.augustakom.hurrican.model.cc.ProduktGruppe");
	
-- Nummernkreis 300-399
-- GUI-Definitionen fuer die Auftrag-Actions.
-- Action, um die Mistral-Auftragspositionen zu einem Hurrican-Auftrag anzuzeigen.
--INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO)
--	VALUES (300, "de.augustakom.hurrican.gui.auftrag.actions.ShowBillingAuftragPosAction", "ACTION", 
--	"show.billing.auftragpos", "Mistral Auftragspositionen...", "Zeigt die zugehörigen Auftragspositionen aus Mistral an", 
--	null, 1);
--INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
--	VALUES (300, 201, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");
-- Action, um einen Bauauftrags-Verlauf zu erstellen
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (301, "de.augustakom.hurrican.gui.auftrag.actions.CreateBAAction", "ACTION", 
	"bauauftrag.erstellen", "Bauauftrag erstellen", "Erstellt einen Bauauftrag für den aktuell ausgewählten Auftrag", 
	null, 5, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (301, 201, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");
-- Action, um einen Bauauftrags-Verlauf zu erstellen
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (302, "de.augustakom.hurrican.gui.auftrag.actions.StornoBAAction", "ACTION", 
	"bauauftrag.stornieren", "Bauauftrag stornieren", "Storniert den Bauauftrag des aktuell gewählten Auftrags", 
	null, 10, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (302, 201, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");
-- Action, um den Bauauftrag zum aktuellen Auftrag zu drucken
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (303, "de.augustakom.hurrican.gui.auftrag.actions.PrintBAAction", "ACTION", 
	"print.bauauftrag", "Bauauftrag drucken", "Druckt den Bauauftrag zum aktuell ausgewählten Auftrag", 
	null, 15, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (303, 201, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");
-- Action, um eine Projektierung zu erstellen
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (304, "de.augustakom.hurrican.gui.auftrag.actions.CreateProjektierungAction", "ACTION", 
	"projektierung.erstellen", "Projektierung erstellen", "Erstellt eine Projektierung für den aktuell gewählten Auftrag", 
	null, 20, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (304, 201, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");
-- Action, um eine Projektierung zu drucken
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (305, "de.augustakom.hurrican.gui.auftrag.actions.PrintProjektierungAction", "ACTION", 
	"print.projektierung", "Projektierung drucken", "Druckt die Projektierung zum aktuell ausgewählten Auftrag", 
	null, 25, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (305, 201, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");
-- Action, um einen Auftrag auf 'Absage' zu setzen
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (306, "de.augustakom.hurrican.gui.auftrag.actions.AuftragAbsageAction", "ACTION", 
	"auftrag.absage", "Absage", "Setzt den Auftrags-Status auf Absage", 
	null, 30, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (306, 201, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");
-- Action, um die Verlaufs-Historie zu einem Auftrag anzuzeigen.
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (307, "de.augustakom.hurrican.gui.verlauf.actions.ShowVerlaufsHistoryAction", "ACTION", 
	"verlauf.history.anzeigen", "Verlaufs-Historie...", "Zeigt die Verlaufs-Historie des Auftrags an", 
	null, 50, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (307, 201, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");	
-- Action, um einen Auftrag zu aendern
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (308, "de.augustakom.hurrican.gui.auftrag.actions.ChangeAuftragAction", "ACTION", 
	"auftrag.aendern", "Änderung...", "Führt eine Änderung an dem Auftrag durch", null, 40, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (308, 201, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");		
-- Action, um das Realisierungstermin fuer einen Bauauftrag zu aendern (nur bei Produkten S2M und S2M-P)
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (309, "de.augustakom.hurrican.gui.auftrag.actions.ChangeBARealDateAction", "ACTION", 
	"ba.realdate.aendern", "Real.-Termin ändern...", "Ändert den Realisierungstermin des aktuellen Bauauftrags", null, 60, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (309, 3, "de.augustakom.hurrican.model.cc.Produkt");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (309, 4, "de.augustakom.hurrican.model.cc.Produkt");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (309, 338, "de.augustakom.hurrican.model.cc.Produkt");
-- Action, um das AKOnline-Anschreiben zu drucken	
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (310, "de.augustakom.hurrican.gui.auftrag.actions.PrintOnlineAction", "ACTION", 
	"print.online", "AKOnline-Anschreiben drucken...", "Druckt das AKOnline-Anschreiben aus", null, 50, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (310, 2, "de.augustakom.hurrican.model.cc.ProduktGruppe");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (310, 3, "de.augustakom.hurrican.model.cc.ProduktGruppe");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (310, 4, "de.augustakom.hurrican.model.cc.ProduktGruppe");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (310, 5, "de.augustakom.hurrican.model.cc.ProduktGruppe");		
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (310, 7, "de.augustakom.hurrican.model.cc.ProduktGruppe");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (310, 16, "de.augustakom.hurrican.model.cc.ProduktGruppe");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (310, 17, "de.augustakom.hurrican.model.cc.ProduktGruppe");	
-- Action, um einen Auftrag zu drucken
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (311, "de.augustakom.hurrican.gui.auftrag.actions.PrintAuftragAction", "ACTION", 
	"print.auftrag", "Drucken", "Ermittelt alle zu druckenden Daten des Auftrags und öffnet MS-Word", null, 70, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (311, 201, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");		
-- Action, um einen Auftrag zu kopieren
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (312, "de.augustakom.hurrican.gui.auftrag.actions.CopyConnectAuftragAction", "ACTION", 
	"copy.connect.auftrag", "Auftrag kopieren", "Kopiert den aktuellen Connect-Auftrag", 
    "de/augustakom/hurrican/gui/images/copy.gif", 75, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (312, 2, "de.augustakom.hurrican.model.cc.ProduktGruppe");		
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (312, 7, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (312, 9, "de.augustakom.hurrican.model.cc.ProduktGruppe");
-- Action, um eine TAL-Nutzungsaenderung zu erzeugen
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ADD_SEPARATOR, ACTIVE)
	VALUES (313, "de.augustakom.hurrican.gui.auftrag.actions.PrintTalNAAction", "ACTION", 
	"print.tal.na", "TAL-Nutzungsänderung", "Erzeugt für den aktuellen Auftrag eine TAL-Nutzungsänderung", null, 80, 1, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (313, 201, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");				
-- Action, um den Online-Status eines xDSL-Auftrags anzuzeigen
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ADD_SEPARATOR, ACTIVE)
	VALUES (315, "de.augustakom.hurrican.gui.auftrag.actions.ShowDSLAMStatusAction", "ACTION", 
	"show.dslam.status", "DSLAM-Status", "Anzeige des DSLAM-Status", null, 130, 0, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 9, "de.augustakom.hurrican.model.cc.Produkt");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 56, "de.augustakom.hurrican.model.cc.Produkt");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 57, "de.augustakom.hurrican.model.cc.Produkt");			
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 321, "de.augustakom.hurrican.model.cc.Produkt");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 315, "de.augustakom.hurrican.model.cc.Produkt");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 316, "de.augustakom.hurrican.model.cc.Produkt");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 317, "de.augustakom.hurrican.model.cc.Produkt");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 320, "de.augustakom.hurrican.model.cc.Produkt");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 324, "de.augustakom.hurrican.model.cc.Produkt");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 325, "de.augustakom.hurrican.model.cc.Produkt");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 326, "de.augustakom.hurrican.model.cc.Produkt");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 327, "de.augustakom.hurrican.model.cc.Produkt");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 330, "de.augustakom.hurrican.model.cc.Produkt");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 331, "de.augustakom.hurrican.model.cc.Produkt");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 332, "de.augustakom.hurrican.model.cc.Produkt");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 333, "de.augustakom.hurrican.model.cc.Produkt");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 400,"de.augustakom.hurrican.model.cc.Produkt");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 410,"de.augustakom.hurrican.model.cc.Produkt");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 411,"de.augustakom.hurrican.model.cc.Produkt");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 420, "de.augustakom.hurrican.model.cc.Produkt");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 421, "de.augustakom.hurrican.model.cc.Produkt");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 430,"de.augustakom.hurrican.model.cc.Produkt");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 431, "de.augustakom.hurrican.model.cc.Produkt");
		
-- Action, um einen Auftrag zu kuendigen
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (316, "de.augustakom.hurrican.gui.auftrag.actions.AuftragKuendigungAction", "ACTION", 
	"auftrag.kuendigen", "Kündigung...", "Setzt den Auftrag auf Kündigung", null, 45, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (316, 201, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");

-- Action, um ein TroubleTicket anzulegen
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ADD_SEPARATOR, ACTIVE)
	VALUES (317, "de.augustakom.hurrican.gui.tt.actions.NewTroubleTicketAction", "ACTION", 
	"tt.neu", "Neues TroubleTicket...", "Erstellt ein neues TroubleTicket", null, 80, 1, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (317, 201, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");		

-- Action, um die techn. Details eines Auftrags zu drucken
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ADD_SEPARATOR, ACTIVE)
	VALUES (318, "de.augustakom.hurrican.gui.auftrag.actions.PrintTechDetails4AuftragAction", "ACTION", 
	"print.tech.details", "techn. Details drucken", "Druckt die techn. Details des Auftrags", 
	null, 35, 1, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (318, 201, "de.augustakom.hurrican.model.cc.gui.GUIDefinition");

-- Action im Auftrags-Menu, um eine CPS-Tx anzulegen bzw. einen Dialog zu oeffnen
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)
	VALUES (321, 'de.augustakom.hurrican.gui.auftrag.actions.OpenCPSTxCreationDlgAction', 'ACTION', 
	'open.cps.tx.creation.dlg.action', 'CPS-Tx erzeugen...', 
	'Oeffnet einen Dialog, um eine CPS-Transaction fuer den Auftrag anzulegen', 
	null, 35, 1);
INSERT INTO T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (1, 321, 201, 'de.augustakom.hurrican.model.cc.gui.GUIDefinition');	

	
--
-- Panel-Definitionen fuer die automatisierte Eingabe von Carrier-LBZ
-- LBZ-Panel fuer Carrier 'DTAG'
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ACTIVE) 
	VALUES (400, "de.augustakom.hurrican.gui.auftrag.carrier.CarrierLbzDTAGPanel", "PANEL", 
	"lbz.panel.dtag", "LBZ-DTAG", null, null, 1);
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (400, 12, "de.augustakom.hurrican.model.cc.Carrier");
	
	
