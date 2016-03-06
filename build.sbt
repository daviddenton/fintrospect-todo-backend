name := "fintrospect-todo-backend"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "JCenter" at "https://jcenter.bintray.com"

libraryDependencies ++= Seq(
  "io.github.daviddenton" %% "fintrospect" % "12.4.0",
  "com.twitter" %% "finagle-http" % "6.33.0",
  "io.circe" %% "circe-core" % "0.3.0",
  "io.circe" %% "circe-parser" % "0.3.0",
  "io.circe" %% "circe-generic" % "0.3.0",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

enablePlugins(JavaAppPackaging)
