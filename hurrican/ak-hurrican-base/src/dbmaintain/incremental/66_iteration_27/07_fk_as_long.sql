alter table T_ENDSTELLE modify ADDRESS_ID number(19);
alter table T_CARRIERBESTELLUNG modify AI_ADDRESS_ID number(19);
alter table T_DSLAM_PROFILE_CHANGE modify AUFTRAG_ID number(19);
alter table T_EG modify INTERNE__ID number(19);
alter table T_EG modify EXT_LEISTUNG__NO number(19);
alter table T_EG modify EGTYPES number(19);
alter table T_EG_2_AUFTRAG modify PAKET_ID number(19);
alter table T_EG_2_AUFTRAG modify VERSANDART number(19);
alter table T_EG_2_AUFTRAG modify LIEFERADRESSE_ID number(19);
alter table T_EG_2_AUFTRAG modify REF_ID_VERSAND_STATUS number(19);
alter table T_EG_2_AUFTRAG modify LIEFERSCHEIN_ID number(19);
alter table T_EG_2_PAKET modify EG_INTERNE__ID number(19);
alter table T_EG_2_PAKET modify PAKET_ID number(19);
alter table T_EXT_SERVICE_PROVIDER modify CONTACT_TYPE number(19);

alter table T_HW_BG_CHANGE_BG_TYPE modify HW_BAUGRUPPEN_ID number(19);
alter table T_HW_BG_CHANGE_BG_TYPE modify HW_BAUGRUPPENTYP_NEW number(19);
alter table T_HW_BG_CHANGE_CARD modify HW_BAUGRUPPEN_ID_NEW number(19);

alter table T_HW_BG_CHANGE_DSLAM_SPLIT modify HW_RACK_ID_OLD number(19);
alter table T_HW_BG_CHANGE_DSLAM_SPLIT modify HW_SUBRACK_ID_OLD number(19);
alter table T_HW_BG_CHANGE_DSLAM_SPLIT modify HW_RACK_ID_NEW number(19);
alter table T_HW_BG_CHANGE_DSLAM_SPLIT modify HW_SUBRACK_ID_NEW number(19);

alter table T_HW_RACK_MDU modify OLT_RACK_ID number(19);

alter table T_IA_BUDGET modify BUDGET_USER_ID number(19);
alter table T_IA_BUDGET modify RESPONSIBLE_USER_ID number(19);

alter table T_KUNDE_NBZ add TMP_ID number(19);
update T_KUNDE_NBZ set TMP_ID=ID;
alter table T_KUNDE_NBZ drop column ID;
alter table T_KUNDE_NBZ add ID number(19);
update T_KUNDE_NBZ set ID=TMP_ID;
alter table T_KUNDE_NBZ drop column TMP_ID;

alter table T_LB_2_LEISTUNG modify OE__NO number(19);
alter table T_LB_2_PRODUKT modify PROTOKOLL_LEISTUNG__NO number(19);
alter table T_LB_2_PRODUKT modify PRODUCT_OE__NO number(19);
alter table T_LEISTUNG_4_DN modify EXTERN_LEISTUNG__NO number(19);
alter table T_LEISTUNG_4_DN modify EXTERN_SONSTIGES__NO number(19);
alter table T_LEISTUNG_DN modify PARAMETER_ID number(19);
alter table T_LEISTUNG_DN modify CPS_TX_ID_CREATION number(19);
alter table T_LEISTUNG_DN modify CPS_TX_ID_CANCEL number(19);

alter table T_LOCK modify PARENT_LOCK_ID number(19);
alter table T_NIEDERLASSUNG modify PARENT number(19);
alter table T_NIEDERLASSUNG modify AREA_NO number(19);

alter table T_PHYSIKTYP modify PT_GROUP number(19);
alter table T_PORT_GESAMT modify ID number(19);
alter table T_REPORT modify TYPE number(19);

alter table T_RSM_EQ_VIEW modify UEVT_ID number(19);
alter table T_RSM_RANG_COUNT modify HVT_ID_STANDORT number(19);

alter table T_TRANSPONDER modify TRANSPONDER_ID number(19);
alter table T_TXT_BAUSTEIN modify ID_ORIG number(19);
alter table T_TXT_BAUSTEIN_2_GRUPPE modify BAUSTEIN_ID_ORIG number(19);

alter table T_VERLAUF_ABTEILUNG modify PARENT_VERLAUF_ABTEILUNG_ID number(19);


