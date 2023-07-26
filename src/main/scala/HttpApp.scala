
import com.google.inject.*

import cats.effect.*

import org.http4s.ember.server.EmberServerBuilder

import infrastructure.mysql.DatabaseModule

object HttpApp extends ResourceApp.Forever:

  override def run(args: List[String]): Resource[IO, Unit] =
    val injector = Guice.createInjector(new DatabaseModule)
    val app = injector.getInstance(classOf[Routes]).router.orNotFound
    EmberServerBuilder
      .default[IO]
      .withHttpApp(app)
      .build
      .flatMap(_ => Resource.unit)
