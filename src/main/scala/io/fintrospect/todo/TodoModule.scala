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
import io.fintrospect.renderers.swagger2dot0.{ApiInfo, Swagger2dot0Json}


class TodoModule(todoDb: TodoDb) extends ServerRoutes[Response] {

  private val id = Path.string("todo item identifier")

  private val todoSpec = Body(BodySpec[TodoPatch](Option("A todo entity"), APPLICATION_JSON, s => decode[TodoPatch](parse(s)), todo => compact(encode(todo))))

  private def listAll = Service.mk { rq: Request => Ok(encode(todoDb.list())) }

  private def lookup(id: String) = Service.mk { rq: Request =>
    todoDb.get(id) match {
      case Some(t) => Ok(encode(t))
      case None => NotFound(encode(TodoNotFound(id)))
    }
  }

  private def deleteAll = Service.mk {
    rq: Request => {
      todoDb.list().foreach(todo => todoDb.delete(todo.id))
      Ok()
    }
  }

  private def add = Service.mk {
    rq: Request =>
      val newTodo = todoSpec <-- rq
      todoDb.save(newTodo.modify(Todo(UUID.randomUUID().toString, "")))
      Ok(encode(newTodo))
  }

  private def update(id: String) = Service.mk {
    rq: Request =>
      todoDb.get(id) match {
        case Some(currentTodo) => {
          val updated = todoSpec <-- rq
          todoDb.delete(id)
          todoDb.save(updated.modify(currentTodo))
          Ok(encode(updated))
        }
        case None => NotFound(encode(TodoNotFound(id)))
      }
  }

  val module = ModuleSpec(Root / "todos", Swagger2dot0Json(ApiInfo("Todo backend API", "1.0")))
    .withDescriptionPath(_ / "api")
    .withRoute(RouteSpec().at(Get) bindTo listAll)
    .withRoute(RouteSpec().at(Get) / id bindTo lookup)
    .withRoute(RouteSpec().body(todoSpec).at(Post) bindTo add)
    .withRoute(RouteSpec().body(todoSpec).at(Patch) / id bindTo update)
    .withRoute(RouteSpec().at(Delete) bindTo deleteAll)
}
