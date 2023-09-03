
package infrastructure.mysql.repository

import javax.sql.DataSource
import javax.inject.Inject

import cats.effect.IO

import ldbc.sql.*
import ldbc.dsl.io.*
import ldbc.dsl.logging.LogHandler
import ldbc.generated.example.Task

class TaskRepository @Inject() (
  dataSource: DataSource
):

  given LogHandler[IO] = LogHandler.consoleLogger[IO]

  given ResultSetReader[IO, Task.Status] =
    ResultSetReader.mapping[IO, String, Task.Status](v => Task.Status.values.find(_.toString == v).get)

  def get(id: Long): IO[Option[Task]] =
    sql"SELECT * FROM task WHERE id = $id"
      .query[Task]
      .headOption
      .readOnly
      .run(dataSource)

  def getAll(): IO[List[Task]] =
    sql"SELECT * FROM task"
      .query[Task]
      .toList
      .readOnly
      .run(dataSource)

  def create(categoryId: Long, title: String, body: String, status: Task.Status): IO[Int] =
    sql"INSERT INTO task (category_id, title, body, status) VALUES ($categoryId, $title, $body, ${ status.toString })"
      .update
      .autoCommit
      .run(dataSource)

  def update(task: Task): IO[Int] =
    sql"""
      UPDATE task SET
        category_id = ${ task.categoryId },
        title = ${ task.title },
        body = ${ task.body },
        status = ${ task.status.toString }
      WHERE id = ${ task.id }
    """
      .update
      .autoCommit
      .run(dataSource)

  def delete(id: Long): IO[Int] =
    sql"DELETE FROM task WHERE id = $id"
      .update
      .autoCommit
      .run(dataSource)
