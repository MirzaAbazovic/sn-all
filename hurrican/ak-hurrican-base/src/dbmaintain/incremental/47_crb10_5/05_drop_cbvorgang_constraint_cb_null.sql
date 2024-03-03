-- Da bei der REX-MK keine CB gesetzt werden darf wird die not null constraint fuer die CB_ID
-- mit einer constraint abhaengig vom Geschaeftsfalltyp ersetzt.

alter table T_CB_VORGANG modify CB_ID NUMBER(10) NULL;

