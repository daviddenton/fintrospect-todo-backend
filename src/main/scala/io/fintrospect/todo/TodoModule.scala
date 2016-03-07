package io.fintrospect.todo

import com.twitter.finagle.Service
import com.twitter.finagle.http.Method.{Delete, Get, Patch, Post}
import com.twitter.finagle.http.Status.{NotFound, Ok}
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

  private def delete(id: String) = Service.mk {
    rq: Request => {
      todoDb.get(id) match {
        case Some(t) =>
          todoDb.delete(t.id)
          Ok(encode(t))
        case None => NotFound(encode(TodoNotFound(id)))
      }
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
      val patch = todoSpec <-- rq
      val newToDo = patch.modify(todoDb.newTodo())
      todoDb.save(newToDo)
      Ok(encode(newToDo))
  }

  private def update(id: String) = Service.mk {
    rq: Request =>
      todoDb.get(id) match {
        case Some(currentTodo) =>
          val patch = todoSpec <-- rq
          todoDb.delete(id)
          val updated = patch.modify(currentTodo)
          todoDb.save(updated)
          Ok(encode(updated))
        case None => NotFound(encode(TodoNotFound(id)))
      }
  }

  val module = ModuleSpec(Root / "todos", Swagger2dot0Json(ApiInfo("todo backend API", "1.0")))
    .withDescriptionPath(_ / "api")
    .withRoute(RouteSpec("lists all todos in the system").at(Get) bindTo listAll)
    .withRoute(RouteSpec("get a todo by id").at(Get) / id bindTo lookup)
    .withRoute(RouteSpec("add a new todo by supplying fields").body(todoSpec).at(Post) bindTo add)
    .withRoute(RouteSpec("patch an existing todo").body(todoSpec).at(Patch) / id bindTo update)
    .withRoute(RouteSpec("delete an existing todo").at(Delete) / id bindTo delete)
    .withRoute(RouteSpec("delete all todos in the system").at(Delete) bindTo deleteAll)
}
