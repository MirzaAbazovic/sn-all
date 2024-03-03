-- Fixed typo in table name
-- Created abbreviation for 'ANSCHLUSSS' since oracle only supports 30 chars in table name
ALTER TABLE
  T_WBCI_GESCHAFTSFALL_ANSCHLUSS
RENAME TO
  T_WBCI_GESCHAEFTSFALL_ANSCHL;

GRANT SELECT, INSERT, UPDATE ON T_WBCI_GESCHAEFTSFALL_ANSCHL TO R_HURRICAN_USER;
GRANT SELECT ON T_WBCI_GESCHAEFTSFALL_ANSCHL TO R_HURRICAN_READ_ONLY;