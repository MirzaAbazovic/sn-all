-- logically delete unused response columns - physical deletion is too time and resource consuming for this large table
-- physical deletion can be done separately with: ALTER TABLE T_IO_ARCHIVE DROP UNUSED COLUMNS;
ALTER TABLE T_IO_ARCHIVE SET UNUSED (RESPONSE_TIMEST, RESPONSE_MELDUNGSCODE, RESPONSE_MELDUNGSTEXT, RESPONSE_XML);