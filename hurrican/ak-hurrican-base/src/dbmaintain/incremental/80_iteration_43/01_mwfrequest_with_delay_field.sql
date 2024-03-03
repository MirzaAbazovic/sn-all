alter table T_MWF_REQUEST add DELAY_SENT_TO_BSI VARCHAR2(50);

alter table T_MWF_REQUEST add constraint CHK_DELAY check (
  DELAY_SENT_TO_BSI in ('DELAY_SENT_TO_BSI', 'ERROR_SENDING_DELAY_TO_BSI'));

-- check constraint auf SENT_TO_BSI nachtragen
alter table T_MWF_REQUEST add constraint CHK_SENT_TO_BSI check (
  SENT_TO_BSI in ('ERROR_SEND_TO_BSI', 'NOT_SENT_TO_BSI', 'SENT_TO_BSI', 'DONT_SEND_TO_BSI'));
