-- Siehe 15_add_type_t_reference.sql und https://jira.m-net.de/browse/HUR-26317
update T_REFERENCE
set
  TYPE = 'PROVIDED_OE_NO_TYPE',
  STR_VALUE = '3400, 3010, 3007',
  DESCRIPTION = 'enthaelt OeNO und VaterOeNO Eintraege aus der Billing OE-Tabelle fuer CustomerOrderService.getCustomerLoginDetails()'
where ID = 23320;
