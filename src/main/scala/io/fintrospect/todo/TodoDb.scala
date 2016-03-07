package io.fintrospect.todo

import java.util.UUID

import scala.collection.mutable

class TodoDb(rootUrl: String) {

  def newTodo() = {
    val id = UUID.randomUUID().toString
    Todo(id, s"$rootUrl/$id")
  }

  private val db = mutable.Map.empty[String, Todo]

  def get(id: String): Option[Todo] = synchronized { db.get(id) }
  def list(): List[Todo] = synchronized { db.values.toList }
  def save(t: Todo): Todo = synchronized { db += (t.id -> t); t }
  def delete(id: String): Unit = synchronized { db -= id }
}
