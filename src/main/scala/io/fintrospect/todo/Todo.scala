package io.fintrospect.todo

case class Todo(id: String, url: String, title: String = "", completed: Boolean = false, order: Int = 0)