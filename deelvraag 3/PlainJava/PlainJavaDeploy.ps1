cd $PSScriptRoot

mvn clean package

sam deploy
sam deploy -t template.snapstart.yaml --config-env snapstart

# If the JAVA_HOME variable is set to an GraalVM distribution mvn clean package -Pnative -Dskiptests should work as well

docker build -t build-graalvm-jdk21 .
docker run -it -v /c/Users/Dylla/OneDrive/Bureaublad/Afstuderen/code/java-frameworks-voor-lambdas/PlainJava:/c/Users/Dylla/OneDrive/Bureaublad/Afstuderen/code/java-frameworks-voor-lambdas/PlainJava -w /c/Users/Dylla/OneDrive/Bureaublad/Afstuderen/code/java-frameworks-voor-lambdas/PlainJava -v ~/.m2:/root/.m2 build-graalvm-jdk21 mvn clean -Pnative package -DskipTests

sam deploy -t template.native.yaml --config-env native

#docker build -t build-graalvm-jdk21-arm64 -f arm.Dockerfile .
#docker run -it -v /c/Users/Dylla/OneDrive/Bureaublad/Afstuderen/code/java-frameworks-voor-lambdas/PlainJava:/c/Users/Dylla/OneDrive/Bureaublad/Afstuderen/code/java-frameworks-voor-lambdas/PlainJava -w /c/Users/Dylla/OneDrive/Bureaublad/Afstuderen/code/java-frameworks-voor-lambdas/PlainJava -v ~/.m2:/root/.m2 build-graalvm-jdk21-arm64 mvn clean -Pnative package -DskipTests
#
#sam deploy -t template.native.arm.yaml --config-env native-arm