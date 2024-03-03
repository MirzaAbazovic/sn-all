update T_WBCI_GESCHAEFTSFALL set status='VERSENDET' where status='versendet';
update T_WBCI_GESCHAEFTSFALL set status='VORGEHALTEN' where status='vorgehalten';
update T_WBCI_GESCHAEFTSFALL set status='BEANTWORTET' where status='beantwortet';

ALTER TABLE T_WBCI_GESCHAEFTSFALL
ADD CONSTRAINT CK_WBCIGF_STATUS
CHECK (TYP IN (
    'VORGEHALTEN',
    'VERSENDET',
    'BEANTWORTET'
  )) ENABLE NOVALIDATE;
