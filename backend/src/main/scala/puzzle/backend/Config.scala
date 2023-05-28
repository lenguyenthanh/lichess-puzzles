package puzzle.backend

import cats.syntax.all.*
import ciris.*
import ciris.refined.*
import ciris.http4s.*
import eu.timepit.refined.types.net.UserPortNumber
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import com.comcast.ip4s.{Host, Port}
import cats.effect.Async

object Config:

  def load[F[_]: Async]: F[AppConfig] = appConfig.load[F]

  def appConfig[F[_]] = (
    PostgreSQLConfig.config,
    HttpServerConfig.config,
  ).parMapN(AppConfig.apply)

case class AppConfig(
  postgresSql: PostgreSQLConfig,
  server: HttpServerConfig,
)

case class HttpServerConfig(host: Host, port: Port)

object HttpServerConfig:
  def httpServerHost[F[_]] = env("HTTP_SERVER_HOST").or(prop("http.server.host")).as[Host]

  def httpServerPort[F[_]] = env("HTTP_SERVER_PORT").or(prop("http.server.port")).as[Port]

  def config[F[_]] = (httpServerHost, httpServerPort).parMapN(HttpServerConfig.apply)

case class FlywayConfig(
  url: String,
  user: Option[String],
  password: Option[Array[Char]],
  migrationsTable: String,
  migrationsLocations: List[String],
)

case class PostgreSQLConfig(
  host: NonEmptyString,
  port: UserPortNumber,
  user: NonEmptyString,
  password: NonEmptyString,
  database: NonEmptyString,
  max: PosInt,
):

  def toFlywayConfig: FlywayConfig = FlywayConfig(
    url = s"jdbc:postgresql://$host:$port/$database",
    user = Some(user.toString),
    password = Some(password.toString.toCharArray.nn),
    migrationsTable = "flyway",
    migrationsLocations = List("/db"),
  )

object PostgreSQLConfig:
  def postgresSQLHost[F[_]] = env("POSTGRES_HOST").or(prop("postgres.host")).as[NonEmptyString]

  def postgresSQLPort[F[_]] = env("POSTGRES_PORT").or(prop("postgres.port")).as[UserPortNumber]

  def postgresSQLUser[F[_]] = env("POSTGRES_USER").or(prop("postgres.user")).as[NonEmptyString]

  def postgresSQLPassword[F[_]] = env("POSTGRES_PASSWORD").or(prop("postgres.password")).as[NonEmptyString]

  def postgresSQLDatabase[F[_]] = env("POSTGRES_DATABASE").or(prop("postgres.database")).as[NonEmptyString]

  def postgresSQLMax[F[_]] = env("POSTGRES_MAX").or(prop("postgres.max")).as[PosInt]

  def config[F[_]] = (
    postgresSQLHost,
    postgresSQLPort,
    postgresSQLUser,
    postgresSQLPassword,
    postgresSQLDatabase,
    postgresSQLMax,
  ).parMapN(PostgreSQLConfig.apply)
