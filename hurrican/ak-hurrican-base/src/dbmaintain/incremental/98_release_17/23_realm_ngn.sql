-- neue Produktgruppe anlegen
Insert into T_PRODUKTGRUPPE
   (ID, PRODUKTGRUPPE, AM_RESPONSIBILITY, REALM, VERSION)
 Values
   (29, 'FTTX Phone', 1200, 'ngn.mnet-online.de', 0);


-- Produkt 511 der neuen Produktgruppe zuordnen
update t_produkt set produktgruppe_id=29 where prod_id=511;


-- Auftrags-GUI fuer ProduktGruppe konfigurieren
Insert into T_GUI_MAPPING
   (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT, VERSION)
 Values
   (S_T_GUI_MAPPING_0.nextVal, 202, 29, 'de.augustakom.hurrican.model.cc.ProduktGruppe', 0);
Insert into T_GUI_MAPPING
   (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT, VERSION)
 Values
   (S_T_GUI_MAPPING_0.nextVal, 203, 29, 'de.augustakom.hurrican.model.cc.ProduktGruppe', 0);
Insert into T_GUI_MAPPING
   (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT, VERSION)
 Values
   (S_T_GUI_MAPPING_0.nextVal, 206, 29, 'de.augustakom.hurrican.model.cc.ProduktGruppe', 0);
Insert into T_GUI_MAPPING
   (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT, VERSION)
 Values
   (S_T_GUI_MAPPING_0.nextVal, 212, 29, 'de.augustakom.hurrican.model.cc.ProduktGruppe', 0);
Insert into T_GUI_MAPPING
   (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT, VERSION)
 Values
   (S_T_GUI_MAPPING_0.nextVal, 213, 29, 'de.augustakom.hurrican.model.cc.ProduktGruppe', 0);
Insert into T_GUI_MAPPING
   (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT, VERSION)
 Values
   (S_T_GUI_MAPPING_0.nextVal, 205, 29, 'de.augustakom.hurrican.model.cc.ProduktGruppe', 0);

