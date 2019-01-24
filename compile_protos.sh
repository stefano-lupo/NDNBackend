#! /bin/bash
out_dir="./NDNCore/target/generated-sources"
mkdir -p $out_dir
protoc NDNCore/src/main/java/com/stefanolupo/ndngame/protos/NDNGame.proto --java_out $out_dir
