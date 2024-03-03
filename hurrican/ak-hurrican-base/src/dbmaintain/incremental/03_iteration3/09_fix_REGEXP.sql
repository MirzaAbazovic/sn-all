-- Fix regular expressions

UPDATE T_CFG_REG_EXP SET REGULAR_EXP = '^\d+-\d+-\d+-\d+$', MATCH_GROUP = 0 WHERE ID IN (2,3);
