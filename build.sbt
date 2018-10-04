name := """Assignment2"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.4"

crossScalaVersions := Seq("2.11.12", "2.12.4")

// https://github.com/sbt/junit-interface
testOptions += Tests.Argument(TestFrameworks.JUnit, "-a", "-v")
libraryDependencies += ws
libraryDependencies += guice

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.196"

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

libraryDependencies ++= Seq(
  "org.twitter4j" % "twitter4j-core" % "4.0.2",
  "org.twitter4j" % "twitter4j-async" % "4.0.2",
  "org.twitter4j" % "twitter4j-stream" % "4.0.2",
  "org.twitter4j" % "twitter4j-media-support" % "4.0.2"
)

libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.11"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit-typed" % "2.5.11" % Test

libraryDependencies += "org.mockito" % "mockito-core" % "2.10.0" % "test"


