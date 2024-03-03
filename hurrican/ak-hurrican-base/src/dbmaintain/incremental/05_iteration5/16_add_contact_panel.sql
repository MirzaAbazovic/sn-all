
insert into T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ADD_SEPARATOR, ACTIVE)
  values (215, 'de.augustakom.hurrican.gui.auftrag.AnsprechpartnerPanel',
  'PANEL', 'auftrag.ansprechpartner', 'Ansprechpartner', 'Panel mit den Kontakten / Ansprechpartner zu dem Auftrag',
  null, 100, null, '1');

insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
  values (11, 215, -99, 'de.augustakom.hurrican.model.cc.ProduktGruppe');

