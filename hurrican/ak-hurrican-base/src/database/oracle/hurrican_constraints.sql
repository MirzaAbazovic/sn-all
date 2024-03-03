--
-- Constraint-Angaben fuer die Hurrican-DB
--

-- Check-Constraint auf T_EQUIPMENT
ALTER TABLE T_EQUIPMENT
  add CONSTRAINT CK_T_EQUIPMENT_STATUS
     CHECK (STATUS IN (null, 'rang', 'vorb', 'frei', 'gelöscht', 'defekt', 'res'));
     
