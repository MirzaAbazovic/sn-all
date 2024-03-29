ALTER TABLE T_MWF_GESCHAEFTSFALL
MODIFY(GESCHAEFTSFALLTYP VARCHAR2(40 BYTE));

update T_MWF_GESCHAEFTSFALL g
set G.GESCHAEFTSFALLTYP = 'BEREITSTELLUNG'
where G.GESCHAEFTSFALLTYP = 'NEU';

update T_MWF_GESCHAEFTSFALL g
set G.GESCHAEFTSFALLTYP = 'BESTANDSUEBERSICHT'
where G.GESCHAEFTSFALLTYP = 'AUS-BUE';

update T_MWF_GESCHAEFTSFALL g
set G.GESCHAEFTSFALLTYP = 'KUENDIGUNG_KUNDE'
where G.GESCHAEFTSFALLTYP = 'KUE-KD';

update T_MWF_GESCHAEFTSFALL g
set G.GESCHAEFTSFALLTYP = 'KUENDIGUNG_TELEKOM'
where G.GESCHAEFTSFALLTYP = 'KUE-DT';

update T_MWF_GESCHAEFTSFALL g
set G.GESCHAEFTSFALLTYP = 'LEISTUNGSMERKMAL_AENDERUNG'
where G.GESCHAEFTSFALLTYP = 'AEN-LMAE';

update T_MWF_GESCHAEFTSFALL g
set G.GESCHAEFTSFALLTYP = 'LEISTUNGS_AENDERUNG'
where G.GESCHAEFTSFALLTYP = 'LAE';

update T_MWF_GESCHAEFTSFALL g
set G.GESCHAEFTSFALLTYP = 'PORTWECHSEL'
where G.GESCHAEFTSFALLTYP = 'SER-POW';

update T_MWF_GESCHAEFTSFALL g
set G.GESCHAEFTSFALLTYP = 'PRODUKTGRUPPENWECHSEL'
where G.GESCHAEFTSFALLTYP = 'PGW';

update T_MWF_GESCHAEFTSFALL g
set G.GESCHAEFTSFALLTYP = 'PROVIDERWECHSEL'
where G.GESCHAEFTSFALLTYP = 'PV';

update T_MWF_GESCHAEFTSFALL g
set G.GESCHAEFTSFALLTYP = 'RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG'
where G.GESCHAEFTSFALLTYP = 'REX-MK';

update T_MWF_GESCHAEFTSFALL g
set G.GESCHAEFTSFALLTYP = 'VERBUNDLEISTUNG'
where G.GESCHAEFTSFALLTYP = 'VBL';