--
-- SQL-Script, um nach der Tabellengenerierung durch DML noch
-- Modifikationen vorzunehmen.
--

-- CHAR auf VARCHAR aendern
alter table T_PHYSIKTYP modify ( NAME VARCHAR2(50) );
alter table T_PHYSIKTYP modify ( BESCHREIBUNG VARCHAR2(100) );

alter table T_SDH_PHYSIK modify ( VPI_A VARCHAR2(50) NULL );
alter table T_SDH_PHYSIK modify ( VCI_A VARCHAR2(50) NULL );
alter table T_SDH_PHYSIK modify ( VPI_B VARCHAR2(50) NULL );
alter table T_SDH_PHYSIK modify ( VCI_B VARCHAR2(50) NULL );
alter table T_SDH_PHYSIK modify ( ENDPUNKT VARCHAR2(50) NULL );
alter table T_SDH_PHYSIK modify ( BEZEICHNUNG VARCHAR2(50) NULL );

alter table T_DSLAM modify ( IP_ADDRESS VARCHAR2(20) );
alter table T_DSLAM modify ( EINBAUPLATZ VARCHAR2(10) );
alter table T_DSLAM modify ( ANSCHLUSS VARCHAR2(25) );
alter table T_DSLAM modify ( ANS_SLOT_PORT VARCHAR2(25) );
alter table T_DSLAM modify ( ANS_ART VARCHAR2(25) );
alter table T_DSLAM modify ( VERSION VARCHAR2(20) );

alter table T_EQUIPMENT modify ( CARRIER VARCHAR2(50) );
alter table T_EQUIPMENT modify ( STATUS VARCHAR2(50) );
alter table T_EQUIPMENT modify ( BEMERKUNG VARCHAR2(255) );
alter table T_EQUIPMENT modify ( USERW VARCHAR2(15) );
alter table T_EQUIPMENT modify ( RANG_SS_TYPE VARCHAR2(10) );
alter table T_EQUIPMENT modify ( UETV VARCHAR2(100) );
alter table T_EQUIPMENT modify ( SWITCH VARCHAR2(10) );
alter table T_EQUIPMENT modify ( HW_PORT VARCHAR2(10) );
alter table T_EQUIPMENT modify ( HW_EQN VARCHAR2(40) );
alter table T_EQUIPMENT modify ( HW_SCHNITTSTELLE VARCHAR2(50) );
alter table T_EQUIPMENT modify ( TS1 VARCHAR2(40) );
alter table T_EQUIPMENT modify ( TS2 VARCHAR2(40) );
alter table T_EQUIPMENT modify ( RANG_VERTEILER VARCHAR2(40) );
alter table T_EQUIPMENT modify ( RANG_REIHE VARCHAR2(40) );
alter table T_EQUIPMENT modify ( RANG_BUCHT VARCHAR2(40) );
alter table T_EQUIPMENT modify ( RANG_LEISTE1 VARCHAR2(40) );
alter table T_EQUIPMENT modify ( RANG_STIFT1 VARCHAR2(40) );
alter table T_EQUIPMENT modify ( RANG_LEISTE2 VARCHAR2(40) );
alter table T_EQUIPMENT modify ( RANG_STIFT2 VARCHAR2(10) );
alter table T_EQUIPMENT modify ( RANG_SCHNITTSTELLE VARCHAR2(50) );

alter table T_RANGIERUNG modify ( LEITUNGSNR_EXTERN VARCHAR2(9) );
alter table T_RANGIERUNG modify ( BEMERKUNG VARCHAR2(255) );
alter table T_RANGIERUNG modify ( USERW VARCHAR2(15) );
alter table T_RANGIERUNG modify ( LNR_LFD VARCHAR2(4) );

alter table T_CARRIERBESTELLUNG modify ( LBZ VARCHAR2(30) );
alter table T_CARRIERBESTELLUNG modify ( VTRNR VARCHAR2(25) );
alter table T_CARRIERBESTELLUNG modify ( AQS VARCHAR2(70) );
alter table T_CARRIERBESTELLUNG modify ( LL VARCHAR2(70) );
alter table T_CARRIERBESTELLUNG modify ( NEGATIVERM VARCHAR2(50) );

alter table T_FAKTURA_MONAT modify ( RECHNUNGSTEXT VARCHAR2(25) );

alter table T_LEITUNGSART modify ( NAME VARCHAR2(30) );
alter table T_SCHNITTSTELLE modify ( SCHNITTSTELLE VARCHAR2(20) );
alter table T_MONTAGEART modify ( NAME VARCHAR2(30) );
alter table T_SDH_PROFIL modify ( NAME VARCHAR2(50) );
alter table T_PRODUKTGRUPPE modify ( PRODUKTGRUPPE VARCHAR2(30) );
alter table T_ANSCHLUSSART modify ( ANSCHLUSSART VARCHAR2(30) );
alter table T_COUNTER modify ( COUNTER VARCHAR2(25) );

alter table T_AUFTRAG_TECHNIK modify ( ANBINDUNGSART VARCHAR2(30) );

alter table T_PHYSIKUEBERNAHME modify ( KRITERIUM VARCHAR2(10) );

alter table T_RECHNUNGSANSCHRIFT modify ( LKZ VARCHAR2(2) );

alter table T_AUFTRAG_IMPORT_FILES modify ( FILE_TYPE NUMBER(1) );

-- Datentyp von TEXT auf VARCHAR2 aendern
alter table T_SERVICE_COMMANDS drop column DESCRIPTION;
alter table T_SERVICE_COMMANDS add DESCRIPTION VARCHAR2(1000);

alter table T_LEISTUNG_DN drop column LEISTUNG_PARAMETER;
alter table T_LEISTUNG_DN add LEISTUNG_PARAMETER VARCHAR2(1000);

alter table T_PORT_FORWARD drop column BEMERKUNG;
alter table T_PORT_FORWARD add BEMERKUNG VARCHAR2(1000);

alter table T_REPORT_DATA drop column KEY_VALUE;
alter table T_REPORT_DATA add KEY_VALUE VARCHAR2(4000);

alter table T_REPORT_GRUPPE drop column DESCRIPTION;
alter table T_REPORT_GRUPPE add DESCRIPTION VARCHAR2(255);

alter table T_REPORT_REASON drop column DESCRIPTION;
alter table T_REPORT_REASON add DESCRIPTION VARCHAR2(255);

alter table T_REPORT_REQUEST drop column ERROR;
alter table T_REPORT_REQUEST add ERROR VARCHAR2(4000);

alter table T_TXT_BAUSTEIN drop column TEXT;
alter table T_TXT_BAUSTEIN add TEXT VARCHAR2(4000);

alter table T_TXT_BAUSTEIN_GRUPPE drop column DESCRIPTION;
alter table T_TXT_BAUSTEIN_GRUPPE add DESCRIPTION VARCHAR2(255);



alter table T_ARCHIV_DATEN drop column TDN;
alter table T_ARCHIV_DATEN add TDN VARCHAR2(4000);

alter table T_ARCHIV_DATEN drop column AUFTRAG;
alter table T_ARCHIV_DATEN add AUFTRAG VARCHAR2(4000);

alter table T_ARCHIV_DATEN drop column AUFTRAG_VORSATZ;
alter table T_ARCHIV_DATEN add AUFTRAG_VORSATZ VARCHAR2(4000);

alter table T_ARCHIV_DATEN drop column KUNDENTYP;
alter table T_ARCHIV_DATEN add KUNDENTYP VARCHAR2(4000);

alter table T_ARCHIV_DATEN drop column MATCHCODE;
alter table T_ARCHIV_DATEN add MATCHCODE VARCHAR2(4000);

alter table T_ARCHIV_DATEN drop column EXT_DEBITOR_ID;
alter table T_ARCHIV_DATEN add EXT_DEBITOR_ID VARCHAR2(4000);

alter table T_ARCHIV_DATEN drop column NAME;
alter table T_ARCHIV_DATEN add NAME VARCHAR2(4000);

alter table T_ARCHIV_DATEN drop column VORNAME;
alter table T_ARCHIV_DATEN add VORNAME VARCHAR2(4000);

alter table T_ARCHIV_DATEN drop column STRASSE;
alter table T_ARCHIV_DATEN add STRASSE VARCHAR2(4000);

alter table T_ARCHIV_DATEN drop column NUMMER;
alter table T_ARCHIV_DATEN add NUMMER VARCHAR2(4000);

alter table T_ARCHIV_DATEN drop column PLZ;
alter table T_ARCHIV_DATEN add PLZ VARCHAR2(4000);

alter table T_ARCHIV_DATEN drop column ORT;
alter table T_ARCHIV_DATEN add ORT VARCHAR2(4000);

alter table T_ARCHIV_DATEN drop column HAENDLER__NO;
alter table T_ARCHIV_DATEN add HAENDLER__NO VARCHAR2(4000);

alter table T_ARCHIV_DATEN drop column DESCRIPTION;
alter table T_ARCHIV_DATEN add DESCRIPTION VARCHAR2(4000);

alter table T_ARCHIV_DATEN drop column PRODUKT;
alter table T_ARCHIV_DATEN add PRODUKT VARCHAR2(4000);

alter table T_ARCHIV_DATEN drop column BELEGART;
alter table T_ARCHIV_DATEN add BELEGART VARCHAR2(4000);

alter table T_ARCHIV_DATEN drop column STATUS;
alter table T_ARCHIV_DATEN add STATUS VARCHAR2(4000);

alter table T_ARCHIV_DATEN drop column R_INFO__NO;
alter table T_ARCHIV_DATEN add R_INFO__NO VARCHAR2(4000);

alter table T_ARCHIV_DATEN drop column SAP_AUFTRAG_NO;
alter table T_ARCHIV_DATEN add SAP_AUFTRAG_NO VARCHAR2(4000);

commit;


alter table T_ABTEILUNG modify (RELEVANT_4_PROJ CHAR(1));
alter table T_ABTEILUNG modify (RELEVANT_4_BA CHAR(1));

alter table T_PRODUKT modify (DN_BLOCK CHAR(1));
alter table T_PRODUKT modify (BRAUCHT_DN4ACCOUNT CHAR(1));
alter table T_PRODUKT modify (ELVERLAUF CHAR(1));
alter table T_PRODUKT modify (VPN_PHYSIK CHAR(1));
alter table T_PRODUKT modify (PROJEKTIERUNG CHAR(1));
alter table T_PRODUKT modify (IS_PARENT CHAR(1));
alter table T_PRODUKT modify (CHECK_CHILD CHAR(1));
alter table T_PRODUKT modify (IS_COMBI_PRODUKT CHAR(1));
alter table T_PRODUKT modify (AUTO_PRODUCT_CHANGE CHAR(1));
alter table T_PRODUKT modify (EXPORT_KDP_M CHAR(1));
alter table T_PRODUKT modify (CREATE_KDP_ACCOUNT_REPORT CHAR(1));
alter table T_PRODUKT modify (EXPORT_AK_PRODUKTE CHAR(1));
alter table T_PRODUKT modify (BA_RUECKLAEUFER CHAR(1));
alter table T_PRODUKT modify (BRAUCHT_VPI_VCI CHAR(1));
alter table T_PRODUKT modify (ABRECHNUNG_IN_HURRICAN CHAR(1));
alter table T_PRODUKT modify (BRAUCHT_BUENDEL CHAR(1));
alter table T_PRODUKT modify (LTGNR_ANLEGEN CHAR(1));
alter table T_PRODUKT modify (AUFTRAGSERSTELLUNG CHAR(1));
alter table T_PRODUKT modify (BRAUCHT_DN CHAR(1));
alter table T_PRODUKT modify (DN_MOEGLICH CHAR(1));

alter table T_AK0800 modify (UEBERNAHME CHAR(1));
alter table T_AK0800 modify (NEUSCHALTUNG CHAR(1));

alter table T_AUFTRAG_DATEN modify (TELBUCH CHAR(1));
alter table T_AUFTRAG_DATEN modify (INVERSSUCHE CHAR(1));

alter table T_AUFTRAG_TECHNIK modify (PROJEKTIERUNG CHAR(1));

alter table T_BA_VERL_ANLASS modify (IS_CONFIGURABLE CHAR(1));
alter table T_BA_VERL_ANLASS modify (IS_AUFTRAGSART CHAR(1));
alter table T_BA_VERL_ANLASS modify (AKT CHAR(1));

alter table T_BA_ZUSATZ modify (AUCH_SM CHAR(1));

alter table T_CARRIER modify (CB_NOTWENDIG CHAR(1));

alter table T_CARRIERBESTELLUNG modify (KUNDE_VOR_ORT CHAR(1));

alter table T_CB_USECASE modify (ACTIVE CHAR(1));

alter table T_DSLAM modify (SCHMIDTSCHES_KONZEPT CHAR(1));

alter table T_DSLAM_PROFILE modify (FASTPATH CHAR(1));

alter table T_EG modify (CONFIGURABLE CHAR(1));
alter table T_EG modify (CONF_PORTFORWARDING CHAR(1));
alter table T_EG modify (CONF_S0BACKUP CHAR(1));

alter table T_EG_2_AUFTRAG modify (SELBSTABHOLUNG CHAR(1));

alter table T_EG_CONFIG modify (NAT CHAR(1));
alter table T_EG_CONFIG modify (DHCP CHAR(1));

alter table T_EQ_DATEN modify (NOTSTROM CHAR(1));
alter table T_EQ_DATEN modify (KLIMAANLAGE CHAR(1));
alter table T_EQ_DATEN modify (ZUGANG_24H CHAR(1));

alter table T_FAKTURA_BEARBEITER modify (AKT CHAR(1));

alter table T_GUI_DEFINITION modify (ADD_SEPARATOR CHAR(1));
alter table T_GUI_DEFINITION modify (ACTIVE CHAR(1));

alter table T_HVT_STANDORT modify (VIRTUELL CHAR(1));

alter table T_IA_BUDGET modify (STORNIERT CHAR(1));

alter table T_IA_MAT_ENTNAHME modify (ENTNAHMETYP CHAR(1));

alter table T_INT_ACCOUNT modify (GESPERRT CHAR(1));

alter table T_KUBENA_TDN modify (VORH CHAR(1));

alter table T_LB_2_LEISTUNG modify (STANDARD CHAR(1));

alter table T_PHYSIKAENDERUNGSTYP modify (EWSD CHAR(1));
alter table T_PHYSIKAENDERUNGSTYP modify (SDH CHAR(1));
alter table T_PHYSIKAENDERUNGSTYP modify (IPS CHAR(1));
alter table T_PHYSIKAENDERUNGSTYP modify (SCT CHAR(1));

alter table T_PORT_FORWARD modify (ACTIVE CHAR(1));

alter table T_PRODUKT_2_PHYSIKTYP modify (VIRTUELL CHAR(1));

alter table T_PROD_2_EG modify (IS_DEFAULT CHAR(1));

alter table T_PROD_2_TECH_LEISTUNG modify (IS_DEFAULT CHAR(1));

alter table T_PROD_SL_FREIGABE modify (POSSIBLE CHAR(1));

alter table T_PVC_VORGABE modify (IST_VATER CHAR(1));

alter table T_RANGIERUNGSMATRIX modify (PROJEKTIERUNG CHAR(1));

alter table T_RECHNUNGSANSCHRIFT modify (EINZEL_RE CHAR(1));

alter table T_REFERENCE modify (GUI_VISIBLE CHAR(1));

alter table T_SDH_PROFIL modify (VPIVCI_ALLOWED CHAR(1));

alter table T_SPERRE_INFO modify (ACTIVE CHAR(1));

alter table T_SPERRKLASSEN modify (ABGEHEND CHAR(1));
alter table T_SPERRKLASSEN modify (NATIONAL CHAR(1));
alter table T_SPERRKLASSEN modify (INNOVATIVE_DIENSTE CHAR(1));
alter table T_SPERRKLASSEN modify (MABEZ CHAR(1));
alter table T_SPERRKLASSEN modify (MOBIL CHAR(1));
alter table T_SPERRKLASSEN modify (VPN CHAR(1));
alter table T_SPERRKLASSEN modify (PRD CHAR(1));
alter table T_SPERRKLASSEN modify (AUSKUNFTSDIENSTE CHAR(1));
alter table T_SPERRKLASSEN modify (INTERNATIONAL CHAR(1));
alter table T_SPERRKLASSEN modify (ANSICHT CHAR(1));
alter table T_SPERRKLASSEN modify (OFF_LINE CHAR(1));

alter table T_TECH_LEISTUNG modify (DISPO CHAR(1));
alter table T_TECH_LEISTUNG modify (EWSD CHAR(1));
alter table T_TECH_LEISTUNG modify (SDH CHAR(1));
alter table T_TECH_LEISTUNG modify (IPS CHAR(1));
alter table T_TECH_LEISTUNG modify (SCT CHAR(1));
alter table T_TECH_LEISTUNG modify (CHECK_QUANTITY CHAR(1));
alter table T_TECH_LEISTUNG modify (SNAPSHOT_REL CHAR(1));

alter table T_UEVT modify (PROJEKTIERUNG CHAR(1));

alter table T_VERLAUF modify (AKT CHAR(1));
alter table T_VERLAUF modify (VERSCHOBEN CHAR(1));
alter table T_VERLAUF modify (OBSERVE_PROCESS CHAR(1));

alter table T_VPN_KONF modify (KANALB CHAR(1));
alter table T_VPN_KONF modify (DIAL_OUT CHAR(1));

alter table T_AUFTRAG_VOIP modify (IS_ACTIVE CHAR(1));

alter table T_CB_VORGANG modify (RET_OK CHAR(1));

alter table T_ARCHIV_PARAMETER_2_OBJECT modify (IS_DEFAULT CHAR(1));
alter table T_ARCHIV_PARAMETER_2_OBJECT modify (IS_APPEND_KEY CHAR(1));
alter table T_ARCHIV_PARAMETER_2_OBJECT modify (MANDATORY CHAR(1));

alter table T_PRINTER modify (DUPLEX_CAPABLE CHAR(1));

alter table T_ANREDE modify (PERS_ANREDE CHAR(1));

alter table T_AUFTRAG_IMPORT modify (PORTIERUNG CHAR(1));
alter table T_AUFTRAG_IMPORT modify (TARIFWECHSEL CHAR(1));
alter table T_AUFTRAG_IMPORT modify (UMZUG CHAR(1));
alter table T_AUFTRAG_IMPORT modify (ACTIVE CHAR(1));

alter table T_AUFTRAG_IMPORT_STATUS modify (IMPORT_ACTIVE CHAR(1));

alter table T_ENDSTELLE modify (EXPORT_TAIFUN CHAR(1));

alter table T_EQUIPMENT modify (BAUGRUPPE_EINGEBAUT CHAR(1));

alter table T_LEISTUNG_DN modify (BILLING_CHECK CHAR(1));

alter table T_LIEFERSCHEIN modify (SOFORT_VERSAND CHAR(1));

alter table T_RANGIERUNG modify (LEITUNG_LOESCHEN CHAR(1));

alter table T_RANGIERUNGS_AUFTRAG modify (CANCELLED CHAR(1));

alter table T_REPORT modify (DUPLEX_DRUCK CHAR(1));

alter table T_REPORT_REASON modify (TO_ARCHIVE CHAR(1));
alter table T_REPORT_REASON modify (ONLY_NOT_ARCHIVED CHAR(1));

alter table T_TXT_BAUSTEIN_GRUPPE modify (MANDATORY CHAR(1));

alter table T_VERLAUF modify (PROJEKTIERUNG CHAR(1));
alter table T_VERLAUF modify (MANUELL_VERTEILT CHAR(1));

commit;

