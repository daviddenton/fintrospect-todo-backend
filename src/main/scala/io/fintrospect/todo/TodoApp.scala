package io.fintrospect.todo

import com.twitter.finagle.Service
import com.twitter.finagle.http.Method.{Delete, Get, Patch, Post}
import com.twitter.finagle.http.Status.{Created, NotFound, Ok}
import com.twitter.finagle.http.filter.Cors.{HttpFilter, UnsafePermissivePolicy}
import com.twitter.finagle.http.path.Root
import com.twitter.finagle.http.{Request, Response}
import io.fintrospect.formats.Json4s.JsonFormat.{bodySpec, encode}
import io.fintrospect.formats.Json4s.ResponseBuilder.implicits._
import io.fintrospect.parameters.{Body, Path}
import io.fintrospect.renderers.swagger2dot0.{ApiInfo, Swagger2dot0Json}
import io.fintrospect.{ModuleSpec, RouteSpec, ServerRoutes}


class TodoApp(todoDb: TodoDb) extends ServerRoutes[Request, Response] {

  private val id = Path.string("todo item identifier")

  private val todoSpec = Body(bodySpec[TodoPatch](Option("A todo entity")))

  private val listAll = Service.mk { rq: Request => Ok(encode(todoDb.list())) }

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

  private val deleteAll = Service.mk {
    rq: Request => {
      todoDb.list().foreach(todo => todoDb.delete(todo.id))
      Ok()
    }
  }

  private val add = Service.mk {
    rq: Request =>
      val patch = todoSpec <-- rq
      val newToDo = patch.modify(todoDb.newTodo())
      todoDb.save(newToDo)
      Created(encode(newToDo))
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

  private val module = ModuleSpec(Root / "todos", Swagger2dot0Json(ApiInfo("todo backend API", "1.0")))
    .withDescriptionPath(_ / "api")
    .withRoute(RouteSpec("lists all todos in the system").at(Get) bindTo listAll)
    .withRoute(RouteSpec("get a todo by id").at(Get) / id bindTo lookup)
    .withRoute(RouteSpec("add a new todo by supplying fields").body(todoSpec).at(Post) bindTo add)
    .withRoute(RouteSpec("patch an existing todo").body(todoSpec).at(Patch) / id bindTo update)
    .withRoute(RouteSpec("delete an existing todo").at(Delete) / id bindTo delete)
    .withRoute(RouteSpec("delete all todos in the system").at(Delete) bindTo deleteAll)

  val service = new HttpFilter(UnsafePermissivePolicy).andThen(module.toService)
}
