package io.fintrospect.todo

case class Todo(id: String, title: Option[String], completed: Option[Boolean], order: Option[Int], url: Option[String])