
import javax.inject.Inject

import cats.Semigroup
import cats.data.NonEmptyList
import cats.syntax.all.*

import cats.effect.IO

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.server.Router

import presentation.controller.*

class Routes @Inject() (
  categoryController: CategoryController
):

  given Semigroup[HttpRoutes[IO]] = _ combineK _

  private val healthcheck: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "healthcheck" => Ok("Healthcheck Ok")
  }

  private val category: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "category" => Ok()
    case POST -> Root / "category" => NoContent()
    case GET -> Root / "category" / id => categoryController.get(id.toLong) >> Ok(id)
    case PUT -> Root / "category" / id => NoContent()
    case DELETE -> Root / "category" / id => NoContent()
  }

  private val task: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "task" => Ok()
    case POST -> Root / "task" => NoContent()
    case GET -> Root / "task" / id => Ok(id)
    case PUT -> Root / "task" / id => NoContent()
    case DELETE -> Root / "task" / id => NoContent()
  }

  val router = Router(
    "/" -> healthcheck,
    "api" -> NonEmptyList.of(category, task).reduce
  )
