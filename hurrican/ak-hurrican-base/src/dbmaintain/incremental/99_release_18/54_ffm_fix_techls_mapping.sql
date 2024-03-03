update T_TECH_LEISTUNG set FFM_QUALIFICATION_ID=null;

update T_TECH_LEISTUNG set
FFM_QUALIFICATION_ID=(select q.ID from T_FFM_QUALIFICATION q where q.QUALIFICATION='SIP Trunk Access')
  where ID=460;

update T_TECH_LEISTUNG set
   FFM_QUALIFICATION_ID=(select q.ID from T_FFM_QUALIFICATION q where QUALIFICATION='Inhousverkabelung')
 where ID=602;

update T_TECH_LEISTUNG set
   FFM_QUALIFICATION_ID=(select q.ID from T_FFM_QUALIFICATION q where QUALIFICATION='VOIP-TK')
 where ID=301;
