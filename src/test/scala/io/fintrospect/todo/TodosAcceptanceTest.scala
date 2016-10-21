package io.fintrospect.todo

import com.twitter.finagle.http.Method.{Delete, Get, Patch, Post}
import com.twitter.finagle.http.Status.{Created, NotFound, Ok}
import com.twitter.finagle.http.{Request, Status}
import com.twitter.util.Await
import io.circe.Json
import io.circe.optics.JsonPath._
import io.fintrospect.ContentTypes
import io.fintrospect.formats.Circe.JsonFormat
import io.fintrospect.util.HttpRequestResponseUtil.statusAndContentFrom
import org.scalatest.{FunSpec, Matchers}

class TodosAcceptanceTest extends FunSpec with Matchers {

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

  def titles(json: Json) = root.each.title.string.getAll(json)

  def id(json: Json) = root.id.string.getOption(json).get

  def title(json: Json) = root.title.string.getOption(json).get

  describe("todo lifecycle") {

    it("starts with no todos") {
      val response = responseTo(Request(Get, "/todos"))
      response._1 shouldBe Ok
      titles(response._2).size shouldBe 0
    }

    var newId: String = null

    it("saving a todo") {
      val request = Request(Post, "/todos")
      request.contentType = ContentTypes.APPLICATION_JSON.value
      request.contentString = """{"title":"bob"}"""

      val response = responseTo(request)
      response._1 shouldBe Created
      title(response._2) shouldBe "bob"
      newId = id(response._2)
    }

    it("can get todo by id") {
      val response = responseTo(Request(Get, "/todos/" + newId))
      response._1 shouldBe Ok
      title(response._2) shouldBe "bob"
    }

    it("list contains new todo") {
      val response = responseTo(Request(Get, "/todos"))
      response._1 shouldBe Ok
      titles(response._2).head shouldBe "bob"
    }

    it("update a todo") {
      val request = Request(Patch, "/todos/" + newId)
      request.contentType = ContentTypes.APPLICATION_JSON.value
      request.contentString = """{"title":"bob2"}"""

      val response = responseTo(request)
      response._1 shouldBe Ok
      title(response._2) shouldBe "bob2"
    }

    it("list contains updated todo") {
      val response = responseTo(Request("/todos"))
      response._1 shouldBe Ok
      titles(response._2).head shouldBe "bob2"
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
      title(response._2) shouldBe "bob"
      newId = id(response._2)
    }

    it("list contains new todo") {
      val response = responseTo(Request(Get, "/todos"))
      response._1 shouldBe Ok
      titles(response._2).head shouldBe "bob"
    }

    it("delete all") {
      statusAndContentFrom(Await.result(svc(Request(Delete, "/todos"))))._1 shouldBe Ok
    }

    it("list is empty again") {
      titles(responseTo(Request(Get, "/todos"))._2).size shouldBe 0
    }
  }

  def responseTo(request: Request): (Status, Json) = {
    val raw = statusAndContentFrom(Await.result(svc(request)))
    (raw._1, JsonFormat.parse(raw._2))
  }
}
