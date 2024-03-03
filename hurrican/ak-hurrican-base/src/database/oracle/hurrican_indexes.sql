--
-- PrimaryKeys und Indexe erzeugen
--

ALTER TABLE T_ABTEILUNG ADD CONSTRAINT PK_T_ABTEILUNG PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_T_ABTEILUNG_NL
	ON T_ABTEILUNG (NIEDERLASSUNG_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_ACC_ART ADD CONSTRAINT PK_T_ACC_ART PRIMARY KEY (ID)
/
ALTER TABLE T_ACC_ART ADD CONSTRAINT UK_T_ACC_ART_LINR UNIQUE (LI_NR)
/
ALTER TABLE T_AK0800 ADD CONSTRAINT PK_T_AK0800 PRIMARY KEY (ID)
/
ALTER TABLE T_ADDRESS ADD CONSTRAINT PK_T_ADDRESS PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_ADDRESS_2_REF
	ON T_ADDRESS (ADDRESS_TYPE) TABLESPACE "I_HURRICAN"
/

CREATE INDEX IX_FK_AK0800_2_AUFTRAG
	ON T_AK0800 (AUFTRAG_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_T_AK0800_2_MODELL
	ON T_AK0800 (MODELL_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_AK0800_MODELL ADD CONSTRAINT PK_T_AK0800_MODELL PRIMARY KEY (ID)
/
ALTER TABLE T_AK0800_MODELL ADD CONSTRAINT UK_T_AK0800_MODELL_NAME UNIQUE (NAME)
/
ALTER TABLE T_ANBINDUNGSART ADD CONSTRAINT PK_T_ANBINDUNGSART PRIMARY KEY (ID)
/
ALTER TABLE T_ANREDE ADD CONSTRAINT PK_T_ANREDE PRIMARY KEY (ID)
/
ALTER TABLE T_ANREDE ADD CONSTRAINT UK_T_ANREDE UNIQUE (ANREDE_KEY, ANREDE_ART)
/
ALTER TABLE T_ANSCHLUSSART ADD CONSTRAINT PK_T_ANSCHLUSSART PRIMARY KEY (ID)
/
ALTER TABLE T_AUFTRAG ADD CONSTRAINT PK_T_AUFTRAG PRIMARY KEY (ID)
/

ALTER TABLE T_AUFTRAG_VOIP ADD CONSTRAINT PK_T_AUFTRAG_VOIP PRIMARY KEY (ID)
/

ALTER TABLE T_AUFTRAG_VOIP_DN ADD CONSTRAINT PK_T_AUFTRAG_VOIP_DN PRIMARY KEY (ID)
/

ALTER TABLE T_AUFTRAG_VOIP_DN ADD CONSTRAINT UK_DN__NO UNIQUE (DN__NO)
/

CREATE INDEX IX_FK_AUFTRAGVOIP_2_REF
    ON T_AUFTRAG_VOIP(EG_MODE) TABLESPACE "I_HURRICAN"
/

CREATE INDEX IX_FK_AUFTRAGVOIP_2_AUFTRAG
    ON T_AUFTRAG_VOIP(AUFTRAG_ID) TABLESPACE "I_HURRICAN"
/

CREATE INDEX IX_FK_AUFTRAG_KUNDE
	ON T_AUFTRAG (KUNDE__NO) TABLESPACE "I_HURRICAN"
/

ALTER TABLE T_AUFTRAG_2_DSLAMPROFILE ADD CONSTRAINT PK_T_AUFTRAG_2_DSLAMPROFILE PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_ADP_2_DSLAMPROF
	ON T_AUFTRAG_2_DSLAMPROFILE (DSLAM_PROFILE_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_ADP_2_AUFTRAG
	ON T_AUFTRAG_2_DSLAMPROFILE (AUFTRAG_ID) TABLESPACE "I_HURRICAN"
/

ALTER TABLE T_AUFTRAG_2_TECH_LS ADD CONSTRAINT PK_T_AUFTRAG_2_TECH_LS PRIMARY KEY (ID)
/
CREATE INDEX IX_ATLS_AID
	ON T_AUFTRAG_2_TECH_LS (AUFTRAG_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_ATLS_TLID
	ON T_AUFTRAG_2_TECH_LS (TECH_LS_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_ATLS_REAL_2_VERLAUF
	ON T_AUFTRAG_2_TECH_LS (VERLAUF_ID_REAL) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_ATLS_KUEND_2_VERLAUF
	ON T_AUFTRAG_2_TECH_LS (VERLAUF_ID_KUEND) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_AUFTRAG_DATEN ADD CONSTRAINT PK_T_AUFTRAG_DATEN PRIMARY KEY (ID)
/

CREATE INDEX IX_AUFTRAGDATEN_PRODAK
	ON T_AUFTRAG_DATEN (PRODAK_ORDER__NO) TABLESPACE "I_HURRICAN"
/

CREATE INDEX IX_AUFTRAGDATEN_BUENDELNO
	ON T_AUFTRAG_DATEN (BUENDEL_NR) TABLESPACE "I_HURRICAN"
/

CREATE INDEX IX_AUFTRAGDATEN_BUENDELH
	ON T_AUFTRAG_DATEN (BUENDEL_NR_HERKUNFT) TABLESPACE "I_HURRICAN"
/

CREATE INDEX IX_FK_AUFTRAGDATEN_2_AUFTRAG
	ON T_AUFTRAG_DATEN (AUFTRAG_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_AUFTRAG_DATEN_ASTATUS
	ON T_AUFTRAG_DATEN (STATUS_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_T_AUFTRAG_DATEN_PROD
	ON T_AUFTRAG_DATEN (PROD_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_ADMMZ_2_REF
	ON T_AUFTRAG_DATEN (MMZ_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_AUFTRAG_FAKTURA ADD CONSTRAINT PK_T_AUFTRAG_FAKTURA PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_AUFTRAG_2_RA
	ON T_AUFTRAG_FAKTURA (RA_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_AFAKTURA_2_AUFTRAG
	ON T_AUFTRAG_FAKTURA (AUFTRAG_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_AUFTRAG_IMPORT ADD CONSTRAINT PK_T_AUFTRAG_IMPORT PRIMARY KEY (ID)
/
ALTER TABLE T_AUFTRAG_IMPORT ADD CONSTRAINT UK_T_AUFTRAG_IMPORT_XML UNIQUE (XML_FILE)
/
ALTER TABLE T_AUFTRAG_IMPORT ADD CONSTRAINT UK_T_AUFTRAG_IMPORT_REF UNIQUE (REF_ID)
/
CREATE INDEX IX_FK_AUFTRAGIMPORT_2_STATUS
	ON T_AUFTRAG_IMPORT (IMPORT_STATUS) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_AUFTRAG_IMPORT_FILES ADD CONSTRAINT PK_T_AUFTRAG_IMPORT_FILES PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_T_AUFTRAG_IMPORT_FILES_1
	ON T_AUFTRAG_IMPORT_FILES (AUFTRAG_IMPORT_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_AUFTRAG_IMPORT_STATUS ADD CONSTRAINT PK_T_AUFTRAG_IMPORT_STATUS PRIMARY KEY (ID)
/
ALTER TABLE T_AUFTRAG_STATUS ADD CONSTRAINT PK_T_AUFTRAG_STATUS PRIMARY KEY (ID)
/
ALTER TABLE T_AUFTRAG_TECHNIK ADD CONSTRAINT PK_T_AUFTRAG_TECHNIK PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_ATECHNIK_2_AUFTRAG
	ON T_AUFTRAG_TECHNIK (AUFTRAG_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_ATECHNIK_2_TDN
	ON T_AUFTRAG_TECHNIK (TDN_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_AUFTRAG_TECHNIK_2_INTACC
	ON T_AUFTRAG_TECHNIK (INT_ACCOUNT_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_ATECHNIK_2_ES
	ON T_AUFTRAG_TECHNIK (AT_2_ES_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_ATECHNIK_2_VPN
	ON T_AUFTRAG_TECHNIK (VPN_ID) TABLESPACE "I_HURRICAN"
/

CREATE INDEX IX_FK_TECHNIK_2_ANBART
	ON T_AUFTRAG_TECHNIK (ANBINDUNGSART_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_AT_2_VERLAEND
	ON T_AUFTRAG_TECHNIK (AUFTRAGSART) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_AUFTRAG_TECHNIK_2_ENDSTELLE ADD CONSTRAINT PK_T_AUFTRAG_TECHNIK_2_ENDSTEL PRIMARY KEY (ID)
/
ALTER TABLE T_BANKVERBINDUNG ADD CONSTRAINT PK_T_BANKVERBINDUNG PRIMARY KEY (ID)
/
ALTER TABLE T_BANKVERBINDUNG ADD CONSTRAINT UK_T_BANKVERBINDUNG_RA UNIQUE (RA_ID)
/
ALTER TABLE T_BA_ABTCONFIG_2_ABT ADD CONSTRAINT PK_T_BA_ABTCONFIG_2_ABT PRIMARY KEY (ID)
/
ALTER TABLE T_BA_ABTCONFIG_2_ABT ADD CONSTRAINT UK_T_BA_ABTCONFIG_2_ABT UNIQUE (CONFIG_ID, ABT_ID)
/
CREATE INDEX IX_FK_BAABTCONFIG_2_ABT
	ON T_BA_ABTCONFIG_2_ABT (ABT_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_BA_ABT_CONFIG ADD CONSTRAINT PK_T_BA_ABT_CONFIG PRIMARY KEY (ID)
/
ALTER TABLE T_BA_VERL_AEND_GRUPPE ADD CONSTRAINT PK_T_BA_VERL_AEND_GRUPPE PRIMARY KEY (ID)
/
ALTER TABLE T_BA_VERL_AEND_PROD_2_GRUPPE ADD CONSTRAINT PK_T_BA_VERL_AEND_PROD_2_GRUPP PRIMARY KEY (PROD_ID, BA_VERL_AEND_GRUPPE_ID)
/
CREATE INDEX IX_FK_BAVERLAEND_PROD_2_GR
	ON T_BA_VERL_AEND_PROD_2_GRUPPE (BA_VERL_AEND_GRUPPE_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_BA_VERL_ANLASS ADD CONSTRAINT PK_T_BA_VERL_ANLASS PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_BAVERLANLASS_2_AENDGR
	ON T_BA_VERL_ANLASS (BA_VERL_GRUPPE) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_BA_VERL_CONFIG ADD CONSTRAINT PK_T_BA_VERL_CONFIG PRIMARY KEY (ID)
/

CREATE INDEX IX_FK_BAVERLCONFIG_2_ANLASS
	ON T_BA_VERL_CONFIG (ANLASS) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_BAVERLCONFIG_2_PROD
	ON T_BA_VERL_CONFIG (PROD_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_VERLCONF_2_ABTCONF
	ON T_BA_VERL_CONFIG (ABT_CONFIG_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_BA_ZUSATZ ADD CONSTRAINT PK_T_BA_ZUSATZ PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_BAZUSATZ_2_VERLCONFIG
	ON T_BA_ZUSATZ (BA_VERL_CONFIG_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_CARRIER ADD CONSTRAINT PK_T_CARRIER PRIMARY KEY (ID)
/
ALTER TABLE T_CARRIERBESTELLUNG ADD CONSTRAINT PK_T_CARRIERBESTELLUNG PRIMARY KEY (CB_ID)
/
CREATE INDEX IX_FK_CBESTELLUNG_2_CARRIER
	ON T_CARRIERBESTELLUNG (CARRIER_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_CB_2_CB2ES
	ON T_CARRIERBESTELLUNG (CB_2_ES_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_CB_2_AUFTRAG
	ON T_CARRIERBESTELLUNG (AUFTRAG_ID_4_TAL_NA) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_CB_2_AENDERUNGSTYP
	ON T_CARRIERBESTELLUNG (TAL_NA_TYP) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_CARRIERBESTELLUNG_LBZ
	ON T_CARRIERBESTELLUNG (LBZ) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_CARRIERBESTELLUNG_VTRNR
	ON T_CARRIERBESTELLUNG (VTRNR) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_CARRIERBESTELLUNG_BST
	ON T_CARRIERBESTELLUNG (BEREITSTELLUNG_AM) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_CARRIERBESTELLUNG_KUENDBST
	ON T_CARRIERBESTELLUNG (KUENDBESTAETIGUNG_CARRIER) TABLESPACE "I_HURRICAN"
/

ALTER TABLE T_CARRIER_KENNUNG ADD CONSTRAINT PK_T_CARRIER_KENNUNG PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_CKENNUNG_2_CARRIER
	ON T_CARRIER_KENNUNG (CARRIER_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_CARRIER_LBZ ADD CONSTRAINT PK_T_CARRIER_LBZ PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_CARRIER_LBZ_2_CARRIER
	ON T_CARRIER_LBZ (CARRIER_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_CB_2_ES ADD CONSTRAINT PK_T_CB_2_ES PRIMARY KEY (ID)
/
ALTER TABLE T_COUNTER ADD CONSTRAINT PK_T_COUNTER PRIMARY KEY (COUNTER)
/
ALTER TABLE T_DB_QUERIES ADD CONSTRAINT PK_T_DB_QUERIES PRIMARY KEY (ID)
/
ALTER TABLE T_DIALER ADD CONSTRAINT PK_T_DIALER PRIMARY KEY (ID)
/
CREATE INDEX IX_DIALER
	ON T_DIALER (KENNZAHL) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_DSLAM ADD CONSTRAINT PK_T_DSLAM PRIMARY KEY (DSLAM)
/
CREATE INDEX IX_FK_DSLAM_2_HVTSTD
	ON T_DSLAM (HVT_ID_STANDORT) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_DSLAM_2_HVTTECH
	ON T_DSLAM (HVT_TECHNIK_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_DSLAM_PROFILE ADD CONSTRAINT PK_T_DSLAM_PROFILE PRIMARY KEY (ID)
/
ALTER TABLE T_DSLAM_PROFILE ADD CONSTRAINT UK_T_DSLAM_PROFILE_NAME UNIQUE (NAME)
/
CREATE INDEX IX_FK_DP_2_TECHLS_DOWN
	ON T_DSLAM_PROFILE (DOWNSTREAM_TECH_LS) TABLESPACE "I_HURRICAN"
/


CREATE INDEX IX_FK_DP_2_TECHLS_UP
	ON T_DSLAM_PROFILE (UPSTREAM_TECH_LS) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_DP_2_TECHLS_FAST
	ON T_DSLAM_PROFILE (FASTPATH_TECH_LS) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_DSLAM_PROFILE_CHANGE ADD CONSTRAINT PK_T_DSLAM_PROFILE_CHANGE PRIMARY KEY (ID)
/

ALTER TABLE T_DSLAM_PROFILE_CHANGE_REASON ADD CONSTRAINT PK_DSLAM_PROFILE_CHANGER PRIMARY KEY (ID)
/

ALTER TABLE T_EG ADD CONSTRAINT PK_T_EG PRIMARY KEY (ID)
/

ALTER TABLE T_EG_IAD ADD CONSTRAINT PK_T_EG_IAD PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_EG_IAD_2_AUFTRAG
	ON T_EG_IAD (AUFTRAG_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_EG_IAD_2_VERLAUF
	ON T_EG_IAD (VERLAUF_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_EG_IAD_2_EG
	ON T_EG_IAD (EG_ID) TABLESPACE "I_HURRICAN"
/

CREATE INDEX IX_T_EG_INTID
	ON T_EG (INTERNE__ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_EG_2_AUFTRAG ADD CONSTRAINT PK_T_EG_2_AUFTRAG PRIMARY KEY (ID)
/
CREATE INDEX IX_T_EG_2_AUFTRAG
	ON T_EG_2_AUFTRAG (EG_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_EG2A_2_AUFTRAG
	ON T_EG_2_AUFTRAG (AUFTRAG_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_EG2A_2_EGHERKUNFT
	ON T_EG_2_AUFTRAG (EG_HERKUNFT) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_EG2A_2_MONTAGEART
	ON T_EG_2_AUFTRAG (MONTAGEART) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_EG_2_PAKET ADD CONSTRAINT PK_T_EG_2_PAKET PRIMARY KEY (ID)
/
CREATE INDEX IX_T_EG_2_PAKET
	ON T_EG_2_PAKET (EG_INTERNE__ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_EG_CONFIG ADD CONSTRAINT PK_T_EG_CONFIG PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_IPCONFIG_2_EG2A
	ON T_EG_CONFIG (EG2A_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_EG_HERKUNFT ADD CONSTRAINT PK_T_EG_HERKUNFT PRIMARY KEY (ID)
/
ALTER TABLE T_ENDSTELLE ADD CONSTRAINT PK_T_ENDSTELLE PRIMARY KEY (ID)
/

CREATE BITMAP INDEX IX_ENDSTELLE_TYP ON T_ENDSTELLE (ES_TYP) TABLESPACE "I_HURRICAN"
/

CREATE INDEX IX_FK_ENDSTELLE_2_SL
	ON T_ENDSTELLE (SL_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_ENDSTELLE_2_HVTSTD
	ON T_ENDSTELLE (HVT_ID_STANDORT) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_ENDSTELLE_2_ESGR
	ON T_ENDSTELLE (ES_GRUPPE) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_ENDSTELLE_2_ANSCHLUSSART
	ON T_ENDSTELLE (ANSCHLUSSART) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_ENDSTELLE_2_MONTAGEART
	ON T_ENDSTELLE (NT_MONTAGEART) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_ES_2_CB2ES
	ON T_ENDSTELLE (CB_2_ES_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_ENDSTELLE2_RANG
	ON T_ENDSTELLE (RANGIER_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_ES_RADD_2_RANG
	ON T_ENDSTELLE (RANGIER_ID_ADDITIONAL) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_EQUIPMENT ADD CONSTRAINT PK_T_EQUIPMENT PRIMARY KEY (EQ_ID)
/
CREATE INDEX IX_EQ_RBUCHT
	ON T_EQUIPMENT (RANG_BUCHT) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_EQ_RSCHNITTST
	ON T_EQUIPMENT (RANG_SCHNITTSTELLE) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_EQ_RSSTYPE
	ON T_EQUIPMENT (RANG_SS_TYPE) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_EQ_RVERTEILER
	ON T_EQUIPMENT (RANG_VERTEILER) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_EQUIPMENT_2_HVTST
	ON T_EQUIPMENT (HVT_ID_STANDORT) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_EQ_STATUS
	ON T_EQUIPMENT (STATUS) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_EQUIPMENT_2_DSLAM
	ON T_EQUIPMENT (DSLAM_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_EQ_DATEN ADD CONSTRAINT PK_T_EQ_DATEN PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_EQ_DATEN_2_RA
	ON T_EQ_DATEN (RA_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_EQ_DATEN_2_OBJEKT
	ON T_EQ_DATEN (OBJEKT_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_EQ_GEBAEUDE ADD CONSTRAINT PK_T_EQ_GEBAEUDE PRIMARY KEY (ID)
/
ALTER TABLE T_EQ_OBJEKT ADD CONSTRAINT PK_T_EQ_OBJEKT PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_EQ_OBJEKT_2_GEBAEUDE
	ON T_EQ_OBJEKT (GEBAEUDE_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_ES_ANSP ADD CONSTRAINT PK_T_ES_ANSP PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_ES_ANSP_2_ES
	ON T_ES_ANSP (ES_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_ES_LTG_DATEN ADD CONSTRAINT PK_T_ES_LTG_DATEN PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_ESLTGDATEN_2_SCHNITTST
	ON T_ES_LTG_DATEN (SCHNITTSTELLE_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_ESLTGDATEN_2_ES
	ON T_ES_LTG_DATEN (ES_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_ESLTGDATEN_2_LART
	ON T_ES_LTG_DATEN (LEITUNGSART) TABLESPACE "I_HURRICAN"
/


ALTER TABLE T_LIEFERSCHEIN ADD CONSTRAINT PK_T_LIEFERSCHEIN PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_LIEFERSCHEIN_2_REFSTATUS
        ON T_LIEFERSCHEIN (STATUS_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_LIEFERSCHEIN_2_REPREQ
        ON T_LIEFERSCHEIN (REQUEST_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_LIEFERSCHEIN_2_REFREASON
        ON T_LIEFERSCHEIN (RETOURE_GRUND_ID) TABLESPACE "I_HURRICAN"
/



ALTER TABLE T_E_RECHNUNG_DRUCK ADD CONSTRAINT PK_T_E_RECHNUNG_DRUCK PRIMARY KEY (ID)
/
ALTER TABLE T_FAKTURA ADD CONSTRAINT PK_T_FAKTURA PRIMARY KEY (ID)
/
ALTER TABLE T_FAKTURA ADD CONSTRAINT UK_T_FAKTURA UNIQUE (FNR_ID, AUFTRAG_ID)
/
CREATE INDEX IX_FK_FAKTURA_2_AUFTRAG
	ON T_FAKTURA (AUFTRAG_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_FAKTURA_BEARBEITER ADD CONSTRAINT PK_T_FAKTURA_BEARBEITER PRIMARY KEY (ID)
/
ALTER TABLE T_FAKTURA_BEARBEITER ADD CONSTRAINT UK_T_FAKTURA_BEARBEITER UNIQUE (NAME)
/
ALTER TABLE T_FAKTURA_MONAT ADD CONSTRAINT PK_T_FAKTURA_MONAT PRIMARY KEY (ID)
/
ALTER TABLE T_FAKTURA_MONAT ADD CONSTRAINT UK_FAKTURA_MONAT_RT UNIQUE (RECHNUNGSTEXT)
/
ALTER TABLE T_FAKTURA_MONAT ADD CONSTRAINT UK_FAKTURA_MONAT_VB UNIQUE (VON, BIS)
/
ALTER TABLE T_FAKTURA_NO ADD CONSTRAINT PK_T_FAKTURA_NO PRIMARY KEY (ID)
/
ALTER TABLE T_FAKTURA_NO ADD CONSTRAINT UK_T_FAKTURA_NO UNIQUE (AUFTRAG_ID, FAKTURA_MONAT)
/
CREATE INDEX IX_FK_FAKTURANO_2_MONAT
	ON T_FAKTURA_NO (FAKTURA_MONAT) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_FAKTURA_NR ADD CONSTRAINT PK_T_FAKTURA_NR PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_FAKTURANR_2_RA
	ON T_FAKTURA_NR (RA_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_FAKTURANR_2_MONAT
	ON T_FAKTURA_NR (FAKTURA_MONAT) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_FAKTURA_2_BEARB
	ON T_FAKTURA_NR (BEARBEITER) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_GUI_DEFINITION ADD CONSTRAINT PK_T_GUI_DEFINITION PRIMARY KEY (ID)
/
ALTER TABLE T_GUI_DEFINITION ADD CONSTRAINT UK_T_GUI_DEFINITION UNIQUE (CLASS)
/
ALTER TABLE T_GUI_MAPPING ADD CONSTRAINT PK_T_GUI_MAPPING PRIMARY KEY (ID)
/
ALTER TABLE T_GUI_MAPPING ADD CONSTRAINT UK_T_GUI_MAPPING UNIQUE (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
/
ALTER TABLE T_HVT_BESTELLUNG ADD CONSTRAINT PK_T_HVT_BESTELLUNG PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_HVTBEST_2_UEVT
	ON T_HVT_BESTELLUNG (UEVT_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_HVT_BES_HIST ADD CONSTRAINT PK_T_HVT_BES_HIST PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_HVTBESHIST_2_HVTBEST
	ON T_HVT_BES_HIST (BEST_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_HVT_GRUPPE ADD CONSTRAINT PK_T_HVT_GRUPPE PRIMARY KEY (HVT_GRUPPE_ID)
/
CREATE INDEX IX_FK_HVTGRUPPE_2_NL
	ON T_HVT_GRUPPE (NIEDERLASSUNG_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_HVT_STANDORT ADD CONSTRAINT PK_T_HVT_STANDORT PRIMARY KEY (HVT_ID_STANDORT)
/
CREATE INDEX IX_FK_HVTSTD_2_HVTGR
	ON T_HVT_STANDORT (HVT_GRUPPE_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_HVTSTD_2_CARRIER
	ON T_HVT_STANDORT (CARRIER_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_HVTSTD_2_CKENNUNG
	ON T_HVT_STANDORT (CARRIER_KENNUNG_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_HVT_STANDORT_2_TECHNIK ADD CONSTRAINT PK_T_HVT_STANDORT_2_TECHNIK PRIMARY KEY (HVT_ID_STANDORT, HVT_TECHNIK_ID)
/
CREATE INDEX IX_FK_ST2T_2_TECHNIK
	ON T_HVT_STANDORT_2_TECHNIK (HVT_TECHNIK_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_HVT_TECHNIK ADD CONSTRAINT PK_T_HVT_TECHNIK PRIMARY KEY (ID)
/
ALTER TABLE T_IA ADD CONSTRAINT PK_T_IA PRIMARY KEY (ID)
/
ALTER TABLE T_IA ADD CONSTRAINT UK_T_IA_NUMBER UNIQUE (IA_NUMMER)
/
ALTER TABLE T_IA ADD CONSTRAINT UK_T_IA_AUFTRAGID UNIQUE (AUFTRAG_ID)
/
CREATE INDEX IX_FK_IA_2_RANGAUFTRAG
        ON T_IA (RANGIERUNGS_AUFTRAG_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_IA_BUDGET ADD CONSTRAINT PK_T_IA_BUDGET PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_IABUDGET_2_IA
	ON T_IA_BUDGET (IA_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_IA_MATERIAL ADD CONSTRAINT PK_T_IA_MATERIAL PRIMARY KEY (ID)
/
ALTER TABLE T_IA_MAT_ENTNAHME ADD CONSTRAINT PK_T_IA_MAT_ENTNAHME PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_IAMATENT_2_BUDGET
	ON T_IA_MAT_ENTNAHME (BUDGET_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_IAMATENT_2_LAGER
	ON T_IA_MAT_ENTNAHME (LAGER_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_IA_MAT_ENTNAHME_ARTIKEL ADD CONSTRAINT PK_T_IA_MAT_ENTNAHME_ARTIKEL PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_IA_ENTART_2_ENTNAHME
	ON T_IA_MAT_ENTNAHME_ARTIKEL (MAT_ENTNAHME_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_INHOUSE ADD CONSTRAINT PK_T_INHOUSE PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_INHOUSE_2_ES
	ON T_INHOUSE (ES_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_INT_ACCOUNT ADD CONSTRAINT PK_T_INT_ACCOUNT PRIMARY KEY (ID)
/
CREATE INDEX IX_INT_ACCOUNT
	ON T_INT_ACCOUNT (ACCOUNT) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_INT_DRUCK ADD CONSTRAINT PK_T_INT_DRUCK PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_INT_DRUCK_2_INTACCOUNT
	ON T_INT_DRUCK (INT_ACCOUNT_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_KUBENA ADD CONSTRAINT PK_T_KUBENA PRIMARY KEY (ID)
/
ALTER TABLE T_KUBENA_HVT ADD CONSTRAINT PK_T_KUBENA_HVT PRIMARY KEY (ID)
/
ALTER TABLE T_KUBENA_HVT ADD CONSTRAINT UK_T_KUBENA_HVT UNIQUE (KUBENA_ID, HVT_ID_STANDORT)
/
CREATE INDEX IX_FK_KUBENAHVT_2_HVTSTD
	ON T_KUBENA_HVT (HVT_ID_STANDORT) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_KUBENA_PROD ADD CONSTRAINT PK_T_KUBENA_PROD PRIMARY KEY (ID)
/
ALTER TABLE T_KUBENA_PROD ADD CONSTRAINT UK_T_KUBENA_PROD UNIQUE (KUBENA_ID, PROD_ID)
/
CREATE INDEX IX_FK_KUBENAPROD_2_PROD
	ON T_KUBENA_PROD (PROD_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_KUBENA_TDN ADD CONSTRAINT PK_T_KUBENA_TDN PRIMARY KEY (ID)
/
ALTER TABLE T_KUBENA_TDN ADD CONSTRAINT UK_T_KUBENA_TDN UNIQUE (KUBENA_ID, TDN)
/
ALTER TABLE T_LAGER ADD CONSTRAINT PK_T_LAGER PRIMARY KEY (ID)
/
ALTER TABLE T_LB_2_LEISTUNG ADD CONSTRAINT PK_T_LB_2_LEISTUNG PRIMARY KEY (ID)
/
CREATE INDEX IX_LB_ID
	ON T_LB_2_LEISTUNG (LB_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_LEISTUNG_ID
	ON T_LB_2_LEISTUNG (LEISTUNG_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_LB_2_PRODUKT ADD CONSTRAINT PK_T_LB_2_PRODUKT PRIMARY KEY (LB_ID, LEISTUNG__NO)
/


CREATE INDEX IX_T_LB_2_PRODUKT_1
	ON T_LB_2_PRODUKT (PRODUCT_OE__NO) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_LEISTUNGSBUENDEL ADD CONSTRAINT PK_T_LEISTUNGSBUENDEL PRIMARY KEY (ID)
/
ALTER TABLE T_LEISTUNG_2_PARAMETER ADD CONSTRAINT PK_T_LEISTUNG_2_PARAMETER PRIMARY KEY (LEISTUNG_ID, LEISTUNG_PARAMETER_ID)
/
CREATE INDEX IX_LEISTUNG_PARAM_ID
	ON T_LEISTUNG_2_PARAMETER (LEISTUNG_PARAMETER_ID) TABLESPACE "I_HURRICAN"
/

ALTER TABLE T_LEISTUNG_4_DN ADD CONSTRAINT PK_T_LEISTUNG_4_DN PRIMARY KEY (ID)
/

ALTER TABLE T_LEISTUNG_DN ADD CONSTRAINT PK_T_LEISTUNG_DN PRIMARY KEY (LFDNR)
/
CREATE INDEX IX_T_LEISTUNG_DN_1
	ON T_LEISTUNG_DN (DN_NO) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_LEISTUNGDN_2_LEISTUNG4DN
	ON T_LEISTUNG_DN (LEISTUNG4DN_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_LEISTDN_2_LEISTBUENDEL
	ON T_LEISTUNG_DN (LEISTUNGSBUENDEL_ID) TABLESPACE "I_HURRICAN"
/

ALTER TABLE T_LEISTUNG_PARAMETER ADD CONSTRAINT PK_T_LEISTUNG_PARAMETER PRIMARY KEY (ID)
/
ALTER TABLE T_LEITUNGSART ADD CONSTRAINT PK_T_LEITUNGSART PRIMARY KEY (ID)
/
ALTER TABLE T_MONTAGEART ADD CONSTRAINT PK_T_MONTAGEART PRIMARY KEY (ID)
/
ALTER TABLE T_NIEDERLASSUNG ADD CONSTRAINT PK_T_NIEDERLASSUNG PRIMARY KEY (ID)
/
ALTER TABLE T_PHYSIKAENDERUNGSTYP ADD CONSTRAINT PK_T_PHYSIKAENDERUNGSTYP PRIMARY KEY (ID)
/
ALTER TABLE T_PHYSIKTYP ADD CONSTRAINT PK_T_PHYSIKTYP PRIMARY KEY (ID)
/
ALTER TABLE T_PHYSIKTYP ADD CONSTRAINT UK_T_PHYSIKTYP UNIQUE (NAME)
/
CREATE INDEX IX_FK_PHYSIKTYP_2_HVTTEC
	ON T_PHYSIKTYP (HVT_TECHNIK_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_PHYSIKUEBERNAHME ADD CONSTRAINT PK_T_PHYSIKUEBERNAHME PRIMARY KEY (ID)
/
ALTER TABLE T_PHYSIKUEBERNAHME ADD CONSTRAINT UK_T_PHYSIKUEBERNAHME UNIQUE (VERLAUF_ID)
/
CREATE INDEX IX_FK_PU_2_AUFTRAG_A
	ON T_PHYSIKUEBERNAHME (AUFTRAG_ID_A) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_PU_2_AUFTRAG_B
	ON T_PHYSIKUEBERNAHME (AUFTRAG_ID_B) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_PHYSIKUEB_2_TYP
	ON T_PHYSIKUEBERNAHME (AENDERUNGSTYP) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_PORTIERUNGSART ADD CONSTRAINT PK_T_PORTIERUNGSART PRIMARY KEY (ID)
/
ALTER TABLE T_PORT_FORWARD ADD CONSTRAINT PK_T_PORT_FORWARD PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_PORTFW_2_REF
	ON T_PORT_FORWARD (PROTOCOL) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_PORTGESAMT_PORT
	ON T_PORT_GESAMT (PORT) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_PORTGESAMT_STAT
	ON T_PORT_GESAMT (STATUS) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_PRODUKT ADD CONSTRAINT PK_T_PRODUKT PRIMARY KEY (PROD_ID)
/
CREATE INDEX IX_FK_T_PRODUKT_2_GRUPPE
	ON T_PRODUKT (PRODUKTGRUPPE_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_PRODUKT_2_LART
	ON T_PRODUKT (LEITUNGSART) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_PRODUKT_2_ABT
	ON T_PRODUKT (VERTEILUNG_DURCH) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_PROD_2_VERLCHAIN
	ON T_PRODUKT (VERLAUF_CHAIN_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_PROD_2_VERLCANCELCHAIN
	ON T_PRODUKT (VERLAUF_CANCEL_CHAIN_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_PRODUKTGRUPPE ADD CONSTRAINT PK_T_PRODUKTGRUPPE PRIMARY KEY (ID)
/

ALTER TABLE T_PRODUKT_2_PHYSIKTYP ADD CONSTRAINT PK_T_PROD_2_PHYSIKTYP PRIMARY KEY (ID)
/
ALTER TABLE T_PRODUKT_2_PHYSIKTYP ADD CONSTRAINT UK_T_PRODUKT_2_PHYSIKTYP UNIQUE (PROD_ID, PHYSIKTYP, PARENTPHYSIKTYP_ID)
/
CREATE INDEX IX_FK_PROD2PTYP_2_PTYP
	ON T_PRODUKT_2_PHYSIKTYP (PHYSIKTYP) TABLESPACE "I_HURRICAN"
/

CREATE INDEX IX_FK_P2PT_2_PT
	ON T_PRODUKT_2_PHYSIKTYP (PARENTPHYSIKTYP_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_P2PTADD_2_PHYSIKTYP
	ON T_PRODUKT_2_PHYSIKTYP (PHYSIKTYP_ADDITIONAL) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_PRODUKT_2_SCHNITTSTELLE ADD CONSTRAINT PK_T_PRODUKT_2_SCHNITTSTELLE PRIMARY KEY (PROD_ID, SCHNITTSTELLE_ID)
/
CREATE INDEX IX_FK_PROD2SCH_2_SCHN
	ON T_PRODUKT_2_SCHNITTSTELLE (SCHNITTSTELLE_ID) TABLESPACE "I_HURRICAN"
/



ALTER TABLE T_PRODUKT_MAPPING ADD CONSTRAINT PK_T_PRODUKT_MAPPING PRIMARY KEY (MAPPING_GROUP, EXT_PROD__NO, PROD_ID)
/
CREATE INDEX IX_FK_PRODMAP_2_PROD
	ON T_PRODUKT_MAPPING (PROD_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_PROD_2_DSLAMPROFILE ADD CONSTRAINT PK_T_PROD_2_DSLAMPROFILE PRIMARY KEY (PROD_ID, DSLAM_PROFILE_ID)
/
CREATE INDEX IX_FK_PDP_2_PROFIL
	ON T_PROD_2_DSLAMPROFILE (DSLAM_PROFILE_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_PROD_2_EG ADD CONSTRAINT PK_T_PROD_2_EG PRIMARY KEY (ID)
/
ALTER TABLE T_PROD_2_EG ADD CONSTRAINT UK_T_PROD_2_EG UNIQUE (PROD_ID, EG_ID)
/

CREATE INDEX IX_FK_PROD2EG_2_EG
	ON T_PROD_2_EG (EG_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_PROD_2_PROD ADD CONSTRAINT PK_T_PROD_2_PROD PRIMARY KEY (ID)
/
ALTER TABLE T_PROD_2_PROD ADD CONSTRAINT UK_T_PROD_2_PROD UNIQUE (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP)
/
CREATE INDEX IX_FK_P2PDEST_2_PROD
	ON T_PROD_2_PROD (PROD_DEST) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_P2P_PHAENDTYP
	ON T_PROD_2_PROD (PHYSIKAEND_TYP) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_P2P_2_CHAIN
	ON T_PROD_2_PROD (CHAIN_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_PROD_2_SL ADD CONSTRAINT PK_T_PROD_2_SL PRIMARY KEY (ID)
/
ALTER TABLE T_PROD_2_SL ADD CONSTRAINT UK_T_PROD_2_SL UNIQUE (PROD_ID, SL_ID)
/
CREATE INDEX IX_FK_PRODSL_2_SL
	ON T_PROD_2_SL (SL_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_PROD_SL_FREI
	ON T_PROD_2_SL (FREIGABE_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_PROD_2_TECH_LEISTUNG ADD CONSTRAINT PK_T_PROD_2_TECH_LEISTUNG PRIMARY KEY (ID)
/
ALTER TABLE T_PROD_2_TECH_LEISTUNG ADD CONSTRAINT UK_T_PROD_2_TECH_LEISTUNG UNIQUE (PROD_ID, TECH_LS_ID, TECH_LS_DEPENDENCY)
/
CREATE INDEX IX_FK_P2TL_2_TL
	ON T_PROD_2_TECH_LEISTUNG (TECH_LS_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_PROD_SL_FREIGABE ADD CONSTRAINT PK_T_PROD_SL_FREIGABE PRIMARY KEY (ID)
/
ALTER TABLE T_PVC_2_PROFIL ADD CONSTRAINT PK_T_PVC_2_PROFIL PRIMARY KEY (ID)
/
ALTER TABLE T_PVC_2_PROFIL ADD CONSTRAINT UK_T_PVC_2_PROFIL UNIQUE (PVC_ID, PROFIL_ID)
/
CREATE INDEX IX_FK_PVC2PROFIL_2_PROFIL
	ON T_PVC_2_PROFIL (PROFIL_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_PVC_VORGABE ADD CONSTRAINT PK_T_PVC_VORGABE PRIMARY KEY (ID)
/
ALTER TABLE T_RANGIERUNG ADD CONSTRAINT PK_T_RANGIERUNG PRIMARY KEY (RANGIER_ID)
/
CREATE INDEX IX_FK_RANGIN_2_EQ
	ON T_RANGIERUNG (EQ_IN_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_RANG_2_PHYSIKTYP
	ON T_RANGIERUNG (PHYSIK_TYP) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_RANG_2_HVTSTD
	ON T_RANGIERUNG (HVT_ID_STANDORT) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_RANGOUT_2_EQ
	ON T_RANGIERUNG (EQ_OUT_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_RANG_2_RANGAUFTRAG
        ON T_RANGIERUNG (RAUFTRAG_ID) TABLESPACE "I_HURRICAN"
/

CREATE INDEX IX_T_RANGIERUNG_LTGGES
	ON T_RANGIERUNG (LEITUNG_GESAMT_ID)
/

ALTER TABLE T_RANGIERUNGSMATRIX ADD CONSTRAINT PK_T_RANGIERUNGSMATRIX PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_RMATRIX_2_UEVT
	ON T_RANGIERUNGSMATRIX (UEVT_ID) TABLESPACE "I_HURRICAN"
/

CREATE INDEX IX_FK_RMATRIX_2_P2P
	ON T_RANGIERUNGSMATRIX (PRODUKT2PHYSIKTYP_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_RMATRIX_2_HVT
	ON T_RANGIERUNGSMATRIX (HVT_STANDORT_ID_ZIEL) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_RMATRIX_2_PRODUKT
	ON T_RANGIERUNGSMATRIX (PROD_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_RECHNUNGSANSCHRIFT ADD CONSTRAINT PK_T_RECHNUNGSANSCHRIFT PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_RAMWST_2_REF
	ON T_RECHNUNGSANSCHRIFT (MWST_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_REFERENCE ADD CONSTRAINT PK_T_REFERENCE PRIMARY KEY (ID)
/

CREATE INDEX IX_T_REFERENCE
	ON T_REFERENCE (TYPE) TABLESPACE "I_HURRICAN"
/

ALTER TABLE T_REGISTRY ADD CONSTRAINT PK_T_REGISTRY PRIMARY KEY (ID)
/
CREATE INDEX IX_T_REGISTRY
	ON T_REGISTRY (NAME) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_SCHNITTSTELLE ADD CONSTRAINT PK_T_SCHNITTSTELLE PRIMARY KEY (ID)
/
ALTER TABLE T_SDH_PHYSIK ADD CONSTRAINT PK_T_SDH_PHYSIK PRIMARY KEY (SDHP_ID)
/
CREATE INDEX IX_FK_SDH_PHYSIK_2_ES
	ON T_SDH_PHYSIK (ES_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_SDHPHYSIK_2_PVC
	ON T_SDH_PHYSIK (NETZKOPPLUNG_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_SDHPHYSIK_2_PVC_ANSCHALT
	ON T_SDH_PHYSIK (ANSCHALTPUNKT_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_SDHPHYSIK_2_PROFIL
	ON T_SDH_PHYSIK (PROFIL_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_SDH_PROFIL ADD CONSTRAINT PK_T_SDH_PROFIL PRIMARY KEY (ID)
/
ALTER TABLE T_SERVICE_CHAIN ADD CONSTRAINT PK_T_SERVICE_CHAIN PRIMARY KEY (ID)
/
ALTER TABLE T_SERVICE_CHAIN ADD CONSTRAINT UK_T_SERVICE_CHAIN UNIQUE (NAME)
/
ALTER TABLE T_SERVICE_COMMANDS ADD CONSTRAINT PK_T_SERVICE_COMMANDS PRIMARY KEY (ID)
/
ALTER TABLE T_SERVICE_COMMANDS ADD CONSTRAINT UK_T_SERVICE_COMMANDS UNIQUE (CLASS)
/
ALTER TABLE T_SERVICE_COMMAND_MAPPING ADD CONSTRAINT PK_T_SERVICE_COMMAND_MAPPING PRIMARY KEY (ID)
/
ALTER TABLE T_SERVICE_COMMAND_MAPPING ADD CONSTRAINT UK_T_SERVICE_COMMAND_MAPPI UNIQUE (COMMAND_ID, REF_ID, REF_CLASS)
/
ALTER TABLE T_SPERRE ADD CONSTRAINT PK_T_SPERRE PRIMARY KEY (S_ID)
/
CREATE INDEX IX_FK_SPERRE_2_ABT
	ON T_SPERRE (S_ABT_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_SPERRE_INFO ADD CONSTRAINT PK_T_SPERRE_INFO PRIMARY KEY (ID)
/
ALTER TABLE T_SPERRE_INFO ADD CONSTRAINT UK_T_SPERRE_INFO UNIQUE (ABTEILUNG_ID, EMAIL)
/
ALTER TABLE T_SPERRE_VERTEILUNG ADD CONSTRAINT PK_T_SPERRE_VERTEILUNG PRIMARY KEY (PROD_ID, ABTEILUNG_ID)
/
CREATE INDEX IX_FK_SPERREVERT_2_ABT
	ON T_SPERRE_VERTEILUNG (ABTEILUNG_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_SPERRKLASSEN ADD CONSTRAINT PK_T_SPERRKLASSEN PRIMARY KEY (ID)
/
ALTER TABLE T_STRASSENLISTE ADD CONSTRAINT PK_T_STRASSENLISTE PRIMARY KEY (SL_ID)
/
CREATE INDEX IX_FK_T_STRASSENLISTE_1
	ON T_STRASSENLISTE (HVT_GRUPPE_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_STRASSEN_DETAILS ADD CONSTRAINT PK_T_STRASSEN_DETAILS PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_T_STRASSEN_DETAILS
	ON T_STRASSEN_DETAILS (SL_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_TAL_AENDERUNGSTYP ADD CONSTRAINT PK_T_TAL_AENDERUNGSTYP PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_TALAENDTYP_2_CARRIER
	ON T_TAL_AENDERUNGSTYP (CARRIER_ID) TABLESPACE "I_HURRICAN"
/

ALTER TABLE T_TDN ADD CONSTRAINT PK_T_TDN PRIMARY KEY (ID)
/
ALTER TABLE T_TDN ADD CONSTRAINT UK_T_TDN UNIQUE (TDN)
/
ALTER TABLE T_TDN_ZAEHLER ADD CONSTRAINT PK_T_TDN_ZAEHLER PRIMARY KEY (NK)
/
ALTER TABLE T_TECH_LEISTUNG ADD CONSTRAINT PK_T_TECH_LEISTUNG PRIMARY KEY (ID)
/
CREATE INDEX IX_TECH_LEISTUNG_EXTMISC
	ON T_TECH_LEISTUNG (EXTERN_MISC__NO) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_TECH_LEISTUNG_EXTLEISTUNG
	ON T_TECH_LEISTUNG (EXTERN_LEISTUNG__NO) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_UEVT ADD CONSTRAINT PK_T_UEVT PRIMARY KEY (UEVT_ID)
/
CREATE INDEX IX_UEVT
	ON T_UEVT (UEVT) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_UEVT_2_HVTSTD
	ON T_UEVT (HVT_ID_STANDORT) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_UEVT_2_ZIEL ADD CONSTRAINT PK_T_UEVT_2_ZIEL PRIMARY KEY (ID)
/
ALTER TABLE T_UEVT_2_ZIEL ADD CONSTRAINT UK_T_UEVT_2_ZIEL UNIQUE (PROD_ID, UEVT_ID, HVT_STANDORT_ID_ZIEL)
/
CREATE INDEX IX_FK_UEVT2ZIEL_2_UEVT
	ON T_UEVT_2_ZIEL (UEVT_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_UEVT2ZIEL_2_HVT
	ON T_UEVT_2_ZIEL (HVT_STANDORT_ID_ZIEL) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_VERLAUF ADD CONSTRAINT PK_T_VERLAUF PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_VERLAUF_2_AUFTRAG
	ON T_VERLAUF (AUFTRAG_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_VERLAUF_2_BAVERLAEND
	ON T_VERLAUF (ANLASS) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_VERLAUF_2_AS
	ON T_VERLAUF (STATUS_ID_ALT) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_VERLAUF_2_VS
	ON T_VERLAUF (VERLAUF_STATUS_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_VERL_2_PORTART
	ON T_VERLAUF (PORTIERUNGSART) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_VERLAUF_ABTEILUNG ADD CONSTRAINT PK_T_VERLAUF_ABTEILUNG PRIMARY KEY (ID)
/
ALTER TABLE T_VERLAUF_ABTEILUNG ADD CONSTRAINT UK_T_VERLAUF_ABTEILUNG UNIQUE (VERLAUF_ID, ABTEILUNG_ID)
/


CREATE INDEX IX_FK_VABT_2_VSTATUS
	ON T_VERLAUF_ABTEILUNG (VERLAUF_STATUS_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_VABT_2_ABT
	ON T_VERLAUF_ABTEILUNG (ABTEILUNG_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_VABT_2_VERLAUF
	ON T_VERLAUF_ABTEILUNG (VERLAUF_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_VERLAUF_STATUS ADD CONSTRAINT PK_T_VERLAUF_STATUS PRIMARY KEY (ID)
/
ALTER TABLE T_VPN ADD CONSTRAINT PK_T_VPN PRIMARY KEY (VPN_ID)
/
ALTER TABLE T_VPN ADD CONSTRAINT UK_T_VPN UNIQUE (VPN_NR)
/
CREATE INDEX IX_FK_VPN_2_REFERENCE
	ON T_VPN (VPN_TYPE) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_VPN_KONF ADD CONSTRAINT PK_T_VPN_KONF PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_VPNKONF_2_AUFTRAG
	ON T_VPN_KONF (NR) TABLESPACE "I_HURRICAN"
/

CREATE INDEX IX_FK_VPNKONF_2_AUFTRAGPHYS
	ON T_VPN_KONF (PHYS_AUFTRAG_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_WOHNHEIM ADD CONSTRAINT PK_T_WOHNHEIM PRIMARY KEY (ID)
/
ALTER TABLE T_ZUGANG ADD CONSTRAINT PK_T_ZUGANG PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_ZUGANG_2_AUFTRAG
	ON T_ZUGANG (AUFTRAG_ID) TABLESPACE "I_HURRICAN"
/
ALTER TABLE WISSEN ADD CONSTRAINT PK_WISSEN PRIMARY KEY ("id")
/


ALTER TABLE T_REP2PROD_STATI ADD CONSTRAINT PK_T_REP2PROD_STATI PRIMARY KEY (ID)
/
ALTER TABLE T_REP2PROD_TECHLS ADD CONSTRAINT PK_T_REP2PROD_TECHLS PRIMARY KEY (ID)
/
ALTER TABLE T_REPORT ADD CONSTRAINT PK_T_REPORT PRIMARY KEY (ID)
/
ALTER TABLE T_REPORT_2_PROD ADD CONSTRAINT PK_T_REPORT_2_PROD PRIMARY KEY (ID)
/
ALTER TABLE T_REPORT_DATA ADD CONSTRAINT PK_T_REPORT_DATA PRIMARY KEY (ID)
/
ALTER TABLE T_REPORT_REQUEST ADD CONSTRAINT PK_T_REPORT_REQUEST PRIMARY KEY (ID)
/
ALTER TABLE T_REPORT_GRUPPE ADD CONSTRAINT PK_T_REPORT_GRUPPE PRIMARY KEY (ID)
/
ALTER TABLE T_REPORT_REASON ADD CONSTRAINT PK_T_REPORT_REASON PRIMARY KEY (ID)
/

ALTER TABLE T_REPORT_TEMPLATE ADD CONSTRAINT PK_T_REPORT_TEMPLATE PRIMARY KEY (ID)
/
ALTER TABLE T_REPORT_2_USERROLE ADD CONSTRAINT PK_T_REPORT_2_USERROLE PRIMARY KEY (ID)
/
ALTER TABLE T_REPORT_PAPERFORMAT ADD CONSTRAINT PK_T_REPORT_PAPERFORMAT PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_T_REPORT_2_PAPERSITE1
	ON t_report (FIRST_PAGE) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_T_REPORT_2_PAPERSITE2
	ON t_report (SECOND_PAGE) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_REPORT_2_ARCHIVOBJECT
	ON t_report (ARCHIV_OBJECT_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_REPREQUEST_2_REPORT
	ON t_report_request (REP_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_REPREQUEST_2_REASON
	ON t_report_request (PRINT_REASON) TABLESPACE "I_HURRICAN"
/

CREATE INDEX IX_FK_REP2PROD_2_PRODUKT
	ON t_report_2_prod (PROD_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_REP2PROD_2_REPORT
	ON t_report_2_prod (REP_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_REP2PROD_2_ASTATUS
	ON t_rep2prod_stati (STATUS_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_REP2PRODSTATI_2_RP
	ON t_rep2prod_stati (REP2PROD_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_REPDATA_2_REQUEST
	ON t_report_data (REQ_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_REPTEMP_2_REPORT
	ON t_report_template (REP_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_REP2TXTB_2_REPORT
	ON t_report_2_txt_baustein (REP_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_REP2PRODTLS_2_REP2PROD
	ON t_rep2prod_techls (REP2PROD_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_REP2PRODTLS_2_TECHLS
	ON t_rep2prod_techls (TECHLS_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_REP2ROLE_2_REP
	ON t_report_2_userrole (REPORT_ID) TABLESPACE "I_HURRICAN"
/

ALTER TABLE T_ARCHIV_AUSWAHL ADD CONSTRAINT PK_T_ARCHIV_AUSWAHL PRIMARY KEY (ID)
/
ALTER TABLE T_ARCHIV_OBJECT ADD CONSTRAINT PK_T_ARCHIV_OBJECT PRIMARY KEY (ID)
/
ALTER TABLE T_ARCHIV_PARAMETER_2_OBJECT ADD CONSTRAINT PK_T_ARCHIV_PARAMETER_2_OBJECT PRIMARY KEY (ID)
/
ALTER TABLE T_ARCHIV_DATEN ADD CONSTRAINT PK_T_ARCHIV_DATEN PRIMARY KEY (ID)
/
CREATE INDEX IX_ARCHD_KUNDENVERTRAG
	ON T_ARCHIV_DATEN (KUNDENVERTRAG) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_ARCHD_LAST_UPDATE
	ON T_ARCHIV_DATEN (LAST_UPDATE) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_ARCHIV_POSSIBLE_PARAMETER ADD CONSTRAINT PK_T_ARCHIV_POSSIBLE_PARAMETER PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_ARCHP2OBJ_2_OBJECT
	ON t_archiv_parameter_2_object (OBJECT_ID_INTERN) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_ARCHP2OBJ_2_PARAM
	ON t_archiv_parameter_2_object (PARAMETER_ID_INTERN) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_ARCHP2OBJ_2_AUSWAHL
	ON t_archiv_parameter_2_object (AUSWAHL_ID) TABLESPACE "I_HURRICAN"
/

ALTER TABLE T_CB_CONFIG ADD CONSTRAINT PK_T_CB_CONFIG PRIMARY KEY (ID)
/
ALTER TABLE T_CB_USECASE ADD CONSTRAINT PK_T_CB_USECASE PRIMARY KEY (ID)
/
ALTER TABLE T_CB_USECASE_2_CARRIER_KNG ADD CONSTRAINT PK_CB_USECASE_2_CARRIER_KNG PRIMARY KEY (ID)
/
ALTER TABLE T_CB_VORGANG ADD CONSTRAINT PK_T_CB_VORGANG PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_CB_VORGANG_2_CB
	ON T_CB_VORGANG (CB_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_CBVORGANG_2_AUFTRAG
	ON T_CB_VORGANG (AUFTRAG_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_CB_VORGANG_2_REF_TYP
	ON T_CB_VORGANG (TYP) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_CB_VORGANG_2_REF_STATUS
	ON T_CB_VORGANG (STATUS) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_CB_VORGANG_2_CARRIER
	ON T_CB_VORGANG (CARRIER_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_CBVORGANG_2_USEC
	ON T_CB_VORGANG (USECASE_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_CBUC_2_CKNG
	ON T_CB_USECASE_2_CARRIER_KNG (CARRIERKENNUNG_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_CBUC_2_UC
	ON T_CB_USECASE_2_CARRIER_KNG (USECASE_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_CBCONFIG_2_USECASE
	ON T_CB_CONFIG (USECASE_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_CBCONFIG_2_CMD
	ON T_CB_CONFIG (COMMAND_ID) TABLESPACE "I_HURRICAN"
/

ALTER TABLE T_TXT_BAUSTEIN ADD CONSTRAINT PK_TXT_BAUSTEIN PRIMARY KEY (ID)
/
ALTER TABLE T_TXT_BAUSTEIN_2_GRUPPE ADD CONSTRAINT PK_TXT_BAUSTEIN_2_GRUPPE PRIMARY KEY (ID)
/
ALTER TABLE T_TXT_BAUSTEIN_GRUPPE ADD CONSTRAINT PK_TXT_BAUSTEIN_GRUPPE PRIMARY KEY (ID)
/
ALTER TABLE T_TXT_BAUSTEIN_GRUPPE_2_REPORT ADD CONSTRAINT PK_TXT_BAUSTEIN_GRUPPE_2_REP PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_TXTBAUSTEIN_2_GRUPPE
	ON t_txt_baustein_2_gruppe (GRUPPE_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_TXTBSTGRUPPE_2_GRUPPE
	ON t_txt_baustein_gruppe_2_report (GRUPPE_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_TXTBSTGRUPPE_2_REP
	ON t_txt_baustein_gruppe_2_report (REPORT_ID) TABLESPACE "I_HURRICAN"
/

ALTER TABLE T_PRINTER ADD CONSTRAINT PK_T_PRINTER PRIMARY KEY (ID)
/
ALTER TABLE T_PRINTER_2_PAPER ADD CONSTRAINT PK_T_PRINTER_2_PAPER PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_PRINTER2PAP_2_PAPER
        ON T_PRINTER_2_PAPER (PAPER_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_PRINTER2PAP_2_PRINTER
        ON T_PRINTER_2_PAPER (PRINTER_ID) TABLESPACE "I_HURRICAN"
/

ALTER TABLE T_PRODUKT_DTAG ADD CONSTRAINT PK_T_PRODUKT_DTAG PRIMARY KEY (ID)
/
CREATE INDEX IX_PRODUKT_DTAG_RANGSSTYPE
	ON T_PRODUKT_DTAG (RANG_SS_TYPE) TABLESPACE "I_HURRICAN"
/


ALTER TABLE T_RANGIERUNGS_AUFTRAG ADD CONSTRAINT PK_T_RANGIERUNGS_AUFTRAG PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_RA_2_HVTSTD
        ON T_RANGIERUNGS_AUFTRAG (HVT_STD_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_RA_2_PHYSIKTYP1
        ON T_RANGIERUNGS_AUFTRAG (PHYSIKTYP_PARENT) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_RA_2_PHYSIKTYP2
        ON T_RANGIERUNGS_AUFTRAG (PHYSIKTYP_CHILD) TABLESPACE "I_HURRICAN"
/
ALTER TABLE T_RANGIERUNGS_MATERIAL ADD CONSTRAINT PK_T_RANGIERUNGS_MATERIAL PRIMARY KEY (ID)
/


ALTER TABLE T_AUFTRAG_QOS ADD CONSTRAINT PK_T_AUFTRAG_QOS PRIMARY KEY (ID)
/
CREATE INDEX IX_FK_AUFTRAGQOS_2_AUFTRAG
        ON T_AUFTRAG_QOS (AUFTRAG_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_AUFTRAGQOS_2_REF
        ON T_AUFTRAG_QOS (QOS_CLASS_REF_ID) TABLESPACE "I_HURRICAN"
/


CREATE INDEX IX_REPREQ_KUNDENO 
		ON t_report_request (KUNDE__NO ASC)TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_REPREQ_ORDERNO 
		ON t_report_request (ORDER__NO ASC) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_REPREQ_2_AUFTRAG
        ON T_REPORT_REQUEST (AUFTRAG_ID) TABLESPACE "I_HURRICAN"
/
CREATE INDEX IX_FK_REPORT_2_REPGRUPPE
        ON T_REPORT (REPORT_GRUPPE_ID) TABLESPACE "I_HURRICAN"
/

ALTER TABLE T_PRODUKT_EQ_CONFIG ADD CONSTRAINT PK_T_PRODUKT_EQ_CONFIG PRIMARY KEY (ID)
/
ALTER TABLE T_PRODUKT_EQ_CONFIG ADD CONSTRAINT UQ_PRODEQCONF UNIQUE (PROD_ID, CONFIG_GROUP, EQ_TYP, EQ_PARAM)
/
