
-- �/COLP X   COLP, Anzeige der verbundenen RN bei ARU      60009
UPDATE T_LEISTUNG_4_DN SET EXTERN_LEISTUNG__NO = 60009 WHERE ID = 9;

-- �/RN-�bermittlung (CLIP) X   CLIP, Rufnummernanzeige     60005
UPDATE T_LEISTUNG_4_DN SET EXTERN_LEISTUNG__NO = 60005 WHERE ID = 5;

-- �/RN-Unterdr�ckung (CLIR1) X   CLIR, Anzeige der eigenen Rufnummer unterdr�cken    60006
UPDATE T_LEISTUNG_4_DN SET EXTERN_LEISTUNG__NO = 60006 WHERE ID = 6;

-- �/RN-Unterdr�ckung kommend (COLR) X   COLR, Anzeige der verb. RN beim A-Tln   60010
UPDATE T_LEISTUNG_4_DN SET EXTERN_LEISTUNG__NO = 60010 WHERE ID = 10;

-- �/Parallel Ringing X   Parallel Ringing (DIVIP)    60021
UPDATE T_LEISTUNG_4_DN SET EXTERN_LEISTUNG__NO = 60021 WHERE ID = 21;

-- �/CLIP no screening X   CLIP NO SCREENING, keine �berpr�fung der A-RN   60004
UPDATE T_LEISTUNG_4_DN SET EXTERN_LEISTUNG__NO = 60004 WHERE ID = 4;

-- �/COLP no screening X COLP NO SCREENING 60080
UPDATE T_LEISTUNG_4_DN SET EXTERN_LEISTUNG__NO = 60080 WHERE ID = 80;
