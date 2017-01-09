import sbt.Keys.version

val common = Seq(
  version := "1.0",
  scalaVersion := "2.12.1"
)

lazy val core = project.settings(common ++ Seq(
  name := "tweeeter",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % "10.0.1",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.1",
    "org.scalatest" %% "scalatest" % "3.0.1" % Test,
    "com.vdurmont" % "emoji-java" % "3.1.3",
    "de.heikoseeberger" %% "akka-sse" % "2.0.0",
    "org.typelevel" %% "cats" % "0.9.0"
  )))

