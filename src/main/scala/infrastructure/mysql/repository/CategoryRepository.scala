
package infrastructure.mysql.repository

import javax.sql.DataSource
import javax.inject.Inject

import cats.data.Kleisli
import cats.effect.IO

import ldbc.sql.*
import ldbc.sql.syntax.*
import ldbc.dsl.io.*
import ldbc.generated.example.Category

class CategoryRepository @Inject() (
  dataSource: DataSource
):

  given Kleisli[IO, ResultSet[IO], Category] =
    for
      id        <- Category.table.id()
      name      <- Category.table.name()
      slug      <- Category.table.slug()
      color     <- Category.table.color()
      updatedAt <- Category.table.updatedAt()
      createdAt <- Category.table.createdAt()
    yield Category(id, name, slug, color, updatedAt, createdAt)

  def get(id: Long): IO[Option[Category]] =
    sql"SELECT * FROM category WHERE id = $id"
      .query[Option[Category]]
      .transaction
      .run(dataSource)

  def getAll(): IO[Seq[Category]] =
    sql"SELECT * FROM category"
      .query[Seq[Category]]
      .transaction
      .run(dataSource)

  def create(name: String, slug: String, color: Short): IO[Int] =
    sql"INSERT INTO category (name, slug, color) VALUES ($name, $slug, $color)"
      .update()
      .transaction
      .run(dataSource)

  def update(category: Category): IO[Int] =
    sql"UPDATE category SET name = ${ category.name }, slug = ${ category.slug }, color = ${ category.color } WHERE id = ${ category.id }"
      .update()
      .transaction
      .run(dataSource)
