
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
    Query.category.selectAll.where(_.id === id)
      .headOption[Category]
      .readOnly(dataSource)

  def getAll(): IO[List[Category]] =
    Query.category.selectAll
      .toList[Category]
      .readOnly(dataSource)

  def create(name: String, slug: String, color: Short): IO[Int] =
    Query.category.insertInto(v => (v.name, v.slug, v.color))
      .values((name, slug, Category.Color.values.find(_.code == color).get))
      .update
      .autoCommit(dataSource)

  def update(category: Category): IO[Int] =
    Query.category.update("name", category.name)
      .set("slug", category.slug)
      .set("color", category.color)
      .where(_.id === category.id)
      .update
      .autoCommit(dataSource)
