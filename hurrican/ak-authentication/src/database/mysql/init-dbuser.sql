--
-- SQL-Script, um die Benutzer fuer die Authentication-DB anzulegen
--

-- User fuer die Verwendung der DB: auth-user
-- Host: 10.1.%, 10.4.%, 10.3.%
GRANT USAGE ON * . * TO "auth-user"@"10.1.%" IDENTIFIED BY "resu-htua" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `authentication` . * TO "auth-user"@"10.1.%";
GRANT SELECT , UPDATE ON `authentication`.`user` TO "auth-user"@"10.1.%";
GRANT SELECT , INSERT , UPDATE ON `authentication`.`pwhistory` TO "auth-user"@"10.1.%";
GRANT SELECT , INSERT, DELETE ON `authentication`.`usersession` TO "auth-user"@"10.1.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`guicomponent` TO "auth-user"@"10.1.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`compbehavior` TO "auth-user"@"10.1.%";
GRANT USAGE ON * . * TO "auth-user"@"10.4.%" IDENTIFIED BY "resu-htua" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `authentication` . * TO "auth-user"@"10.4.%";
GRANT SELECT , UPDATE ON `authentication`.`user` TO "auth-user"@"10.4.%";
GRANT SELECT , INSERT , UPDATE ON `authentication`.`pwhistory` TO "auth-user"@"10.4.%";
GRANT SELECT , INSERT, DELETE ON `authentication`.`usersession` TO "auth-user"@"10.4.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`guicomponent` TO "auth-user"@"10.4.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`compbehavior` TO "auth-user"@"10.4.%";
GRANT USAGE ON * . * TO "auth-user"@"10.5.%" IDENTIFIED BY "resu-htua" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `authentication` . * TO "auth-user"@"10.5.%";
GRANT SELECT , UPDATE ON `authentication`.`user` TO "auth-user"@"10.5.%";
GRANT SELECT , INSERT , UPDATE ON `authentication`.`pwhistory` TO "auth-user"@"10.5.%";
GRANT SELECT , INSERT, DELETE ON `authentication`.`usersession` TO "auth-user"@"10.5.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`guicomponent` TO "auth-user"@"10.5.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`compbehavior` TO "auth-user"@"10.5.%";
GRANT USAGE ON * . * TO "auth-user"@"10.3.%" IDENTIFIED BY "resu-htua" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `authentication` . * TO "auth-user"@"10.3.%";
GRANT SELECT , UPDATE ON `authentication`.`user` TO "auth-user"@"10.3.%";
GRANT SELECT , INSERT , UPDATE ON `authentication`.`pwhistory` TO "auth-user"@"10.3.%";
GRANT SELECT , INSERT, DELETE ON `authentication`.`usersession` TO "auth-user"@"10.3.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`guicomponent` TO "auth-user"@"10.3.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`compbehavior` TO "auth-user"@"10.3.%";
GRANT USAGE ON * . * TO "auth-user"@"192.168.250.%" IDENTIFIED BY "resu-htua" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `authentication` . * TO "auth-user"@"192.168.250.%";
GRANT SELECT , UPDATE ON `authentication`.`user` TO "auth-user"@"192.168.250.%";
GRANT SELECT , INSERT , UPDATE ON `authentication`.`pwhistory` TO "auth-user"@"192.168.250.%";
GRANT SELECT , INSERT, DELETE ON `authentication`.`usersession` TO "auth-user"@"192.168.250.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`guicomponent` TO "auth-user"@"192.168.250.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`compbehavior` TO "auth-user"@"192.168.250.%";
GRANT USAGE ON * . * TO "auth-user"@"192.168.251.%" IDENTIFIED BY "resu-htua" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `authentication` . * TO "auth-user"@"192.168.251.%";
GRANT SELECT , UPDATE ON `authentication`.`user` TO "auth-user"@"192.168.251.%";
GRANT SELECT , INSERT , UPDATE ON `authentication`.`pwhistory` TO "auth-user"@"192.168.251.%";
GRANT SELECT , INSERT, DELETE ON `authentication`.`usersession` TO "auth-user"@"192.168.251.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`guicomponent` TO "auth-user"@"192.168.251.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`compbehavior` TO "auth-user"@"192.168.251.%";
GRANT USAGE ON * . * TO "auth-user"@"10.203.1.%" IDENTIFIED BY "resu-htua" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `authentication` . * TO "auth-user"@"10.203.1.%";
GRANT SELECT , UPDATE ON `authentication`.`user` TO "auth-user"@"10.203.1.%";
GRANT SELECT , INSERT , UPDATE ON `authentication`.`pwhistory` TO "auth-user"@"10.203.1.%";
GRANT SELECT , INSERT, DELETE ON `authentication`.`usersession` TO "auth-user"@"10.203.1.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`guicomponent` TO "auth-user"@"10.203.1.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`compbehavior` TO "auth-user"@"10.203.1.%";
GRANT USAGE ON * . * TO "auth-user"@"172.22.%" IDENTIFIED BY "resu-htua" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `authentication` . * TO "auth-user"@"172.22.%";
GRANT SELECT , UPDATE ON `authentication`.`user` TO "auth-user"@"172.22.%";
GRANT SELECT , INSERT , UPDATE ON `authentication`.`pwhistory` TO "auth-user"@"172.22.%";
GRANT SELECT , INSERT, DELETE ON `authentication`.`usersession` TO "auth-user"@"172.22.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`guicomponent` TO "auth-user"@"172.22.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`compbehavior` TO "auth-user"@"172.22.%";

-- Host: 10.10.%, 10.20.%, 10.30.%
GRANT USAGE ON * . * TO "auth-user"@"10.10.%" IDENTIFIED BY "resu-htua" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `authentication` . * TO "auth-user"@"10.10.%";
GRANT SELECT , UPDATE ON `authentication`.`user` TO "auth-user"@"10.10.%";
GRANT SELECT , INSERT , UPDATE ON `authentication`.`pwhistory` TO "auth-user"@"10.10.%";
GRANT SELECT , INSERT, DELETE ON `authentication`.`usersession` TO "auth-user"@"10.10.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`guicomponent` TO "auth-user"@"10.10.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`compbehavior` TO "auth-user"@"10.10.%";
GRANT USAGE ON * . * TO "auth-user"@"10.20.%" IDENTIFIED BY "resu-htua" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `authentication` . * TO "auth-user"@"10.20.%";
GRANT SELECT , UPDATE ON `authentication`.`user` TO "auth-user"@"10.20.%";
GRANT SELECT , INSERT , UPDATE ON `authentication`.`pwhistory` TO "auth-user"@"10.20.%";
GRANT SELECT , INSERT, DELETE ON `authentication`.`usersession` TO "auth-user"@"10.20.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`guicomponent` TO "auth-user"@"10.20.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`compbehavior` TO "auth-user"@"10.20.%";
GRANT USAGE ON * . * TO "auth-user"@"10.30.%" IDENTIFIED BY "resu-htua" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `authentication` . * TO "auth-user"@"10.30.%";
GRANT SELECT , UPDATE ON `authentication`.`user` TO "auth-user"@"10.30.%";
GRANT SELECT , INSERT , UPDATE ON `authentication`.`pwhistory` TO "auth-user"@"10.30.%";
GRANT SELECT , INSERT, DELETE ON `authentication`.`usersession` TO "auth-user"@"10.30.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`guicomponent` TO "auth-user"@"10.30.%";
GRANT SELECT , INSERT, UPDATE, DELETE ON `authentication`.`compbehavior` TO "auth-user"@"10.30.%";

-- Fallback-User
GRANT USAGE ON * . * TO "fallback"@"10.1.%" IDENTIFIED BY "fallback" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `authentication` . * TO "fallback"@"10.1.%";
GRANT SELECT , UPDATE ON `authentication`.`user` TO "fallback"@"10.1.%";
GRANT SELECT , INSERT , UPDATE ON `authentication`.`pwhistory` TO "fallback"@"10.1.%";
GRANT SELECT , INSERT, DELETE ON `authentication`.`usersession` TO "fallback"@"10.1.%";
GRANT USAGE ON * . * TO "fallback"@"10.10.%" IDENTIFIED BY "fallback" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `authentication` . * TO "fallback"@"10.10.%";
GRANT SELECT , UPDATE ON `authentication`.`user` TO "fallback"@"10.10.%";
GRANT SELECT , INSERT , UPDATE ON `authentication`.`pwhistory` TO "fallback"@"10.10.%";
GRANT SELECT , INSERT, DELETE ON `authentication`.`usersession` TO "fallback"@"10.10.%";
GRANT USAGE ON * . * TO "fallback"@"10.4.%" IDENTIFIED BY "fallback" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `authentication` . * TO "fallback"@"10.4.%";
GRANT SELECT , UPDATE ON `authentication`.`user` TO "fallback"@"10.4.%";
GRANT SELECT , INSERT , UPDATE ON `authentication`.`pwhistory` TO "fallback"@"10.4.%";
GRANT SELECT , INSERT, DELETE ON `authentication`.`usersession` TO "fallback"@"10.4.%";
GRANT USAGE ON * . * TO "fallback"@"10.20.%" IDENTIFIED BY "fallback" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `authentication` . * TO "fallback"@"10.20.%";
GRANT SELECT , UPDATE ON `authentication`.`user` TO "fallback"@"10.20.%";
GRANT SELECT , INSERT , UPDATE ON `authentication`.`pwhistory` TO "fallback"@"10.20.%";
GRANT SELECT , INSERT, DELETE ON `authentication`.`usersession` TO "fallback"@"10.20.%";
GRANT USAGE ON * . * TO "fallback"@"10.3.%" IDENTIFIED BY "fallback" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `authentication` . * TO "fallback"@"10.3.%";
GRANT SELECT , UPDATE ON `authentication`.`user` TO "fallback"@"10.3.%";
GRANT SELECT , INSERT , UPDATE ON `authentication`.`pwhistory` TO "fallback"@"10.3.%";
GRANT SELECT , INSERT, DELETE ON `authentication`.`usersession` TO "fallback"@"10.3.%";
GRANT USAGE ON * . * TO "fallback"@"10.30.%" IDENTIFIED BY "fallback" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `authentication` . * TO "fallback"@"10.30.%";
GRANT SELECT , UPDATE ON `authentication`.`user` TO "fallback"@"10.30.%";
GRANT SELECT , INSERT , UPDATE ON `authentication`.`pwhistory` TO "fallback"@"10.30.%";
GRANT SELECT , INSERT, DELETE ON `authentication`.`usersession` TO "fallback"@"10.30.%";
GRANT USAGE ON * . * TO "fallback"@"192.168.250.47" IDENTIFIED BY "fallback" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT ON `authentication` . * TO "fallback"@"192.168.250.47";
GRANT SELECT , UPDATE ON `authentication`.`user` TO "fallback"@"192.168.250.47";
GRANT SELECT , INSERT , UPDATE ON `authentication`.`pwhistory` TO "fallback"@"192.168.250.47";
GRANT SELECT , INSERT, DELETE ON `authentication`.`usersession` TO "fallback"@"192.168.250.47";


-- User fuer die Administration: auth-admin
-- Host: 10.1.%
GRANT USAGE ON * . * TO "auth-admin"@"10.1.%" IDENTIFIED BY "akom01" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE , DELETE , CREATE , DROP , REFERENCES , INDEX , ALTER , LOCK TABLES ON `authentication` . * TO "auth-admin"@"10.1.%"WITH GRANT OPTION ;
GRANT USAGE ON * . * TO "auth-admin"@"10.4.%" IDENTIFIED BY "akom01" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE , DELETE , CREATE , DROP , REFERENCES , INDEX , ALTER , LOCK TABLES ON `authentication` . * TO "auth-admin"@"10.4.%"WITH GRANT OPTION ;
GRANT USAGE ON * . * TO "auth-admin"@"10.3.%" IDENTIFIED BY "akom01" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE , DELETE , CREATE , DROP , REFERENCES , INDEX , ALTER , LOCK TABLES ON `authentication` . * TO "auth-admin"@"10.3.%"WITH GRANT OPTION ;
GRANT USAGE ON * . * TO "auth-admin"@"10.5.%" IDENTIFIED BY "akom01" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE , DELETE , CREATE , DROP , REFERENCES , INDEX , ALTER , LOCK TABLES ON `authentication` . * TO "auth-admin"@"10.5.%"WITH GRANT OPTION ;
-- Host: 10.10.%
GRANT USAGE ON * . * TO "auth-admin"@"10.10.%" IDENTIFIED BY "akom01" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE , DELETE , CREATE , DROP , REFERENCES , INDEX , ALTER , LOCK TABLES ON `authentication` . * TO "auth-admin"@"10.10.%"WITH GRANT OPTION ;
GRANT USAGE ON * . * TO "auth-admin"@"10.20.%" IDENTIFIED BY "akom01" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE , DELETE , CREATE , DROP , REFERENCES , INDEX , ALTER , LOCK TABLES ON `authentication` . * TO "auth-admin"@"10.20.%"WITH GRANT OPTION ;
GRANT USAGE ON * . * TO "auth-admin"@"10.30.%" IDENTIFIED BY "akom01" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE , DELETE , CREATE , DROP , REFERENCES , INDEX , ALTER , LOCK TABLES ON `authentication` . * TO "auth-admin"@"10.30.%"WITH GRANT OPTION ;
GRANT USAGE ON * . * TO "auth-admin"@"10.50.%" IDENTIFIED BY "akom01" WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 ;
GRANT SELECT , INSERT , UPDATE , DELETE , CREATE , DROP , REFERENCES , INDEX , ALTER , LOCK TABLES ON `authentication` . * TO "auth-admin"@"10.50.%"WITH GRANT OPTION ;


