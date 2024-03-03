-- Script fuegt weitere notwendige techn. Leistungen ein

alter session set nls_date_format='yyyy-mm-dd';
insert into T_TECH_LEISTUNG (ID, NAME, EXTERN_LEISTUNG__NO, TYP, LONG_VALUE, STR_VALUE,
    DISPO, EWSD, SDH, IPS, SCT, SNAPSHOT_REL, GUELTIG_VON, GUELTIG_BIS, PROD_NAME_STR)
    values (35, '4600 kbit/s', 10036, 'DOWNSTREAM', 4600, '4600',
    '0', '0', '1', '0', '0', '1', '2009-01-01', '2200-01-01', '4600');

