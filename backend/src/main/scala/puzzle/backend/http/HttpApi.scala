package puzzle
package backend.http

import scala.concurrent.duration.*

import cats.syntax.all.*
import cats.effect.{ Async, Temporal }
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.server.middleware.*

import puzzle.services.Services

object HttpApi:
  def apply[F[_]: Async: Temporal](services: Services[F]): HttpApi[F] = new HttpApi[F](services)

class HttpApi[F[_]: Async: Temporal] private (services: Services[F]):
  private val userRoutes = UserRoutes[F](services.users).routes

  private val autoSlash: HttpRoutes[F] => HttpRoutes[F] =
    AutoSlash(_)

  private val timeout: HttpRoutes[F] => HttpRoutes[F] =
    Timeout(60.seconds)

  private val middleware: HttpRoutes[F] => HttpRoutes[F] =
    autoSlash andThen timeout

  private val loggers: HttpApp[F] => HttpApp[F] =
    RequestLogger.httpApp[F](true, true) andThen
      ResponseLogger.httpApp[F, Request[F]](true, true)

  val httpApp: HttpApp[F] = loggers(middleware(userRoutes).orNotFound)
