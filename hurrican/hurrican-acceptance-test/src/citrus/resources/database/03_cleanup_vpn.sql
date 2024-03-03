update T_AUFTRAG_TECHNIK set VPN_ID=NULL 
  where VPN_ID=(select VPN_ID from T_VPN where VPN_NAME='VPN789');
delete from T_VPN where VPN_NAME='VPN789';