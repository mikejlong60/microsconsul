import sbt._
import sbt.Keys._

object Build extends Build {

  val appName = "microconsul"
  val appVersion = "0.0.1-SNAPSHOT"


  lazy val broadcastService = Project(appName, file("."))
    .settings(
      scalaVersion := "2.11.7",
      version := appVersion,
      libraryDependencies ++= Dependencies.dependencies
    )
}

object Dependencies {

val dependencies = Seq(

  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.typesafe.scala-logging" % "scala-logging-slf4j_2.11" % "2.1.2",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.3.11",
  "com.orbitz.consul" % "consul-client" % "0.9.6",
  "org.apache.cxf" % "cxf-rt-rs-client" % "3.1.1",
  "org.apache.cxf" % "cxf-rt-transports-http" % "3.1.1"

  )

}