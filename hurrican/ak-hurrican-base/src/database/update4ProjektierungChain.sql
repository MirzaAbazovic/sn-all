--
-- SQL-Script, um auch fuer Projektierungen eine Service-Chain
-- konfigurieren zu koennen.
--

ALTER TABLE T_PRODUKT add column PROJEKTIERUNG_CHAIN_ID INTEGER(9) after BRAUCHT_VPI_VCI;
ALTER TABLE T_PRODUKT
  ADD CONSTRAINT FK_PROD_2_SC_PROJ
      FOREIGN KEY (PROJEKTIERUNG_CHAIN_ID)
      REFERENCES t_service_chain (ID)
   ON DELETE NO ACTION
   ON UPDATE CASCADE;

insert into T_SERVICE_CHAIN (ID, NAME, TYPE, DESCRIPTION) 
  values ('34', 'Projektierung Connect-Check', 'VERLAUF_CHECK', 
  'Check fuer eine Projektierung, um zu pruefen, ob z.B. SAP-Auftragsnummer=TDN ist');
  
insert into T_SERVICE_COMMAND_MAPPING (ORDER_NO, COMMAND_ID, REF_ID, REF_CLASS) 
  values (1, 2011, 34, 'de.augustakom.hurrican.model.cc.command.ServiceChain');

update t_produkt set projektierung_chain_id=34 where prod_id in (450,451,452,453,454,455);
