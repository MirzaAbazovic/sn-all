-- Den DSL Downstream Schwellwert in die Registry schreiben
insert into T_REGISTRY (ID, NAME, STR_VALUE, INT_VALUE, DESCRIPTION)
  values (250, 'dsl.downstream.schwellwert', null, 6000, 
  'Schwellwert, der angibt ab welchem Wert eine Anschlussübernahme für alte ADSL Ports noch möglich ist');
