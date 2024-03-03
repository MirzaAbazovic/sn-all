ALTER TABLE T_REPORT ADD DISABLED CHAR(1);
COMMENT ON COLUMN T_REPORT.DISABLED IS 'Falls disabled wird der Report nicht mehr angezeigt.';

update T_REPORT set DISABLED=1
 where ID in
          (1,
           2,
           3,
           4,
           5,
           7,
           8,
           9,
           10,
           11,
           12,
           13,
           14,
           15,
           22,
           23,
           24,
           25,
           26,
           27,
           28,
           29,
           70,
           72,
           73);

delete from T_REPORT_2_PROD where REP_ID in
          (1,
           2,
           3,
           4,
           5,
           7,
           8,
           9,
           10,
           11,
           12,
           13,
           14,
           15,
           22,
           23,
           24,
           25,
           26,
           27,
           28,
           29,
           70,
           72,
           73);

