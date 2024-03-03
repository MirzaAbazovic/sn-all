update T_HW_SUBRACK_TYP set PORT_COUNT = 32 where NAME = 'VDSL_MA5600';

INSERT INTO T_HW_SUBRACK (ID, MOD_NUMBER, RACK_ID,  SUBRACK_TYP_ID, VERSION)
VALUES ( S_T_HW_SUBRACK_0.nextval, '1-1', 706, (select ID from T_HW_SUBRACK_TYP where NAME = 'VDSL_MA5600'), 0 );

-- Subracks zuordnen
update T_HW_BAUGRUPPE bg set SUBRACK_ID = (select ID from T_HW_SUBRACK where RACK_ID = bg.rack_ID) where HW_BG_TYP_ID = 231;
