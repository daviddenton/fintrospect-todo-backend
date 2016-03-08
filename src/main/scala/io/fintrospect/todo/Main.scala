package io.fintrospect.todo

import com.twitter.finagle.Http
import com.twitter.util.Await

object Main {
  def main(args: Array[String]) {
    val port = if (args.length == 0) "5000" else args(0)
    Await.ready(Http.serve(s":$port", new TodoApp(new TodoDb("https://fintrospect-todo-backend.herokuapp.com/todos")).service))
  }
}
