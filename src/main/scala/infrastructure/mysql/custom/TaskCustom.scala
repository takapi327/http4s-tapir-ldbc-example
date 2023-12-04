
package infrastructure.mysql.custom

import cats.effect.IO

import ldbc.sql.*

import ldbc.generated.example.Task

trait TaskCustom:

  given ResultSetReader[IO, Task.Status] =
    ResultSetReader.mapping[IO, String, Task.Status](v => Task.Status.values.find(_.toString == v).get)
