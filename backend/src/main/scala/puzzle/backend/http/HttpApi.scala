package puzzle
package backend.http

import scala.concurrent.duration.*

import cats.syntax.all.*
import cats.effect.{ Async, Temporal }
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.server.middleware.*

import puzzle.services.Services

final class HttpApi[F[_]: Async: Temporal](services: Services[F], healthCheck: HealthCheck[F]):

  private val userRoutes   = UserRoutes[F](services.users).routes
  private val healthRoutes = HealthRoutes[F](healthCheck).routes

  private val routes: HttpRoutes[F] = userRoutes <+> healthRoutes

  type Middleware = HttpRoutes[F] => HttpRoutes[F]

  private val autoSlash: Middleware = AutoSlash(_)
  private val timeout: Middleware   = Timeout(60.seconds)

  private val middleware = autoSlash andThen timeout

  private val loggers: HttpApp[F] => HttpApp[F] =
    RequestLogger.httpApp[F](true, true) andThen
      ResponseLogger.httpApp[F, Request[F]](true, true)

  val httpApp: HttpApp[F] = loggers(middleware(routes).orNotFound)
