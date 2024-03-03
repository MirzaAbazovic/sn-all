--
-- SO-Types lock / query auf visible=false setzen
--

update T_REFERENCE set GUI_VISIBLE='0' where ID in (14006, 14003);
