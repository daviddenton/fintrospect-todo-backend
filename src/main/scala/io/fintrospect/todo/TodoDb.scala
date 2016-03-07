package io.fintrospect.todo

import java.util.UUID

import scala.collection.mutable

class TodoDb {
  private val db = mutable.Map.empty[UUID, Todo]

  def get(id: UUID): Option[Todo] = synchronized { db.get(id) }
  def list(): List[Todo] = synchronized { db.values.toList }
  def save(t: Todo): Todo = synchronized { db += (t.id -> t); t }
  def delete(id: UUID): Unit = synchronized { db -= id }
}
