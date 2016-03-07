package io.fintrospect.todo

import java.util.UUID

import io.fintrospect.ContentTypes._
import io.fintrospect.formats.json.Json4s.Native.JsonFormat._
import io.fintrospect.parameters.{Body, BodySpec}

case class Todo(id: UUID, title: String, completed: Boolean, order: Int, url: String)

object Todo {
}