insert into T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) values (462, 10);

update T_PRODUKT set TDN_KIND_OF_USE_TYPE = 'E' where PROD_ID = 462;

insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT, IS_ACTIVE) values (s_t_prod_2_eg_0.nextval, 462, 12, 0, 1);


-- Neues Produtk für 4-Draht-Option
insert into T_PRODUKT (PROD_ID, PRODUKTGRUPPE_ID, PRODUKT_NR, ANSCHLUSSART, PROD_NAME_PATTERN, LEITUNGSART, AKTIONS_ID, BRAUCHT_DN, DN_MOEGLICH, MAX_DN_COUNT, DN_BLOCK, DN_TYP, BRAUCHT_DN4ACCOUNT, GUELTIG_VON, GUELTIG_BIS, AUFTRAGSERSTELLUNG, LTGNR_VORS, LTGNR_ANLEGEN, BRAUCHT_BUENDEL, ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, BESCHREIBUNG, ACCOUNT_VORS, LI_NR, VPN_PHYSIK, PROJEKTIERUNG, IS_PARENT, CHECK_CHILD, IS_COMBI_PRODUKT, AUTO_PRODUCT_CHANGE, EXPORT_KDP_M, CREATE_KDP_ACCOUNT_REPORT, EXPORT_AK_PRODUKTE, VERTEILUNG_DURCH, BA_RUECKLAEUFER, BRAUCHT_VPI_VCI, PROJEKTIERUNG_CHAIN_ID, VERLAUF_CHAIN_ID, VERLAUF_CANCEL_CHAIN_ID, BA_CHANGE_REALDATE, CREATE_AP_ADDRESS, ASSIGN_IAD, CPS_PROVISIONING, CPS_PROD_NAME, STATUSMELDUNGEN, CPS_AUTO_CREATION, CPS_ACCOUNT_TYPE, CPS_DSL_PRODUCT, TDN_KIND_OF_USE_PRODUCT, TDN_KIND_OF_USE_TYPE, TDN_USE_FROM_MASTER)
values (463, 2, null, 'Connect Basic 4-Draht Option', null, null, 1, 0, 0, null, 0, null, 0, to_date('01.11.2009', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), 0, 'DV', 1, 0, 1, 0, 2, null, null, null, 1, 0, 0, 0, 0, 0, 0, 0, null, 11, 1, 0, null, 33, 15, 1, 1, 0, null, null, 0, null, null, null, null, null, 1);

-- T_PRODUKT_MAPPING
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE)
VALUES (463, 463, 463, 'connect');

-- T_PRODUKT_2_PHYSIKTYP
INSERT INTO T_PRODUKT_2_PHYSIKTYP (id, prod_id, physiktyp, virtuell)
VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextval, 463, 108, '0');
INSERT INTO T_PRODUKT_2_PHYSIKTYP (id, prod_id, physiktyp, virtuell)
VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextval, 463, 515, '0');

-- T_BA_VERL_AEND_PROD_2_GRUPPE
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (prod_id, ba_verl_aend_gruppe_id)
VALUES(463, 1);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (prod_id, ba_verl_aend_gruppe_id)
VALUES(463, 2);

-- T_PRODUKT_2_SCHNITTSTELLE
insert into T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID)
values (463, 10);

-- T_PRODUKT
update t_produkt set Vier_Draht=1 where prod_id = 463;


-- Entferne AK-Connect Panel
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 6, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 7, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 8, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 19, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 20, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 21, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 22, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 23, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 24, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 25, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 26, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 39, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 42, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 43, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 44, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 45, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 46, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 47, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 48, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 450, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 451, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 452, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 453, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 454, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 455, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 208, 456, 'de.augustakom.hurrican.model.cc.Produkt');

delete from T_GUI_MAPPING where ID = 121;

-- Entferne QoS-Panel
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 6, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 7, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 8, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 19, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 20, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 21, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 22, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 23, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 24, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 25, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 26, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 39, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 42, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 43, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 44, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 45, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 46, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 47, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 48, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 450, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 451, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 452, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 453, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 454, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 455, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 213, 456, 'de.augustakom.hurrican.model.cc.Produkt');

delete from T_GUI_MAPPING where ID = 238;

-- Entferne VPN-Panel
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 6, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 7, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 8, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 19, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 20, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 21, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 22, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 23, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 24, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 25, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 26, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 39, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 42, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 43, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 44, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 45, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 46, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 47, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 48, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 450, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 451, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 452, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 453, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 454, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 455, 'de.augustakom.hurrican.model.cc.Produkt');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
values (S_T_GUI_MAPPING_0.nextval, 205, 456, 'de.augustakom.hurrican.model.cc.Produkt');

delete from T_GUI_MAPPING where ID = 99;


insert into T_EXT_SERVICE_PROVIDER (ID, NAME, FIRSTNAME, STREET, HOUSE_NUM, POSTAL_CODE, CITY, PHONE, MAIL, FAX, CONTACT_TYPE) 
values (S_T_EXT_SERVICE_PROVIDER_0.nextval, 'Stadtwerke München', null, 'Emmy-Noether-Str.', '2', '80992', 'München', null, 'ut.service@swm.de', null, null);

update T_PRODUKT_MAPPING set MAPPING_PART_TYPE = 'phone' where MAPPING_GROUP = 445;
update T_PRODUKT_MAPPING set MAPPING_PART_TYPE = 'phone' where MAPPING_GROUP = 446;

insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) 
values (2, 45729, null, 3007, 'Premium MGA');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) 
values (2, 45691, null, 3007, 'Premium MGA + DSL');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) 
values (10, 45731, null, 3007, 'Premium PMX');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) 
values (1, 46101, null, 3007, 'Premium Analog + DSL');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) 
values (1, 45728, null, 3007, 'Premium Analog');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) 
values (9, 45730, null, 3007, 'Premium TK');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) 
values (9, 45692, null, 3007, 'Premium TK + DSL');
insert into T_LB_2_PRODUKT (LB_ID, LEISTUNG__NO, PROTOKOLL_LEISTUNG__NO, PRODUCT_OE__NO, DESCRIPTION) 
values (9, 45701, null, 3007, 'Premium weitere TK ');
