package puzzle.backend

import cats.effect.{IO, IOApp}
import cats.effect.*
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp.Simple:

  given Logger[IO] = Slf4jLogger.getLogger[IO]

  val flywayConfig = FlywayConfig(
    url = "jdbc:postgresql://localhost:5432/puzzle",
    user = Some("admin"),
    password = Some("dummy".toCharArray.nn),
    migrationsTable = "flyway",
    migrationsLocations = List("/db"),
  )

  override def run: IO[Unit] =
    Config.load[IO].flatMap: cfg =>
      AppResources
      .instance[IO](cfg.postgresSql.toFlywayConfig)
      .evalMap(_.flyway.migrate)
      .flatMap(_ => MkHttpServer[IO].newEmber(cfg.server))
      .useForever
