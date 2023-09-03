
package infrastructure.mysql.repository

import javax.sql.DataSource
import javax.inject.Inject

import cats.effect.IO

import ldbc.sql.*
import ldbc.dsl.io.*
import ldbc.dsl.logging.LogHandler
import ldbc.generated.example.Task
import ldbc.query.builder.TableQuery

class TaskRepository @Inject() (
  dataSource: DataSource
):

  given LogHandler[IO] = LogHandler.consoleLogger[IO]

  given ResultSetReader[IO, Task.Status] =
    ResultSetReader.mapping[IO, String, Task.Status](v => Task.Status.values.find(_.toString == v).get)

  private val task = TableQuery[IO, Task](Task.table)

  def get(id: Long): IO[Option[Task]] =
    task.selectAll
      .query[Task]
      .headOption
      .readOnly
      .run(dataSource)

  def getAll(): IO[List[Task]] =
    task.selectAll
      .query[Task]
      .toList
      .readOnly
      .run(dataSource)

  def create(categoryId: Long, title: String, body: String, status: Task.Status): IO[Int] =
    task.selectInsert[(Long, String, String, Task.Status)](table => (table.categoryId, table.title, table.body, table.status))
      .values((categoryId, title, body, status))
      .update
      .autoCommit
      .run(dataSource)

  def update(row: Task): IO[Int] =
    task.update("categoryId", row.categoryId)
      .set("title", row.title)
      .set("body", row.body)
      .set("status", row.status)
      .update
      .autoCommit
      .run(dataSource)

  def delete(id: Long): IO[Int] =
    sql"DELETE FROM task WHERE id = $id"
      .update
      .autoCommit
      .run(dataSource)
