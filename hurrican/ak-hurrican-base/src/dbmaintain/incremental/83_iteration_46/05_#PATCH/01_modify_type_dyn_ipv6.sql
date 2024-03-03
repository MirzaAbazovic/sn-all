-- Type der technischen Leistung 'dynamische IPv6' modifizieren (aus ONL_IP_OPTION -> ONL_IP_DEFAULT )
update T_TECH_LEISTUNG set TYP = 'ONL_IP_DEFAULT' where id = 57;