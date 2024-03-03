alter table T_HW_RACK_DSLAM add  CC_OFFSET NUMBER(9);
COMMENT ON COLUMN T_HW_RACK_DSLAM.CC_OFFSET IS 'Offset zur Cross Connection Berechnung in KVZ-REMs';
