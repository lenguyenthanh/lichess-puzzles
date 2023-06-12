package puzzle
package db
package tests

import cats.syntax.all.*
import cats.effect.IO
import com.dimafeng.testcontainers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import cats.effect.kernel.Resource
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.types.net.UserPortNumber
import eu.timepit.refined.types.numeric.PosInt
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.noop.NoOpLogger

object Fixture:

  private def parseConfig(cont: PostgreSQLContainer): IO[PostgresConfig] =
    IO:
      val jdbcUrl = java.net.URI.create(cont.jdbcUrl.substring(5)).nn
      (
        NonEmptyString.from(jdbcUrl.getHost.nn),
        UserPortNumber.from(jdbcUrl.getPort.nn),
        NonEmptyString.from(cont.username.nn),
        NonEmptyString.from(cont.password.nn),
        NonEmptyString.from(cont.databaseName.nn),
        PosInt.from(10),
      ).parMapN(PostgresConfig(_, _, _, _, _, _)).getOrElse(throw new Exception("Failed to parse JDBC URL"))

  private def postgresContainer: Resource[IO, PostgreSQLContainer] =
    val start = IO(
      PostgreSQLContainer(dockerImageNameOverride = DockerImageName.parse("postgres:15.3").nn)
    ).flatTap(cont => IO(cont.start()))

    Resource.make(start)(cont => IO(cont.stop()))

  def repositoryRes: Resource[IO, Repository[IO]] =

    given Logger[IO] = NoOpLogger[IO]

    postgresContainer
      .evalMap(parseConfig)
      .flatMap: config =>
        DbResources.instance[IO](config)
          .evalTap(_.flyway.migrate)
          .map(x => Repository.instance[IO](x.postgres))
