CREATE TABLE MIG_MNET_PRODUKT_MAP_CON_TMP as
select
a.leistung_no|| '_' || b.value as comp,
-- b.userw,
-- c.userw,
-- a.fibu_account,
a.oe__no,
a.leistung_no,
b.value,
a.name,
--b.*,
--a.*,
-- c.name,
-- c.FIBU_ACCOUNT,
-- decode(
-- b.value, NULL,
'''' || a.name  || ''',NULL,' || '    --     ' || c.name || '  / ' || a.oe__no
--,
-- '''' || b.value || ''',NULL,' || '    --     ' || c.name || ' :  ' || a.name || '  / ' || a.oe__no
-- )
as c
from
(
select c.*
from
leistung_lang c
where
C.LANGUAGE =
'german      '
) c,
SERVICE_VALUE_PRICE b,
leistung a
where
-- a.leistung_no = 41014 and
-----------------------------------------------------
-- a.name like 'MV006ETH' and
-- a.name like 'MV%ETH' and
-----------------------------------------------------
-- here
-- b.leistung_no is NULL and
c.USERW in ('FAKT_DB','FAKT-DB','MNETTEST') and
--------------------------------------------
a.leistungkat = 'WIEDERHOLT     ' and
--
a.oe__no in (2157,2158,2159,2160,2161,2162)
and
-- bisherige Konfig nicht ansehen --
(
a.EXT_PRODUKT__NO is NULL and
a.EXT_LEISTUNG__NO is NULL and
b.EXT_PRODUKT__NO is NULL and
b.EXT_LEISTUNG__NO is NULL
) and
-----------------------------------------------------
a.hist_last = 1 and
-- a.leistung_no = 31300 and
a.leistung_no = c.leistung_no(+) and
NVL(b.value,'X') = NVL(c.value,'X') and
a.leistung_no = b.leistung_no(+)
/

CREATE INDEX I1MIG_MNET_PRODUKT_MAP_CON_TMP ON MIG_MNET_PRODUKT_MAP_CON_TMP
(leistung_no)
NOLOGGING
TABLESPACE TAIFUN_INDEX
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL
/

CREATE INDEX I2MIG_MNET_PRODUKT_MAP_CON_TMP ON MIG_MNET_PRODUKT_MAP_CON_TMP
(name)
NOLOGGING
TABLESPACE TAIFUN_INDEX
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL
/

CREATE INDEX I3MIG_MNET_PRODUKT_MAP_CON_TMP ON MIG_MNET_PRODUKT_MAP_CON_TMP
(comp)
NOLOGGING
TABLESPACE TAIFUN_INDEX
PCTFREE    10
INITRANS   2
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
NOPARALLEL
/
