ALTER TABLE T_WBCI_GESCHAEFTSFALL drop constraint ck_wbcigf_status;
ALTER TABLE T_WBCI_GESCHAEFTSFALL
ADD CONSTRAINT CK_WBCIGF_STATUS
CHECK (STATUS IN (
    'ACTIVE',
    'COMPLETE'
  )) ENABLE NOVALIDATE;

UPDATE T_WBCI_GESCHAEFTSFALL set STATUS = 'ACTIVE';