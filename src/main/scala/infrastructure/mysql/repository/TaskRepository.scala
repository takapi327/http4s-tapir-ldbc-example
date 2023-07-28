
package infrastructure.mysql.repository

import javax.sql.DataSource
import javax.inject.Inject

import cats.data.Kleisli
import cats.effect.IO

import ldbc.sql.*
import ldbc.sql.syntax.*
import ldbc.dsl.io.*
import ldbc.dsl.logging.{ LogEvent, LogHandler }
import ldbc.generated.example.Task

class TaskRepository @Inject() (
  dataSource: DataSource
):

  // SQL文中の`?`を順にリストの値で置き換える関数
  def replacePlaceholders(sql: String, values: List[Any]): String = {
    val placeholders = List.fill(values.length)("?")
    placeholders.zip(values.map(quoteValue)).foldLeft(sql)((result, pair) => result.replace(pair._1, pair._2))
  }

  // 値を適切な形式でクォートする関数
  def quoteValue(value: Any): String = value match {
    case stringValue: String => s"'$stringValue'"
    case _ => value.toString
  }

  given LogHandler[IO] with
    override def run(logEvent: LogEvent): IO[Unit] =
      IO.println(
        logEvent match
          case LogEvent.Success(sql, args) => s"Successful Statement Execution: ${ replacePlaceholders(sql, args) }"
          case LogEvent.ExecFailure(sql, args, failure) => s"Failed Statement Execution: ${ replacePlaceholders(sql, args) } \n ${ failure.getMessage }"
      )

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
      .readOnly
      .run(dataSource)

  def getAll(): IO[Seq[Task]] =
    sql"SELECT * FROM task"
      .query[Seq[Task]]
      .readOnly
      .run(dataSource)

  def create(categoryId: Long, title: String, body: String, status: Task.Status): IO[Int] =
    sql"INSERT INTO task (category_id, title, body, status) VALUES ($categoryId, $title, $body, ${ status.toString })"
      .update()
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
      .update()
      .autoCommit
      .run(dataSource)

  def delete(id: Long): IO[Int] =
    sql"DELETE FROM task WHERE id = $id"
      .update()
      .autoCommit
      .run(dataSource)
