INSERT INTO T_PROFILE_PARAMETER (ID, PARAMETER_NAME, PARAMETER_VALUE, IS_DEFAULT, HW_BAUGRUPPEN_TYP_ID)
  SELECT
    S_T_PROFILE_PARAMETER_0.nextval,
    'Traffic Table Downstream' AS PARAMETER_NAME,
    '1' AS PARAMETER_VALUE,
    0 AS IS_DEFAULT,
    EXISTING_BAUGRUPPEN_TYP_ID
  FROM (SELECT HW_BAUGRUPPEN_TYP_ID AS EXISTING_BAUGRUPPEN_TYP_ID FROM T_PROFILE_PARAMETER GROUP BY HW_BAUGRUPPEN_TYP_ID);