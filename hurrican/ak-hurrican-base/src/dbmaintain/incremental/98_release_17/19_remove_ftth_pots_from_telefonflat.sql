delete from t_rangierungsmatrix where produkt2physiktyp_id = (
  select id from t_produkt_2_physiktyp where prod_id=511 and physiktyp=807);

delete from t_produkt_2_physiktyp where prod_id=511 and physiktyp=807;
