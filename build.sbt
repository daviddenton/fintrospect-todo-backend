name := "fintrospect-todo-backend"

version := "1.3"

scalaVersion := "2.12.1"

resolvers += "JCenter" at "https://jcenter.bintray.com"

libraryDependencies ++= Seq(
  "io.fintrospect" %% "fintrospect-core" % "14.10.0",
  "io.fintrospect" %% "fintrospect-circe" % "14.10.0",
  "io.circe" %% "circe-optics" % "0.7.0",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

enablePlugins(JavaAppPackaging)