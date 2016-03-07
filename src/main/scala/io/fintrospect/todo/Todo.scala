package io.fintrospect.todo

case class TodoPatch(id: String, title: Option[String], completed: Option[Boolean], order: Option[Int], url: Option[String]) {
  def modify(original: Todo) = original.copy(
    title = title.getOrElse(original.title),
    completed = completed.getOrElse(original.completed),
    order = order.getOrElse(original.order),
    url = url.getOrElse(original.url)
  )
}

case class Todo(id: String, url: String, title: String = "", completed: Boolean = false, order: Int = 0)