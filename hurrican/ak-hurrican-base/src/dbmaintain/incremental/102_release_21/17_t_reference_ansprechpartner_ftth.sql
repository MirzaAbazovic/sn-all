insert into T_REFERENCE (ID, TYPE, STR_VALUE, int_value, GUI_VISIBLE, ORDER_NO, DESCRIPTION)
  select 23315, 'ANSPRECHPARTNER', 'Partner FttH Modernisierung NE4', 204, '1', 190, 'Ansprechpartner: Partner FttH Modernisierung NE4'
  from dual where not exists (select 1 from T_REFERENCE tt where tt.id = 23315);
