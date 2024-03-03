--
-- SQL-Statements generiert die notwendigen Tabellen 
-- fuer die Innenauftraege.
--

DROP TABLE T_IA_MAT_ENTNAHME_ARTIKEL;
DROP TABLE T_IA_MAT_ENTNAHME;
DROP TABLE T_IA_BUDGET;
DROP TABLE T_LAGER;
DROP TABLE T_IA;
DROP TABLE T_IA_MATERIAL;

CREATE TABLE T_IA_MATERIAL (
       ID INTEGER(9) NOT NULL AUTO_INCREMENT
     , ARTIKEL VARCHAR(75) NOT NULL
     , TEXT VARCHAR(200) NOT NULL
     , MATERIAL_NR VARCHAR(25) NOT NULL
     , EINZELPREIS DECIMAL(10, 2)
     , PRIMARY KEY (ID)
);

CREATE TABLE T_IA (
       ID INTEGER(9) NOT NULL AUTO_INCREMENT
     , AUFTRAG_ID INTEGER(9) NOT NULL
     , IA_NUMMER VARCHAR(20) NOT NULL
     , PRIMARY KEY (ID)
)TYPE=InnoDB;

CREATE TABLE T_LAGER (
       ID INTEGER(9) NOT NULL AUTO_INCREMENT
     , NAME VARCHAR(50) NOT NULL
     , STRASSE VARCHAR(50)
     , NUMMER VARCHAR(10)
     , PLZ VARCHAR(10)
     , ORT VARCHAR(50)
     , PRIMARY KEY (ID)
)TYPE=InnoDB;

CREATE TABLE T_IA_BUDGET (
       ID INTEGER(9) NOT NULL AUTO_INCREMENT
     , IA_ID INTEGER(9) NOT NULL
     , PROJEKTLEITER VARCHAR(30) NOT NULL
     , BUDGET DECIMAL(10, 2)
     , CREATED_AT DATE NOT NULL
     , CLOSED_AT DATE
     , STORNIERT TINYINT(1)
     , PRIMARY KEY (ID)
)TYPE=InnoDB;

CREATE TABLE T_IA_MAT_ENTNAHME (
       ID INTEGER(9) NOT NULL AUTO_INCREMENT
     , BUDGET_ID INTEGER(9) NOT NULL
     , ENTNAHMETYP TINYINT(2) NOT NULL
     , LAGER_ID INTEGER(9) NOT NULL
     , CREATED_AT DATE NOT NULL
     , CREATED_FROM VARCHAR(20) NOT NULL
     , PRIMARY KEY (ID)
)TYPE=InnoDB;

CREATE TABLE T_IA_MAT_ENTNAHME_ARTIKEL (
       ID INTEGER(9) NOT NULL AUTO_INCREMENT
     , MAT_ENTNAHME_ID INTEGER(9) NOT NULL
     , ARTIKEL VARCHAR(50) NOT NULL
     , MATERIAL_NR VARCHAR(25) NOT NULL
     , ANZAHL DECIMAL(10, 2) NOT NULL
     , EINZELPREIS DECIMAL(10, 2)
     , REMOVED_AT DATE
     , REMOVED_FROM VARCHAR(30)
     , PRIMARY KEY (ID)
)TYPE=InnoDB;

ALTER TABLE T_IA
  ADD CONSTRAINT FK_IA_2_AUFTRAG
      FOREIGN KEY (AUFTRAG_ID)
      REFERENCES T_AUFTRAG (ID);

ALTER TABLE T_IA
  ADD CONSTRAINT UQ_T_IA
      UNIQUE (IA_NUMMER);
      
ALTER TABLE T_IA
  ADD CONSTRAINT UQ_T_IA_AUFTRAGID
      UNIQUE (AUFTRAG_ID);

ALTER TABLE T_IA_BUDGET
  ADD CONSTRAINT FK_IABUDGET_2_IA
      FOREIGN KEY (IA_ID)
      REFERENCES T_IA (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;

ALTER TABLE T_IA_MAT_ENTNAHME
  ADD CONSTRAINT FK_IAMATENT_2_BUDGET
      FOREIGN KEY (BUDGET_ID)
      REFERENCES T_IA_BUDGET (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;

ALTER TABLE T_IA_MAT_ENTNAHME
  ADD CONSTRAINT FK_IAMATENT_2_LAGER
      FOREIGN KEY (LAGER_ID)
      REFERENCES T_LAGER (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;

ALTER TABLE T_IA_MAT_ENTNAHME_ARTIKEL
  ADD CONSTRAINT FK_IA_ENTART_2_ENTNAHME
      FOREIGN KEY (MAT_ENTNAHME_ID)
      REFERENCES T_IA_MAT_ENTNAHME (ID);



-- Daten...
insert into t_lager (id, name, strasse, nummer, plz, ort) 
	values (1, 'Lager Augsburg', 'Curt-Frenzel-Str.', '4', '86167', 'Augsburg');

-- Zuordnung der Registerseite 'Innenauftrag' zu den Produktgruppen Connect, Phone und SDSL
-- Definition des Panels fuer die Innenauftraege
INSERT INTO T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ACTIVE)				
   VALUES (211, "de.augustakom.hurrican.gui.auftrag.innenauftrag.InnenauftragPanel", "PANEL", 
   "innenauftrag.panel", "Innenauftrag", "Panel fuer die Anzeige der zugeordneten Innenaufträge", null, 80, 1);	
-- Zuordnung zu den Produktgruppen (noch nicht eingespielt!!)
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (211, 1, "de.augustakom.hurrican.model.cc.ProduktGruppe");   
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (211, 2, "de.augustakom.hurrican.model.cc.ProduktGruppe");  
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (211, 4, "de.augustakom.hurrican.model.cc.ProduktGruppe");  
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (211, 7, "de.augustakom.hurrican.model.cc.ProduktGruppe");  
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (211, 9, "de.augustakom.hurrican.model.cc.ProduktGruppe");  
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (211, 11, "de.augustakom.hurrican.model.cc.ProduktGruppe");  
INSERT INTO T_GUI_MAPPING (GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT) 
	VALUES (211, 18, "de.augustakom.hurrican.model.cc.ProduktGruppe");  

	
--
-- Registry-Eintraege
insert into t_registry (ID, NAME, STR_VALUE) values (5002, 'EMail-Host', '10.1.2.133');	
insert into t_registry (ID, NAME, STR_VALUE) values (5003, 'EMail Fibu', 'i.regauer@augustakom.de');




