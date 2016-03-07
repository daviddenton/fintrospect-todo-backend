package io.fintrospect.todo

import com.twitter.finagle.Http
import com.twitter.finagle.http.filter.Cors._
import com.twitter.util.Await

object Main {
  def main(args: Array[String]) {
    val port = if (args.length == 0) "5000" else args(0)
    val module = new TodoModule(new TodoDb("https://fintrospect-todo-backend.herokuapp.com/todos")).module

    val server = Http.serve(s":$port", new HttpFilter(UnsafePermissivePolicy).andThen(module.toService))

    Await.ready(server)
  }
}
