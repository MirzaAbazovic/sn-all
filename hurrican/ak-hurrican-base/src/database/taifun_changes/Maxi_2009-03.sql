--
-- SQL-Script, um die Preisaenderungen (wg. Rundungsfehler) einzutragen.
--


-- fuer TAPROD01
insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (400, 'Preisaenderung wg. Rundungsfehler', '2009-03-31', '2009-04-01', 
    42386, 25.12, 25.11, '25,11', 0);
insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (401, 'Preisaenderung wg. Rundungsfehler', '2009-03-31', '2009-04-01', 
    42387, 26.81, 26.8, '26,8', 0);
insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (402, 'Preisaenderung wg. Rundungsfehler', '2009-03-31', '2009-04-01', 
    42388, 28.49, 28.48, '28,48', 0);


-- fuer TATEST01
insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (400, 'Preisaenderung wg. Rundungsfehler', '2009-03-31', '2009-04-01', 
    42273, 25.12, 25.11, '25,11', 0);
insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (401, 'Preisaenderung wg. Rundungsfehler', '2009-03-31', '2009-04-01', 
    42274, 26.81, 26.8, '26,8', 0);
insert into leistung_modification (ID, DESCRIPTION, CANCEL_DATE, CHANGE_DATE, 
	LEISTUNG_NO, NETTO_OLD, NETTO_NEW, NETTO_NEW_S, UPDATE_REFERENCE)
    values (402, 'Preisaenderung wg. Rundungsfehler', '2009-03-31', '2009-04-01', 
    42275, 28.49, 28.48, '28,48', 0);


-- delete Anweisungen
delete from LEISTUNG_MODIFICATION_LOG where MOD_ID in (400,401,402);
delete from LEISTUNG_MODIFICATION where ID in (400,401,402);

update LEISTUNG_MODIFICATION set EXECUTED_AT=null where ID in (400,401,402);

