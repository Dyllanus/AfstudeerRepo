cd C:\Users\Dylla\OneDrive\Bureaublad\afstuderen\code\optiesvooropstarttijd\GraalVM\HelloWorldFunction

#Build native image for x86
#docker build -t build-graalvm-jdk21-amd64 .
#docker run -it -v /c/Users/Dylla/OneDrive/Bureaublad/Afstuderen/code/optiesvooropstarttijd/GraalVM/HelloWorldFunction:/c/Users/Dylla/OneDrive/Bureaublad/Afstuderen/code/optiesvooropstarttijd/GraalVM/HelloWorldFunction -w /c/Users/Dylla/OneDrive/Bureaublad/Afstuderen/code/optiesvooropstarttijd/GraalVM/HelloWorldFunction -v ~/.m2:/root/.m2 build-graalvm-jdk21-amd64 mvn clean -Pnative package -DskipTests

# build native image for arm 64 on x86
docker build -t build-graalvm-jdk21-arm64 -f arm.Dockerfile .
docker run -it -v /c/Users/Dylla/OneDrive/Bureaublad/Afstuderen/code/optiesvooropstarttijd/GraalVM/HelloWorldFunction:/c/Users/Dylla/OneDrive/Bureaublad/Afstuderen/code/optiesvooropstarttijd/GraalVM/HelloWorldFunction -w /c/Users/Dylla/OneDrive/Bureaublad/Afstuderen/code/optiesvooropstarttijd/GraalVM/HelloWorldFunction -v ~/.m2:/root/.m2 build-graalvm-jdk21-arm64 mvn clean -Pnative package -DskipTests
sam deploy

#sam deploy