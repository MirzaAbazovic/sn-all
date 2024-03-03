
alter table T_MWF_REQUEST drop constraint CHK_DELAY;
alter table T_MWF_REQUEST add constraint CHK_DELAY check (
  DELAY_SENT_TO_BSI in ('DELAY_SENT_TO_BSI', 'ERROR_SENDING_DELAY_TO_BSI', 'DONT_SEND_DELAY_TO_BSI'));
