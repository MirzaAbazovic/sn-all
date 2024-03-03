-- Physiktyp fuer ADBF2 Baugruppen hinzufuegen (only Variante, also ohne Phone)
INSERT INTO T_PHYSIKTYP
(ID, NAME, BESCHREIBUNG, HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP, CPS_TRANSFER_METHOD, VERSION)
VALUES
  (806, 'FTTH_RF', 'FTTH TV Versorgung', NULL, NULL, 'RF', 3, NULL, 0);

INSERT INTO T_PHYSIKTYP
(ID, NAME, BESCHREIBUNG, HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP, CPS_TRANSFER_METHOD, VERSION)
VALUES
  (807, 'FTTH_POTS', 'FTTH Telefonie', NULL, NULL, 'POTS', 1, NULL, 0);

INSERT INTO T_PHYSIKTYP
(ID, NAME, BESCHREIBUNG, HVT_TECHNIK_ID, MAX_BANDWIDTH, HW_SCHNITTSTELLE, PT_GROUP, CPS_TRANSFER_METHOD, VERSION)
VALUES
  (808, 'FTTH_ETH', 'FTTH Ethernet Breitband Versorgung', NULL, 100000, 'ETH', 2, NULL, 0);


-- FTTH_RF Physiktyp mit Baugruppen mappen
INSERT INTO T_HW_BG_TYP_2_PHYSIK_TYP
(ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID, VERSION)
VALUES
  (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval, (SELECT
                                            bgt.id
                                          FROM t_hw_baugruppen_typ bgt
                                          WHERE bgt.name = 'I-221E-A_RF'), 806, 0);

INSERT INTO T_HW_BG_TYP_2_PHYSIK_TYP
(ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID, VERSION)
VALUES
  (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval, (SELECT
                                            bgt.id
                                          FROM t_hw_baugruppen_typ bgt
                                          WHERE bgt.name = 'I-010G-P_RF'), 806, 0);

INSERT INTO T_HW_BG_TYP_2_PHYSIK_TYP
(ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID, VERSION)
VALUES
  (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval, (SELECT
                                            bgt.id
                                          FROM t_hw_baugruppen_typ bgt
                                          WHERE bgt.name = 'I-241G-Q_RF'), 806, 0);

INSERT INTO T_HW_BG_TYP_2_PHYSIK_TYP
(ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID, VERSION)
VALUES
  (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval, (SELECT
                                            bgt.id
                                          FROM t_hw_baugruppen_typ bgt
                                          WHERE bgt.name = 'HG8242_RF'), 806, 0);

INSERT INTO T_HW_BG_TYP_2_PHYSIK_TYP
(ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID, VERSION)
VALUES
  (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval, (SELECT
                                            bgt.id
                                          FROM t_hw_baugruppen_typ bgt
                                          WHERE bgt.name = 'HG865_RF'), 806, 0);


-- FTTH_POTS Physiktyp mit Baugruppen mappen
INSERT INTO T_HW_BG_TYP_2_PHYSIK_TYP
(ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID, VERSION)
VALUES
  (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval, (SELECT
                                            bgt.id
                                          FROM t_hw_baugruppen_typ bgt
                                          WHERE bgt.name = 'I-221E-A_POTS'), 807, 0);

INSERT INTO T_HW_BG_TYP_2_PHYSIK_TYP
(ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID, VERSION)
VALUES
  (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval, (SELECT
                                            bgt.id
                                          FROM t_hw_baugruppen_typ bgt
                                          WHERE bgt.name = 'I-010G-P_POTS'), 807, 0);

INSERT INTO T_HW_BG_TYP_2_PHYSIK_TYP
(ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID, VERSION)
VALUES
  (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval, (SELECT
                                            bgt.id
                                          FROM t_hw_baugruppen_typ bgt
                                          WHERE bgt.name = 'I-241G-Q_POTS'), 807, 0);

INSERT INTO T_HW_BG_TYP_2_PHYSIK_TYP
(ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID, VERSION)
VALUES
  (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval, (SELECT
                                            bgt.id
                                          FROM t_hw_baugruppen_typ bgt
                                          WHERE bgt.name = 'HG8242_POTS'), 807, 0);

INSERT INTO T_HW_BG_TYP_2_PHYSIK_TYP
(ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID, VERSION)
VALUES
  (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval, (SELECT
                                            bgt.id
                                          FROM t_hw_baugruppen_typ bgt
                                          WHERE bgt.name = 'HG865_POTS'), 807, 0);


-- FTTH_ETH Physiktyp mit Baugruppen mappen
INSERT INTO T_HW_BG_TYP_2_PHYSIK_TYP
(ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID, VERSION)
VALUES
  (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval, (SELECT
                                            bgt.id
                                          FROM t_hw_baugruppen_typ bgt
                                          WHERE bgt.name = 'I-221E-A_ETH'), 808, 0);

INSERT INTO T_HW_BG_TYP_2_PHYSIK_TYP
(ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID, VERSION)
VALUES
  (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval, (SELECT
                                            bgt.id
                                          FROM t_hw_baugruppen_typ bgt
                                          WHERE bgt.name = 'I-010G-P_ETH'), 808, 0);

INSERT INTO T_HW_BG_TYP_2_PHYSIK_TYP
(ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID, VERSION)
VALUES
  (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval, (SELECT
                                            bgt.id
                                          FROM t_hw_baugruppen_typ bgt
                                          WHERE bgt.name = 'I-241G-Q_ETH'), 808, 0);

INSERT INTO T_HW_BG_TYP_2_PHYSIK_TYP
(ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID, VERSION)
VALUES
  (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval, (SELECT
                                            bgt.id
                                          FROM t_hw_baugruppen_typ bgt
                                          WHERE bgt.name = 'HG8242_ETH'), 808, 0);

INSERT INTO T_HW_BG_TYP_2_PHYSIK_TYP
(ID, BAUGRUPPEN_TYP_ID, PHYSIKTYP_ID, VERSION)
VALUES
  (S_T_HW_BG_TYP_2_PHYSIK_TYP_0.nextval, (SELECT
                                            bgt.id
                                          FROM t_hw_baugruppen_typ bgt
                                          WHERE bgt.name = 'HG865_ETH'), 808, 0);


-- Produktmapping auf Physiktyp
INSERT INTO T_PRODUKT_2_PHYSIKTYP
(ID, PROD_ID, PHYSIKTYP, PHYSIKTYP_ADDITIONAL, VIRTUELL,
 PARENTPHYSIKTYP_ID, VERSION)
VALUES
  (S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, 500, 806, NULL, NULL,
   NULL, 0);

UPDATE T_PRODUKT_2_PHYSIKTYP
SET PHYSIKTYP = 807
WHERE PROD_ID = 511 AND PHYSIKTYP = 803;

UPDATE T_PRODUKT_2_PHYSIKTYP
SET PHYSIKTYP = 808
WHERE PROD_ID = 512 AND PHYSIKTYP = 803;

UPDATE T_PRODUKT_2_PHYSIKTYP
SET PHYSIKTYP = 808
WHERE PROD_ID = 513 AND PHYSIKTYP = 803;

INSERT INTO T_PRODUKT_2_PHYSIKTYP
(ID, PROD_ID, PHYSIKTYP, PHYSIKTYP_ADDITIONAL, VIRTUELL,
 PARENTPHYSIKTYP_ID, VERSION)
VALUES
  (S_T_PRODUKT_2_PHYSIKTYP_0.nextVal, 521, 806, NULL, NULL,
   NULL, 0);

UPDATE T_PRODUKT_2_PHYSIKTYP
SET PHYSIKTYP = 808
WHERE PROD_ID = 540 AND PHYSIKTYP = 803;

UPDATE T_PRODUKT_2_PHYSIKTYP
SET PHYSIKTYP = 808
WHERE PROD_ID = 541 AND PHYSIKTYP = 803;

UPDATE T_PRODUKT_2_PHYSIKTYP
SET PHYSIKTYP = 808
WHERE PROD_ID = 570 AND PHYSIKTYP = 803;

