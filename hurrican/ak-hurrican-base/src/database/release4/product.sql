--
-- Skript zur Anlage von Produkten fuer die Einfuehrung 
-- der IP-VPN Anschlussvarianten
--

--
-- SDSL Partner 4000
--
INSERT INTO T_PRODUKT (
  prod_id
, produktgruppe_id
, anschlussart
, leitungsart
, aktions_id
, braucht_dn
, gueltig_von
, gueltig_bis
, auftragserstellung
, ltgnr_vors
, ltgnr_anlegen
, braucht_buendel
, elverlauf
, abrechnung_in_hurrican
, endstellen_typ
, beschreibung
, account_vors
, li_nr
, check_child
, export_kdp_m
, create_kdp_account_report
, verteilung_durch
, verlauf_chain_id
, verlauf_cancel_chain_id
, ba_change_realdate
, create_ap_address
, assign_iad
, tdn_kind_of_use_product
, tdn_kind_of_use_type
) VALUES (
  297                                   -- prod_id
, 8                                     -- produktgruppe_id
, 'SDSL Partner 4000'                   -- anschlussart
, 60                                    -- leitungsart
, 1                                     -- aktions_id
, 0                                     -- braucht_dn
, to_date('01.11.2009', 'dd.mm.yyyy')   -- gueltig_von
, to_date('01.01.2200', 'dd.mm.yyyy')   -- gueltig_bis
, 0                                     -- auftragserstellung
, 'DV'                                  -- ltgnr_vors
, 1                                     -- ltgnr_anlegen
, 0                                     -- braucht_buendel
, 1                                     -- elverlauf
, 0                                     -- abrechnung_in hurrican
, 1                                     -- endstellen_typ
, 'von uns bei Fremdcarrier bestellt'   -- beschreibung
, 'XS'                                  -- account_vors
, 0                                     -- li_nr
, 0                                     -- check_child
, 1                                     -- export_kdp_m
, 1                                     -- create_kdp_account_report
, 11                                    -- verteilung_durch
, 20                                    -- verlauf_chain_id
, 15                                    -- verlauf_cancel_chain_id
, 1                                     -- ba_change_realdate
, 0                                     -- create_ap_address
, 0                                     -- assign_iad
, 'V'                                   -- tdn_kind_of_use_product
, 'L'                                   -- tdn_kind_of_use_type
);

--
-- SDSL Partner 10000
--
INSERT INTO T_PRODUKT (
  prod_id
, produktgruppe_id
, anschlussart
, leitungsart
, aktions_id
, braucht_dn
, gueltig_von
, gueltig_bis
, auftragserstellung
, ltgnr_vors
, ltgnr_anlegen
, braucht_buendel
, elverlauf
, abrechnung_in_hurrican
, endstellen_typ
, beschreibung
, account_vors
, li_nr
, check_child
, export_kdp_m
, create_kdp_account_report
, verteilung_durch
, verlauf_chain_id
, verlauf_cancel_chain_id
, ba_change_realdate
, create_ap_address
, assign_iad
, tdn_kind_of_use_product
, tdn_kind_of_use_type
) VALUES (
  298                                   -- prod_id
, 8                                     -- produktgruppe_id
, 'SDSL Partner 10000'                  -- anschlussart
, 4                                     -- leitungsart
, 1                                     -- aktions_id
, 0                                     -- braucht_dn
, to_date('01.11.2009', 'dd.mm.yyyy')   -- gueltig_von
, to_date('01.01.2200', 'dd.mm.yyyy')   -- gueltig_bis
, 0                                     -- auftragserstellung
, 'DV'                                  -- ltgnr_vors
, 1                                     -- ltgnr_anlegen
, 0                                     -- braucht_buendel
, 1                                     -- elverlauf
, 0                                     -- abrechnung_in hurrican
, 1                                     -- endstellen_typ
, 'von uns bei Fremdcarrier bestellt'   -- beschreibung
, 'XS'                                  -- account_vors
, 0                                     -- li_nr
, 0                                     -- check_child
, 1                                     -- export_kdp_m
, 1                                     -- create_kdp_account_report
, 11                                    -- verteilung_durch
, 20                                    -- verlauf_chain_id
, 15                                    -- verlauf_cancel_chain_id
, 1                                     -- ba_change_realdate
, 0                                     -- create_ap_address
, 0                                     -- assign_iad
, 'V'                                   -- tdn_kind_of_use_product
, 'L'                                   -- tdn_kind_of_use_type
);

--
-- SDSL Partner 15000
--
INSERT INTO T_PRODUKT (
  prod_id
, produktgruppe_id
, anschlussart
, leitungsart
, aktions_id
, braucht_dn
, gueltig_von
, gueltig_bis
, auftragserstellung
, ltgnr_vors
, ltgnr_anlegen
, braucht_buendel
, elverlauf
, abrechnung_in_hurrican
, endstellen_typ
, beschreibung
, account_vors
, li_nr
, check_child
, export_kdp_m
, create_kdp_account_report
, verteilung_durch
, verlauf_chain_id
, verlauf_cancel_chain_id
, ba_change_realdate
, create_ap_address
, assign_iad
, tdn_kind_of_use_product
, tdn_kind_of_use_type
) VALUES (
  299                                   -- prod_id
, 8                                     -- produktgruppe_id
, 'SDSL Partner 15000'                  -- anschlussart
, 4                                    -- leitungsart
, 1                                     -- aktions_id
, 0                                     -- braucht_dn
, to_date('01.11.2009', 'dd.mm.yyyy')   -- gueltig_von
, to_date('01.01.2200', 'dd.mm.yyyy')   -- gueltig_bis
, 0                                     -- auftragserstellung
, 'DV'                                  -- ltgnr_vors
, 1                                     -- ltgnr_anlegen
, 0                                     -- braucht_buendel
, 1                                     -- elverlauf
, 0                                     -- abrechnung_in hurrican
, 1                                     -- endstellen_typ
, 'von uns bei Fremdcarrier bestellt'   -- beschreibung
, 'XS'                                  -- account_vors
, 0                                     -- li_nr
, 0                                     -- check_child
, 1                                     -- export_kdp_m
, 1                                     -- create_kdp_account_report
, 11                                    -- verteilung_durch
, 20                                    -- verlauf_chain_id
, 15                                    -- verlauf_cancel_chain_id
, 1                                     -- ba_change_realdate
, 0                                     -- create_ap_address
, 0                                     -- assign_iad
, 'V'                                   -- tdn_kind_of_use_product
, 'L'                                   -- tdn_kind_of_use_type
);


--
-- T_PRODUKT_MAPPING
--
INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE)
VALUES (297, 297, 297, ' ');

INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE)
VALUES (298, 298, 298, ' ');

INSERT INTO T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE)
VALUES (299, 299, 299, ' ');


--
-- T_PRODUKT_2_PHYSIKTYP
--
INSERT INTO T_PRODUKT_2_PHYSIKTYP (id, prod_id, physiktyp)
VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextval, 297, 9);

INSERT INTO T_PRODUKT_2_PHYSIKTYP (id, prod_id, physiktyp)
VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextval, 298, 9);

INSERT INTO T_PRODUKT_2_PHYSIKTYP (id, prod_id, physiktyp)
VALUES (S_T_PRODUKT_2_PHYSIKTYP_0.nextval, 299, 9);


--
-- T_PROD_2_TECH_LEISTUNG
--
INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 297, 50);

INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 298, 50);

INSERT INTO T_PROD_2_TECH_LEISTUNG (id, prod_id, tech_ls_id) 
VALUES (S_T_PROD_2_TECH_LEISTUNG_0.nextval, 299, 50);


--
-- T_BA_VERL_AEND_PROD_2_GRUPPE
--
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (prod_id, ba_verl_aend_gruppe_id) 
VALUES(297, 1);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (prod_id, ba_verl_aend_gruppe_id) 
VALUES(297, 2);

INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (prod_id, ba_verl_aend_gruppe_id) 
VALUES(298, 1);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (prod_id, ba_verl_aend_gruppe_id) 
VALUES(298, 2);

INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (prod_id, ba_verl_aend_gruppe_id) 
VALUES(299, 1);
INSERT INTO T_BA_VERL_AEND_PROD_2_GRUPPE (prod_id, ba_verl_aend_gruppe_id) 
VALUES(299, 2);