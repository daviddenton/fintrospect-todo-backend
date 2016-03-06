package io.github.daviddenton

import com.twitter.finagle.http.Request
import com.twitter.finagle.http.Status.Ok
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await
import io.fintrospect.formats.PlainText.ResponseBuilder._

object Main extends App {
  val host = System.getProperty("http.host", "0.0.0.0")
  val port = System.getProperty("http.port", "9000")

  val server = Http.serve(s"$host:$port", Service.mk {
    r: Request => Ok("hello")
  })

  Await.ready(server)
}
