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

  "com.typesafe.akka" % "akka-actor_2.11" % "2.3.11",
  "com.orbitz.consul" % "consul-client" % "0.9.6"

  )

}