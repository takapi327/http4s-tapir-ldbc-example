
package infrastructure.mysql

import javax.sql.DataSource

import com.mysql.cj.jdbc.MysqlDataSource

import com.google.inject.*

class DatabaseModule extends AbstractModule:

  override def configure(): Unit =
    val dataSource = new MysqlDataSource()
    dataSource.setServerName("127.0.0.1")
    dataSource.setPortNumber(13306)
    dataSource.setDatabaseName("example")
    dataSource.setUser("takapi327")
    dataSource.setPassword("takapi327")

    bind(classOf[DataSource]).toInstance(dataSource)
