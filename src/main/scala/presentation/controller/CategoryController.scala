
package presentation.controller

import javax.inject.Inject

import io.circe.Decoder
import io.circe.generic.semiauto.*

import cats.effect.IO

import org.http4s.Request
import org.http4s.circe.CirceEntityDecoder.*

import ldbc.generated.example.Category

import infrastructure.mysql.repository.CategoryRepository
import application.service.TaskCategoryLinksService

class CategoryController @Inject() (
  categoryRepository: CategoryRepository,
  taskCategoryLinksService: TaskCategoryLinksService
):

  case class Input(name: String, slug: String, color: Short)
  given Decoder[Input] = deriveDecoder

  def get(id: Long): IO[Option[Category]] =
    categoryRepository.get(id)

  def getAll(): IO[Seq[Category]] = categoryRepository.getAll()

  def create(request: Request[IO]): IO[Int] =
    for
      input <- request.as[Input]
      result <- categoryRepository.create(input.name, input.slug, input.color)
    yield result

  def update(id: Long, request: Request[IO]): IO[Int] =
    for
      input <- request.as[Input]
      old <- categoryRepository.get(id)
      result <- old match
        case Some(value) => categoryRepository.update(value.copy(name = input.name, slug = input.slug, color = input.color))
        case None => IO.pure(0)
    yield result

  def delete(id: Long): IO[Int] = taskCategoryLinksService.delete(id)
