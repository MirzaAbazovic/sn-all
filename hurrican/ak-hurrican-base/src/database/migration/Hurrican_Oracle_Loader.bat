rem
rem Script, um die MySQL Data-Files nach Oracle zu importieren.
rem Die SQL-Files (Daten-Files) muessen im gleichen Verzeichnis wie
rem die zugehoerigen CTL-Files liegen. 
rem Zu jeder Tabelle wird ein Log-File im Sub-Directory '.\log' angelegt.
rem

REM SET NLS_DATE_FORMAT=Mon dd YYYY  HH:mi:ssAM
REM SET NLS_TIMESTAMP_FORMAT=Mon dd YYYY  HH:mi:ss:ffAM
REM SET NLS_LANGUAGE=<insert the language of your database e.g. US7ASCII> 

SET NLS_DATE_FORMAT=YYYY-MM-DD HH:mi:ss
SET NLS_TIMESTAMP_FORMAT=YYYY-MM-DD HH:mi:ss

sqlldr HURR/hurrican@test control=T_ABTEILUNG.ctl log=.\log\T_ABTEILUNG.log direct=true
sqlldr HURR/hurrican@test control=T_ACC_ART.ctl log=.\log\T_ACC_ART.log direct=true
sqlldr HURR/hurrican@test control=T_AK0800.ctl log=.\log\T_AK0800.log direct=true
sqlldr HURR/hurrican@test control=T_AK0800_MODELL.ctl log=.\log\T_AK0800_MODELL.log
sqlldr HURR/hurrican@test control=T_ANBINDUNGSART.ctl log=.\log\T_ANBINDUNGSART.log direct=true
sqlldr HURR/hurrican@test control=T_ANREDE.ctl log=.\log\T_ANREDE.log direct=true
sqlldr HURR/hurrican@test control=T_ANSCHLUSSART.ctl log=.\log\T_ANSCHLUSSART.log direct=true
sqlldr HURR/hurrican@test control=T_AUFTRAG.ctl log=.\log\T_AUFTRAG.log direct=true
sqlldr HURR/hurrican@test control=T_AUFTRAG_DATEN.ctl log=.\log\T_AUFTRAG_DATEN.log
sqlldr HURR/hurrican@test control=T_AUFTRAG_FAKTURA.ctl log=.\log\T_AUFTRAG_FAKTURA.log direct=true
sqlldr HURR/hurrican@test control=T_AUFTRAG_IMPORT.ctl log=.\log\T_AUFTRAG_IMPORT.log
sqlldr HURR/hurrican@test control=T_AUFTRAG_IMPORT_FILES.ctl log=.\log\T_AUFTRAG_IMPORT_FILES.log
sqlldr HURR/hurrican@test control=T_AUFTRAG_IMPORT_STATUS.ctl log=.\log\T_AUFTRAG_IMPORT_STATUS.log direct=true
sqlldr HURR/hurrican@test control=T_AUFTRAG_STATUS.ctl log=.\log\T_AUFTRAG_STATUS.log direct=true
sqlldr HURR/hurrican@test control=T_AUFTRAG_TECHNIK.ctl log=.\log\T_AUFTRAG_TECHNIK.log direct=true
sqlldr HURR/hurrican@test control=T_AUFTRAG_TECHNIK_2_ENDSTELLE.ctl log=.\log\T_AUFTRAG_TECHNIK_2_ENDSTELLE.log direct=true
sqlldr HURR/hurrican@test control=T_BANKVERBINDUNG.ctl log=.\log\T_BANKVERBINDUNG.log direct=true
sqlldr HURR/hurrican@test control=T_BA_VERL_AEND.ctl log=.\log\T_BA_VERL_AEND.log direct=true
sqlldr HURR/hurrican@test control=T_BA_VERL_AEND_GRUPPE.ctl log=.\log\T_BA_VERL_AEND_GRUPPE.log direct=true
sqlldr HURR/hurrican@test control=T_BA_VERL_AEND_PROD_2_GRUPPE.ctl log=.\log\T_BA_VERL_AEND_PROD_2_GRUPPE.log direct=true
sqlldr HURR/hurrican@test control=T_BA_VERL_HIST.ctl log=.\log\T_BA_VERL_HIST.log direct=true
sqlldr HURR/hurrican@test control=T_BA_VERL_NEU.ctl log=.\log\T_BA_VERL_NEU.log direct=true
sqlldr HURR/hurrican@test control=T_BA_VERL_NEU_ZUSATZ.ctl log=.\log\T_BA_VERL_NEU_ZUSATZ.log direct=true
sqlldr HURR/hurrican@test control=T_BILLING_LEISTUNG_KONFIG.ctl log=.\log\T_BILLING_LEISTUNG_KONFIG.log
sqlldr HURR/hurrican@test control=T_CARRIER.ctl log=.\log\T_CARRIER.log direct=true
sqlldr HURR/hurrican@test control=T_CARRIERBESTELLUNG.ctl log=.\log\T_CARRIERBESTELLUNG.log direct=true
sqlldr HURR/hurrican@test control=T_CARRIER_KENNUNG.ctl log=.\log\T_CARRIER_KENNUNG.log direct=true
sqlldr HURR/hurrican@test control=T_CARRIER_LBZ.ctl log=.\log\T_CARRIER_LBZ.log
sqlldr HURR/hurrican@test control=T_CB_2_ES.ctl log=.\log\T_CB_2_ES.log direct=true
sqlldr HURR/hurrican@test control=T_COUNTER.ctl log=.\log\T_COUNTER.log direct=true
sqlldr HURR/hurrican@test control=T_DB_QUERIES.ctl log=.\log\T_DB_QUERIES.log
sqlldr HURR/hurrican@test control=T_DIALER.ctl log=.\log\T_DIALER.log direct=true
sqlldr HURR/hurrican@test control=T_DSLAM.ctl log=.\log\T_DSLAM.log direct=true
sqlldr HURR/hurrican@test control=T_EG_HERKUNFT.ctl log=.\log\T_EG_HERKUNFT.log direct=true
sqlldr HURR/hurrican@test control=T_EG_TYP.ctl log=.\log\T_EG_TYP.log direct=true
sqlldr HURR/hurrican@test control=T_ENDGERAET.ctl log=.\log\T_ENDGERAET.log direct=true
sqlldr HURR/hurrican@test control=T_ENDSTELLE.ctl log=.\log\T_ENDSTELLE.log direct=true
sqlldr HURR/hurrican@test control=T_EQUIPMENT.ctl log=.\log\T_EQUIPMENT.log
sqlldr HURR/hurrican@test control=T_EQ_DATEN.ctl log=.\log\T_EQ_DATEN.log direct=true
sqlldr HURR/hurrican@test control=T_EQ_GEBAEUDE.ctl log=.\log\T_EQ_GEBAEUDE.log direct=true
sqlldr HURR/hurrican@test control=T_EQ_OBJEKT.ctl log=.\log\T_EQ_OBJEKT.log direct=true
sqlldr HURR/hurrican@test control=T_ES_ANSP.ctl log=.\log\T_ES_ANSP.log direct=true
sqlldr HURR/hurrican@test control=T_ES_LTG_DATEN.ctl log=.\log\T_ES_LTG_DATEN.log direct=true
sqlldr HURR/hurrican@test control=T_E_RECHNUNG_DRUCK.ctl log=.\log\T_E_RECHNUNG_DRUCK.log direct=true
sqlldr HURR/hurrican@test control=T_FAKTURA.ctl log=.\log\T_FAKTURA.log direct=true
sqlldr HURR/hurrican@test control=T_FAKTURA_BEARBEITER.ctl log=.\log\T_FAKTURA_BEARBEITER.log direct=true
sqlldr HURR/hurrican@test control=T_FAKTURA_MONAT.ctl log=.\log\T_FAKTURA_MONAT.log direct=true
sqlldr HURR/hurrican@test control=T_FAKTURA_NO.ctl log=.\log\T_FAKTURA_NO.log direct=true
sqlldr HURR/hurrican@test control=T_FAKTURA_NR.ctl log=.\log\T_FAKTURA_NR.log direct=true
sqlldr HURR/hurrican@test control=T_GUI_DEFINITION.ctl log=.\log\T_GUI_DEFINITION.log
sqlldr HURR/hurrican@test control=T_GUI_MAPPING.ctl log=.\log\T_GUI_MAPPING.log
sqlldr HURR/hurrican@test control=T_HVT_BESTELLUNG.ctl log=.\log\T_HVT_BESTELLUNG.log direct=true
sqlldr HURR/hurrican@test control=T_HVT_BES_HIST.ctl log=.\log\T_HVT_BES_HIST.log direct=true
sqlldr HURR/hurrican@test control=T_HVT_GRUPPE.ctl log=.\log\T_HVT_GRUPPE.log direct=true
sqlldr HURR/hurrican@test control=T_HVT_STANDORT.ctl log=.\log\T_HVT_STANDORT.log direct=true
sqlldr HURR/hurrican@test control=T_HVT_STANDORT_2_TECHNIK.ctl log=.\log\T_HVT_STANDORT_2_TECHNIK.log direct=true
sqlldr HURR/hurrican@test control=T_HVT_TECHNIK.ctl log=.\log\T_HVT_TECHNIK.log
sqlldr HURR/hurrican@test control=T_INHOUSE.ctl log=.\log\T_INHOUSE.log
sqlldr HURR/hurrican@test control=T_INT_ACCOUNT.ctl log=.\log\T_INT_ACCOUNT.log
sqlldr HURR/hurrican@test control=T_INT_DRUCK.ctl log=.\log\T_INT_DRUCK.log direct=true
sqlldr HURR/hurrican@test control=T_KUBENA.ctl log=.\log\T_KUBENA.log direct=true
sqlldr HURR/hurrican@test control=T_KUBENA_HVT.ctl log=.\log\T_KUBENA_HVT.log direct=true
sqlldr HURR/hurrican@test control=T_KUBENA_PROD.ctl log=.\log\T_KUBENA_PROD.log direct=true
sqlldr HURR/hurrican@test control=T_KUBENA_TDN.ctl log=.\log\T_KUBENA_TDN.log direct=true
sqlldr HURR/hurrican@test control=T_LB_2_LEISTUNG.ctl log=.\log\T_LB_2_LEISTUNG.log direct=true
sqlldr HURR/hurrican@test control=T_LB_2_PRODUKT.ctl log=.\log\T_LB_2_PRODUKT.log direct=true
sqlldr HURR/hurrican@test control=T_LEISTUNGSBUENDEL.ctl log=.\log\T_LEISTUNGSBUENDEL.log
sqlldr HURR/hurrican@test control=T_LEISTUNG_2_PARAMETER.ctl log=.\log\T_LEISTUNG_2_PARAMETER.log direct=true
sqlldr HURR/hurrican@test control=T_LEISTUNG_4_DN.ctl log=.\log\T_LEISTUNG_4_DN.log
sqlldr HURR/hurrican@test control=T_LEISTUNG_DN.ctl log=.\log\T_LEISTUNG_DN.log direct=true
sqlldr HURR/hurrican@test control=T_LEISTUNG_PARAMETER.ctl log=.\log\T_LEISTUNG_PARAMETER.log
sqlldr HURR/hurrican@test control=T_LEISTUNG_SNAPSHOT.ctl log=.\log\T_LEISTUNG_SNAPSHOT.log direct=true
sqlldr HURR/hurrican@test control=T_LEITUNGSART.ctl log=.\log\T_LEITUNGSART.log direct=true
sqlldr HURR/hurrican@test control=T_MONTAGEART.ctl log=.\log\T_MONTAGEART.log direct=true
sqlldr HURR/hurrican@test control=T_NIEDERLASSUNG.ctl log=.\log\T_NIEDERLASSUNG.log direct=true
sqlldr HURR/hurrican@test control=T_PHYSIKAENDERUNGSTYP.ctl log=.\log\T_PHYSIKAENDERUNGSTYP.log direct=true
sqlldr HURR/hurrican@test control=T_PHYSIKTYP.ctl log=.\log\T_PHYSIKTYP.log direct=true
sqlldr HURR/hurrican@test control=T_PHYSIKUEBERNAHME.ctl log=.\log\T_PHYSIKUEBERNAHME.log direct=true
sqlldr HURR/hurrican@test control=T_PORTIERUNGSART.ctl log=.\log\T_PORTIERUNGSART.log direct=true
sqlldr HURR/hurrican@test control=T_PORT_GESAMT.ctl log=.\log\T_PORT_GESAMT.log direct=true
sqlldr HURR/hurrican@test control=T_PRODUKT.ctl log=.\log\T_PRODUKT.log direct=true
sqlldr HURR/hurrican@test control=T_PRODUKTGRUPPE.ctl log=.\log\T_PRODUKTGRUPPE.log direct=true
sqlldr HURR/hurrican@test control=T_PRODUKT_2_PHYSIKTYP.ctl log=.\log\T_PRODUKT_2_PHYSIKTYP.log direct=true
sqlldr HURR/hurrican@test control=T_PRODUKT_2_SCHNITTSTELLE.ctl log=.\log\T_PRODUKT_2_SCHNITTSTELLE.log direct=true
sqlldr HURR/hurrican@test control=T_PROD_2_PROD.ctl log=.\log\T_PROD_2_PROD.log
sqlldr HURR/hurrican@test control=T_PROD_2_SL.ctl log=.\log\T_PROD_2_SL.log direct=true
sqlldr HURR/hurrican@test control=T_PROD_SL_FREIGABE.ctl log=.\log\T_PROD_SL_FREIGABE.log direct=true
sqlldr HURR/hurrican@test control=T_PVC_2_PROFIL.ctl log=.\log\T_PVC_2_PROFIL.log direct=true
sqlldr HURR/hurrican@test control=T_PVC_VORGABE.ctl log=.\log\T_PVC_VORGABE.log direct=true
sqlldr HURR/hurrican@test control=T_RANGIERUNG.ctl log=.\log\T_RANGIERUNG.log
sqlldr HURR/hurrican@test control=T_RANGIERUNGSMATRIX.ctl log=.\log\T_RANGIERUNGSMATRIX.log direct=true
sqlldr HURR/hurrican@test control=T_RECHNUNGSANSCHRIFT.ctl log=.\log\T_RECHNUNGSANSCHRIFT.log direct=true
sqlldr HURR/hurrican@test control=T_REFERENCE.ctl log=.\log\T_REFERENCE.log
sqlldr HURR/hurrican@test control=T_REGISTRY.ctl log=.\log\T_REGISTRY.log
sqlldr HURR/hurrican@test control=T_SCHNITTSTELLE.ctl log=.\log\T_SCHNITTSTELLE.log direct=true
sqlldr HURR/hurrican@test control=T_SDH_PHYSIK.ctl log=.\log\T_SDH_PHYSIK.log direct=true
sqlldr HURR/hurrican@test control=T_SDH_PROFIL.ctl log=.\log\T_SDH_PROFIL.log direct=true
sqlldr HURR/hurrican@test control=T_SERVICECHAIN_2_COMMAND.ctl log=.\log\T_SERVICECHAIN_2_COMMAND.log direct=true
sqlldr HURR/hurrican@test control=T_SERVICE_CHAIN.ctl log=.\log\T_SERVICE_CHAIN.log
sqlldr HURR/hurrican@test control=T_SERVICE_COMMANDS.ctl log=.\log\T_SERVICE_COMMANDS.log
sqlldr HURR/hurrican@test control=T_SPERRE.ctl log=.\log\T_SPERRE.log direct=true
sqlldr HURR/hurrican@test control=T_SPERRE_INFO.ctl log=.\log\T_SPERRE_INFO.log direct=true
sqlldr HURR/hurrican@test control=T_SPERRE_VERTEILUNG.ctl log=.\log\T_SPERRE_VERTEILUNG.log direct=true
sqlldr HURR/hurrican@test control=T_SPERRKLASSEN.ctl log=.\log\T_SPERRKLASSEN.log direct=true
sqlldr HURR/hurrican@test control=T_STRASSENLISTE.ctl log=.\log\T_STRASSENLISTE.log direct=true
sqlldr HURR/hurrican@test control=T_TAL_AENDERUNGSTYP.ctl log=.\log\T_TAL_AENDERUNGSTYP.log
sqlldr HURR/hurrican@test control=T_TDN.ctl log=.\log\T_TDN.log direct=true
sqlldr HURR/hurrican@test control=T_TDN_ZAEHLER.ctl log=.\log\T_TDN_ZAEHLER.log direct=true
sqlldr HURR/hurrican@test control=T_UEVT.ctl log=.\log\T_UEVT.log direct=true
sqlldr HURR/hurrican@test control=T_UEVT_2_ZIEL.ctl log=.\log\T_UEVT_2_ZIEL.log direct=true
sqlldr HURR/hurrican@test control=T_VERLAUF.ctl log=.\log\T_VERLAUF.log
sqlldr HURR/hurrican@test control=T_VERLAUF_ABTEILUNG.ctl log=.\log\T_VERLAUF_ABTEILUNG.log
sqlldr HURR/hurrican@test control=T_VERLAUF_ACTIONS.ctl log=.\log\T_VERLAUF_ACTIONS.log direct=true
sqlldr HURR/hurrican@test control=T_VERLAUF_STATUS.ctl log=.\log\T_VERLAUF_STATUS.log direct=true
sqlldr HURR/hurrican@test control=T_VPN.ctl log=.\log\T_VPN.log direct=true
sqlldr HURR/hurrican@test control=T_VPN_KONF.ctl log=.\log\T_VPN_KONF.log direct=true
sqlldr HURR/hurrican@test control=T_WOHNHEIM.ctl log=.\log\T_WOHNHEIM.log direct=true
sqlldr HURR/hurrican@test control=T_ZUGANG.ctl log=.\log\T_ZUGANG.log direct=true
sqlldr HURR/hurrican@test control=WISSEN.ctl log=.\log\WISSEN.log direct=true
