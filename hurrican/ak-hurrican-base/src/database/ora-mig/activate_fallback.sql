--
-- Script, um die User auf ein Fallback-System zu verweisen.
--

-- auszufuehren auf DB_SERVER_1

use authentication;

update db set url='jdbc:oracle:thin:@192.168.229.19:1521:MNETPRODxxx' where id=3;

