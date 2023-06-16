package puzzle
package db

import scala.util.control.NoStackTrace
import cats.syntax.all.*
import cats.effect.kernel.Resource
import cats.effect.MonadCancelThrow

import skunk.*
import skunk.implicits.*

import Codecs.*

trait PuzzleThemes[F[_]]:
  def upsert(puzzleId: PuzzleId, themeId: ThemeId): F[Unit]

case class PuzzleIdExists(id: PuzzleId) extends NoStackTrace

object PuzzleThemes:
  def apply[F[_]: MonadCancelThrow](postgres: Resource[F, Session[F]]): PuzzleThemes[F] = new:
    import PuzzleThemesSQL.*

    def upsert(puzzleId: PuzzleId, themeId: ThemeId) =
      postgres.use: session =>
        session.prepare(insert).flatMap: cmd =>
          cmd.execute(puzzleId, themeId).void
        .recoverWith:
          case SqlState.UniqueViolation(_) =>
            PuzzleIdExists(puzzleId).raiseError

private object PuzzleThemesSQL:
  val insert: Command[(PuzzleId, ThemeId)] =
    sql"""
        INSERT INTO puzzle_themes (puzzle_id, theme_id)
        VALUES ($puzzleId, $themeId)
       """.command
