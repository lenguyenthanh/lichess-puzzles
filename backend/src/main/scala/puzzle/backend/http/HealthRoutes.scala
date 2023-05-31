package puzzle
package backend.http

import cats.*
import org.http4s.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.circe.CirceEntityEncoder.*

final class HealthRoutes[F[_]: Monad](
    healthCheck: HealthCheck[F]
) extends Http4sDsl[F]:
  private[http] val prefixPath = "/health"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F]:
    case GET -> Root =>
      Ok(healthCheck.status)

  val routes: HttpRoutes[F] = Router(prefixPath -> httpRoutes)
