update T_TECH_LEISTUNG
  set FFM_QUALIFICATION_ID=(select f.ID from T_FFM_QUALIFICATION f where f.QUALIFICATION='Installationsservice')
  where EXTERN_LEISTUNG__NO in (10100,10101);

