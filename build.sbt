name := "fintrospect-todo-backend"

version := "1.4"

scalaVersion := "2.12.1"

resolvers += "JCenter" at "https://jcenter.bintray.com"

libraryDependencies ++= Seq(
  "io.fintrospect" %% "fintrospect-core" % "14.12.1",
  "io.fintrospect" %% "fintrospect-circe" % "14.12.1",
  "io.circe" %% "circe-optics" % "0.7.0" % "test",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

enablePlugins(JavaAppPackaging)