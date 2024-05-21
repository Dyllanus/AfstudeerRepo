FROM --platform=linux/amd64 amazonlinux:2

# Add the Amazon Corretto repository
RUN rpm --import https://yum.corretto.aws/corretto.key
RUN curl -L -o /etc/yum.repos.d/corretto.repo https://yum.corretto.aws/corretto.repo

# Update the packages and install Amazon Corretto 19, Maven and Zip
RUN yum -y update
RUN yum install -y java-21-amazon-corretto-devel maven zip

# Set Java 19 as the default
RUN update-alternatives --set java "/usr/lib/jvm/java-21-amazon-corretto/bin/java"
RUN update-alternatives --set javac "/usr/lib/jvm/java-21-amazon-corretto/bin/javac"

# Copy the software folder to the image and build the function
COPY . .
WORKDIR .
RUN mvn clean package

# Find JDK module dependencies dynamically from the uber jar
RUN jdeps -q \
    --ignore-missing-deps \
    --multi-release 21 \
    --print-module-deps \
    target/lambda-app.jar > jre-deps.info

# Create a slim Java 19 JRE which only contains the required modules to run the function
RUN jlink --verbose \
    --compress 2 \
    --strip-java-debug-attributes \
    --no-header-files \
    --no-man-pages \
    --output /jre-custom \
    --add-modules $(cat jre-deps.info)

# Use Javas Application Class Data Sharing feature
# It creates the file /jre18-custom/lib/server/classes.jsa
RUN /jre-custom/bin/java -Xshare:dump

# Package everything together into a custom runtime archive
WORKDIR /
COPY bootstrap bootstrap
RUN chmod 755 bootstrap
RUN cp /target/lambda-app.jar lambda-app.jar
RUN zip -r lambda-app.zip \
    lambda-app.jar

RUN zip -r layer.zip \
     bootstrap \
    /jre-custom
