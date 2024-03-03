update T_WBCI_GESCHAEFTSFALL set status='versendet' where status is null;
alter table T_WBCI_GESCHAEFTSFALL modify STATUS NOT NULL;
