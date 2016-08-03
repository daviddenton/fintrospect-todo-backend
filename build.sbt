name := "fintrospect-todo-backend"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "JCenter" at "https://jcenter.bintray.com"

libraryDependencies ++= Seq(
  "io.fintrospect" %% "fintrospect-core" % "13.5.2",
  "io.fintrospect" %% "fintrospect-json4s" % "13.5.2",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

enablePlugins(JavaAppPackaging)
