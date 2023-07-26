
package presentation.controller

import javax.inject.Inject

import scala.util.{ Success, Failure }

import io.circe.*
import io.circe.generic.semiauto.*

import cats.effect.IO

import org.http4s.Request
import org.http4s.circe.CirceEntityDecoder.*

import ldbc.generated.example.Task

import infrastructure.mysql.repository.*

class TaskController @Inject() (
  taskRepository: TaskRepository,
  categoryRepository: CategoryRepository
):

  case class Input(categoryId: Long, title: String, body: String, status: Task.Status)
  given Decoder[Task.Status] = Decoder.decodeString.emapTry(str =>
    Task.Status.values.find(_.toString == str)
      .map(Success(_))
      .getOrElse(Failure(new NoSuchElementException(str)))
  )
  given Decoder[Input] = deriveDecoder

  def get(id: Long): IO[Option[Task]] =
    taskRepository.get(id)

  def getAll(): IO[Seq[Task]] = taskRepository.getAll()

  def create(request: Request[IO]): IO[Int] =
    for
      input <- request.as[Input]
      category <- categoryRepository.get(input.categoryId)
      result <- category match
        case Some(value) => taskRepository.create(value.id, input.title, input.body, input.status)
        case None => IO.pure(-1)
    yield result

  def update(id: Long, request: Request[IO]): IO[Int] =
    for
      input <- request.as[Input]
      old <- taskRepository.get(id)
      result <- old match
        case Some(value) => taskRepository.update(value.copy(categoryId = input.categoryId, title = input.title, body = input.body, status = input.status))
        case None => IO.pure(0)
    yield result

  def delete(id: Long): IO[Int] = taskRepository.delete(id)
