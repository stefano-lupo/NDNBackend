#! /bin/bash
out_dir="./NDNCore/target/generated-sources"
mkdir -p $out_dir
protoc NDNCore/src/main/resources/NDNGame.proto --java_out $out_dir
