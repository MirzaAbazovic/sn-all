import groovy.json.JsonSlurper
import groovy.transform.Field

// Define constants
@Field
String WORKDIR = new File(".").canonicalPath

@Field
def servicesJson = new JsonSlurper().parse(new File("$WORKDIR/services/services.json"))

@Field
String NAMESPACE = servicesJson.NAMESPACE

@Field
String DOMAIN = servicesJson.DOMAIN
if (DOMAIN == "SERVICE-APP") {
    DOMAIN = "SERVICE-APP.$NAMESPACE"
}

// Function to remove content in base directory
def removeBaseContent() {
    def baseDir = new File("$WORKDIR/deploy/base")
    baseDir.deleteDir()
    baseDir.mkdirs()
}

// Function to remove content in overlays directory
def removeOverlaysContent() {
    def overlaysDir = new File("$WORKDIR/deploy/overlays")
    overlaysDir.deleteDir()
    overlaysDir.mkdirs()
}

// Function to copy base configs
def copyBaseConfigs() {
    def deployBaseConfigDir = new File("$WORKDIR/deploy/base/configs")
    deployBaseConfigDir.mkdirs()

    // Copy base configs into deployBaseConfigDir
    def skeletonBaseConfigDir = new File("$WORKDIR/skeleton/base/configs")
    if (skeletonBaseConfigDir.exists()) {
        skeletonBaseConfigDir.eachFile { configFile ->
            def destFile = new File(deployBaseConfigDir, configFile.name)
            destFile.text = configFile.text
        }
    }
}

// Function to copy environment configs
def copyEnvironmentConfigs(env) {
    def deployOverlaysConfigDir = new File("$WORKDIR/deploy/overlays/$env/configs")
    deployOverlaysConfigDir.mkdirs()

    // Copy base configs into deployOverlaysConfigDir
    def skeletonBaseConfigDir = new File("$WORKDIR/skeleton/base/configs")
    if (skeletonBaseConfigDir.exists()) {
        skeletonBaseConfigDir.eachFile { configFile ->
            def destFile = new File(deployOverlaysConfigDir, configFile.name)
            destFile.text = configFile.text
        }
    }

    // Copy overlays configs into deployOverlaysConfigDir
    def skeletonOverlaysConfigDir = new File("$WORKDIR/skeleton/overlays/$env/configs")
    if (skeletonOverlaysConfigDir.exists()) {
        skeletonOverlaysConfigDir.eachFile { configFile ->
            def destFile = new File(deployOverlaysConfigDir, configFile.name)
            destFile.text = configFile.text
        }
    }
}

// Function to encrypt and copy secrets
def encryptAndCopySecrets(level) {
    def azureKV = "https://sops-c04c896bd2964a4d.vault.azure.net/keys/sops-key/105d3b48b28c4e22a59626cee1619cc1"
    def skeletonEncodedSecretsDir = new File("$WORKDIR/skeleton/$level/secrets/secrets-encoded")
    def skeletonEncryptedSecretsDir = new File("$WORKDIR/skeleton/$level/secrets/secrets-encrypted")
    def deployEncryptedSecretsDir = new File("$WORKDIR/deploy/$level/secrets-encrypted")
    deployEncryptedSecretsDir.mkdirs()

    createOrReplaceKustomization("$WORKDIR/deploy/$level/secrets-encrypted/kustomization.yaml")

    if (skeletonEncodedSecretsDir.exists()) {
        skeletonEncodedSecretsDir.eachFile { secret ->
            def secretName = secret.name
            def encryptedSecret = new File(skeletonEncryptedSecretsDir, secretName)
            encryptedSecret.text = secret.text.replaceAll(/(?m)^/, 'sops --encrypt --input-type=yaml --azure-kv ' + azureKV + ' ')
        }
    }

    if (skeletonEncryptedSecretsDir.exists()) {
        skeletonEncryptedSecretsDir.eachFile { secret ->
            def destFile = new File(deployEncryptedSecretsDir, secret.name)
            destFile.text = secret.text

            def secretName = secret.name
            def kustomization = new File("$WORKDIR/deploy/$level/secrets-encrypted/kustomization.yaml")
            kustomization.append("  - ./$secretName\n")
        }
    }
}

// Function to create or replace kustomization.yaml
def createOrReplaceKustomization(path) {
    def kustomization = new File(path)
    kustomization.append("apiVersion: kustomize.config.k8s.io/v1beta1\n")
    kustomization.append("kind: Kustomization\n")
    kustomization.append("resources:\n")
}

// Function to add config to generator
def addConfigToGenerator(String config, env, configPath) {
    def kustomizationPath = "$WORKDIR/deploy/overlays/$env/kustomization.yaml"
    def configName = config.split('\\.')[0]
    def file = new File(kustomizationPath)

    file.append("  - name: $configName\n")
    file.append("    namespace: $NAMESPACE-$env\n")
    file.append("    envs:\n")
    file.append("    -  $configPath/$config\n")
    file.append("    options:\n")
    file.append("       disableNameSuffixHash: true\n")
}

// Function to reference directory
def referenceDir(dir, level) {
    def file = new File("$WORKDIR/deploy/$level/kustomization.yaml")
    file.append("  - $dir\n")
}

// Function to copy patches
def copyPatches(String serviceApp, String level, String env, String image, String tag) {
    def patchesDir = new File("$WORKDIR/deploy/$level/modules/$serviceApp/patches")
    patchesDir.mkdirs()

    if(new File("$WORKDIR/skeleton/$level/patches/$serviceApp").exists()) {
        def skeletonPatchesDir = new File("$WORKDIR/skeleton/$level/patches/$serviceApp")
        skeletonPatchesDir.eachFile { patchFile ->
            def patchedFile = new File(patchesDir, patchFile.name)
            def patchedContent = patchFile.text.replaceAll("DOMAIN", DOMAIN)
                    .replaceAll("SERVICE-APP", serviceApp)
                    .replaceAll("NAMESPACE", NAMESPACE)
                    .replaceAll("ENV", env)
                    .replaceAll("IMAGE", image)
                    .replaceAll("TAG", tag)

            patchedFile.text = patchedContent
        }
    }
}

// Function to reference patches
def referencePatches(serviceApp, level) {
    def kustomizationPath = "$WORKDIR/deploy/$level/modules/$serviceApp/kustomization.yaml"
    def kustomization = new File(kustomizationPath)

    kustomization.append("\npatches:\n")

    def patchesDir = new File("$WORKDIR/deploy/$level/modules/$serviceApp/patches")
    patchesDir.eachFile { patchFile ->
        kustomization.append("  -  path: ./patches/$patchFile.name\n")
    }
}

// Function to inject base env variables
def injectBaseEnvVariables(String serviceApp, String image, String tag) {
    def baseModuleDir = new File("$WORKDIR/deploy/base/modules/$serviceApp")
    baseModuleDir.mkdirs()

    def skeletonBaseDir = new File("$WORKDIR/skeleton/base/SERVICE-APP")
    skeletonBaseDir.eachFile { patchFile ->
        def patchedFile = new File(baseModuleDir, patchFile.name)
        def content = patchFile.text.replaceAll(/DOMAIN/, DOMAIN)
                .replaceAll("SERVICE-APP", serviceApp)
                .replaceAll("NAMESPACE", NAMESPACE)
                .replaceAll("IMAGE", image)
                .replaceAll("TAG", tag)

        patchedFile.text = content
    }
}

// Function to inject overlays env variables
def injectOverlaysEnvVariables(String serviceApp, String env, String image, String tag) {
    def overlaysModuleDir = new File("$WORKDIR/deploy/overlays/$env/modules/$serviceApp")
    overlaysModuleDir.mkdirs()

    def skeletonOverlaysDir = new File("$WORKDIR/skeleton/overlays/$env/SERVICE-APP")
    skeletonOverlaysDir.eachFile { overlaysFile ->
        def outputFile = new File(overlaysModuleDir, overlaysFile.name)
        def content = overlaysFile.text.replaceAll(/DOMAIN/, DOMAIN)
                .replaceAll("SERVICE-APP", serviceApp)
                .replaceAll("NAMESPACE", NAMESPACE)
                .replaceAll("ENV", env)
                .replaceAll("IMAGE", image)
                .replaceAll("TAG", tag)

        outputFile.text = content
    }
}

// Function to reference configs
def referenceConfigs(env) {
    def kustomizationPath = "$WORKDIR/deploy/overlays/$env/kustomization.yaml"
    def kustomization = new File(kustomizationPath)

    kustomization.append("\nconfigMapGenerator:\n")

    def configsDir = new File("$WORKDIR/deploy/overlays/$env/configs")
    configsDir.eachFile { config ->
        addConfigToGenerator(config.name, env, "./configs")
    }
}

// Function to reference base secrets
def referenceBaseSecrets(env) {
    def kustomizationPath = "$WORKDIR/deploy/overlays/$env/secrets-encrypted/kustomization.yaml"
    def kustomization = new File(kustomizationPath)

    kustomization.append("  - ../../../base/secrets-encrypted\n")
}

// Function to patch base secrets namespace
def patchBaseSecretsNamespace(env) {
    def kustomizationPath = "$WORKDIR/deploy/overlays/$env/secrets-encrypted/kustomization.yaml"
    def kustomization = new File(kustomizationPath)

    kustomization.append("patches:\n")

    def skeletonSecretEncryptedDir = new File("$WORKDIR/skeleton/base/secrets/secrets-encrypted")
    if (skeletonSecretEncryptedDir.exists()) {
        skeletonSecretEncryptedDir.eachFile { secretFile ->
            def secretName = secretFile.name.split('\\.')[0]
            kustomization.append("- patch: |-\n")
            kustomization.append("    - op: add\n")
            kustomization.append("      path: /metadata/namespace\n")
            kustomization.append("      value: ${NAMESPACE}-${env}\n")
            kustomization.append("  target:\n")
            kustomization.append("    group:\n")
            kustomization.append("    kind: Secret\n")
            kustomization.append("    name: $secretName\n")
            kustomization.append("    version: v1\n")
        }
    }
}

// Main Script
removeBaseContent()
removeOverlaysContent()
copyBaseConfigs()
encryptAndCopySecrets("base")

createOrReplaceKustomization("$WORKDIR/deploy/overlays/kustomization.yaml")

// Loop over all services in services.json
servicesJson.services.each { s ->
    String SERVICE_APP = s."SERVICE-APP"
    String IMAGE = s.IMAGE
    String TAG = s.TAG

    injectBaseEnvVariables(SERVICE_APP, IMAGE, TAG)
    copyPatches(SERVICE_APP, "base", "", IMAGE, TAG)
    referencePatches(SERVICE_APP, "base")

    // Loop over all environments
    new File("$WORKDIR/skeleton/overlays").eachDir { environment ->
        injectOverlaysEnvVariables(SERVICE_APP, environment.name, IMAGE, TAG)
        copyPatches(SERVICE_APP, "overlays/${environment.name}", environment.name, IMAGE, TAG)
        referencePatches(SERVICE_APP, "overlays/${environment.name}")
    }
}

new File("$WORKDIR/skeleton/overlays").eachDir { environment ->
    referenceDir("./${environment.name}", "overlays")

    createOrReplaceKustomization("$WORKDIR/deploy/overlays/${environment.name}/kustomization.yaml")
    referenceDir("./secrets-encrypted", "overlays/${environment.name}")

    servicesJson.services.each { s ->
        def SERVICE_APP = s."SERVICE-APP"
        referenceDir("./modules/$SERVICE_APP", "overlays/${environment.name}")
    }

    copyEnvironmentConfigs(environment.name)
    encryptAndCopySecrets("overlays/${environment.name}")

    referenceConfigs(environment.name)
    referenceBaseSecrets(environment.name)
    patchBaseSecretsNamespace(environment.name)
}
