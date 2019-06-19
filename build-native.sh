#!/usr/bin/env bash
mvn package
native-image --tool:truffle -cp language/target/nock-language.jar:launcher/target/nock-launcher.jar net.frodwith.jaque.launcher.NockMain run-nock

