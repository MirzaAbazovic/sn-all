-- GSLAMs haben in Command eine alternative Geraetebezeichnung (OLT-XXXXXX)
ALTER TABLE t_hw_rack_dslam ADD ("ALT_GSLAM_BEZ" VARCHAR2(50));
COMMENT ON COLUMN t_hw_rack_dslam.ALT_GSLAM_BEZ IS 'Alternative Gereatebezeichnung fuer GSLAMs in Command';
