alter table T_AUFTRAG drop column AUFTRAG_ID_NEU;

alter table T_AUFTRAG_HOUSING modify BUILDING_ID number(19);
alter table T_AUFTRAG_HOUSING modify FLOOR_ID number(19);
alter table T_AUFTRAG_HOUSING modify ROOM_ID number(19);
alter table T_AUFTRAG_HOUSING modify PARCELL_ID number(19);
alter table T_AUFTRAG_HOUSING modify RACK_UNITS number(19);

alter table T_AUFTRAG_INTERN modify WORKING_TYPE_REF_ID number(19);

alter table T_AUFTRAG_TECHNIK modify PROJECT_RESPONSIBLE number(19);
alter table T_AUFTRAG_TECHNIK modify PROJECT_LEAD number(19);

alter table T_BA_VERL_CONFIG modify PROD_ID number(19);

alter table T_BA_ZUSATZ modify ABT_ID number(19);
alter table T_BA_ZUSATZ modify HVT_GRUPPE_ID number(19);

alter table T_CB_VORGANG modify WITA_AUFTRAG_KLAMMER number(19);
alter table T_CB_VORGANG modify STATUS_LAST number(19);

alter table T_HW_RACK modify HVT_ID_STANDORT number(19);

alter table T_IA modify PROJEKTLEITER_USER_ID number(19);

alter table T_KUNDE_NBZ modify ID not null;
ALTER TABLE T_KUNDE_NBZ ADD PRIMARY KEY (ID);

alter table T_PRODUKT modify PROJEKTIERUNG_CHAIN_ID number(19);
alter table T_PRODUKT drop column ABRECHNUNG_IN_HURRICAN;

alter table T_REFERENCE modify UNIT_ID number(19);

alter table T_REPORT_2_USERROLE modify ROLE_ID number(19);

alter table T_TECH_LEISTUNG modify EXTERN_MISC__NO number(19);
alter table T_TECH_LEISTUNG modify EXTERN_LEISTUNG__NO number(19);
alter table T_TECH_LEISTUNG modify LONG_VALUE number(19);
