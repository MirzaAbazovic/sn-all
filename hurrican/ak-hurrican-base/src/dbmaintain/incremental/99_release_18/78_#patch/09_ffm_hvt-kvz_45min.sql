update T_FFM_PRODUCT_MAPPING set FFM_PLANNED_DURATION=45 where FFM_ACTIVITY_TYPE in (
    'RTL_Neu_MK_FttC',
    'RTL_Neu_MK_HVT'
    );
