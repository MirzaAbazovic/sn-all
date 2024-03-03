-- Endgeräte für IPSec

insert into T_EG (ID, INTERNE__ID, NAME, BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO, VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT, PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING, CONF_S0BACKUP, TYPE)
values (80, 80, 'IPSec-Router (aktiv)', 'Aktiver Router fuer IPSec-Anschluesse', 'IPSec-Router', null, to_date('01.11.2009', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null, null, '1', '1', null, 4);

insert into T_EG (ID, INTERNE__ID, NAME, BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO, VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT, PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING, CONF_S0BACKUP, TYPE)
values (81, 81, 'IPSec-Router (passiv)', 'Passiver Router fuer IPSec-Anschluesse', 'IPSec-Router', null, to_date('01.11.2009', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null, null, '1', '1', null, 4);

insert into T_EG (ID, INTERNE__ID, NAME, BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO, VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT, PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING, CONF_S0BACKUP, TYPE)
values (82, 82, 'IPSec-Router (Kundenendgerät)', 'Kundenendgerät fuer IPSec-Anschluesse', 'IPSec-Router', null, to_date('01.11.2009', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null, null, '1', '1', null, 4);

insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT, IS_ACTIVE) values (S_T_PROD_2_EG_0.nextVal, 444, 80, '0', '1');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT, IS_ACTIVE) values (S_T_PROD_2_EG_0.nextVal, 444, 81, '0', '1');
insert into T_PROD_2_EG (ID, PROD_ID, EG_ID, IS_DEFAULT, IS_ACTIVE) values (S_T_PROD_2_EG_0.nextVal, 444, 82, '0', '1');
