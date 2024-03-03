alter table T_ADDRESS modify KUNDE__NO number(19);

alter table T_AUFTRAG modify KUNDE__NO number(19);

alter table T_AUFTRAG_DATEN modify PRODAK_ORDER__NO number(19);

alter table T_EG_2_AUFTRAG modify PRODAK_ORDER__NO number(19);
alter table T_EG_2_AUFTRAG modify PRODAK_ADRESSE_NO number(19);

alter table T_LEISTUNG_DN modify DN_NO number(19);

alter table T_LOCK modify CUSTOMER_NO number(19);
alter table T_LOCK modify TAIFUN_ORDER__NO number(19);

alter table T_REPORT_REQUEST modify KUNDE__NO number(19);
alter table T_REPORT_REQUEST modify ORDER__NO number(19);

alter table T_TRANSPONDER_GROUP modify KUNDE__NO number(19);

alter table T_TXT_BAUSTEIN_GRUPPE_2_REPORT modify ORDER_NO number(19);

alter table T_WOHNHEIM modify KUNDE__NO number(19);
