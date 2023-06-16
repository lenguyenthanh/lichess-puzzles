package puzzle
package db

import cats.effect.Resource
import cats.effect.MonadCancelThrow
import skunk.Session

object Repository:
  def instance[F[_]: MonadCancelThrow](postgres: Resource[F, Session[F]]): Repository[F] =
    Repository(Users(postgres), Themes(postgres), Openings(postgres), PuzzleThemes(postgres), PuzzleOpenings(postgres))

class Repository[F[_]] private (
    val users: Users[F],
    val themes: Themes[F],
    val openings: Openings[F],
    val puzzleThemes: PuzzleThemes[F],
    val puzzleOpenings: PuzzleOpenings[F],
)
