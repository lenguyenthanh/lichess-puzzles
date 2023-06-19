package puzzle
package db

import scala.util.control.NoStackTrace
import cats.syntax.all.*
import cats.effect.kernel.Resource
import cats.effect.MonadCancelThrow
import eu.timepit.refined.types.string.NonEmptyString

import skunk.*
import skunk.implicits.*

import Codecs.*

trait Openings[F[_]]:
  def create(opening: Opening): F[Unit]
  def list: F[List[Opening]]

case class OpeningIdExists(id: OpeningId) extends NoStackTrace

object Openings:
  def apply[F[_]: MonadCancelThrow](postgres: Resource[F, Session[F]]): Openings[F] = new:
    import OpeningSQL.*

    def create(opening: Opening): F[Unit] =
      postgres.use: session =>
        session.prepare(insertOpening).flatMap: cmd =>
          cmd.execute(opening).void
        .recoverWith:
          case SqlState.UniqueViolation(_) =>
            OpeningIdExists(opening.id).raiseError

    def list: F[List[Opening]] = postgres.use: session =>
      session.execute(selectOpenings)

private object OpeningSQL:
  val codec: Codec[Opening] = (openingId *: nonEmptyString).to[Opening]

  val insertOpening: Command[Opening] =
    sql"""
         INSERT INTO opening
         VALUES ($codec)
       """.command

  val selectOpenings: Query[Void, Opening] =
    sql"""
         SELECT id, name FROM opening
       """.query(codec)
