package puzzle
package services

import skunk.*
import skunk.implicits.*
import cats.effect.kernel.Resource
import cats.effect.MonadCancelThrow
import cats.syntax.all.*
import eu.timepit.refined.types.string.NonEmptyString

import Codecs.*

trait Users[F[_]]:
  def create(user: User): F[Unit]

object Users:
  def instance[F[_]: MonadCancelThrow](postgres: Resource[F, Session[F]]): Users[F] = new:
    import UserSQL.*

    def create(user: User): F[Unit] =
      postgres.use: session =>
        session.prepare(insertUser).flatMap: cmd =>
          cmd.execute(user).void

private object UserSQL:

  val codec: Codec[User] = (userId *: nonEmptyString).to[User]

  val insertUser: Command[User] = sql"""
        INSERT INTO brands
        VALUES ($codec)
        """.command
