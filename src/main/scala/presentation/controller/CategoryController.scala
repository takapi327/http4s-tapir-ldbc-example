
package presentation.controller

import javax.inject.Inject

import cats.effect.IO

import ldbc.generated.example.Category

import infrastructure.mysql.repository.CategoryRepository

class CategoryController @Inject() (
  categoryRepository: CategoryRepository
):

  def get(id: Long): IO[Option[Category]] =
    categoryRepository.get(id)
