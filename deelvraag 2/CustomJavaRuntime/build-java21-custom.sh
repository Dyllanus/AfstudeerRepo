#!/bin/sh

# Remove a previously created application
file1="lambda-app.zip"
if [ -f "$file1" ] ; then
    rm "$file1"
fi

# Remove a previously created custom layer
file2="layer.zip"
if [ -f "$file2" ] ; then
    rm "$file2"
fi

# Build the custom Java runtime from the Dockerfile
docker build -f Dockerfile --progress=plain -t lambda-custom-runtime-minimal-jre-21-x86 .

# Extract the runtime.zip from the Docker environment and store it locally
docker run --rm --entrypoint cat lambda-custom-runtime-minimal-jre-21-x86 lambda-app.zip > lambda-app.zip
docker run --rm --entrypoint cat lambda-custom-runtime-minimal-jre-21-x86 layer.zip > layer.zip
