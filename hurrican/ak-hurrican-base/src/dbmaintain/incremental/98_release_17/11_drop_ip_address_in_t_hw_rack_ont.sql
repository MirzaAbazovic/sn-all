-- IP configs are not usable for ONTs, see HUR-19354
ALTER TABLE T_HW_RACK_ONT DROP COLUMN IP_ADDRESS;