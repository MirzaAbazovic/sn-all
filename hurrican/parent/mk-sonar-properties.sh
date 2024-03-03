#!/bin/bash

set -e

function join { local IFS="$1"; shift; echo "$*"; }

# set modules
MODULES=()
MODULES_BLACKLIST=""

for POM in `find -name pom.xml`; do
	DIR=$(echo $POM | sed -e 's/^\.\///' -e 's/pom\.xml//' -e 's/\/$//')
	MODULE=$(echo $DIR | sed -e 's/\//_/g' -e 's/\s//')
	SOURCE_DIRS=()
	TEST_DIRS=()

	if [ -z "$MODULE" ]; then
		continue
	fi
	if echo "$MODULES_BLACKLIST" | grep -q "$MODULE"; then
		continue
	fi

	if [ -d "$DIR/src/main/java" ]; then
		SOURCE_DIRS+=("src/main/java")
	fi
	if [ -d "$DIR/src/java" ]; then
		SOURCE_DIRS+=("src/java")
	fi
	if [ ${#SOURCE_DIRS[@]} -eq 0 ]; then
		continue
	fi

	MODULES+=("$MODULE")
    echo "$MODULE.sonar.projectBaseDir=$DIR"
	echo "$MODULE.sonar.sources=$(join , "${SOURCE_DIRS[@]}")"

	if [ -d "$DIR/src/test/java" ]; then
		TEST_DIRS+=("src/test/java")
	fi
	if [ -d "$DIR/test/java" ]; then
		TEST_DIRS+=("test/java")
	fi
	if [ ${#TEST_DIRS[@]} -ne 0 ]; then
		echo "$MODULE.sonar.tests=$(join , "${TEST_DIRS[@]}")"
	fi

	echo
done

# Extract the version from the root POM.
VERSION=$(grep '<version>.*</version>' pom.xml | head -n1 | sed -e 's/\s*<version>//g' -e 's/<\/version>\s*//')
DATE=$(date +%Y%m%d)

# Find all JARs.
LIBRARIES="$(find "`pwd`" -name "*.jar" | tr "\\n" "," | sed 's/,$//')"

echo "sonar.exclusions=**/*.properties"
echo
echo "sonar.modules=$(join , "${MODULES[@]}")"
echo
echo "sonar.analysis.mode=publish"
echo "sonar.projectKey=de.mnet.hurrican:hurrican"
echo "sonar.projectName=Hurrican"
echo "sonar.projectVersion=$VERSION-$DATE"
echo
echo "sonar.java.source=1.8"
echo "sonar.java.target=1.8"
echo
echo "sonar.java.binaries=target/classes"
echo "sonar.binaries=target/test-classes"
echo "sonar.junit.reportsPath=target/surefire-reports"
echo "sonar.cobertura.reportPath=target/site/cobertura/coverage.xml"
echo "sonar.sourceEncoding=UTF-8"
echo
echo "sonar.libraries=$LIBRARIES"
