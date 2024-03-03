
update t_reference set int_value=7 where ID=22000;
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (22001, 'PORT_CHANGE_REASON', 'DSLAM Port defekt', 7, '1', 10, NULL);
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (22002, 'PORT_CHANGE_REASON', 'DLU Port defekt', 7, '1', 10, NULL);
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (22003, 'PORT_CHANGE_REASON', 'Test-Wechsel', 7, '1', 10, NULL);
insert into T_REFERENCE (ID, TYPE, STR_VALUE, INT_VALUE, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  values (22004, 'PORT_CHANGE_REASON', 'Wechsel aufheben - kein Defekt', 1, '1', 10, NULL);


