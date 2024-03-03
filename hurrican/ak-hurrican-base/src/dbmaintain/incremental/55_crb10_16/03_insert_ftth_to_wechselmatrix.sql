DROP SEQUENCE S_T_ANBIETERWECHSELCONFIG_0;

ALTER TABLE T_ANBIETERWECHSELCONFIG
 DROP CONSTRAINT T_ANBIETERWECHSELCONFIG_ALT;

ALTER TABLE T_ANBIETERWECHSELCONFIG
 ADD CONSTRAINT T_ANBIETERWECHSELCONFIG_ALT
  CHECK (ALTPRODUKT IN ('DTAG_ANY','CLS', 'ADSL_SH', 'TAL', 'VDSL', 'SDSL', 'ADSL_SA', 'FTTH'));



INSERT INTO T_ANBIETERWECHSELCONFIG (ID,
                                     VERSION,
                                     CARRIERABGEBEND,
                                     ALTPRODUKT,
                                     NEUPRODUKT,
                                     GESCHAEFTSFALLTYP)
     VALUES (15,
             0,
             'OTHER',
             'FTTH',
             'TAL',
             'BEREITSTELLUNG');

INSERT INTO T_ANBIETERWECHSELCONFIG (ID,
                                     VERSION,
                                     CARRIERABGEBEND,
                                     ALTPRODUKT,
                                     NEUPRODUKT,
                                     GESCHAEFTSFALLTYP)
     VALUES (16,
             0,
             'OTHER',
             'FTTH',
             'KEINE_DTAG_LEITUNG',
             'RUFNUMMERNEXPORT_ANSCHLUSSKUENDIGUNG');
