-- AEN-LMAE needs 25 chars, extend to 40 to avoid further problems

ALTER TABLE T_IO_ARCHIVE
MODIFY(REQUEST_GESCHAEFTSFALL VARCHAR2(40 BYTE));