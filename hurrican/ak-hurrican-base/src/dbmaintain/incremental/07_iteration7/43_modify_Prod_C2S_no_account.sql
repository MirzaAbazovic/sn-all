-- Deaktiviere die automatische Account-Erzeugung fuer Client2Site

update t_produkt set li_nr = null where
anschlussart = 'VPN IPSec Client-to-Site';