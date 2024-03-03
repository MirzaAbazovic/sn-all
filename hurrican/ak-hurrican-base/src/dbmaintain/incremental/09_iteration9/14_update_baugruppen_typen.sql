update T_HW_BAUGRUPPEN_TYP set HW_SCHNITTSTELLE_NAME = 'SDSL'
where HW_SCHNITTSTELLE_NAME is null
  and NAME in ('SHEB', 'SHLB',
               'SMLTJ', 'SMLTC', 'SMLTA', 'SHLTB');

update T_HW_BAUGRUPPEN_TYP set HW_SCHNITTSTELLE_NAME = 'ADSL'
where HW_SCHNITTSTELLE_NAME is null
  and NAME in ('SUADSL:32I', 'ADQD', 'ADBF',
               'EBLTD', 'ADLTN', 'ADLTK', 'ABLTF', 'NALTD', 'NSLTA', 'EBLTF');
