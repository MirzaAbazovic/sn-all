
alter table T_INT_ACCOUNT add EVN_STATUS CHAR(1 BYTE);
comment on column T_INT_ACCOUNT.EVN_STATUS is 'Einzelverbindungsnachweise (EVN) Status';

alter table T_INT_ACCOUNT add PENDING_EVN_STATUS CHAR(1 BYTE);
comment on column T_INT_ACCOUNT.PENDING_EVN_STATUS is 'Kommende aenderung in Einzelverbindungsnachweise (EVN) Status';

