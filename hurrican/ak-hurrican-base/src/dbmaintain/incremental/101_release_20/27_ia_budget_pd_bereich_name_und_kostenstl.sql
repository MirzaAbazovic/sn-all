insert into t_registry (ID, NAME, STR_VALUE, DESCRIPTION)
select 1204, 'ia.budget.pd.bereich.name', 'TE-PD', 'PD Bereich Name'
from dual where not exists (select 1 from t_registry t where t.id = 1204 );

insert into t_registry (ID, NAME, STR_VALUE, DESCRIPTION)
select  1205, 'ia.budget.pd.bereich.kostenstelle', 'Z533414', 'PD Bereich Kostenstelle'
from dual where not exists (select 1 from t_registry t where t.id = 1205 );
