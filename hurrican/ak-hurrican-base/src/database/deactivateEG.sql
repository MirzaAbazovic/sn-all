--
-- SQL-Script, um die Endgeraete in Hurrican zu de-aktivieren.
-- Es handelt sich hier hauptsaechlich um das Handling der Standard-EGs. 
-- Zusaetzlich zu den Aenderungen in der DB ist es auch noch notwendig, dass
-- bestimmte Systemberechtigungen gesperrt werden.
--


-- Konfiguration Produkt-2-EG um Active-Flag erweitern
--alter table T_PROD_2_EG add IS_ACTIVE CHAR(1);
--update T_PROD_2_EG set IS_ACTIVE='1';
--commit;

--
-- Verlaufs-Tabelle um Montageart erweitern
-- (Definition der Montagearten in Tabelle T_REFERENCE!)
--
--alter table T_VERLAUF add INSTALLATION_REF_ID NUMBER(10);
--CREATE INDEX IX_FK_VERLAUF_2_REF
--	ON T_VERLAUF (INSTALLATION_REF_ID) TABLESPACE "I_HURRICAN";
--ALTER TABLE T_VERLAUF
--  ADD CONSTRAINT FK_VERLAUF_2_REF
--      FOREIGN KEY (INSTALLATION_REF_ID)
--      REFERENCES T_REFERENCE (ID);

--insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION) 
--	values (900, 'INSTALLATION_TYPE', 'Selbstmontage', 0, 1, 10, 
--	'Installation durch den Kunden; INT_VALUE definiert, ob Field-Service oder ext. Dienstleister (bei int_value=1) informiert werden muss.');
--insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION) 
--	values (901, 'INSTALLATION_TYPE', 'M-net', 1, 1, 20, 
--	'Installation durch M-net; INT_VALUE definiert, ob Field-Service oder ext. Dienstleister (bei int_value=1) informiert werden muss.');
--insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION) 
--	values (902, 'INSTALLATION_TYPE', 'extern', 1, 1, 30, 
--	'Installation durch externen Dienstleister; INT_VALUE definiert, ob Field-Service oder ext. Dienstleister (bei int_value=1) informiert werden muss.');
--commit;





--
-- SQL-Scripts, um die EG-Funktionen zu deaktivieren
-- 

-- nicht mehr notwendige P2EG-Kombinationen deaktivieren
-- (ID 30 - TilginVood - bleibt fuer MaxiDeluxe vorerst bestehen, bis MAC-Adresse ueber Taifun verwaltet wird)
update T_PROD_2_EG set IS_ACTIVE='0' where EG_ID not in (12, 26, 30);
commit;

-- Reference 'Postversand' deaktivieren
update T_REFERENCE set GUI_VISIBLE='0' where ID=800;
commit;

-- GUI-Mapping fuer Ansicht/Abgleich EGs entfernen (Menu-Eintraege entfallen)
delete from T_GUI_MAPPING where GUI_ID in (110,111);
commit;


-- User-Berechtigungen de-aktivieren (in AUTHENTICATION)
-- Button 'IAD zuordnen' deaktivieren
--update COMPBEHAVIOR set EXECUTABLE='0' where COMP_ID=420;
--update GUICOMPONENT set DESCRIPTION='IAD-Zuordnung nur noch ueber Taifun!' where ID=420;
-- Button fuer Sofortversand (auf EG-Maske) deaktivieren
update COMPBEHAVIOR set EXECUTABLE='0' where COMP_ID=429;
update GUICOMPONENT set DESCRIPTION='EG-Versand nur noch ueber Taifun!' where ID=429;
-- Menu-Item fuer den EG-Abgleich (Kaufgeraete) deaktivieren
update COMPBEHAVIOR set EXECUTABLE='0' where COMP_ID=320;
update GUICOMPONENT set DESCRIPTION='EG-Versand nur noch ueber Taifun!' where ID=320;
-- Save-Button von IAD-Zuordnungsdialog entfernen (Rechte-Konfiguration)
--delete from COMPBEHAVIOR where COMP_ID=506;
--delete from GUICOMPONENT where ID=506;
commit;



-- ################################################################
-- # Abfragen 
-- ################################################################


-- Anzahl EGs fuer Versand bestimmen
-- Default-EGs
alter session set nls_date_format='yyyy-mm-dd';
select eg.name, count(e2a.eg_id) as anzahl
from t_eg_2_auftrag e2a
inner join t_eg eg on e2a.eg_id=eg.id
inner join t_auftrag_daten ad on e2a.auftrag_id=ad.auftrag_id
left join t_lieferschein l on e2a.lieferschein_id=l.id
where ad.gueltig_bis>SYSDATE and ad.status_id not in (1150,3400)
and e2a.versandart=800
and eg.type in (1,4,5)
and (l.status_id is null or (l.status_id<703 and l.status_id>699))
and ad.vorgabe_scv<'2009-05-01'
group by eg.name
order by eg.name asc;

-- Kauf-EGs
select eg.name, count(e2a.eg_id) as anzahl
from t_eg_2_auftrag e2a
inner join t_eg eg on e2a.eg_id=eg.id
where e2a.prodak_order__no is not null
and e2a.EXPORTDATUM is null 
and eg.type in (1,4,5)
group by eg.name;


-- Default-EGs fuer Auftrag/Kunde
select a.id as hurrican_auftrag_id, a.kunde__no, ad.prodak_order__no, eg.name, ad.vorgabe_scv, ad.status_id,
e2a.LIEFERADRESSE_ID, adr.name, adr.vorname, adr.strasse, adr.hausnummer, adr.HAUSNUMMER_ZUSATZ,
adr.PLZ, adr.ORT, adr.ORT_ZUSATZ
from t_eg_2_auftrag e2a
inner join t_eg eg on e2a.eg_id=eg.id
inner join t_auftrag_daten ad on e2a.auftrag_id=ad.auftrag_id
inner join t_auftrag a on ad.auftrag_id=a.id
left join t_lieferschein l on e2a.lieferschein_id=l.id
left join t_address adr on e2a.lieferadresse_id=adr.id
where ad.gueltig_bis>SYSDATE and ad.status_id not in (1150,3400)
and e2a.versandart=800
and eg.type in (1,4,5)
and (l.status_id is null or (l.status_id<700 and l.status_id>699))
and ad.vorgabe_scv>='2009-05-01'
order by ad.vorgabe_scv asc, a.kunde__no asc;


-- Kauf-EGs fuer Auftrag/Kunde --> erledigt
select a.id as hurrican_auftrag_id, a.kunde__no, ad.prodak_order__no, eg.name, e2a.SELBSTABHOLUNG
from t_eg_2_auftrag e2a
inner join t_eg eg on e2a.eg_id=eg.id
inner join t_auftrag_daten ad on e2a.prodak_order__no=ad.prodak_order__no
inner join t_auftrag a on ad.auftrag_id=a.id
where e2a.prodak_order__no is not null
and e2a.SELBSTABHOLUNG <> '1' or e2a.SELBSTABHOLUNG is null
and ad.gueltig_bis>SYSDATE
and e2a.EXPORTDATUM is null 
and eg.type in (1,4,5)
order by a.kunde__no;




