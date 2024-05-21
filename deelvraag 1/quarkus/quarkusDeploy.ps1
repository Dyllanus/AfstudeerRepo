$configEnvNormal = "normal"
$configEnvSnapstart = "snapstart"
$configEnvNative = "nativeX86"
$configEnvNativeARM = "nativeARM"

cd $PSScriptRoot

# deploy normal on x86
mvn clean package
sam deploy --config-env $configEnvNormal


# deploy snapstart on x86
sam deploy -t template.snapstart.yaml --config-env $configEnvSnapstart

# because of the settings in the pom.xml Quarkus uses a docker container to build the native executable although that can be changed

#build and deploy native on x86
mvn clean package -Pnative
sam deploy -t template.native.yaml --config-env $configEnvNative

#build and deploy native on ARM
mvn clean package -Pnative-arm
sam deploy -t template.native.arm.yaml --config-env $configEnvNativeARM