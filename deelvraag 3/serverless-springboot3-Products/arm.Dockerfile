FROM --platform=linux/arm64 public.ecr.aws/amazonlinux/amazonlinux:2023

#Install depencencies
RUN yum -y update \
    && yum install -y unzip zip tar gzip bzip2-devel ed gcc gcc-c++ gcc-gfortran \
    less libcurl-devel openssl openssl-devel readline-devel xz-devel \
    zlib-devel glibc-static zlib-static \
    && rm -rf /var/cache/yum

# Graal VM
ENV GRAAL_FOLDERNAME graalvm-jdk-21.0.3+7.1
ENV GRAAL_FILENAME graalvm-jdk-21_linux-aarch64_bin.tar.gz
RUN curl https://download.oracle.com/graalvm/21/latest/${GRAAL_FILENAME}| tar -xvz
RUN mv $GRAAL_FOLDERNAME /usr/lib/graalvm
RUN rm -rf $GRAAL_FOLDERNAME

# Maven
ENV MVN_VERSION 3.9.6
ENV MVN_FOLDERNAME apache-maven-${MVN_VERSION}
ENV MVN_FILENAME apache-maven-${MVN_VERSION}-bin.tar.gz
RUN curl -4 -L https://archive.apache.org/dist/maven/maven-3/${MVN_VERSION}/binaries/${MVN_FILENAME} | tar -xvz
RUN mv $MVN_FOLDERNAME /usr/lib/maven

#Native Image dependencies
RUN ln -s /usr/lib/graalvm/bin/native-image /usr/bin/native-image
RUN ln -s /usr/lib/maven/bin/mvn /usr/bin/mvn

ENV JAVA_HOME /usr/lib/graalvm

VOLUME /project
WORKDIR /project

WORKDIR /SpringCloudFunctions

