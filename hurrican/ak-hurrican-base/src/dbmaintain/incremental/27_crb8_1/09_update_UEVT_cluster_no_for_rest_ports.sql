
-- default for rest is 1 (Kollokation und anderes in MUC)
UPDATE T_EQUIPMENT EQ SET EQ.UEVT_CLUSTER_NO = 1 WHERE EQ.UEVT_CLUSTER_NO IS NULL AND EQ.CARRIER = 'DTAG';
