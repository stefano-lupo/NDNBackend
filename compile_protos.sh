#! /bin/bash
in_file=./NdnCore/src/main/java/com/stefanolupo/ndngame/protos/NdnGame.proto
out_dir=./NdnCore/build/generated/sources/protos
mkdir -p $out_dir
protoc $in_file --java_out $out_dir
