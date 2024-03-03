ALTER TABLE T_WBCI_GESCHAEFTSFALL drop constraint CK_WBCIGF_TYP;
ALTER TABLE T_WBCI_GESCHAEFTSFALL
ADD CONSTRAINT CK_WBCIGF_TYP
CHECK (TYP IN (
    'VA_KUE_MRN',
    'VA_KUE_ORN',
    'VA_RRNP',
    'VA_UNBEKANNT'
  )) ENABLE NOVALIDATE;