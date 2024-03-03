alter table T_CB_VORGANG add TAL_REAL_TIMESLOT varchar2(25);

alter table T_CB_VORGANG add constraint CHK_CBV_REAL_TIMESLOT check
  (TAL_REAL_TIMESLOT in ('VORMITTAG', 'NACHMITTAG', 'GANZTAGS'));
