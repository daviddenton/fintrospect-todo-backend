package io.github.daviddenton

import com.twitter.finagle.http.Request
import com.twitter.finagle.http.Status.Ok
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await
import io.fintrospect.formats.PlainText.ResponseBuilder._

object Main {
  def main (args: Array[String] ) {
    val port = if(args.length == 0) "5000" else args(0)

    val server = Http.serve(s":$port", Service.mk {
      r: Request => Ok("hello")
    })

    Await.ready(server)
  }
}
