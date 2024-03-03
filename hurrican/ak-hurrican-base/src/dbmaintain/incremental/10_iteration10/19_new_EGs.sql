ALTER TABLE T_EG MODIFY(NAME VARCHAR2(100 BYTE));
ALTER TABLE T_EG MODIFY(BESCHREIBUNG VARCHAR2(100 BYTE));

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'Baukasten ISDN',
    'Baukasten ISDN', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'Bestandskunden mit AVM',
    'Bestandskunden mit AVM', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'CD RASPPPoE 0.98',
    'CD RASPPPoE 0.98', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'CD-WinPoet V6.61',
    'CD-WinPoet V6.61', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'DeTeWe TA 33 Terminaladapter',
    'DeTeWe TA 33 Terminaladapter', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'Gerätetausch Turbolink 1203',
    'Gerätetausch Turbolink 1203', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'IAD für MaxiPur Option',
    'IAD für MaxiPur Option', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'INSTALLATIONSANLEITUNG home/350i + Install-CD',
    'INSTALLATIONSANLEITUNG home/350i + Install-CD', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'INSTALLATIONSANLEITUNG home/350i + Install-CD + PPPoE-CD',
    'INSTALLATIONSANLEITUNG home/350i + Install-CD + PPPoE-CD', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'INSTALLATIONSANLEITUNG USB + Install-CD',
    'INSTALLATIONSANLEITUNG USB + Install-CD', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'INSTALLATIONSANLEITUNG 510i',
    'INSTALLATIONSANLEITUNG 510i', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'INSTALLATIONSANLEITUNG 510i + CD',
    'INSTALLATIONSANLEITUNG 510i + CD', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'INSTALLATIONSANLEITUNG 510i/530i + CD',
    'INSTALLATIONSANLEITUNG 510i/530i + CD', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'ISDN NTBA 4B3T',
    'ISDN NTBA 4B3T', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'Komplett analog mit AVM',
    'Komplett analog mit AVM', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'Komplett analog ohne AVM',
    'Komplett analog ohne AVM', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'Komplett ISDN mit AVM',
    'Komplett ISDN mit AVM', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'Komplett ISDN ohne AVM',
    'Komplett ISDN ohne AVM', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'Mobilteil S45',
    'Mobilteil S45', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'NTBAsplit & ETH- Modem',
    'NTBAsplit & ETH- Modem', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'NTBAsplit & USB- Modem',
    'NTBAsplit & USB- Modem', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'Pur',
    'Pur', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'Pur mit VoIP Option',
    'Pur mit VoIP Option', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'Set Splitter + ADSL ETH',
    'Set Splitter + ADSL ETH', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'Set Splitter + ADSL USB/ETH',
    'Set Splitter + ADSL USB/ETH', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'Set WLAN Router WBR14-G2 EU und Client 2862W-G EU',
    'Set WLAN Router WBR14-G2 EU und Client 2862W-G EU', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'SET2 NTBA +  USB/ETH',
    'SET2 NTBA +  USB/ETH', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'SET2 NTBA + ADSL ETH',
    'SET2 NTBA + ADSL ETH', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'SET2 SPLITTER + ADSL ETH',
    'SET2 SPLITTER + ADSL ETH', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'SET2 SPLITTER + ADSL USB',
    'SET2 SPLITTER + ADSL USB', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'SET3 NTBA + ADSL USB',
    'SET3 NTBA + ADSL USB', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'SET5 NTBA + ADSL 350I',
    'SET5 NTBA + ADSL 350I', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'SET5 Splitter + ADSL 350I',
    'SET5 Splitter + ADSL 350I', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'SET6 NTBA + ADSL 536I',
    'SET6 NTBA + ADSL 536I', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'SET6 Splitter + ADSL 536I',
    'SET6 Splitter + ADSL 536I', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'SET7 ADSL 536I',
    'SET7 ADSL 536I', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'SET8 NTBA + Turbolink AR871',
    'SET8 NTBA + Turbolink AR871', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'SET8 Splitter + Turbolink AR871',
    'SET8 Splitter + Turbolink AR871', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'SET8 Turbolink AR871',
    'SET8 Turbolink AR871', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'SET9 Turbolink AR871 MaxiPur',
    'SET9 Turbolink AR871 MaxiPur', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'Siemens Gigaset C475',
    'Siemens Gigaset C475', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'Siemens Gigaset C475 (mit Anrufbeantworter)',
    'Siemens Gigaset C475 (mit Anrufbeantworter)', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'WLAN Client: SMC WUSB-G EU',
    'WLAN Client: SMC WUSB-G EU', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

INSERT INTO T_EG (
   ID, INTERNE__ID, NAME,
   BESCHREIBUNG, LS_TEXT, EXT_LEISTUNG__NO,
   VERFUEGBAR_VON, VERFUEGBAR_BIS, GARANTIEZEIT,
   PRODUKTCODE, CONFIGURABLE, CONF_PORTFORWARDING,
   CONF_S0BACKUP, TYPE)
VALUES ( S_T_EG_0.nextval, S_T_EG_0.currval, 'WLAN Router: SMC WBR 14-G2 EU',
    'WLAN Router: SMC WBR 14-G2 EU', null, null,
    to_date('01.01.2000', 'dd.mm.yyyy'), to_date('01.01.2200', 'dd.mm.yyyy'), null,
    null, 0, null,
    null, 3);

