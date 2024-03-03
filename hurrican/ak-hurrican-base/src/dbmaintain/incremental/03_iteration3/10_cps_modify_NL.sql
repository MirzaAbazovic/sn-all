--
-- Erweiterung der Niederlassung um einen CPS-Namen
--

alter table T_NIEDERLASSUNG add CPS_PROVISIONING_NAME VARCHAR2(20);
update T_NIEDERLASSUNG set CPS_PROVISIONING_NAME='Augsburg' where ID in (1,2);
update T_NIEDERLASSUNG set CPS_PROVISIONING_NAME='Muenchen' where ID in (3);
update T_NIEDERLASSUNG set CPS_PROVISIONING_NAME='Nuernberg' where ID in (4);
update T_NIEDERLASSUNG set CPS_PROVISIONING_NAME='Muenchen' where ID in (5);
