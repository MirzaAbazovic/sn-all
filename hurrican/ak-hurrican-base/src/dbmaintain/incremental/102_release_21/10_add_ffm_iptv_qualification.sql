-- ANF-547/HUR-23774

-- Add Qualification
INSERT INTO T_FFM_QUALIFICATION
   (ID, VERSION, QUALIFICATION, ADDITIONAL_DURATION)
 VALUES
   (S_T_FFM_QUALIFICATION_0.nextval, 0, 'Installationsservice+IPTV', 30);

-- Add Mapping
INSERT INTO T_FFM_QUALIFICATION_MAPPING
   (ID, QUALIFICATION_ID, PRODUCT_ID, TECH_LEISTUNG_ID, STANDORT_REF_ID,
    VERSION, VPN)
 VALUES
   (S_T_FFM_QUAL_MAPPING_0.nextval, (SELECT ID FROM T_FFM_QUALIFICATION WHERE QUALIFICATION = 'Installationsservice+IPTV'),
   NULL, 117, NULL, 0, NULL);