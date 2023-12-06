
import javax.inject.Inject

import cats.Semigroup
import cats.data.NonEmptyList
import cats.syntax.all.*

import cats.effect.IO

import io.circe.syntax.*
import io.circe.generic.auto.*

import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.server.Router
import org.http4s.circe.CirceEntityEncoder.*

import presentation.controller.*

class Routes @Inject() (
  categoryController: CategoryController,
  taskController: TaskController
):

  given Semigroup[HttpRoutes[IO]] = _ combineK _

  private val healthcheck: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "healthcheck" => Ok("Healthcheck Ok")
  }

  private val category: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "category" => categoryController.getAll.flatMap(Ok(_))
    case request@POST -> Root / "category" => categoryController.create(request) >>= {
      case -1 => BadRequest()
      case _ => Created()
    }
    case GET -> Root / "category" / id => categoryController.get(id.toLong) >>= {
      case Some(value) => Ok(value.asJson)
      case None => NotFound()
    }
    case request@PUT -> Root / "category" / id => categoryController.update(id.toLong, request) >> NoContent()
    case DELETE -> Root / "category" / id => categoryController.delete(id.toLong) >> NoContent()
  }

  private val task: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "task" => taskController.getAll.flatMap(Ok(_))
    case request@POST -> Root / "task" => taskController.create(request) >>= {
      case -1 => BadRequest()
      case 0 => NoContent()
      case _ => Created()
    }
    case GET -> Root / "task" / id => taskController.get(id.toLong) >>= {
      case Some(value) => Ok(value.asJson)
      case None => NotFound()
    }
    case request@PUT -> Root / "task" / id => taskController.update(id.toLong, request) >> NoContent()
    case DELETE -> Root / "task" / id => taskController.delete(id.toLong) >> NoContent()
  }

  val router = Router(
    "/" -> healthcheck,
    "api" -> NonEmptyList.of(category, task).reduce
  )
