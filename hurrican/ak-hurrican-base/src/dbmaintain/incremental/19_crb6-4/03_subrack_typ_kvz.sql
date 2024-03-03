-- neuen Subrack-Typ anlegen
Insert into T_HW_SUBRACK_TYP
   (ID, NAME, DESCRIPTION, RACK_TYP, BG_COUNT,
    PORT_COUNT, HW_TYPE_NAME, VERSION)
 Values
   (S_T_HW_SUBRACK_TYP_0.nextVal, 'REM', NULL, 'DSLAM', 2, 48, 'XDSL_MUC', 0);
