-- Site-to-Site Panel dem Produkt zuordnen

insert into T_GUI_DEFINITION (ID, CLASS, TYPE, NAME, TEXT, TOOLTIP, ICON, ORDER_NO, ADD_SEPARATOR, ACTIVE)
  values (216, 'de.augustakom.hurrican.gui.auftrag.AuftragIPSecSiteToSitePanel',
  'PANEL', 'ipsec.site.to.site', 'IPSec S2S', 'Panel mit den IPSec Site-to-Site Daten',
  null, 30, null, '1');
insert into T_GUI_MAPPING (ID, GUI_ID, REFERENZ_ID, REFERENZ_HERKUNFT)
  values (S_T_GUI_MAPPING_0.nextVal, 216, 444, 'de.augustakom.hurrican.model.cc.Produkt');

update T_PRODUKT set BRAUCHT_BUENDEL='0' where PROD_ID=444;
