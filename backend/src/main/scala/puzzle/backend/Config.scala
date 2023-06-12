package puzzle.backend

import cats.syntax.all.*
import ciris.*
import ciris.refined.*
import ciris.http4s.*
import eu.timepit.refined.types.net.UserPortNumber
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import com.comcast.ip4s.{ Host, Port }
import cats.effect.Async

import puzzle.db.PostgresConfig

object Config:

  def load[F[_]: Async]: F[AppConfig] = appConfig.load[F]

  def appConfig[F[_]] = (
    postgresConfig[F],
    HttpServerConfig.config,
  ).parMapN(AppConfig.apply)

case class AppConfig(
    postgres: PostgresConfig,
    server: HttpServerConfig,
)

case class HttpServerConfig(host: Host, port: Port)

object HttpServerConfig:
  def httpServerHost[F[_]] = env("HTTP_SERVER_HOST").or(prop("http.server.host")).as[Host]

  def httpServerPort[F[_]] = env("HTTP_SERVER_PORT").or(prop("http.server.port")).as[Port]

  def config[F[_]] = (httpServerHost, httpServerPort).parMapN(HttpServerConfig.apply)

def host[F[_]] = env("POSTGRES_HOST").or(prop("postgres.host")).as[NonEmptyString]

def port[F[_]] = env("POSTGRES_PORT").or(prop("postgres.port")).as[UserPortNumber]

def user[F[_]] = env("POSTGRES_USER").or(prop("postgres.user")).as[NonEmptyString]

def password[F[_]] = env("POSTGRES_PASSWORD").or(prop("postgres.password")).as[NonEmptyString]

def database[F[_]] = env("POSTGRES_DATABASE").or(prop("postgres.database")).as[NonEmptyString]

def max[F[_]] = env("POSTGRES_MAX").or(prop("postgres.max")).as[PosInt]

def postgresConfig[F[_]] = (host, port, user, password, database, max).parMapN(PostgresConfig.apply)
