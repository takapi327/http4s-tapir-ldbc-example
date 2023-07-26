
package infrastructure.mysql.repository

import javax.sql.DataSource
import javax.inject.Inject

import cats.data.Kleisli
import cats.effect.IO

import ldbc.sql.*
import ldbc.sql.syntax.*
import ldbc.dsl.io.*
import ldbc.generated.example.Task

class TaskRepository @Inject() (
  dataSource: DataSource
):

  given Kleisli[IO, ResultSet[IO], Task] =
    for
      id         <- Task.table.id()
      categoryId <- Task.table.categoryId()
      title      <- Task.table.title()
      body       <- Task.table.body()
      status     <- Task.table.status()
      updatedAt  <- Task.table.updatedAt()
      createdAt  <- Task.table.createdAt()
    yield Task(id, categoryId, title, body, status, updatedAt, createdAt)

  given ResultSetReader[IO, Task.Status] =
    ResultSetReader.mapping[IO, String, Task.Status](v => Task.Status.values.find(_.toString == v).get)

  def get(id: Long): IO[Option[Task]] =
    sql"SELECT * FROM task WHERE id = $id"
      .query[Option[Task]]
      .transaction
      .run(dataSource)

  def getAll(): IO[Seq[Task]] =
    sql"SELECT * FROM task"
      .query[Seq[Task]]
      .transaction
      .run(dataSource)

  def create(categoryId: Long, title: String, body: String, status: Task.Status): IO[Int] =
    sql"INSERT INTO task (category_id, title, body, status) VALUES ($categoryId, $title, $body, ${ status.toString })"
      .update()
      .transaction
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
      .update()
      .transaction
      .run(dataSource)

  def delete(id: Long): IO[Int] =
    sql"DELETE FROM task WHERE id = $id"
      .update()
      .transaction
      .run(dataSource)
