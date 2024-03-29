ALTER TABLE T_FFM_PRODUCT_MAPPING drop constraint CK_FFMPRODMAP_AGGSTRATEGY;

update T_FFM_PRODUCT_MAPPING set AGGREGATION_STRATEGY = 'HEADER_ONLY_WITH_TIMESLOT' where AGGREGATION_STRATEGY = 'HEADER_ONLY_WITHOUT_TIMESLOT';

ALTER TABLE T_FFM_PRODUCT_MAPPING ADD (
  CONSTRAINT CK_FFMPRODMAP_AGGSTRATEGY
  CHECK (AGGREGATION_STRATEGY IN (
    'HEADER_ONLY_WITH_TIMESLOT',
    'HOUSING',
    'TECHNICAL_PARAMS_INCLUDED_WITH_TIMESLOT'
  )));
