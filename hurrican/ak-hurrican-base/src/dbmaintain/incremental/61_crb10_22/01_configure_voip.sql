-- VoIP Leistungen MGA, TK, PMX auf LONG_VALUE=1 setzen, damit MultipleCalls enabled sind!
update t_tech_leistung set long_value=1 where id in (300,301,302);
