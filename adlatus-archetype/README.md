# Archetype for Adlatus service

Used to create new adlatus service.

In interactive mode :
```shell
mvn archetype:generate \
-DarchetypeGroupId=de.bitconex.adlatus \
-DarchetypeArtifactId=adlatus-archetype\
-DarchetypeVersion=1.0.0
```

not interactive (you give service name)

```shell
mvn archetype:generate \
  -DgroupId=de.bitconex.adlatus \
  -DartifactId=ServiceName \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackage=de.bitconex.adlatus \
  -DarchetypeGroupId=de.bitconex.adlatus \
  -DarchetypeArtifactId=adlatus-archetype\
  -DarchetypeVersion=1.0.0 \
  -DinteractiveMode=false
```

archetypeVersion may change from 1.0.0


