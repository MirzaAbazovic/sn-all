delete from USERACCOUNT 
where (DB_ID in (select ID from DB where NAME like 'production')) 
or ACCOUNT_ID in (select ID from ACCOUNT where ACC_NAME like 'production.default'); 

delete from ACCOUNT where ACC_NAME like 'production.default';
delete from DB where NAME like 'production';

delete from COMPBEHAVIOR where COMP_ID in (select ID from GUICOMPONENT where PARENT like 'de.augustakom.hurrican.gui.verlauf.ProductionPanel');

delete from GUICOMPONENT where PARENT like 'de.augustakom.hurrican.gui.verlauf.ProductionPanel';