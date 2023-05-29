package puzzle.backend

import org.http4s.*
import cats.effect.kernel.{ Async, Resource }
import fs2.io.net.Network
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.defaults.Banner
import org.typelevel.log4cats.Logger

trait MkHttpServer[F[_]]:
  def newEmber(cfg: HttpServerConfig, httpApp: HttpApp[F]): Resource[F, Server]

object MkHttpServer:

  def apply[F[_]: MkHttpServer](
      using server: MkHttpServer[F]
  ): MkHttpServer[F] = server

  given forAsyncLogger[F[_]: Async: Network: Logger]: MkHttpServer[F] = new:

    def newEmber(cfg: HttpServerConfig, httpApp: HttpApp[F]): Resource[F, Server] = EmberServerBuilder
      .default[F]
      .withHost(cfg.host)
      .withPort(cfg.port)
      .withHttpApp(httpApp)
      .build
      .evalTap(showEmberBanner[F])

  private def showEmberBanner[F[_]: Logger](s: Server): F[Unit] =
    Logger[F].info(s"\n${Banner.mkString("\n")}\nHTTP Server started at ${s.address}")
