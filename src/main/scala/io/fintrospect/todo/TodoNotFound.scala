package io.fintrospect.todo

import java.util.UUID

case class TodoNotFound(id: UUID) {
  val message = s"Todo(${id.toString}) not found."
}
