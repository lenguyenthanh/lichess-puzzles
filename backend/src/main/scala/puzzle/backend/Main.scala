package puzzle.backend

import cats.effect.{IO, IOApp}
import cats.effect.*
import com.comcast.ip4s.{host, port}
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

  val serverConifg = HttpServerConfig(
    host"0.0.0.0",
    port"5011",
  )

  override def run: IO[Unit] = AppResources
    .instance[IO](flywayConfig)
    .evalMap(_.flyway.migrate)
    .flatMap(_ => MkHttpServer[IO].newEmber(serverConifg))
    .useForever
