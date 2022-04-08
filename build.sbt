name := "sesnosrs-statistics"

version := "0.1"

scalaVersion := "2.13.8"
val AkkaVersion = "2.6.19"
libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.4.1",
//Akka
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
"com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,
"org.scalatest" %% "scalatest" % "3.2.11" % "test",
  "org.scalatest" %% "scalatest-funspec" % "3.2.11" % "test"

)

