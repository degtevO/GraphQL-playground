name := "graphQL"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.0-RC2",
  "com.typesafe.akka" %% "akka-stream" % "2.5.9",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.0-RC2",
  "org.sangria-graphql" %% "sangria" % "1.4.0",
  "org.sangria-graphql" %% "sangria-spray-json" % "1.0.0"
)
