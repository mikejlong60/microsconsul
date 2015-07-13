#!/bin/sh
# attempts to execute ~/.sbtrc then <project>/.sbtrc
java \
  -XX:+CMSClassUnloadingEnabled \
  -jar `dirname $0`/sbt-launch.jar \
  "$@"
