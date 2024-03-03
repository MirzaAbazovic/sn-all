-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Physiktyp DSL2+ 
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
INSERT INTO T_SCHNITTSTELLE (ID, SCHNITTSTELLE) VALUES (32, 'ADSL2+');
INSERT INTO T_PHYSIKTYP (ID, NAME, BESCHREIBUNG, HVT_TECHNIK_ID) VALUES (13, 'ADSL2+', null, 1);
INSERT INTO T_PHYSIKTYP (ID, NAME, BESCHREIBUNG, HVT_TECHNIK_ID) VALUES (513, 'ADSL2+ (H)', null, 2);

-- Physiktyp ADSL2+ auch den 'alten' ADSL-Produkten zuordnen
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (9, 13);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (56, 13);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (57, 13);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (320, 13);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (315, 13);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (316, 13);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (317, 13);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (321, 13);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (324, 13);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (325, 13);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (326, 13);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (9, 513);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (56, 513);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (57, 513);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (320, 513);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (315, 513);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (316, 513);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (317, 513);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (321, 513);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (324, 513);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (325, 513);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (326, 513);

--
-- Konfiguration fuer die Maxi-Produkte
--

-- Maxi Analog (322)
UPDATE T_PRODUKT SET LI_NR=0, BRAUCHT_BUENDEL=0, LEISTUNG='' where PROD_ID=322;
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (322, 7);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (322, 3);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (322, 503);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (322, 3);

-- Maxi ISDN (323)
UPDATE T_PRODUKT SET LI_NR=0, BRAUCHT_BUENDEL=0, LEISTUNG='', LEITUNGSART=52 where PROD_ID=323;
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (323, 22);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (323, 1);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (323, 501);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (323, 3);

-- Maxi DSL 2000 (324)
UPDATE T_PRODUKT SET PROJEKTIERUNG=0 WHERE PROD_ID=324;
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (324, 8);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (324, 7);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (324, 507);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (324, 2);

-- Maxi DSL 3000 (325)
UPDATE T_PRODUKT SET PROJEKTIERUNG=0 WHERE PROD_ID=325;
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (325, 8);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (325, 7);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (325, 507);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (325, 2);

-- Maxi DSL 6000 (326)
UPDATE T_PRODUKT SET PROJEKTIERUNG=0 WHERE PROD_ID=326;
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (326, 8);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (326, 7);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (326, 507);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (326, 2);

-- Maxi MAX (327)
DELETE FROM T_PRODUKT_2_SCHNITTSTELLE WHERE PROD_ID=327;
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (327, 32);
UPDATE T_PRODUKT SET PROJEKTIERUNG=0 WHERE PROD_ID=327;
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (327, 13);
INSERT INTO T_PRODUKT_2_PHYSIKTYP (PROD_ID, PHYSIKTYP) VALUES (327, 513);
INSERT INTO T_SPERRE_VERTEILUNG (PROD_ID, ABTEILUNG_ID) VALUES (327, 2);

-- Maxi DSL analog (328)
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (328, 7);

-- Maxi DSL ISDN (329)
INSERT INTO T_PRODUKT_2_SCHNITTSTELLE (PROD_ID, SCHNITTSTELLE_ID) VALUES (329, 22);

--
-- GUI-Updates, die fuer die Maxi-Produkte durchgefuehrt werden muessen.
--

INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (202, 17, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (203, 17, "de.augustakom.hurrican.model.cc.ProduktGruppe");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (205, 17, "de.augustakom.hurrican.model.cc.ProduktGruppe");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (206, 17, "de.augustakom.hurrican.model.cc.ProduktGruppe");
	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (314, 324, "de.augustakom.hurrican.model.cc.Produkt");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (314, 325, "de.augustakom.hurrican.model.cc.Produkt");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (314, 326, "de.augustakom.hurrican.model.cc.Produkt");
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (314, 327, "de.augustakom.hurrican.model.cc.Produkt");	
	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 324, "de.augustakom.hurrican.model.cc.Produkt");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 325, "de.augustakom.hurrican.model.cc.Produkt");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 326, "de.augustakom.hurrican.model.cc.Produkt");	
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (315, 327, "de.augustakom.hurrican.model.cc.Produkt");	

 
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Definition von ServiceCommands und Chains fuer DSL2+ (MaxiMAX)
-- +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION) 
	VALUES (23, 'assert.equal.uevt', 'de.augustakom.hurrican.service.cc.impl.command.physik.AssertEqualUetvCommand', 
	'PHYSIK', 'Ueberprueft, ob die Uebertragungsverfahren des alten und neuen EQ-Out Stiftes identisch sind. Ist dies nicht der Fall, wird eine Exception erzeugt.');

INSERT INTO T_SERVICE_CHAIN (ID, NAME, DESCRIPTION)
	VALUES (6, 'DSL-Kreuzung (DSL <--> DSL2+)', 'Service-Chain-Definition fuer eine DSL-Kreuzung zwischen DSL und DSL2+');

INSERT INTO T_SERVICECHAIN_2_COMMAND (CHAIN_ID, COMMAND_ID, ORDER_NO) VALUES (6, 13, 1);
INSERT INTO T_SERVICECHAIN_2_COMMAND (CHAIN_ID, COMMAND_ID, ORDER_NO) VALUES (6, 23, 2);
INSERT INTO T_SERVICECHAIN_2_COMMAND (CHAIN_ID, COMMAND_ID, ORDER_NO) VALUES (6, 18, 3);
INSERT INTO T_SERVICECHAIN_2_COMMAND (CHAIN_ID, COMMAND_ID, ORDER_NO) VALUES (6, 16, 4);
INSERT INTO T_SERVICECHAIN_2_COMMAND (CHAIN_ID, COMMAND_ID, ORDER_NO) VALUES (6, 6, 5);
INSERT INTO T_SERVICECHAIN_2_COMMAND (CHAIN_ID, COMMAND_ID, ORDER_NO) VALUES (6, 4, 6);
INSERT INTO T_SERVICECHAIN_2_COMMAND (CHAIN_ID, COMMAND_ID, ORDER_NO) VALUES (6, 10, 7);


-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Prod2Prod-Definitionen fuer Maxi-Produkte
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

-- Maxi-Analog (322)
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (5, 322, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (1, 322, 5002, 8);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (2, 322, 5002, 8);

-- Maxi-ISDN (323)
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (1, 323, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (2, 323, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (5, 323, 5003, 8);

-- Maxi DSL 2000 (324)
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (9, 324, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (56, 324, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (57, 324, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (320, 324, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (315, 324, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (316, 324, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (317, 324, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (321, 324, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 324, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 324, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 324, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 324, 5004, 3);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 324, 5004, 3);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (327, 324, 5004, 3);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 324, 5005, 4);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 324, 5005, 4);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 324, 5005, 4);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (327, 324, 5005, 4);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (9, 324, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (56, 324, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (57, 324, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (320, 324, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (315, 324, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (316, 324, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (317, 324, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (321, 324, 5005, 5);

-- Maxi DSL 3000 (325)
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (9, 325, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (56, 325, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (57, 325, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (320, 325, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (315, 325, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (316, 325, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (317, 325, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (321, 325, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 325, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 325, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 325, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 325, 5004, 3);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 325, 5004, 3);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (327, 325, 5004, 3);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 325, 5005, 4);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 325, 5005, 4);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 325, 5005, 4);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (327, 325, 5005, 4);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (9, 325, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (56, 325, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (57, 325, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (320, 325, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (315, 325, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (316, 325, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (317, 325, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (321, 325, 5005, 5);

-- Maxi DSL 6000 (326)
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (9, 326, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (56, 326, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (57, 326, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (320, 326, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (315, 326, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (316, 326, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (317, 326, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (321, 326, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 326, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 326, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 326, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 326, 5004, 3);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 326, 5004, 3);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (327, 326, 5004, 3);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 326, 5005, 4);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 326, 5005, 4);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 326, 5005, 4);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (327, 326, 5005, 4);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (9, 326, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (56, 326, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (57, 326, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (320, 326, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (315, 326, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (316, 326, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (317, 326, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (321, 326, 5005, 5);

-- maxiDSL auf AK-ADSL1000 (9)
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 9, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 9, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 9, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 9, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 9, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 9, 5005, 5);

-- maxiDSL auf AK-ADSL2000 (56)
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 56, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 56, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 56, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 56, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 56, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 56, 5005, 5);

-- maxiDSL auf AK-ADSL3000 (57)
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 57, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 57, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 57, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 57, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 57, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 57, 5005, 5);

-- maxiDSL auf AK-ADSL6000 (320)
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 320, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 320, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 320, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 320, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 320, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 320, 5005, 5);

-- maxiDSL auf AK-DSLplus1000 (315)
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 315, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 315, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 315, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 315, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 315, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 315, 5005, 5);

-- maxiDSL auf AK-DSLplus2000 (316)
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 316, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 316, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 316, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 316, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 316, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 316, 5005, 5);

-- maxiDSL auf AK-DSLplus3000 (317)
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 317, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 317, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 317, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 317, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 317, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 317, 5005, 5);

-- maxiDSL auf AK-DSLplus3000 (324)
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 321, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 321, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 321, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 321, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 321, 5005, 5);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 321, 5005, 5);

-- MaxiDSL a/b (328)
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (318, 328, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (328, 328, 5000, 1);

-- MaxiDSL S0 (329)
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (1, 329, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (319, 329, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (329, 329, 5000, 1);

-- MaxiDSL S0 auf AK-ADSL S0 (10)
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (329, 10, 5000, 1);

-- MaxiDSL S0 auf AK-ISDNplus (319)
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (329, 319, 5000, 1);

-- MaxiDSL analog auf AK-phoneplus (318)
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (328, 318, 5000, 1);

-- MaxiMAX (327)
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (327, 327, 5000, 1);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (327, 327, 5005, 4);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (9, 327, 5005, 6);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (56, 327, 5005, 6);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (57, 327, 5005, 6);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (320, 327, 5005, 6);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (315, 327, 5005, 6);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (321, 327, 5005, 6);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (324, 327, 5005, 6);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (325, 327, 5005, 6);
INSERT INTO T_PROD_2_PROD (PROD_SRC, PROD_DEST, PHYSIKAEND_TYP, CHAIN_ID) VALUES (326, 327, 5005, 6);

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Verlaufskonfiguration (Neuanlage) fuer Maxi-Produkte
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
INSERT INTO T_BA_VERL_NEU (PROD_ID, DISPO, EWSD, SDH, IPS) VALUES (322, 1, 1, 0, 0);
INSERT INTO T_BA_VERL_NEU (PROD_ID, DISPO, EWSD, SDH, IPS) VALUES (323, 1, 1, 0, 0);
INSERT INTO T_BA_VERL_NEU (PROD_ID, DISPO, EWSD, SDH, IPS, SCT) VALUES (324, 1, 0, 1, 1, 1);
INSERT INTO T_BA_VERL_NEU (PROD_ID, DISPO, EWSD, SDH, IPS, SCT) VALUES (325, 1, 0, 1, 1, 1);
INSERT INTO T_BA_VERL_NEU (PROD_ID, DISPO, EWSD, SDH, IPS, SCT) VALUES (326, 1, 0, 1, 1, 1);
INSERT INTO T_BA_VERL_NEU (PROD_ID, DISPO, EWSD, SDH, IPS, SCT) VALUES (327, 1, 0, 1, 1, 1);
INSERT INTO T_BA_VERL_NEU (PROD_ID, DISPO, EWSD, SDH, IPS) VALUES (328, 1, 1, 0, 0);
INSERT INTO T_BA_VERL_NEU (PROD_ID, DISPO, EWSD, SDH, IPS) VALUES (329, 1, 1, 0, 0);


-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Registry-Eintraege
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
INSERT INTO T_REGISTRY (ID, NAME, STR_VALUE, DESCRIPTION) 
	VALUES (11, 'ANSCHREIBEN_ERECHNUNG_MAXI', 
	'W:\\8 Allgemeines\\Vorlagen\\Maxi\Anschreiben Kundenportal_00,01.doc',
	'Pfad zum Kundenportal-Anschreiben fuer Maxi-Produkte.');
	