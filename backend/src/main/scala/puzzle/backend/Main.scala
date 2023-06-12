package puzzle.backend

import cats.effect.{ IO, IOApp }
import cats.effect.*
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import puzzle.db.Repository

import puzzle.backend.http.HttpApi
import puzzle.backend.http.HealthCheck

object Main extends IOApp.Simple:

  given Logger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = Config
    .load[IO]
    .flatMap: cfg =>
      AppResources
        .instance[IO](cfg.postgres)
        .evalTap(_.db.flyway.migrate)
        .flatMap: res =>
          val services    = Repository.instance[IO](res.db.postgres)
          val healthCheck = HealthCheck.instance[IO](res.db.postgres)
          val app         = HttpApi[IO](services, healthCheck).httpApp
          MkHttpServer[IO].newEmber(cfg.server, app)
        .useForever
