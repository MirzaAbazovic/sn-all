insert into T_REFERENCE (ID, TYPE, STR_VALUE, GUI_VISIBLE, ORDER_NO)
  values (207, 'ADDRESS_TYPE', 'E-Mail-Adresse', '1', 130);

update T_REFERENCE
  set INT_VALUE = 207
  where id = 16015;

alter table T_ADDRESS modify ORT NULL;
alter table T_ADDRESS modify LAND_ID NULL;
