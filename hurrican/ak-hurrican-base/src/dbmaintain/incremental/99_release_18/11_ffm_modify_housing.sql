-- Housing
-- Skill/Activity Type korrigieren
update T_FFM_QUALIFICATION set FFM_QUALIFICATION='Housing' where FFM_QUALIFICATION='Endkundenservice Housing';
update T_FFM_PRODUCT_MAPPING set FFM_ACTIVITY_TYPE='RTL_NEU_IK' where FFM_ACTIVITY_TYPE='RTL_Hou';

