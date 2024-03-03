--
-- Sequence T_CARRIER_MAPPING
--
CREATE sequence S_T_CARRIER_MAPPING_0 START WITH 80;
grant select on S_T_CARRIER_MAPPING_0 to public;

--
-- Sequence T_CARRIER_CONTACT
--
CREATE sequence S_T_CARRIER_CONTACT_0 START WITH 80;
grant select on S_T_CARRIER_CONTACT_0 to public;

--
-- Grant's f�r T_CARRIER_MAPPING
--
GRANT SELECT, INSERT, UPDATE ON T_CARRIER_MAPPING TO R_HURRICAN_USER;
GRANT SELECT ON T_CARRIER_MAPPING TO R_HURRICAN_READ_ONLY;

--
-- Neues Feld EMAIL in T_CARRIER_CONTACT
--
ALTER TABLE T_CARRIER_CONTACT ADD ( "FAULT_CLEARING_EMAIL" VARCHAR(50) );


