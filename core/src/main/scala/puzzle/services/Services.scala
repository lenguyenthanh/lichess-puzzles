package puzzle.services

import cats.effect.Resource
import cats.effect.MonadCancelThrow
import skunk.Session

object Services:
  def instance[F[_]: MonadCancelThrow](postgres: Resource[F, Session[F]]): Services[F] =
    Services[F](Users.instance(postgres))

class Services[F[_]] private (
    val users: Users[F]
)
