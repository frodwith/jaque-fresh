#!/usr/bin/env bash
mvn package
java -cp launcher/target/nock-launcher.jar -Dtruffle.class.path.append=language/target/nock-language.jar net.frodwith.jaque.launcher.NockMain

