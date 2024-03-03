# Adlatus TMF Generators

## Description

Adlatus TMF Generators is a Java application designed for generating TMF specifications using OpenAPI specifications. This repository is dedicated to the generation of files that are later used to build a `.jar` file. The resulting `.jar` file can be found in the following repository: [Adlatus TMF Artifacts](https://bitbucket.org/bitconex/adlatus-tmf-artifacts/src/master/).

## Usage

To use this application, follow these steps:

1. Clone this repository.
2. (optional) Configure `OpenAPI generator plugin` inside `pom.xml`, like:
```
<!--Resource Catalog Management-->
<!--v5.0.0-->
<execution>
	<id>rcm-v5.0.0-model</id>
	<goals>
		<goal>generate</goal>
	</goals>
	<configuration>
		<inputSpec>src/main/resources/specs/rcm/rcm-v5.0.0.yaml</inputSpec>
		<output>${project.build.directory}/generated-sources/openapi/rcm/v5.0.0/model</output>
		<generatorName>java</generatorName>
		<configurationFile>src/main/resources/configs/rcm/v5.0.0/model-config.yaml</configurationFile>
		<templateDirectory>src/main/resources/templates/custom/model</templateDirectory>
	</configuration>
</execution>
```
Make sure to provide valid input, output, template and config paths. Configuration properties for all every generator can be found on by clicking on generator name on [OpenAPI Generators](https://openapi-generator.tech/docs/generators)


3. Run `mvn clean install`. If configured properly, all generated TMF Artifacts will be generated inside target/generated-sources/openapi (if not configured otherwise)
5. Use these generated files to build your `.jar` file using the [Adlatus TMF Artifacts](https://bitbucket.org/bitconex/adlatus-tmf-artifacts/src/master/) repository.

