--
-- SQL-Script, um die Hurrican-Daten an die neue Struktur anzupassen.
--

-- VerlaufStati erweitern
insert into T_VERLAUF_STATUS (ID, VERLAUF_STATUS) values (4, 'erledigt System');

-- Produkte aktualisieren
update T_PRODUKT set BA_RUECKLAEUFER=1 where produktgruppe_id in (2,4,7,9,11,12,14);

--
-- Auftrags-Leistungen abgleichen
-- --> Zugaenge ermitteln
insert into T_AUFTRAG_2_TECH_LS (auftrag_id, tech_ls_id, quantity, aktiv_von, verlauf_id_real)
	select snap.auftrag_id, tls.id, 1, v.realisierungstermin, snap.verlauf_id 
		from t_leistung_snapshot snap, t_verlauf v, t_tech_leistung tls 
		where snap.verlauf_id=v.id and snap.extern_leistung__no=tls.extern_leistung__no 
		  and v.verlauf_status_id <= 4900
		order by snap.id asc;
-- --> Kuendigungen ermitteln
create temporary table tmp_atls 
  select snap.auftrag_id as auftrag_id, tls.id as tech_ls_id, v.realisierungstermin as termin, snap.verlauf_id as verl_id
	from t_leistung_snapshot snap, t_verlauf v, t_tech_leistung tls 
		where snap.verlauf_id=v.id and snap.extern_leistung__no=tls.extern_leistung__no 
		  and v.verlauf_status_id>=9000 and v.verlauf_status_id<=9190;	  
update T_AUFTRAG_2_TECH_LS tls 
    inner join tmp_atls tmp on tmp.auftrag_id=tls.auftrag_id and tmp.tech_ls_id=tls.tech_ls_id
  set tls.aktiv_bis=tmp.termin, tls.verlauf_id_kuend=tmp.verl_id;
DROP TABLE IF EXISTS tmp_atls;


-- Leistungs-Commands definieren
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION) 
	VALUES (1000, 'check.zugang.bandwidth.16000', 
	'de.augustakom.hurrican.service.cc.impl.command.leistung.CheckZugangBandwidth16000Command', 
	'LS_CHECK_ZUGANG', 'Check-Command um zu pruefen, ob die Bandbreite 16000 auf der aktuellen Physik moeglich ist.');
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION) 
	VALUES (1001, 'zugang.bandwidth.16000', 
	'de.augustakom.hurrican.service.cc.impl.command.leistung.ZugangBandwidth16000Command', 
	'LS_ZUGANG', 'Command fuer den Leistungszugang Bandbreite 16000.');
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION) 
	VALUES (1002, 'create.einwahlaccount', 
	'de.augustakom.hurrican.service.cc.impl.command.leistung.CreateEinwahlaccountCommand', 
	'LS_ZUGANG', 'Command, um einen Einwahlaccount anzulegen bzw. einen bestehenden gesperrten EAcc zu entsperren.');
INSERT INTO T_SERVICE_COMMANDS (ID, NAME, CLASS, TYPE, DESCRIPTION) 
	VALUES (1003, 'lock.einwahlaccount', 
	'de.augustakom.hurrican.service.cc.impl.command.leistung.LockEinwahlaccountCommand', 
	'LS_KUENDIGUNG', 'Command, um einen Einwahlaccount anzulegen bzw. einen bestehenden gesperrten EAcc zu entsperren.');
-- Commands den Leistungen zuordnen
insert into T_TECH_LS_2_COMMAND (tech_ls_id, command_id) values (12, 1000);
insert into T_TECH_LS_2_COMMAND (tech_ls_id, command_id) values (12, 1001);
insert into T_TECH_LS_2_COMMAND (tech_ls_id, command_id) values (7, 1002);
insert into T_TECH_LS_2_COMMAND (tech_ls_id, command_id) values (7, 1003);

--
-- Verlaufsaenderungen konfigurieren
update t_ba_verl_aend set text='Leistungsänderungen', manuell=0, akt=1 where id=3;
update t_ba_verl_aend set akt=0 where id=1;
update t_ba_verl_aend set akt=0 where id=2;
update t_ba_verl_aend set akt=0 where id=5;
update t_ba_verl_aend set akt=0 where id=6;
update t_ba_verl_aend set akt=0 where id=7;
update t_ba_verl_aend set akt=0 where id=8;
update t_ba_verl_aend set akt=0 where id=10;
update t_ba_verl_aend set akt=0 where id=11;
update t_ba_verl_aend set akt=0 where id=12;
update t_ba_verl_aend set akt=0 where id=15;
update t_ba_verl_aend set akt=0 where id=22;
update t_ba_verl_aend set akt=0 where id=23;
update t_ba_verl_aend set akt=0 where id=24;
update t_ba_verl_aend set akt=0 where id=25;
update t_ba_verl_aend set akt=0 where id=28;
update t_ba_verl_aend set akt=0 where id=29;
update t_ba_verl_aend set akt=0 where id=30;
update t_ba_verl_aend set akt=0 where id=31;
update t_ba_verl_aend set akt=0 where id=32;
update t_ba_verl_aend set akt=0 where id=34;
update t_ba_verl_aend set akt=0 where id=35;
update t_ba_verl_aend set akt=0 where id=36;
update t_ba_verl_aend set akt=0 where id=37;
update t_ba_verl_aend set akt=0 where id=38;
update t_ba_verl_aend set akt=0 where id=39;
update t_ba_verl_aend set akt=0 where id=40;
update t_ba_verl_aend set akt=0 where id=51;
update t_ba_verl_aend set akt=0 where id=60;
update t_ba_verl_aend set akt=0 where id=61;
update t_ba_verl_aend set akt=0 where id=62;
update t_ba_verl_aend set akt=0 where id=63;
update t_ba_verl_aend set akt=0 where id=64;


-- DN-Konfiguration fuer Produkte aufnehmen (urspruenglich auf ProductType von Taifun)
update T_PRODUKT set MAX_DN_COUNT=1, DN_BLOCK=1, DN_TYP=60 where PROD_ID=1;
update T_PRODUKT set MAX_DN_COUNT=10, DN_BLOCK=0, DN_TYP=60 where PROD_ID=2;
update T_PRODUKT set MAX_DN_COUNT=1000, DN_BLOCK=0, DN_TYP=60 where PROD_ID=3;
update T_PRODUKT set MAX_DN_COUNT=1000, DN_BLOCK=0, DN_TYP=60 where PROD_ID=4;
update T_PRODUKT set MAX_DN_COUNT=1, DN_BLOCK=0, DN_TYP=60 where PROD_ID=5;
update T_PRODUKT set MAX_DN_COUNT=10, DN_BLOCK=0, DN_TYP=60 where PROD_ID=10;
update T_PRODUKT set MAX_DN_COUNT=10, DN_BLOCK=0, DN_TYP=60 where PROD_ID=36;
update T_PRODUKT set MAX_DN_COUNT=1, DN_BLOCK=1, DN_TYP=60 where PROD_ID=37;
update T_PRODUKT set MAX_DN_COUNT=10, DN_BLOCK=0, DN_TYP=60 where PROD_ID=38;
update T_PRODUKT set MAX_DN_COUNT=1000, DN_BLOCK=0, DN_TYP=60 where PROD_ID=65;
update T_PRODUKT set MAX_DN_COUNT=1, DN_BLOCK=0, DN_TYP=60 where PROD_ID=318;
update T_PRODUKT set MAX_DN_COUNT=10, DN_BLOCK=0, DN_TYP=60 where PROD_ID=319;
update T_PRODUKT set MAX_DN_COUNT=1, DN_BLOCK=0, DN_TYP=60 where PROD_ID=322;
update T_PRODUKT set MAX_DN_COUNT=10, DN_BLOCK=0, DN_TYP=60 where PROD_ID=323;
update T_PRODUKT set MAX_DN_COUNT=1, DN_BLOCK=0, DN_TYP=60 where PROD_ID=328;
update T_PRODUKT set MAX_DN_COUNT=10, DN_BLOCK=0, DN_TYP=60 where PROD_ID=329;
update T_PRODUKT set MAX_DN_COUNT=1, DN_BLOCK=0, DN_TYP=60 where PROD_ID=334;
update T_PRODUKT set MAX_DN_COUNT=10, DN_BLOCK=0, DN_TYP=60 where PROD_ID=335;
update T_PRODUKT set MAX_DN_COUNT=10, DN_BLOCK=0, DN_TYP=60 where PROD_ID=336;
update T_PRODUKT set MAX_DN_COUNT=1, DN_BLOCK=1, DN_TYP=60 where PROD_ID=337;		
update T_PRODUKT set MAX_DN_COUNT=1000, DN_BLOCK=0, DN_TYP=60 where PROD_ID=338;
update T_PRODUKT set MAX_DN_COUNT=1, DN_BLOCK=0, DN_TYP=60 where PROD_ID=340;
update T_PRODUKT set MAX_DN_COUNT=1, DN_BLOCK=0, DN_TYP=68 where PROD_ID=101;
		
		
-- technische Leistungen aktualisieren
update T_TECH_LEISTUNG set STR_VALUE='1000' where ID=8;
update T_TECH_LEISTUNG set STR_VALUE='2000' where ID=9;
update T_TECH_LEISTUNG set STR_VALUE='3000' where ID=10;
update T_TECH_LEISTUNG set STR_VALUE='6000' where ID=11;
update T_TECH_LEISTUNG set STR_VALUE='16000' where ID=12;
update T_TECH_LEISTUNG set STR_VALUE='256' where ID=13;
update T_TECH_LEISTUNG set STR_VALUE='512' where ID=14;
update T_TECH_LEISTUNG set STR_VALUE='800' where ID=15;
		

-- Produkt-Mappings aufnehmen
insert into T_PRODUKT_MAPPING (MAPPING_GROUP, EXT_PROD__NO, PROD_ID, MAPPING_PART_TYPE) 
	select p.PROD_ID, p.EXT_PRODUKT__NO, p.PROD_ID, '' from T_PRODUKT p where p.PROD_ID<400;

update T_PRODUKT_MAPPING set MAPPING_PART_TYPE='phone' where PROD_ID in (
	select p.PROD_ID from T_PRODUKT p where p.BRAUCHT_DN=1);
update T_PRODUKT_MAPPING set MAPPING_PART_TYPE='sdsl' where PROD_ID in (
	select p.PROD_ID from T_PRODUKT p where p.produktgruppe_id=4);
update T_PRODUKT_MAPPING set MAPPING_PART_TYPE='connect' where PROD_ID in (
	select p.PROD_ID from T_PRODUKT p where p.produktgruppe_id=2);
update T_PRODUKT_MAPPING set MAPPING_PART_TYPE='dsl' where PROD_ID 
	in (9,56,57,315,316,317,320,321,324,325,326,327,330,331,332,333,339);
update T_PRODUKT_MAPPING set MAPPING_PART_TYPE='online' where PROD_ID in (
	select p.PROD_ID from T_PRODUKT p where p.produktgruppe_id=5);
	
-- neue Command-Definitionen
insert into t_service_chain (id, name, description) values (10, 'Anschlussüb. - DSL+phone auf Kombi', 
  'ServiceChain, um die Physik von einem DSL- und einem Phone-Auftrag auf einen Kombi-Auftrag zu uebernehmen.');
insert into t_service_commands (id, name, class, type, description) 
	values (26, 'assert.src.auftrag.is.buendel', 
	'de.augustakom.hurrican.service.cc.impl.command.physik.AssertSrcIsBuendelCommand', 
	'PHYSIK', 'Command, um zu pruefen, ob der Ursprungs-Auftrag ein Buendel-Auftrag ist.');
insert into t_service_commands (id, name, class, type, description) 
	values (27, 'move.physiktyp.src2dest.buendel', 
	'de.augustakom.hurrican.service.cc.impl.command.physik.MovePhysikFromBuendelCommand', 
	'PHYSIK', 'Command, um die Physik des Buendel-Auftrags auf den Ziel-Auftrag zu uebernehmen.');
insert into t_service_commands (id, name, class, type, description) 
	values (28, 'kuendigung.src.with.buendel.auftrag', 
	'de.augustakom.hurrican.service.cc.impl.command.physik.KuendigungSrcBuendelAuftragCommand', 
	'PHYSIK', 'Command, um die Ursprungsauftraege auf Status 9800 (gekuendigt) zu setzen.');
insert into t_service_commands (id, name, class, type, description) 
	values (29, 'copy.endgeraet.with.buendel.src2dest', 
	'de.augustakom.hurrican.service.cc.impl.command.physik.CopyEndgeraetDatenWithBuendelCommand', 
	'PHYSIK', 'Command, um die Endgeraete-Daten der Ursprungs-Auftraege zu kopieren.');
-- neue ServiceChain-Definition
delete from t_servicechain_2_command where chain_id=10;
insert into t_servicechain_2_command (chain_id, command_id, order_no) values (10,15,1);
insert into t_servicechain_2_command (chain_id, command_id, order_no) values (10,26,2);
insert into t_servicechain_2_command (chain_id, command_id, order_no) values (10,24,3);
insert into t_servicechain_2_command (chain_id, command_id, order_no) values (10,8,4);
insert into t_servicechain_2_command (chain_id, command_id, order_no) values (10,28,5);
insert into t_servicechain_2_command (chain_id, command_id, order_no) values (10,3,6);
insert into t_servicechain_2_command (chain_id, command_id, order_no) values (10,27,7);
insert into t_servicechain_2_command (chain_id, command_id, order_no) values (10,4,8);
insert into t_servicechain_2_command (chain_id, command_id, order_no) values (10,16,9);
insert into t_servicechain_2_command (chain_id, command_id, order_no) values (10,11,10);
insert into t_servicechain_2_command (chain_id, command_id, order_no) values (10,29,11);
insert into t_servicechain_2_command (chain_id, command_id, order_no) values (10,22,12);
insert into t_servicechain_2_command (chain_id, command_id, order_no) values (10,25,13);
insert into t_servicechain_2_command (chain_id, command_id, order_no) values (10,7,14);
insert into t_servicechain_2_command (chain_id, command_id, order_no) values (10,21,15);
insert into t_servicechain_2_command (chain_id, command_id, order_no) values (10,10,16);
	