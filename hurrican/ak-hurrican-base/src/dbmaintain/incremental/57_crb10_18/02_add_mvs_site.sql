-- MVS Enterprise Produkt anlegen
Insert into T_PRODUKTGRUPPE (ID, PRODUKTGRUPPE, AM_RESPONSIBILITY, VERSION)
 values (28, 'MVS Site', 1201, 0);

Insert into T_PRODUKT
   (PROD_ID, PRODUKTGRUPPE_ID, ANSCHLUSSART, AKTIONS_ID, MAX_DN_COUNT, 
    DN_BLOCK, BRAUCHT_DN4ACCOUNT, GUELTIG_VON, GUELTIG_BIS, AUFTRAGSERSTELLUNG, 
    LTGNR_ANLEGEN, BRAUCHT_BUENDEL, ELVERLAUF, ABRECHNUNG_IN_HURRICAN, ENDSTELLEN_TYP, 
    BESCHREIBUNG, VPN_PHYSIK, PROJEKTIERUNG, IS_PARENT, CHECK_CHILD, 
    IS_COMBI_PRODUKT, AUTO_PRODUCT_CHANGE, EXPORT_KDP_M, CREATE_KDP_ACCOUNT_REPORT, VERTEILUNG_DURCH, 
    BA_RUECKLAEUFER, VERLAUF_CHAIN_ID, VERLAUF_CANCEL_CHAIN_ID, BA_CHANGE_REALDATE, CREATE_AP_ADDRESS, 
    ASSIGN_IAD, CPS_PROVISIONING, STATUSMELDUNGEN, CPS_AUTO_CREATION, TDN_KIND_OF_USE_PRODUCT, 
    TDN_KIND_OF_USE_TYPE, VIER_DRAHT, VERSION, MIN_DN_COUNT, CPS_IP_DEFAULT, 
    CPS_MULTI_DRAHT)
 values
   (536, 28, 'MVS Site', 1, 0, 
    '0', '0', TO_DATE('01/01/2012 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), '0', 
    '1', '0', '1', '0', 1, 
    'MVS Site - Sub-Auftrag fuer MVS Konstellationen', '0', '0', '0', '0', 
    '0', '0', '0', '0', 4, 
    '0', NULL, 15, '1', '0', 
    '0', '0', '0', '0', 'M', 
    'V', '0', 6, 0, '0', 
    '0');

-- ServiceChain definieren
Insert into T_SERVICE_CHAIN (ID, NAME, TYPE, VERSION)
 values (37, 'MVS-Site_Check', 'VERLAUF_CHECK', 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, (select c.ID from T_SERVICE_COMMANDS c where c.NAME='verl.check.auftrag.niederlassung'), 37, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 1, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, (select c.ID from T_SERVICE_COMMANDS c where c.NAME='verl.check.billing.order.exist'), 37, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 2, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, (select c.ID from T_SERVICE_COMMANDS c where c.NAME='verl.check.bill.channel'), 37, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 3, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, (select c.ID from T_SERVICE_COMMANDS c where c.NAME='verl.check.dn.count'), 37, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 4, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, (select c.ID from T_SERVICE_COMMANDS c where c.NAME='verl.check.dn.services'), 37, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 5, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, (select c.ID from T_SERVICE_COMMANDS c where c.NAME='verl.check.dn.portierung'), 37, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 6, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, (select c.ID from T_SERVICE_COMMANDS c where c.NAME='verl.check.dn.portierung.ab'), 37, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 7, 0);
insert into T_SERVICE_COMMAND_MAPPING (ID, COMMAND_ID, REF_ID, REF_CLASS, ORDER_NO, VERSION)
  values (S_T_SERVICE_COMMAND_MAPPI_0.nextVal, (select c.ID from T_SERVICE_COMMANDS c where c.NAME='verl.check.real.date.short.term'), 37, 'de.augustakom.hurrican.model.cc.command.ServiceChain', 8, 0);
update T_PRODUKT set VERLAUF_CHAIN_ID=37 where PROD_ID=536;
   


-- BA Anlaesse konfigurieren
Insert into T_BA_VERL_AEND_PROD_2_GRUPPE (PROD_ID, BA_VERL_AEND_GRUPPE_ID) Values (536, 1);
-- BA Konfiguration
Insert into T_BA_VERL_CONFIG (ID, PROD_ID, ANLASS, ABT_CONFIG_ID, GUELTIG_VON, GUELTIG_BIS, DATEW, USERW, VERSION, AUTO_VERTEILEN, CPS_NECESSARY)
 values (S_T_BA_VERL_CONFIG_0.nextVal, 536, 27, 17, SYSDATE, TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), SYSDATE, 'Import', 0, '1', '1');
Insert into T_BA_VERL_CONFIG (ID, PROD_ID, ANLASS, ABT_CONFIG_ID, GUELTIG_VON, GUELTIG_BIS, DATEW, USERW, VERSION, AUTO_VERTEILEN, CPS_NECESSARY)
 values (S_T_BA_VERL_CONFIG_0.nextVal, 536, 13, 17, SYSDATE, TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), SYSDATE, 'Import', 0, '1', '1');
Insert into T_BA_VERL_CONFIG (ID, PROD_ID, ANLASS, ABT_CONFIG_ID, GUELTIG_VON, GUELTIG_BIS, DATEW, USERW, VERSION, AUTO_VERTEILEN, CPS_NECESSARY)
 values (S_T_BA_VERL_CONFIG_0.nextVal, 536, 44, 17, SYSDATE, TO_DATE('01/01/2200 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), SYSDATE, 'Import', 0, '1', '1');
 

-- Standorttyp HVT zuordnen
Insert into T_PRODUKT_2_TECH_LOCATION_TYPE (ID, PROD_ID, TECH_LOCATION_TYPE_REF_ID, PRIORITY, USERW, DATEW, VERSION)
 values (S_T_PRODUKT_2_TECH_LOC_TYPE_0.nextVal, 536, 11000, 1, 'IMPORT', SYSDATE, 0);


