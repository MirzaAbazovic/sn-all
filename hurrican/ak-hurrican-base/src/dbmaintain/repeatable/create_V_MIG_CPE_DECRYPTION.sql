-- View fuer die Ermittlung der verschluesselten Passwoerter auf CPEs

create or replace view V_MIG_CPE_DECRYPTION as
  select
    egc.ID as EG_CONFIG_ID,
    egc.EG_PASSWORD as EG_PASSWORD
  from
    T_EG_CONFIG egc
  where
    egc.EG_PASSWORD is not null;
