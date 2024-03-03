#!/bin/bash

export WORKDIR=$(dirname $0)
export NAMESPACE=$(cat $WORKDIR/services/services.json | jq -r '.NAMESPACE')
export DOMAIN=$(cat $WORKDIR/services/services.json | jq -r '.DOMAIN')
if [ "$DOMAIN" == "SERVICE-APP" ]; then
    DOMAIN=SERVICE-APP.NAMESPACE
fi

remove_base_content(){
    rm -r $WORKDIR/deploy/base/*
}

remove_overlays_content(){
    rm -r $WORKDIR/deploy/overlays/*
}

copy_base_configs(){
    mkdir -p $WORKDIR/deploy/base/configs/ && cp -a "$WORKDIR/skeleton/base/configs/." "$WORKDIR/deploy/base/configs/"
}

copy_environment_configs(){
    local ENV=$1
    mkdir -p $WORKDIR/deploy/overlays/$ENV/configs/ 
    cp -a "$WORKDIR/skeleton/base/configs/." "$WORKDIR/deploy/overlays/$ENV/configs/"
    cp -a "$WORKDIR/skeleton/overlays/$ENV/configs/." "$WORKDIR/deploy/overlays/$ENV/configs/"
}

encrypt_and_copy_secrets(){
    local LEVEL=$1
    mkdir -p $WORKDIR/deploy/$LEVEL/secrets-encrypted
    create_or_replace_kustomization $WORKDIR/deploy/$LEVEL/secrets-encrypted/kustomization.yaml
    for secret in $(find "$WORKDIR/skeleton/$LEVEL/secrets/secrets-encoded" -type f -printf '%P\n'); do
        sops --encrypt --input-type=yaml --azure-kv https://sops-c04c896bd2964a4d.vault.azure.net/keys/sops-key/105d3b48b28c4e22a59626cee1619cc1 $WORKDIR/skeleton/$LEVEL/secrets/secrets-encoded/$secret > $WORKDIR/skeleton/$LEVEL/secrets/secrets-encrypted/$secret
    done
    for secret in $(find "$WORKDIR/skeleton/$LEVEL/secrets/secrets-encrypted" -type f -printf '%P\n'); do
        echo "  - ./$secret" >> $WORKDIR/deploy/$LEVEL/secrets-encrypted/kustomization.yaml
    done
    cp -a "$WORKDIR/skeleton/$LEVEL/secrets/secrets-encrypted/." "$WORKDIR/deploy/$LEVEL/secrets-encrypted"
}

create_or_replace_kustomization() {
    local path=$1
    echo "apiVersion: kustomize.config.k8s.io/v1beta1" > $path
    echo "kind: Kustomization" >> $path
    echo "resources:" >> $path
}

add_config_to_generator(){
    local config=$1
    local ENV=$2
    local config_path=$3
    local kustomization_path="$WORKDIR/deploy/overlays/$ENV/kustomization.yaml"
    local config_name=$(echo "$config" | cut -d '.' -f 1)
    echo "  - name: $config_name" >> $kustomization_path
    echo "    namespace: $NAMESPACE-$ENV" >> $kustomization_path
    echo "    envs:" >> $kustomization_path
    echo "    -  $config_path/$config" >> $kustomization_path
    echo "    options:" >> $kustomization_path
    echo "      disableNameSuffixHash: true" >> $kustomization_path
}

reference_dir(){
    local DIR=$1
    local LEVEL=$2
    echo "  - $DIR" >> $WORKDIR/deploy/$LEVEL/kustomization.yaml
}

copy_patches(){
    local SERVICE_APP=$1
    local LEVEL=$2
    local ENV=$3
    mkdir -p $WORKDIR/deploy/$LEVEL/modules/$SERVICE_APP/patches
    for file in $(find "$WORKDIR/skeleton/$LEVEL/patches/$SERVICE_APP" -type f -printf '%P\n'); do
        sed -e "s/DOMAIN/$DOMAIN/g" -e "s/SERVICE-APP/$SERVICE_APP/g" -e "s/NAMESPACE/$NAMESPACE/g" -e "s|ENV|${ENV}|g" -e "s|IMAGE|${IMAGE}|g" -e "s/TAG/$TAG/g" $WORKDIR/skeleton/$LEVEL/patches/$SERVICE_APP/$file > $WORKDIR/deploy/$LEVEL/modules/$SERVICE_APP/patches/$file
    done
}

reference_patches(){
    local SERVICE_APP=$1
    local LEVEL=$2
    echo -e "\npatches:" >> $WORKDIR/deploy/$LEVEL/modules/$SERVICE_APP/kustomization.yaml
    for patch in $(find "$WORKDIR/deploy/$LEVEL/modules/$SERVICE_APP/patches" -type f -printf '%P\n'); do
        echo "  -  path: ./patches/$patch" >> $WORKDIR/deploy/$LEVEL/modules/$SERVICE_APP/kustomization.yaml
    done
}

inject_base_env_variables(){
    local SERVICE_APP=$1
    mkdir -p $WORKDIR/deploy/base/modules/$SERVICE_APP/
    for file in $(find "$WORKDIR/skeleton/base/SERVICE-APP" -type f -printf '%P\n'); do
        sed -e "s/DOMAIN/$DOMAIN/g" -e "s/SERVICE-APP/$SERVICE_APP/g" -e "s/NAMESPACE/$NAMESPACE/g" -e "s|IMAGE|${IMAGE}|g" -e "s/TAG/$TAG/g" $WORKDIR/skeleton/base/SERVICE-APP/$file > $WORKDIR/deploy/base/modules/$SERVICE_APP/$file
    done
}

inject_overlays_env_variables(){
    local SERVICE_APP=$1
    local ENV=$2
    mkdir -p $WORKDIR/deploy/overlays/$ENV/modules/$SERVICE_APP/
    for file in $(find "$WORKDIR/skeleton/overlays/$ENV/SERVICE-APP" -type f -printf '%P\n'); do
        sed -e "s/DOMAIN/$DOMAIN/g" -e "s/SERVICE-APP/$SERVICE_APP/g" -e "s/NAMESPACE/$NAMESPACE/g" -e "s|ENV|${ENV}|g" -e "s|IMAGE|${IMAGE}|g" -e "s/TAG/$TAG/g" $WORKDIR/skeleton/overlays/$ENV/SERVICE-APP/$file > $WORKDIR/deploy/overlays/$ENV/modules/$SERVICE_APP/$file
    done
}

reference_configs(){
    local ENV=$1
    echo -e "\nconfigMapGenerator:" >> $WORKDIR/deploy/overlays/$ENV/kustomization.yaml
    for config in $(find "$(pwd)/deploy/overlays/$ENV/configs" -type f -printf '%P\n'); do
        add_config_to_generator $config $ENV ./configs
    done
}

reference_base_secrets(){
    local ENV=$1
    echo "  - ../../../base/secrets-encrypted" >> $WORKDIR/deploy/overlays/$ENV/secrets-encrypted/kustomization.yaml
}

patch_base_secrets_namespace(){
    local ENV=$1
    local kustomization_path=$WORKDIR/deploy/overlays/$ENV/secrets-encrypted/kustomization.yaml
    echo "patches:" >> $kustomization_path
    for secret in $(find "$WORKDIR/skeleton/base/secrets/secrets-encrypted" -type f -printf '%P\n'); do
        local secret_name=$(echo "$secret" | cut -d '.' -f 1)
        echo "- patch: |-" >> $kustomization_path
        echo "    - op: add" >> $kustomization_path
        echo "      path: /metadata/namespace" >> $kustomization_path
        echo "      value: $NAMESPACE-$ENV" >> $kustomization_path
        echo "  target:" >> $kustomization_path
        echo "    group:" >> $kustomization_path
        echo "    kind: Secret" >> $kustomization_path
        echo "    name: $secret_name" >> $kustomization_path
        echo "    version: v1" >> $kustomization_path
    done
    
}

# - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

remove_base_content
remove_overlays_content
copy_base_configs
encrypt_and_copy_secrets base

create_or_replace_kustomization $WORKDIR/deploy/overlays/kustomization.yaml

# Loop all services in services.json
for s in $(cat $WORKDIR/services/services.json | jq -c '.services/services[]'); do
    SERVICE_APP=$(echo $s | jq -r '."SERVICE-APP"')
    IMAGE=$(echo $s | jq -r '.IMAGE')
    TAG=$(echo $s | jq -r '.TAG')
    
    inject_base_env_variables $SERVICE_APP base
    copy_patches $SERVICE_APP base
    reference_patches $SERVICE_APP base

    # Loop over all environments (directories under skeleton/overlays)
    for environment in $(find "$(pwd)/skeleton/overlays"  -maxdepth 1 -type d -printf '%P\n'); do
        inject_overlays_env_variables $SERVICE_APP $environment
        copy_patches $SERVICE_APP overlays/$environment $environment
        reference_patches $SERVICE_APP overlays/$environment
    done
done

for environment in $(find "$(pwd)/skeleton/overlays"  -maxdepth 1 -type d -printf '%P\n'); do
    reference_dir ./$environment overlays
    
    create_or_replace_kustomization $WORKDIR/deploy/overlays/$environment/kustomization.yaml
    reference_dir ./secrets-encrypted overlays/$environment
    for s in $(cat $WORKDIR/services/services.json | jq -c '.services/services[]'); do
        SERVICE_APP=$(echo $s | jq -r '."SERVICE-APP"')
        reference_dir ./modules/$SERVICE_APP overlays/$environment
    done
    

    copy_environment_configs $environment
    encrypt_and_copy_secrets overlays/$environment

    reference_configs $environment

    reference_base_secrets $environment
    patch_base_secrets_namespace $environment
done
