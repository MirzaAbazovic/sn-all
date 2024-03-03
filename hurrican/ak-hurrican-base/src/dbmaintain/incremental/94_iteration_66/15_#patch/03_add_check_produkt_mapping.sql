-- ' ' und '.' bedeuten beide das kein mapping_part_type notwendig ist
update T_PRODUKT_MAPPING set MAPPING_PART_TYPE = '.' where MAPPING_PART_TYPE = ' ';

ALTER TABLE T_PRODUKT_MAPPING ADD (
  CONSTRAINT CHK_PRODUKTMAP_MAP_PART_TYPE
  CHECK (MAPPING_PART_TYPE in ('phone', 'dsl', 'phone_dsl', 'sdsl', 'sdsl_sub', 'connect', 'online',
          'tv', 'housing', 'adsl', '.')) ENABLE VALIDATE);