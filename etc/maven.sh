#!/bin/bash

DIR=$(cd "$(dirname "$0")"; pwd)

echo "$@"

mvn -pl '!docs,!tests,!examples,!modules/jooby-apt' "$@"
