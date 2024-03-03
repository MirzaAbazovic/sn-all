CREATE OR REPLACE VIEW V_MIG_GETDBLINK4CURRENTUSER AS
SELECT
DECODE
(myCURRENT_SCHEMA,
'HURRICAN_KUP_E2E', 'kupmig01',
'HURRICAN_KUP_SCHWARZFL', 'kupmig01',
'HURRICAN_KUP_EYMILLERAN', 'kupmig01',
'HURRICAN_KUP_BSIMIG', 'kupmig01',
'HURRICAN_KUP_CI', 'kupmig01',
'HURRICAN_KUP_WAGNERMA', 'kupmig01',
'HURRICAN_KUP_TRUNK', 'kupmig01',
'HURRICAN_KUP_RELEASE', 'kupmig01',
'HURRICAN_KUP_KOSKAWO', 'kupmig01',
'HURRICAN_KUP_GLINKJO', 'kupmig01',
'HURRICAN_CPS_INTEGRATION', 'kupmig01',
'HURRICAN_KUP_USERTEST', 'kupmig01',
'HURRICAN_KUP_KEMPFLE', 'kupmig01',
'HURRICAN_KUP_TEST', 'kupmig01',
'HURRICAN_KUP_GILGAN', 'kupmig01',
'HURRICAN_KUP_WOLFST', 'kupmig01',
-- default is
'HURRICAN', 'kupvis',
'kupvis'
) AS myDBLINK FROM
(
SELECT SYS_CONTEXT('USERENV','CURRENT_SCHEMA')  AS myCURRENT_SCHEMA FROM DUAL
)
/