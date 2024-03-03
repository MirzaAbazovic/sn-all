-- Ebene 3 fuer PD 'verdrahten':
-- >= 20.000€ => 'Großprojkte GK'
-- <  20.000€ => 'Standardprojekte GK'

INSERT INTO T_REGISTRY (ID, NAME, INT_VALUE, DESCRIPTION) VALUES
  (1200, 'ia.budget.projekte.schwellwert', 20000,
   '< Schwellwert -> Standardprojekt, >= Schwellwert -> Grossprojekt');

INSERT INTO T_REGISTRY (ID, NAME, STR_VALUE, DESCRIPTION) VALUES
  (1201, 'ia.budget.ebene3.pd.standardprojekte_gk', 'Standardprojekte GK',
   'Auswahl der Ebene 3 fuer PD als Standardprojekt (siehe T_IA_LEVEL3)');

INSERT INTO T_REGISTRY (ID, NAME, STR_VALUE, DESCRIPTION) VALUES
  (1202, 'ia.budget.ebene3.pd.grossprojekte_gk', 'Gro' || UNISTR('\00DF') || 'projekte GK',
   'Auswahl der Ebene 3 fuer PD als Grossprojekt(siehe T_IA_LEVEL3)');
