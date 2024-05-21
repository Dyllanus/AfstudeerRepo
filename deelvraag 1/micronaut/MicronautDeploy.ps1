$configEnvNormalARM = "normalARM"
$configEnvSnapstart = "snapstart"
$configEnvNative = "nativeX86"
#$configEnvNativeARM = "nativeARM"

cd $PSScriptRoot

# deploy normal on x86
mvn clean package
sam deploy

# deploy snapstart on x86
sam deploy -t template.snapstart.yaml --config-env $configEnvSnapstart

#deploy native on x86
mvn clean package --% -Dpackaging=docker-native -Dmicronaut.runtime=lambda -Pgraalvm
sam deploy -t template.native.yaml --config-env $configEnvNative

# deploy native on ARM not doable on a amd64 CPU
#mvn clean package --% -Dpackaging=docker-native -Dmicronaut.runtime=lambda -Pgraalvm -Dos.arch=arm64
#sam deploy -t template.native.arm.yaml --config-env $configEnvNativeARM