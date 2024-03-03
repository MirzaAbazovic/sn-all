--
-- Skript fuer die Anlage von Connect Basic
--

INSERT INTO T_PRODUKT (
  prod_id
, produktgruppe_id
, anschlussart
, prod_name_pattern
, aktions_id
, braucht_dn
, dn_moeglich
, dn_block
, braucht_dn4account
, gueltig_von
, gueltig_bis
, auftragserstellung
, ltgnr_vors
, ltgnr_anlegen
, braucht_buendel
, elverlauf
, abrechnung_in_hurrican
, endstellen_typ
, account_vors
, li_nr
, vpn_physik
, projektierung
, is_parent
, check_child
, is_combi_produkt
, auto_product_change
, export_kdp_m
, create_kdp_account_report
, verteilung_durch
, ba_ruecklaeufer
, braucht_vpi_vci
, projektierung_chain_id
, verlauf_chain_id
, verlauf_cancel_chain_id
, ba_change_realdate
, create_ap_address
, assign_iad
, tdn_kind_of_use_product
, tdn_kind_of_use_type
) VALUES (
  462                                   -- prod_id
, 2                                     -- produktgruppe_id
, 'Connect Basic'                       -- anschlussart
, 'Connect Basic'                       -- prod_name_pattern
, 1                                     -- aktions_id
, 0                                     -- braucht_dn
, 0                                     -- dn_moeglich
, 0                                     -- dn_block
, 0                                     -- braucht_dn4account
, to_date('01.11.2009', 'dd.mm.yyyy')   -- gueltig_von
, to_date('01.01.2200', 'dd.mm.yyyy')   -- gueltig_bis
, 0                                     -- auftragserstellung
, 'FV'                                  -- ltgnr_vors
, 1                                     -- ltgnr_anlegen
, 0                                     -- braucht_buendel
, 1                                     -- elverlauf
, 0                                     -- abrechnung_in hurrican
, 2                                     -- endstellen_typ
, 'XC'                                  -- account_vors
, 0                                     -- li_nr
, 1                                     -- vpn_physik
, 1                                     -- projektierung
, 0                                     -- is_parent
, 0                                     -- check_child
, 0                                     -- is_combi_produkt
, 0                                     -- auto_product_change
, 0                                     -- export_kdp_m
, 0                                     -- create_kdp_account_report
, 11                                    -- verteilung_durch
, 1                                     -- ba_ruecklaeufer
, 0                                     -- braucht_vpi_vci
, 34                                    -- projektierung_chain_id
, 33                                    -- verlauf_chain_id
, 15                                    -- verlauf_cancel_chain_id
, 1                                     -- ba_change_realdate
, 1                                     -- create_ap_address
, 0                                     -- assign_iad
, 'F'                                   -- tdn_kind_of_use_product
, '?'                                   -- tdn_kind_of_use_type
);

--
-- T_PRODUKT_MAPPING
--
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE)
VALUES (462, 462, 462, ' ');

--
-- T_PRODUKT_2_PHYSIKTYP
--
INSERT INTO T_PRODUKT_2_PHYSIKTYP (id, prod_id, physiktyp)
VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextval, 462, 108);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (id, prod_id, physiktyp, virtuell)
VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextval, 462, 515, '0');

--
-- T_PROD_2_TECH_LEISTUNG
--
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 200, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 218, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 219, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 220, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 221, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 222, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 223, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 224, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 225, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 226, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 227, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 228, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 229, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 230, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 231, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 232, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 233, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 234, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 235, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 236, '0');
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id, is_default) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 462, 237, '0');

--
-- T_BA_VERL_AEND_PROD_2_GRUPPE
--
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (prod_id, ba_verl_aend_gruppe_id) 
VALUES(462, 1);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (prod_id, ba_verl_aend_gruppe_id) 
VALUES(462, 2);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (prod_id, ba_verl_aend_gruppe_id) 
VALUES(462, 3);