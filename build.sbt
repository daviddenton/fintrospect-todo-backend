name := "fintrospect-todo-backend"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "JCenter" at "https://jcenter.bintray.com"

libraryDependencies ++= Seq(
  "io.github.daviddenton" %% "fintrospect" % "12.5.0",
  "com.twitter" %% "finagle-http" % "6.33.0",
  "org.json4s" %% "json4s-native" % "3.3.0",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

enablePlugins(JavaAppPackaging)
