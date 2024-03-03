--
-- Die Seriennummer sollte besser eindeutig sein
--

ALTER TABLE T_IPSEC_C2S_TOKEN
 ADD UNIQUE (SERIAL_NUMBER);