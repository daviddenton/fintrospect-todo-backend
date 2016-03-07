package io.fintrospect.todo

case class TodoNotFound(id: String) {
  val message = s"Todo ($id) not found."
}
