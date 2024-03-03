--
-- Anpassungen an HW-Struktur
--

alter table T_HW_BAUGRUPPEN_TYP add IS_ACTIVE CHAR(1);
update T_HW_BAUGRUPPEN_TYP set IS_ACTIVE='1' where ID not in (3,4,8);
update T_HW_BAUGRUPPEN_TYP set IS_ACTIVE='0' where ID in (3,4,8,13);
commit;


alter table T_HW_BAUGRUPPEN_TYP add HW_SCHNITTSTELLE_NAME VARCHAR2(50);
update T_HW_BAUGRUPPEN_TYP set HW_SCHNITTSTELLE_NAME='UK0' where ID in (2,4);
update T_HW_BAUGRUPPEN_TYP set HW_SCHNITTSTELLE_NAME='AB' where ID in (1,3);
commit;



delete from T_REFERENCE where TYPE='EQ_HW_SCHNITTSTELLE';
delete from T_REGISTRY where ID in (4000,4001);


insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (12000, 'IA_NIEDERLASSUNG', 'AK', '1', 10, 'Technischer Betrieb Augsburg');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (12001, 'IA_NIEDERLASSUNG', 'MK', '1', 20, 'Technischer Betrieb Muenchen');  
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (12002, 'IA_NIEDERLASSUNG', 'NK', '1', 30, 'Technischer Betrieb Nuernberg');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (12003, 'IA_NIEDERLASSUNG', 'ZK', '1', 40, 'Technischer Betrieb Zentral');

insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (12100, 'IA_PRODUKTART', 'F', '1', 10, 'Produktart Festverbindung');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (12101, 'IA_PRODUKTART', 'V', '1', 20, 'Produktart Virtuelles Netz');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (12102, 'IA_PRODUKTART', 'M', '1', 30, 'Produktart Multimediaanschluss');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (12103, 'IA_PRODUKTART', 'W', '1', 40, 'Produktart WebAccess');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (12104, 'IA_PRODUKTART', 'C', '1', 50, 'Produktart Call');
  
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (12200, 'IA_BETRIEBSRAUM', 'B', '1', 10, 'Kennzeichnung fuer Betriebsraum');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (12201, 'IA_BETRIEBSRAUM', 'O', '1', 20, 'Kennzeichnung fuer OVST');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (12202, 'IA_BETRIEBSRAUM', 'K', '1', 30, 'Kennzeichnung fuer KVZ');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (12203, 'IA_BETRIEBSRAUM', 'F', '1', 10, 'Kennzeichnung fuer FttX');

insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (12300, 'IA_ART', 'G', '1', 10, 'Kennzeichnung fuer Grundausstattung');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (12301, 'IA_ART', 'E', '1', 20, 'Kennzeichnung fuer Erweiterung');
