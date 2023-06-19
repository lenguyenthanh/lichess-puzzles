package puzzle
package db

import cats.syntax.all.*
import cats.effect.kernel.Resource
import cats.effect.MonadCancelThrow

import skunk.*
import skunk.codec.all.*
import skunk.implicits.*

import Codecs.*

trait Puzzles[F[_]]:
  def create(puzzle: NewPuzzle): F[Unit]

object Puzzles:
  def apply[F[_]: MonadCancelThrow](postgres: Resource[F, Session[F]]): Puzzles[F] = new:
    import PuzzleSql.*

    def create(puzzle: NewPuzzle) =
      postgres.use: session =>
        session.prepare(insertPuzzle).flatMap: cmd =>
          cmd.execute(puzzle).void
        .recoverWith:
          case SqlState.UniqueViolation(_) =>
            PuzzleIdExists(puzzle.id).raiseError

private object PuzzleSql:
  val codec: Codec[NewPuzzle] =
    (puzzleId *: epdFen *: moves *: nonNegInt *: int4 *: nonNegInt *: nonNegInt).to[NewPuzzle]

  val insertPuzzle: Command[NewPuzzle] =
    sql"""
        INSERT INTO puzzle (id, fen, moves, rating, rating_deviation, popularity, play_times)
        VALUES ($codec)
       """.command
