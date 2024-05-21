$configEnvSnapstart = "snapstart"

cd $PSScriptRoot

# deploy normal on x86
mvn clean package

sam deploy
# deploy snapstart on x86
sam deploy -t template.snapstart.yaml --config-env $configEnvSnapstart

docker build -t build-graalvm-jdk21-amd64 .
docker run -it -v /c/Users/Dylla/OneDrive/Bureaublad/Afstuderen/code/java-frameworks-voor-lambdas/dagger:/c/Users/Dylla/OneDrive/Bureaublad/Afstuderen/code/java-frameworks-voor-lambdas/dagger -w /c/Users/Dylla/OneDrive/Bureaublad/Afstuderen/code/java-frameworks-voor-lambdas/dagger -v ~/.m2:/root/.m2 build-graalvm-jdk21-amd64 mvn clean -Pnative package -DskipTests

sam deploy -t template.native.yaml --config-env native

#docker build -t build-graalvm-jdk21-arm64 -f arm.Dockerfile .
#docker run -it -v /c/Users/Dylla/OneDrive/Bureaublad/Afstuderen/code/java-frameworks-voor-lambdas/dagger:/c/Users/Dylla/OneDrive/Bureaublad/Afstuderen/code/java-frameworks-voor-lambdas/dagger -w /c/Users/Dylla/OneDrive/Bureaublad/Afstuderen/code/java-frameworks-voor-lambdas/dagger -v ~/.m2:/root/.m2 build-graalvm-jdk21-arm64 mvn clean -Pnative package -DskipTests
#
#sam deploy -t template.native.arm.yaml --config-env native-arm