package io.fintrospect.todo

import scala.collection.mutable

class TodoDb {
  private val db = mutable.Map.empty[String, Todo]

  def get(id: String): Option[Todo] = synchronized { db.get(id) }
  def list(): List[Todo] = synchronized { db.values.toList }
  def save(t: Todo): Todo = synchronized { db += (t.id -> t); t }
  def delete(id: String): Unit = synchronized { db -= id }
}
