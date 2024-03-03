alter table T_HVT_TECHNIK add CPS_NAME VARCHAR2(100);
comment on column T_HVT_TECHNIK.CPS_NAME is 'Spezieller Hersteller-Name fuer die CPS-Provisionierung; Aenderungen immer mit CPS-Team abstimmen!';

update T_HVT_TECHNIK set CPS_NAME=HERSTELLER;
update T_HVT_TECHNIK set CPS_NAME='Alcatel' where ID=6;

