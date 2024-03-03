-- aktiviert optimistisches locking f�r fehlende Tabellen

ALTER TABLE T_ARCHIV_AUSWAHL ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_ARCHIV_OBJECT ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_ARCHIV_PARAMETER_2_OBJECT ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_ARCHIV_POSSIBLE_PARAMETER ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_SERVICE_CHAIN ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_SERVICE_COMMANDS ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_SERVICE_COMMAND_MAPPING ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_CPS_DATA_CHAIN_CONFIG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_CPS_TX ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_CPS_TX_LOG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_CPS_TX_SUB_ORDERS ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_LB_2_LEISTUNG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_LEISTUNG_DN ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_LEISTUNG_4_DN ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_LEISTUNG_PARAMETER ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_LEISTUNGSBUENDEL ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_LB_2_PRODUKT ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_SPERRKLASSEN ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_E_RECHNUNG_DRUCK ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_FAKTURA ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_FAKTURA_BEARBEITER ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_FAKTURA_MONAT ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_FAKTURA_NO ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_FAKTURA_NR ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_GUI_DEFINITION ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_GUI_MAPPING ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_HW_BAUGRUPPE ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_HW_BAUGRUPPEN_TYP ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_HW_BG_TYP_2_PHYSIK_TYP ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_HW_RACK ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_HW_SUBRACK ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_HW_SUBRACK_TYP ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_IA ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_IA_BUDGET ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_IA_MATERIAL ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_IA_MAT_ENTNAHME ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_IA_MAT_ENTNAHME_ARTIKEL ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_RANGIERUNGS_MATERIAL ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_KUBENA ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_KUBENA_HVT ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_KUBENA_PROD ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_KUBENA_TDN ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_CB_CONFIG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
-- ALTER TABLE T_CB_CONFIG_DATA ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_CB_USECASE ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_CB_USECASE_2_CARRIER_KNG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_CB_VORGANG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_PRODUKT_DTAG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_TREE_DEFINITION ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_TREE_GUI_CONFIG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_ABTEILUNG_2_NIEDERLASSUNG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_ABTEILUNG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_ACC_ART ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_AK0800 ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_ANBINDUNGSART ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_ANREDE ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_ANSCHLUSSART ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_ANSPRECHPARTNER ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_AUFTRAG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_AUFTRAG_2_DSLAMPROFILE ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_AUFTRAG_2_TECH_LS ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_AUFTRAG_DATEN ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_AUFTRAG_FAKTURA ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_AUFTRAG_IMPORT ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_AUFTRAG_IMPORT_FILES ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_AUFTRAG_IMPORT_STATUS ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_AUFTRAG_INTERN ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_AUFTRAG_QOS ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_AUFTRAG_STATUS ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_AUFTRAG_TECHNIK ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_AUFTRAG_VOIP ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_AUFTRAG_VOIP_DN ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_BANKVERBINDUNG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_BA_ABT_CONFIG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_BA_ABTCONFIG_2_ABT ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_BA_VERL_AEND_GRUPPE ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_BA_VERL_ANLASS ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_ZUGANG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_WOHNHEIM ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_WEBSERVICE_CONFIG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_VPN_KONF ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_VPN ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_VERLAUF_STATUS ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_VERLAUF_ABTEILUNG_STATUS ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_VERLAUF_ABTEILUNG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_VERLAUF ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_UEVT_2_ZIEL ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_UEVT ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_TECH_LEISTUNG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_TDN ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_TAL_AENDERUNGSTYP ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_STRASSEN_DETAILS ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_STRASSENLISTE ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_SPERRE_INFO ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_SDH_PROFIL ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_SDH_PHYSIK ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_SCHNITTSTELLE ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_RS_MONITOR_RUN ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_RS_MONITOR_CONFIG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_REGISTRY ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_REFERENCE ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_RECHNUNGSANSCHRIFT ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_RANGIERUNGSMATRIX ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_RANGIERUNGS_AUFTRAG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_RANGIERUNG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_PVC_VORGABE ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_PROD_SL_FREIGABE ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_PRODUKT_MAPPING ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_PRODUKTGRUPPE ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_PRODUKT_EQ_CONFIG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_PROD_2_TECH_LEISTUNG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_PROD_2_SL ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_PROD_2_PROD ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_PRODUKT_2_PHYSIKTYP ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_PROD_2_EG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_PRODUKT ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_Port_Gesamt ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_PHYSIKUEBERNAHME ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_PHYSIKTYP ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_PHYSIKAENDERUNGSTYP ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_NIEDERLASSUNG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_MONTAGEART ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_LOCK_DETAIL ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_LOCK ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_LIEFERSCHEIN ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_LEITUNGSART ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_LAGER ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_INT_DRUCK ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_INT_ACCOUNT ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_INHOUSE ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_EG_IAD ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_HVT_TECHNIK ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_HVT_STANDORT_2_TECHNIK ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_HVT_STANDORT ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_HVT_RAUM ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_HVT_GRUPPE ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_HVT_BESTELLUNG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_HVT_BES_HIST ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_EXT_SERVICE_PROVIDER ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_EQUIPMENT ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_EQ_OBJEKT ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_EQ_GEBAEUDE ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_EQ_DATEN ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_EQ_CROSS_CONNECTION ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_ES_LTG_DATEN ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_ES_ANSP ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_ENDSTELLE ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_EG_HERKUNFT ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_EMERGENCYCALL_CONFIG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_EG_2_PAKET ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_EG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_DSLAM_PROFILE_CHANGE_REASON ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_DSLAM_PROFILE_CHANGE ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_DSLAM_PROFILE ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_DIALER ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_DB_QUERIES ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_COUNTER ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_CFG_REG_EXP ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_ADDRESS ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_CARRIER_MAPPING ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_CARRIER_LBZ ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_CARRIER_KENNUNG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_CARRIER_CONTACT ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_CARRIERBESTELLUNG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_CARRIER ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_BA_ZUSATZ ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE T_BA_VERL_CONFIG ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;

-- reporting tabellen
ALTER TABLE t_txt_baustein_gruppe ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE t_txt_baustein_2_gruppe ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE t_txt_baustein ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE t_report_template ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE t_report_request ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE t_report_reason ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE t_report_paperformat ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE t_report_gruppe ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE t_report_data ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE t_report_2_userrole ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE t_txt_baustein_gruppe_2_report ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE t_rep2prod_techls ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE t_rep2prod_stati ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE t_report_2_prod ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE t_report ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE t_printer_2_paper ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE t_printer ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;
ALTER TABLE t_archiv_daten ADD VERSION NUMBER(18) DEFAULT 0 NOT NULL;