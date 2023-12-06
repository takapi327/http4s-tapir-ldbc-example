
package infrastructure.mysql.repository

import javax.sql.DataSource
import javax.inject.Inject

import cats.effect.IO

import ldbc.sql.*
import ldbc.dsl.ConnectionIO
import ldbc.dsl.io.*
import ldbc.dsl.logging.LogHandler
import ldbc.generated.example.Task
import ldbc.query.builder.TableQuery

class TaskRepository @Inject() (
  dataSource: DataSource
):

  given LogHandler[IO] = LogHandler.consoleLogger[IO]

  def get(id: Long): IO[Option[Task]] =
    Query.task.selectAll
      .where(_.id === id)
      .headOption[Task]
      .readOnly(dataSource)

  def getAll: IO[List[Task]] =
    Query.task.selectAll
      .toList[Task]
      .readOnly(dataSource)

  def create(categoryId: Long, title: String, body: String, status: Task.Status): IO[Int] =
    Query.task.insertInto(table => (table.categoryId, table.title, table.body, table.status))
      .values((categoryId, title, body, status))
      .update
      .autoCommit(dataSource)

  def update(id: Long, categoryId: Long, title: String, body: String, status: Task.Status): IO[Int] =
    val query = for
      old <- Query.task.selectAll.where(_.id === id).headOption[Task]
      result <- old match
        case Some(value) => Query.task.update("categoryId", categoryId)
          .set("title", title)
          .set("body", body)
          .set("status", status)
          .where(_.id === value.id)
          .update
        case None => ConnectionIO.pure[IO, Int](0)
    yield result
    query.transaction(dataSource)

  def delete(id: Long): IO[Int] =
    Query.task.delete.where(_.id === id).update.autoCommit(dataSource)
