--Die Spalte AUSWAEHLBAR_PV sollte not null sein da es einen default Wert hat--

alter table T_CARRIER modify ( AUSWAEHLBAR_PV not null );