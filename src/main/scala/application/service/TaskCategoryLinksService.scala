
package application.service

import javax.sql.DataSource
import javax.inject.Inject

import cats.data.Kleisli
import cats.effect.IO

import ldbc.sql.*
import ldbc.dsl.io.*

class TaskCategoryLinksService @Inject() (
  dataSource: DataSource,
):

  def delete(id: Long): IO[Int] =
    (for
      categoryDeleted <- sql"DELETE FROM category WHERE id = $id".update()
      taskDeleted <- categoryDeleted match
        case 0 => Kleisli.pure[IO, Connection[IO], Int](0)
        case _ => sql"DELETE FROM task WHERE category_id = $id".update()
    yield categoryDeleted + taskDeleted).transaction.run(dataSource)
