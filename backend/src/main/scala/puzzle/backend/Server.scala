package puzzle.backend

import org.http4s.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import cats.MonadThrow
import cats.effect.kernel.{Async, Resource}
import fs2.io.net.Network
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.defaults.Banner
import org.typelevel.log4cats.Logger

final class ServerRoutes[F[_]: MonadThrow] extends Http4sDsl[F]:

  def httpRoutes: HttpRoutes[F] = HttpRoutes
    .of[F]:
      case GET -> Root                  => Ok("Hello World!")
      case GET -> Root / "hello" / name => Ok(s"Hello, $name!")

  val routes: HttpRoutes[F] = Router("/" -> httpRoutes)

trait MkHttpServer[F[_]]:
  def newEmber(cfg: HttpServerConfig): Resource[F, Server]

object MkHttpServer:

  def apply[F[_]: MkHttpServer](
    using server: MkHttpServer[F]
  ): MkHttpServer[F] = server

  private def showEmberBanner[F[_]: Logger](s: Server): F[Unit] =
    Logger[F].info(s"\n${Banner.mkString("\n")}\nHTTP Server started at ${s.address}")

  implicit def forAsyncLogger[F[_]: Async: Network: Logger]: MkHttpServer[F] = new:

    def newEmber(cfg: HttpServerConfig): Resource[F, Server] = EmberServerBuilder
      .default[F]
      .withHost(cfg.host)
      .withPort(cfg.port)
      .withHttpApp(ServerRoutes[F]().routes.orNotFound)
      .build
      .evalTap(showEmberBanner[F])
