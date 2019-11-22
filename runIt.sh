#!/bin/bash

mvn package -DskipTests && rm -Rf fakefed/ && ./urbit -F fed -B ~/src/urbit/bin/solid.pill -c fakefed
