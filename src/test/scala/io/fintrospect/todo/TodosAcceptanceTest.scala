package io.fintrospect.todo

import com.twitter.finagle.http.Method.{Delete, Get, Patch, Post}
import com.twitter.finagle.http.Request
import com.twitter.finagle.http.Status.{Created, NotFound, Ok}
import com.twitter.util.Await
import io.fintrospect.ContentTypes
import io.fintrospect.formats.Json4s.JsonFormat
import io.fintrospect.util.HttpRequestResponseUtil.statusAndContentFrom
import org.json4s.JsonAST.JString
import org.scalatest.{FunSpec, ShouldMatchers}

class TodosAcceptanceTest extends FunSpec with ShouldMatchers {

  val svc = new TodoApp(new TodoDb("http://local/todos")).service

  describe("not found conditions") {
    it("lookup non existent todo") {
      responseTo(Request(Get, "/todos/123"))._1 shouldBe NotFound
    }

    it("delete non existent todo") {
      responseTo(Request(Delete, "/todos/123"))._1 shouldBe NotFound
    }

    it("update non existent todo") {
      val request = Request(Patch, "/todos/123")
      request.contentType = ContentTypes.APPLICATION_JSON.value
      request.contentString = """{"title":"bob2"}"""

      responseTo(request)._1 shouldBe NotFound
    }
  }

  describe("todo lifecycle") {
    it("starts with no todos") {
      responseTo(Request(Get, "/todos")) shouldBe(Ok, JsonFormat.array())
    }

    var newId: String = null

    it("saving a todo") {
      val request = Request(Post, "/todos")
      request.contentType = ContentTypes.APPLICATION_JSON.value
      request.contentString = """{"title":"bob"}"""

      val response = responseTo(request)
      response._1 shouldBe Created
      response._2 \ "title" shouldBe JsonFormat.string("bob")
      newId = (response._2 \ "id").asInstanceOf[JString].values
    }

    it("can get todo by id") {
      val response = responseTo(Request(Get, "/todos/" + newId))
      response._1 shouldBe Ok
      response._2 \ "title" shouldBe JsonFormat.string("bob")
    }

    it("list contains new todo") {
      val response = responseTo(Request(Get, "/todos"))
      response._1 shouldBe Ok
      response._2 \ "title" shouldBe JsonFormat.string("bob")
    }

    it("update a todo") {
      val request = Request(Patch, "/todos/" + newId)
      request.contentType = ContentTypes.APPLICATION_JSON.value
      request.contentString = """{"title":"bob2"}"""

      val response = responseTo(request)
      response._1 shouldBe Ok
      response._2 \ "title" shouldBe JsonFormat.string("bob2")
    }

    it("list contains updated todo") {
      val response = responseTo(Request("/todos"))
      response._1 shouldBe Ok
      response._2 \ "title" shouldBe JsonFormat.string("bob2")
    }

    it("delete the todo") {
      responseTo(Request(Delete, "/todos/" + newId))._1 shouldBe Ok
    }

    it("list is empty again") {
      responseTo(Request(Get, "/todos")) shouldBe(Ok, JsonFormat.array())
    }
  }

  describe("delete all") {
    it("starts with no todos") {
      responseTo(Request(Get, "/todos")) shouldBe(Ok, JsonFormat.array())
    }

    var newId: String = null

    it("saving a todo") {
      val request = Request(Post, "/todos")
      request.contentType = ContentTypes.APPLICATION_JSON.value
      request.contentString = """{"title":"bob"}"""

      val response = responseTo(request)
      response._1 shouldBe Created
      response._2 \ "title" shouldBe JsonFormat.string("bob")
      newId = (response._2 \ "id").asInstanceOf[JString].values
    }

    it("list contains new todo") {
      val response = responseTo(Request(Get, "/todos"))
      response._1 shouldBe Ok
      response._2 \ "title" shouldBe JsonFormat.string("bob")
    }

    it("delete all") {
      val response = responseTo(Request(Delete, "/todos"))
      response._1 shouldBe Ok
    }

    it("list is empty again") {
      responseTo(Request(Get, "/todos")) shouldBe(Ok, JsonFormat.array())
    }
  }

  def responseTo(request: Request) = {
    val raw = statusAndContentFrom(Await.result(svc(request)))
    (raw._1, JsonFormat.parse(raw._2))
  }
}
