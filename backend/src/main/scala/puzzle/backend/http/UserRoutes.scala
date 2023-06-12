package puzzle
package backend.http

import cats.*
import cats.syntax.all.*
import cats.effect.Concurrent
import org.http4s.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.circe.CirceEntityDecoder.*

import puzzle.db.Users
import puzzle.db.UserIdExist

final class UserRoutes[F[_]: MonadThrow: Concurrent](users: Users[F]) extends Http4sDsl[F]:

  private[http] val prefixPath = "/users"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F]:

    case req @ POST -> Root =>
      req
        .decode[User]: user =>
          users
            .create(user)
            .flatMap(Created(_))
            .recoverWith:
              case UserIdExist(id) => Conflict(s"User with id ${id.value} already exists")
              case x               => InternalServerError(x.getMessage().nn)

  val routes: HttpRoutes[F] = Router(prefixPath -> httpRoutes)
