
package application.service

import javax.sql.DataSource
import javax.inject.Inject

import cats.effect.IO

import ldbc.sql.*
import ldbc.dsl.ConnectionIO
import ldbc.dsl.io.*
import ldbc.dsl.logging.{ LogEvent, LogHandler }
import ldbc.query.builder.TableQuery

import ldbc.generated.example.{ Task, Category }

import infrastructure.mysql.repository.Query

class TaskCategoryLinksService @Inject() (
  dataSource: DataSource,
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
          case LogEvent.Success(sql, args) => s"Successful Statement Execution: ${replacePlaceholders(sql, args)}"
          case LogEvent.ProcessingFailure(sql, args, failure) => s"Failed ResultSet Processing: ${replacePlaceholders(sql, args)}" ++ failure.getMessage
          case LogEvent.ExecFailure(sql, args, failure) => s"Failed Statement Execution: ${replacePlaceholders(sql, args)} \n ${failure.getMessage}"
      )

  private val task = TableQuery[IO, Task](Task.table)

  def create(categoryId: Long, title: String, body: String, status: Task.Status): IO[Int] =
    val query = for
      categoryOpt <- Query.category.select(_.id).where(_.id === categoryId).headOption
      result <- categoryOpt match
        case Some(_) => task.insertInto(
          table => (table.categoryId, table.title, table.body, table.status)
        ).values((categoryId, title, body, status)).update
        case None => ConnectionIO.pure[IO, Int](-1)
    yield result
    query.transaction(dataSource)

  def delete(id: Long): IO[Int] =
    (for
      categoryDeleted <- Query.category.delete.where(_.id === id).update
      taskDeleted <- categoryDeleted match
        case 0 => ConnectionIO.pure[IO, Int](0)
        case _ => Query.task.delete.where(_.categoryId === id).update
    yield categoryDeleted + taskDeleted).transaction(dataSource)
