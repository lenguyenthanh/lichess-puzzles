package puzzle
package db

import skunk.*
import skunk.implicits.*
import cats.effect.kernel.Resource
import cats.effect.MonadCancelThrow
import cats.syntax.all.*
import eu.timepit.refined.types.string.NonEmptyString

import Codecs.*
import scala.util.control.NoStackTrace

trait Users[F[_]]:
  def create(user: User): F[Unit]

case class UserIdExist(id: UserId) extends NoStackTrace

object Users:
  def apply[F[_]: MonadCancelThrow](postgres: Resource[F, Session[F]]): Users[F] = new:
    import UserSQL.*

    def create(user: User): F[Unit] =
      postgres.use: session =>
        session.prepare(insertUser).flatMap: cmd =>
          cmd.execute(user).void
        .recoverWith:
          case SqlState.UniqueViolation(_) =>
            UserIdExist(user.id).raiseError

private object UserSQL:

  val codec: Codec[User] = (userId *: nonEmptyString).to[User]

  val insertUser: Command[User] = sql"""
        INSERT INTO users
        VALUES ($codec)
        """.command
