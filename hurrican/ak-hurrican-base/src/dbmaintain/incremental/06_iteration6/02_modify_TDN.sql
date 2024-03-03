--
-- Script modifiziert die T_TDN Tabelle fuer die neue
-- Verbindungsbezeichnungsstruktur.
--

alter table T_TDN add CUSTOMER_IDENT VARCHAR2(6);
comment on column T_TDN.CUSTOMER_IDENT is 'Definiert die Nutzerkennung fuer die Verbindungsbezeichnung';
alter table T_TDN add KIND_OF_USE_PRODUCT VARCHAR2(1);
comment on column T_TDN.KIND_OF_USE_PRODUCT is 'Definiert die Produktgruppe';
alter table T_TDN add KIND_OF_USE_TYPE VARCHAR2(1);
comment on column T_TDN.KIND_OF_USE_TYPE is 'Definiert die Nutzungsart';
alter table T_TDN add UNIQUE_CODE NUMBER(8);
comment on column T_TDN.UNIQUE_CODE is 'Eindeutige Codierung der Verbindungsbezeichnung';
alter table T_TDN add OVERWRITTEN CHAR(1);
comment on column T_TDN.OVERWRITTEN is 'gesetztes Flag definiert, dass die Verbindungsbezeichnung manuell definiert / ueberschrieben wurde';

create SEQUENCE S_T_TDN_UNIQUE_CODE_0 start with 10000000;
grant select on S_T_TDN_UNIQUE_CODE_0 to public;
