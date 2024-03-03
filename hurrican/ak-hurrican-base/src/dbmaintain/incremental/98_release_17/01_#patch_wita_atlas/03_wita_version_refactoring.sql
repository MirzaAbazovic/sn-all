-- Column WITA_VERSION renamed to CDM_VERSION. Hurrican uses the WITA CDM interface from AtlasESB.
alter table T_MWF_REQUEST rename column WITA_VERSION to CDM_VERSION;
update T_MWF_REQUEST set CDM_VERSION = '1';
alter table T_MWF_MELDUNG rename column WITA_VERSION to CDM_VERSION;
update T_MWF_MELDUNG set CDM_VERSION = '1';
