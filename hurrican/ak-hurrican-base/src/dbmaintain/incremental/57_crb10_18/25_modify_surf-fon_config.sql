-- Surf&Fon Komfort nicht ueber Protokoll-Leistung! (wird ueber Produkt-Mapping Type 'phone' bzw. 'phone_dsl' bereits unterschieden)
update T_LB_2_PRODUKT set LEISTUNG__NO=72779, PROTOKOLL_LEISTUNG__NO=null where PROTOKOLL_LEISTUNG__NO=72779;
update T_LB_2_PRODUKT set LEISTUNG__NO=72780, PROTOKOLL_LEISTUNG__NO=null where PROTOKOLL_LEISTUNG__NO=72780;
