package infrastructure.mysql.repository

import cats.effect.IO

import ldbc.query.builder.TableQuery

import ldbc.generated.example.*

object Query:
  val task = TableQuery[IO, Task](Task.table)
  val category = TableQuery[IO, Category](Category.table)
