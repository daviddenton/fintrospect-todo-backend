package io.fintrospect.todo

import java.util.UUID

import com.twitter.finagle.Service
import com.twitter.finagle.http.Method.{Delete, Get, Patch, Post}
import com.twitter.finagle.http.Status.{Conflict, NotFound, Ok}
import com.twitter.finagle.http.path.Root
import com.twitter.finagle.http.{Request, Response}
import io.fintrospect.ContentTypes._
import io.fintrospect._
import io.fintrospect.formats.json.Json4s.Native.JsonFormat._
import io.fintrospect.formats.json.Json4s.Native.ResponseBuilder._
import io.fintrospect.parameters.{Body, BodySpec, Path}


class TodoModule(todoDb: TodoDb) extends ServerRoutes[Response] {

  private val id = Path.uuid("todo item identifier")

  private val todoSpec = Body(BodySpec[Todo](Option("A todo entity"), APPLICATION_JSON, s => decode[Todo](parse(s)), todo => compact(encode(todo))))

  private def list = Service.mk { r: Request => Ok(encode(todoDb.list())) }

  private def lookup(id: UUID) = Service.mk { r: Request =>
    todoDb.get(id) match {
      case Some(t) => Ok(encode(t))
      case None => NotFound(encode(TodoNotFound(id)))
    }
  }

  private def delete(id: UUID) = Service.mk { r: Request =>
    todoDb.get(id) match {
      case Some(t) => todoDb.delete(id); Ok(encode(t))
      case None => NotFound(encode(TodoNotFound(id)))
    }
  }

  private def add = Service.mk { r: Request =>
    val newTodo = todoSpec <-- r
    todoDb.get(newTodo.id) match {
      case None => {
        todoDb.save(newTodo)
        Ok(encode(newTodo))
      }
      case Some(todo) => Conflict(encode(todo))
    }
  }

  private def update(id: UUID) = Service.mk { r: Request =>
    todoDb.get(id) match {
      case Some(currentTodo) => {
        val updated = todoSpec <-- r
        todoDb.delete(id)
        todoDb.save(updated)
        Ok(encode(updated))
      }
      case None => NotFound(encode(TodoNotFound(id)))
    }
  }

  val module = ModuleSpec(Root / "todos")
    .withRoute(RouteSpec().at(Get) bindTo list)
    .withRoute(RouteSpec().at(Get) / id bindTo lookup)
    .withRoute(RouteSpec().body(todoSpec).at(Post) bindTo add)
    .withRoute(RouteSpec().body(todoSpec).at(Patch) / id bindTo update)
    .withRoute(RouteSpec().at(Delete) / id bindTo delete)
}
