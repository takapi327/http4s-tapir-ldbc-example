
import cats.effect.*

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder

object HttpApp extends ResourceApp.Forever:

  private val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "healthcheck" => Ok("Healthcheck Ok")
  }

  override def run(args: List[String]): Resource[IO, Unit] =
    EmberServerBuilder
      .default[IO]
      .withHttpApp(routes.orNotFound)
      .build
      .flatMap(_ => Resource.unit)
