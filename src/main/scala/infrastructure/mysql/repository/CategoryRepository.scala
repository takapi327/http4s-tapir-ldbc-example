
package infrastructure.mysql.repository

import javax.sql.DataSource
import javax.inject.Inject

import cats.effect.IO

import ldbc.sql.*
import ldbc.dsl.io.*
import ldbc.dsl.logging.LogHandler
import ldbc.generated.example.Category

class CategoryRepository @Inject() (
  dataSource: DataSource
):

  given LogHandler[IO] = LogHandler.consoleLogger[IO]

  def get(id: Long): IO[Option[Category]] =
    sql"SELECT * FROM category WHERE id = $id"
      .query[Category]
      .headOption
      .readOnly
      .run(dataSource)

  def getAll(): IO[List[Category]] =
    sql"SELECT * FROM category"
      .query[Category]
      .toList
      .readOnly
      .run(dataSource)

  def create(name: String, slug: String, color: Short): IO[Int] =
    sql"INSERT INTO category (name, slug, color) VALUES ($name, $slug, $color)"
      .update
      .autoCommit
      .run(dataSource)

  def update(category: Category): IO[Int] =
    sql"UPDATE category SET name = ${ category.name }, slug = ${ category.slug }, color = ${ category.color } WHERE id = ${ category.id }"
      .update
      .autoCommit
      .run(dataSource)
