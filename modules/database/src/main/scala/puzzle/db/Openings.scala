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
  def create(name: NonEmptyString): F[Unit]
  def list: F[List[Opening]]

case class OpeningExists(name: NonEmptyString) extends NoStackTrace

object Openings:
  def apply[F[_]: MonadCancelThrow](postgres: Resource[F, Session[F]]): Openings[F] = new:
    import OpeningSQL.*

    def create(name: NonEmptyString): F[Unit] =
      postgres.use: session =>
        session.prepare(insertOpening).flatMap: cmd =>
          cmd.execute(name).void
        .recoverWith:
          case SqlState.UniqueViolation(_) =>
            OpeningExists(name).raiseError

    def list: F[List[Opening]] = postgres.use: session =>
      session.execute(selectOpenings)

private object OpeningSQL:
  val codec: Codec[Opening] = (openingId *: nonEmptyString).to[Opening]

  val insertOpening: Command[NonEmptyString] =
    sql"""
         INSERT INTO opening (name)
         VALUES ($nonEmptyString)
       """.command

  val selectOpenings: Query[Void, Opening] =
    sql"""
         SELECT id, name FROM opening
       """.query(codec)
