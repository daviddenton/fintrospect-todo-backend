name := "fintrospect-todo-backend"

version := "1.1"

scalaVersion := "2.11.8"

resolvers += "JCenter" at "https://jcenter.bintray.com"

libraryDependencies ++= Seq(
  "io.fintrospect" %% "fintrospect-core" % "14.1.0",
  "io.fintrospect" %% "fintrospect-circe" % "14.1.0",
  "io.circe" %% "circe-optics" % "0.6.1",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

enablePlugins(JavaAppPackaging)