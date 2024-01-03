
package infrastructure.mysql

import com.mysql.cj.jdbc.MysqlDataSource

import com.google.inject.*

import cats.effect.IO

import ldbc.dsl.io.*

class DatabaseModule extends AbstractModule:

  override def configure(): Unit =
    val dataSource = new MysqlDataSource()
    dataSource.setServerName("127.0.0.1")
    dataSource.setPortNumber(13306)
    dataSource.setDatabaseName("example")
    dataSource.setUser("takapi327")
    dataSource.setPassword("takapi327")

    bind(classOf[DataSource[IO]]).toInstance(DataSource[IO](dataSource))
