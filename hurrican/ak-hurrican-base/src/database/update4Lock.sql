


-- Referenzen fuer LOCK_MODE anlegen
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (1500, 'LOCK_MODE', 'Teilsperre', '1', 10, 
	'Teilsperre (abgehend)');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (1501, 'LOCK_MODE', 'Vollsperre', '1', 20, 
	'Vollsperre (abgehend und kommend)');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (1502, 'LOCK_MODE', 'Wandlung auf Vollsperre', '1', 30, 
	'Wandlung einer Teil- in eine Vollsperre (abgehend und kommend)');
insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)  
	values (1503, 'LOCK_MODE', 'entsperren', '1', 40, 
	'Entsperren');
commit;

alter table T_SPERRE add DEBITOR_ID VARCHAR2(20);
alter table T_SPERRE add LOCK_MODE_REF_ID NUMBER(10);
CREATE INDEX IX_FK_SPERRE_2_REF ON T_SPERRE (LOCK_MODE_REF_ID) TABLESPACE "I_HURRICAN";
ALTER TABLE T_SPERRE
  ADD CONSTRAINT FK_SPERRE_2_REF
      FOREIGN KEY (LOCK_MODE_REF_ID)
      REFERENCES T_REFERENCE (ID);

delete from T_SPERRE where S_ART_SPERRE is null or S_ART_SPERRE=' ';
update T_SPERRE set LOCK_MODE_REF_ID=1500 where S_ART_SPERRE='abgehend';
update T_SPERRE set LOCK_MODE_REF_ID=1501 where S_ART_SPERRE='voll';
update T_SPERRE set LOCK_MODE_REF_ID=1502 where S_ART_SPERRE='wandlung auf voll';
update T_SPERRE set LOCK_MODE_REF_ID=1503 where S_ART_SPERRE='entsperren';


CREATE INDEX IX_SPERRE_KUNDE_NO ON T_SPERRE (S_KD_NR) TABLESPACE "I_HURRICAN";


-- Query, um offene Sperren anzuzeigen
insert into T_DB_QUERIES (ID, NAME, DESCRIPTION, SERVICE, SQL_QUERY)
  values (13, 'Sperren - aktiv', 
  'Zeigt aktive Sperren an (nur Kunden, die noch aktive Auftraege haben)',
  'CC',
  'select s.S_KD_NR as KUNDE__NO, s.DEBITOR_ID as DEBITOR_ID  
from 
T_SPERRE s
inner join T_AUFTRAG a on s.S_KD_NR=a.KUNDE__NO
inner join T_AUFTRAG_DATEN ad on a.ID=ad.AUFTRAG_ID
where 
(S_VORGANG > (select max(S_VORGANG) from T_SPERRE tmp where tmp.S_KD_NR=s.S_KD_NR and tmp.LOCK_MODE_REF_ID=1503)
  or
 (select max(S_VORGANG) from T_SPERRE tmp where tmp.S_KD_NR=s.S_KD_NR and tmp.LOCK_MODE_REF_ID=1503) is null
)
and ad.STATUS_ID not in (1150,3400) and ad.STATUS_ID<9800
and ad.GUELTIG_BIS>SYSDATE
group by s.S_KD_NR, s.DEBITOR_ID
order by s.S_KD_NR ASC, s.DEBITOR_ID ASC');
commit;


-- select distinct s.S_KD_NR, s.DEBITOR_ID, s.S_ART_SPERRE, max(S_VORGANG)  
-- from 
-- T_SPERRE s
-- inner join T_AUFTRAG a on s.S_KD_NR=a.KUNDE__NO
-- inner join T_AUFTRAG_DATEN ad on a.ID=ad.AUFTRAG_ID
-- where S_VORGANG > (select max(S_VORGANG) from T_SPERRE tmp where tmp.S_KD_NR=s.S_KD_NR and tmp.LOCK_MODE_REF_ID=1503)
-- and ad.STATUS_ID not in (1150,3400) and ad.STATUS_ID<9800
-- and ad.GUELTIG_BIS>SYSDATE
-- group by s.S_KD_NR, s.DEBITOR_ID, s.S_ART_SPERRE, S_VORGANG
-- order by s.S_KD_NR ASC, s.DEBITOR_ID ASC



