package puzzle
package db

import cats.syntax.all.*
import cats.effect.kernel.Resource
import cats.effect.MonadCancelThrow

import skunk.*
import skunk.implicits.*

import Codecs.*

trait PuzzleOpenings[F[_]]:
  def upsert(puzzleId: PuzzleId, openingId: OpeningId): F[Unit]

object PuzzleOpenings:
  def apply[F[_]: MonadCancelThrow](postgres: Resource[F, Session[F]]): PuzzleOpenings[F] = new:
    import PuzzleOpeningSQL.*

    def upsert(puzzleId: PuzzleId, openingId: OpeningId) =
      postgres.use: session =>
        session.prepare(insert).flatMap: cmd =>
          cmd.execute(puzzleId, openingId).void
        .recoverWith:
          case SqlState.UniqueViolation(_) =>
            PuzzleIdExists(puzzleId).raiseError

private object PuzzleOpeningSQL:
  val insert: Command[(PuzzleId, OpeningId)] =
    sql"""
        INSERT INTO puzzle_opening (puzzle_id, opening_id)
        VALUES ($puzzleId, $openingId)
       """.command
