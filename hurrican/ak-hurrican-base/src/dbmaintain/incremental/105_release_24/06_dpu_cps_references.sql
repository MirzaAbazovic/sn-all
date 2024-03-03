insert into T_REFERENCE (ID, TYPE, STR_VALUE, int_value, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  select 14013, 'CPS_SERVICE_ORDER_TYPE', 'initializeDPU', null, 0, null, 'Wert definiert den ServiceOrder-Typ, ueber den eine DPU zu initialisieren'
  from dual where not exists (select 1 from T_REFERENCE tt where tt.id = 14013);

insert into T_REFERENCE (ID, TYPE, STR_VALUE, int_value, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  select 14014, 'CPS_SERVICE_ORDER_TYPE', 'updateDPU', null, 0, null, 'Wert definiert den ServiceOrder-Typ, ueber den eine DPU aktualisiert wird'
  from dual where not exists (select 1 from T_REFERENCE tt where tt.id = 14014);

insert into T_REFERENCE (ID, TYPE, STR_VALUE, int_value, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  select 14108, 'CPS_TX_SOURCE', 'HURRICAN_DPU', null, 0, null, 'Wert definiert, dass die Transaction fuer eine DPU bestimmt ist'
  from dual where not exists (select 1 from T_REFERENCE tt where tt.id = 14108);

