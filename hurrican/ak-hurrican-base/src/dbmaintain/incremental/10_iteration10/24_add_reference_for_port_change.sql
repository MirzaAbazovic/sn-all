
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (22005, 'PORT_CHANGE_REASON', 'Systemkabel defekt', 7, '1', 10, NULL);

update T_REFERENCE SET ORDER_NO=20 where ID=22001;
update T_REFERENCE SET ORDER_NO=30 where ID=22002;
update T_REFERENCE SET ORDER_NO=40 where ID=22003;
update T_REFERENCE SET ORDER_NO=50 where ID=22005;
update T_REFERENCE SET ORDER_NO=60 where ID=22004;
