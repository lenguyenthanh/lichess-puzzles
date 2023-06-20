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
  def create(puzzle: Puzzle): F[Unit]

object Puzzles:
  def apply[F[_]: MonadCancelThrow](postgres: Resource[F, Session[F]]): Puzzles[F] = new:
    import PuzzleSql.*

    def insert(cmd: PreparedCommand[F, NewPuzzle])(puzzle: NewPuzzle) =
      cmd.execute(puzzle).void
        .recoverWith:
          case SqlState.UniqueViolation(_) =>
            PuzzleIdExists(puzzle.id).raiseError

    def insert(cmd: PreparedCommand[F, List[(PuzzleId, OpeningId)]])(xs: List[(PuzzleId, OpeningId)]) =
      cmd.execute(xs).void

    def create(puzzle: Puzzle) =
      postgres.use: session =>
        for
          insertPuzzleCmd   <- session.prepare(insertPuzzle)
          insertOpeningsCmd <- session.prepare(insertOpening(puzzle.openings.size))
          _ <- session.transaction.use: _ =>
            insert(insertPuzzleCmd)(puzzle.toNewPuzzle) >>
              insert(insertOpeningsCmd)(puzzle.puzzleOpenings)
        yield ()

private object PuzzleSql:
  val codec: Codec[NewPuzzle] =
    (puzzleId *: epdFen *: moves *: nonNegInt *: int4 *: nonNegInt *: nonNegInt).to[NewPuzzle]

  val insertPuzzle: Command[NewPuzzle] =
    sql"""
        INSERT INTO puzzle (id, fen, moves, rating, rating_deviation, popularity, play_times)
        VALUES ($codec)
       """.command

  def insertOpening(n: Int): Command[List[(PuzzleId, OpeningId)]] =
    val xs = (puzzleId *: openingId).values.list(n)
    sql"""
        INSERT INTO puzzle_opening
        VALUES $xs
       """.command
