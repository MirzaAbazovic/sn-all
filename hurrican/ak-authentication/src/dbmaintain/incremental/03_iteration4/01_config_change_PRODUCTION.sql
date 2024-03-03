--
-- SQL Script konfiguriert in der Authentication DB die URL
-- fuer Hurrican um.
-- Der Update ist nur fuer die Produktiv-DB relevant!
--

update DB set URL='jdbc:oracle:thin:@hcprod01-vip.m-net.de:1524:HCPROD01'
  where URL='jdbc:oracle:thin:@mnetdbsvr17.m-net.de:1521:HCPROD01';
