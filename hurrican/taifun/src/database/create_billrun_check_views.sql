-- Basis View für Navision Abrechnung
create or replace view ${billdata.billrun.type}_BR_NAVI_BILLING_DATA as
select
    bb.CHARGED_FROM as BB_CHARGED_FROM,
    bb.CHARGED_TO as BB_CHARGED_TO,
    bi.*
from NAVI_BIE_BILL_ITEM bi
join NAVI_BIE_BILL bb
    on bb.NAVI_DOCUMENT_NO = bi.NAVI_DOCUMENT_NO
where
    BB.BILLRUN_TYPE = '${billdata.billrun.type}'
/

-- Die drei folgenden Tabellen listen die Kosten in Navision für alle Positionen auf,
-- die in sich aus Gebühren ergeben. Verbindungskosten sind hier nicht enthalten
-- Navision Abrechnung nach Gebühren sortiert
create or replace view ${billdata.billrun.type}_BR_GEB_GEBUEHREN_NAVI as
select
    *
from ${billdata.billrun.type}_BR_NAVI_BILLING_DATA bd
where
    bd.PRINT_OPTION_LINE_TYPE = 'GEB'
/

-- Navision Abrechnung nach Aufträgen sortiert
create or replace view ${billdata.billrun.type}_BR_GEB_AUFTRAG_NAVI as
select
    BB_CHARGED_FROM,
    BB_CHARGED_TO,
    bg.navi_customer_no,
    bg.service__no, -- consider renaming to NAVI_CONTRACT_NO
    sum(amount) as amount
from ${billdata.billrun.type}_BR_GEB_GEBUEHREN_NAVI bg
group by BB_CHARGED_FROM, BB_CHARGED_TO, bg.navi_customer_no, bg.service__no
order by bg.navi_customer_no, bg.service__no
/

-- Navision Abrechnung nach Kunden sortiert
create or replace view ${billdata.billrun.type}_BR_GEB_CUSTOMER_NAVI as
select
    BB_CHARGED_FROM,
    BB_CHARGED_TO,
    ba.navi_customer_no,
    sum(amount) as amount
from ${billdata.billrun.type}_BR_GEB_AUFTRAG_NAVI ba
group by  BB_CHARGED_FROM, BB_CHARGED_TO, ba.navi_customer_no
order by  ba.navi_customer_no
/

-- Die drei folgenden Tabellen listen die Verbindungskosten auf:
-- Navision Abrechnung von Verbindungskosten auf unterster Ebene
create or replace view ${billdata.billrun.type}_BR_VER_VERBINDUNG_NAVI as
select
    *
from ${billdata.billrun.type}_BR_NAVI_BILLING_DATA bd
where
    bd.PRINT_OPTION_LINE_TYPE = 'VER'
/

-- Navision Abrechnung von Verbindungskosten nach Aufträgen sortiert
create or replace view ${billdata.billrun.type}_BR_VER_AUFTRAG_NAVI as
select
    BB_CHARGED_FROM,
    BB_CHARGED_TO,
    bg.navi_customer_no,
    bg.service__no,
    sum(MNET_AMOUNT_NET) as amount
from ${billdata.billrun.type}_BR_VER_VERBINDUNG_NAVI bg
group by BB_CHARGED_FROM, BB_CHARGED_TO, bg.navi_customer_no, bg.service__no
order by bg.navi_customer_no, bg.service__no
/

-- Navision Abrechnung von Verbindungskosten nach Kunden sortiert
create or replace view ${billdata.billrun.type}_BR_VER_CUSTOMER_NAVI as
select
    BB_CHARGED_FROM,
    BB_CHARGED_TO,
    ba.navi_customer_no,
    sum(amount) as amount
from ${billdata.billrun.type}_BR_VER_AUFTRAG_NAVI ba
group by  BB_CHARGED_FROM, BB_CHARGED_TO, ba.navi_customer_no
order by  ba.navi_customer_no
/

-- Taifun Tabellen

-- Basistabelle für alle Bill-Items, die über das NAVI_AUFTRAG_MAPPING auf einen
-- Navision Kunden gematcht werden können
-- alle Auftragspositionen des Billruns (mit zugehöriger Navision-Gebuehr-No falls vorhanden)
-- hierzu gehören auch die Verbindungskosten
create or replace view ${billdata.billrun.type}_BR_AUFTRAGPOS_TAIFUN as
select
    auftrag.NAVI_CUST_NO,
    auftrag.NAVI_CONTRACT_NO,
    gebuehr.NAVI_GEBUEHR_NO as NAVI_GEBUEHR_NO,
    BB.*
from BIE_BILL_ITEM_${billing.year}${billing.month} BB
join NAVI_AUFTRAG_MAPPING auftrag
    on auftrag.TAIFUN_CUST_NO = BB.CUSTOMER_NO
        and auftrag.TAIFUN__NO = BB.SERVICE__NO
join NAVI_CUSTOMER_MAPPING customer
    on CUSTOMER.TAIFUN_NO =BB.CUSTOMER_NO
    and CUSTOMER.NAVI_NO = AUFTRAG.NAVI_CUST_NO
left join NAVI_GEBUEHREN_MAPPING gebuehr
    on gebuehr.TAIFUN_AUFTRAGPOS_NO = BB.SERVICE_ITEM__NO
where
    BB.ORIG_RUN_NO in (${billing.run_no})
    and BB.ITEM_TYPE_NO in (-1003, -1002, -1001, 2,3,4,58)
    and CUSTOMER.TEST_CUSTOMER = '0'
/

-- Alle Auftragspositionen, die aus einer Navision-Gebuehr enstanden sind,
-- aufsummiert und mit Navision Kunden- und Auftrags-No versehen und gruppiert
-- hierbei werden nur Gebühren betrachtet, die nicht zu Kunden gehören, welche
-- als Testkunden in Navision abgerechnet werden
create or replace view ${billdata.billrun.type}_BR_GEB_GEBUEHREN_TAIFUN as
select
    BB.NAVI_CUST_NO,
    BB.NAVI_CONTRACT_NO,
    BB.NAVI_GEBUEHR_NO,
    sum(BB.amount) as amount
from ${billdata.billrun.type}_BR_AUFTRAGPOS_TAIFUN BB
where
    BB.NAVI_GEBUEHR_NO is not null
    and BB.ITEM_TYPE_NO in (2,3,4,58)
group by BB.NAVI_CUST_NO, BB.NAVI_CONTRACT_NO, BB.NAVI_GEBUEHR_NO
order by BB.NAVI_CUST_NO, BB.NAVI_CONTRACT_NO, BB.NAVI_GEBUEHR_NO
/

-- Alle Taifun-Gebuehren zu Aufträgen zusammengefasst
create or replace view ${billdata.billrun.type}_BR_GEB_AUFTRAG_TAIFUN as
select
    NAVI_CUST_NO,
    NAVI_CONTRACT_NO,
    sum(amount) as amount
from ${billdata.billrun.type}_BR_GEB_GEBUEHREN_TAIFUN
group by NAVI_CUST_NO,NAVI_CONTRACT_NO
order by NAVI_CUST_NO,NAVI_CONTRACT_NO
/

-- Alle Gebuehren-Aufträge nach Kunden zusammengefasst
create or replace view ${billdata.billrun.type}_BR_GEB_CUSTOMER_TAIFUN as
select
    NAVI_CUST_NO,
    sum(amount) as amount
from ${billdata.billrun.type}_BR_GEB_AUFTRAG_TAIFUN
group by NAVI_CUST_NO
order by NAVI_CUST_NO
/

-- Alle Auftragspositionen, die Verbindungskosten beschreiben zu Aufträgen zusammengefasst
-- hierbei werden nur Gebühren betrachtet, die nicht zu Kunden gehören, welche
-- als Testkunden in Navision abgerechnet werden
create or replace view ${billdata.billrun.type}_BR_VER_AUFTRAG_TAIFUN as
select
    BB.NAVI_CUST_NO,
    BB.NAVI_CONTRACT_NO,
    sum(BB.MNET_AMOUNT_NET) as amount
from ${billdata.billrun.type}_BR_AUFTRAGPOS_TAIFUN BB
where
    BB.NAVI_GEBUEHR_NO is null
    and BB.ITEM_TYPE_NO in (-1003, -1002, -1001)
group by BB.NAVI_CUST_NO, BB.NAVI_CONTRACT_NO
order by BB.NAVI_CUST_NO, BB.NAVI_CONTRACT_NO
/

-- Alle Verbindungskosten-Aufträge nach Kunden zusammengefasst
create or replace view ${billdata.billrun.type}_BR_VER_CUSTOMER_TAIFUN as
select
    NAVI_CUST_NO,
    sum(amount) as amount
from ${billdata.billrun.type}_BR_VER_AUFTRAG_TAIFUN
group by NAVI_CUST_NO
order by NAVI_CUST_NO
/

-- Vergleichviews für Gebuehren
create or replace view ${billdata.billrun.type}_BR_COMPARE_GEBUEHREN_ALL as
select
    navi.navi_customer_no,
    navi.service__no,
    navi.service_item__no, -- consider renaming to NAVI_GEBUEHR_NO
    navi.amount as AMOUNT_NAVISION,
    taifun.amount as AMOUNT_TAIFUN,
    abs(navi.amount - taifun.amount) as DIFFERENCE
from ${billdata.billrun.type}_BR_GEB_GEBUEHREN_NAVI navi
left join ${billdata.billrun.type}_BR_GEB_GEBUEHREN_TAIFUN taifun
    on navi.navi_customer_no = taifun.NAVI_CUST_NO
        and navi.service__no = taifun.NAVI_CONTRACT_NO
        and navi.service_item__no = taifun.NAVI_GEBUEHR_NO
order by abs(navi.amount - taifun.amount) desc, navi.amount desc
/

create or replace view ${billdata.billrun.type}_BR_COMPARE_GEBUEHREN as
select * from ${billdata.billrun.type}_BR_COMPARE_GEBUEHREN_ALL
where
    abs(AMOUNT_NAVISION - AMOUNT_TAIFUN) > 0.01
    or AMOUNT_TAIFUN is null
/

create or replace view ${billdata.billrun.type}_BR_COMPARE_GEB_AUFTR_ALL as
select
    navi.navi_customer_no,
    navi.service__no,
    navi.amount as AMOUNT_NAVISION,
    taifun.amount as AMOUNT_TAIFUN,
    abs(navi.amount - taifun.amount) as DIFFERENCE
from ${billdata.billrun.type}_BR_GEB_AUFTRAG_NAVI navi
left join ${billdata.billrun.type}_BR_GEB_AUFTRAG_TAIFUN taifun
    on navi.navi_customer_no = taifun.NAVI_CUST_NO
        and navi.service__no = taifun.NAVI_CONTRACT_NO
order by abs(navi.amount - taifun.amount) desc, navi.amount desc
/

create or replace view ${billdata.billrun.type}_BR_COMPARE_GEB_AUFTRAG as
select * from ${billdata.billrun.type}_BR_COMPARE_GEB_AUFTR_ALL
where
    abs(AMOUNT_NAVISION - AMOUNT_TAIFUN) > 0.01
    or AMOUNT_TAIFUN is null
/

create or replace view ${billdata.billrun.type}_BR_COMPARE_GEB_CUST_ALL as
select
    navi.navi_customer_no,
    navi.amount as AMOUNT_NAVISION,
    taifun.amount as AMOUNT_TAIFUN,
    abs(navi.amount - taifun.amount) as DIFFERENCE
from ${billdata.billrun.type}_BR_GEB_CUSTOMER_NAVI navi
left join ${billdata.billrun.type}_BR_GEB_CUSTOMER_TAIFUN taifun
    on navi.navi_customer_no = taifun.NAVI_CUST_NO
order by abs(navi.amount - taifun.amount) desc, navi.amount desc
/

create or replace view ${billdata.billrun.type}_BR_COMPARE_GEB_CUSTOMER as
select * from ${billdata.billrun.type}_BR_COMPARE_GEB_CUST_ALL
where
    abs(AMOUNT_NAVISION - AMOUNT_TAIFUN) > 0.01
    or AMOUNT_TAIFUN is null
/


-- Vergleichviews für Verbindungskosten
create or replace view ${billdata.billrun.type}_BR_COMPARE_VER_AUFTR_ALL as
select
    navi.navi_customer_no,
    navi.service__no,
    navi.amount as AMOUNT_NAVISION,
    taifun.amount as AMOUNT_TAIFUN,
    abs(navi.amount - taifun.amount) as DIFFERENCE
from ${billdata.billrun.type}_BR_VER_AUFTRAG_NAVI navi
left join ${billdata.billrun.type}_BR_VER_AUFTRAG_TAIFUN taifun
    on navi.navi_customer_no = taifun.NAVI_CUST_NO
        and navi.service__no = taifun.NAVI_CONTRACT_NO
order by abs(navi.amount - taifun.amount) desc, navi.amount desc
/

create or replace view ${billdata.billrun.type}_BR_COMPARE_VER_AUFTRAG as
select * from ${billdata.billrun.type}_BR_COMPARE_VER_AUFTR_ALL
where
    abs(AMOUNT_NAVISION - AMOUNT_TAIFUN) > 0.01
    or AMOUNT_TAIFUN is null
/

create or replace view ${billdata.billrun.type}_BR_COMPARE_VER_CUST_ALL as
select
    navi.navi_customer_no,
    navi.amount as AMOUNT_NAVISION,
    taifun.amount as AMOUNT_TAIFUN,
    abs(navi.amount - taifun.amount) as DIFFERENCE
from ${billdata.billrun.type}_BR_VER_CUSTOMER_NAVI navi
left join ${billdata.billrun.type}_BR_VER_CUSTOMER_TAIFUN taifun
    on navi.navi_customer_no = taifun.NAVI_CUST_NO
order by abs(navi.amount - taifun.amount) desc, navi.amount desc
/

create or replace view ${billdata.billrun.type}_BR_COMPARE_VER_CUSTOMER as
select * from ${billdata.billrun.type}_BR_COMPARE_VER_CUST_ALL
where
    abs(AMOUNT_NAVISION - AMOUNT_TAIFUN) > 0.01
    or AMOUNT_TAIFUN is null
/

create or replace view ${billdata.billrun.type}_BR_COMPARE_VER_CUST_PHONE as
SELECT c.*, m.TAIFUN__NO, M.NAVI_CUST_NO
  FROM ${billdata.billrun.type}_BR_COMPARE_GEBUEHREN c
       JOIN NAVI_AUFTRAG_MAPPING m
          ON C.SERVICE__NO = m.NAVI_CONTRACT_NO
          AND C.NAVI_CUSTOMER_NO = M.NAVI_CUST_NO
       JOIN OE oe
          ON m.TAIFUN_OE_NO = OE.OE_NO
       WHERE OE.BILLING_GROUP_NO = 3
/

create or replace view ${billdata.billrun.type}_BR_COMPARE_AUFTRAG_NETTO as
SELECT     TAIFUN_AMOUNT,
           TAIFUN.TAIFUN__NO,
           NAVI_CUSTOMER_NO,
           NAVI_CONTRACT_NO,
           NAVI_AMOUNT AS NAVI_AMOUNT,
           ABS (NAVI_AMOUNT - TAIFUN_AMOUNT) AS DIFF
    FROM(SELECT     B_AUFTRAG.TAIFUN__NO, SUM (B.AMOUNT) AS TAIFUN_AMOUNT
             FROM      (SELECT   DISTINCT M.TAIFUN__NO AS TAIFUN__NO
                          FROM NAVI_AUFTRAG_MAPPING M
                              JOIN AUFTRAG A
                                 ON M.TAIFUN__NO = A.AUFTRAG__NO
                              JOIN OE OE
                                 ON A.OE__NO = OE.OE__NO
                                    AND (OE.BILLING_GROUP_NO = decode('${billdata.billrun.type}','PHON',3,'CONN',2)
                                    or OE.BILLING_GROUP_NO = decode('${billdata.billrun.type}','PHON',4,'CONN',2))
                         WHERE   M.ALREADY_IN_TAIFUN = '0') B_AUFTRAG
                    JOIN
                       TATEST_BILLINGTEST.BIE_BILL_ITEM_201012 B
                    ON B.SERVICE__NO = B_AUFTRAG.TAIFUN__NO
                       AND B.ITEM_TYPE_NO = 60
         GROUP BY   B_AUFTRAG.TAIFUN__NO) TAIFUN
        JOIN NAVI_AUFTRAG_MAPPING MAP
           ON TAIFUN.TAIFUN__NO = MAP.TAIFUN__NO
              AND MAP.ALREADY_IN_TAIFUN = '0'
        FULL OUTER JOIN (SELECT     SUM (MNET_AMOUNT_NET) AS NAVI_AMOUNT,
                                    NAVI_CUSTOMER_NO,
                                    SERVICE__NO AS NAVI_CONTRACT_NO
                             FROM   NAVI_BIE_BILL_ITEM
                            WHERE   BILLRUN_TYPE = decode('${billdata.billrun.type}','PHON','phon','CONN','conn')
                         GROUP BY   NAVI_CUSTOMER_NO, SERVICE__NO) NAVI
           ON MAP.NAVI_CUST_NO = NAVI.NAVI_CUSTOMER_NO
              AND MAP.NAVI_CONTRACT_NO = NAVI.NAVI_CONTRACT_NO
   WHERE   NOT (   NAVI_AMOUNT = TAIFUN_AMOUNT
                OR (NAVI_AMOUNT IS NULL
                    AND TAIFUN_AMOUNT = 0)
                OR (NAVI_AMOUNT = 0
                    AND TAIFUN_AMOUNT IS NULL))
ORDER BY   ABS (NVL (NAVI_AMOUNT, 0) - NVL (TAIFUN_AMOUNT, 0))
           --/ DECODE (ABS (NAVI_AMOUNT), 0, 0.000000001, ABS (NAVI_AMOUNT))
           DESC;

