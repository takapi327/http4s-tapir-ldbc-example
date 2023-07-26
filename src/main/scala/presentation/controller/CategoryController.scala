
package presentation.controller

import javax.sql.DataSource
import javax.inject.Inject

import cats.effect.IO

class CategoryController @Inject() (
  dataSource: DataSource
):

  def get(id: Long): IO[Unit] =
    IO.println(dataSource.toString)
