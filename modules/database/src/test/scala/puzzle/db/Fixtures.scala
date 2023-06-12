package puzzle
package db
package tests

import cats.syntax.all.*
import cats.effect.IO
import com.dimafeng.testcontainers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import cats.effect.kernel.Resource
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.types.numeric.PosInt
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.noop.NoOpLogger
import eu.timepit.refined.types.net.PortNumber

object Fixture:

  private def parseConfig(cont: PostgreSQLContainer): IO[PostgresConfig] =
    IO:
      val jdbcUrl = java.net.URI.create(cont.jdbcUrl.substring(5)).nn
      PostgresConfig(
        NonEmptyString.unsafeFrom(jdbcUrl.getHost.nn),
        PortNumber.unsafeFrom(jdbcUrl.getPort.nn),
        NonEmptyString.unsafeFrom(cont.username.nn),
        NonEmptyString.unsafeFrom(cont.password.nn),
        NonEmptyString.unsafeFrom(cont.databaseName.nn),
        PosInt.unsafeFrom(10),
      )

  private def postgresContainer: Resource[IO, PostgreSQLContainer] =
    val start = IO(
      PostgreSQLContainer(dockerImageNameOverride = DockerImageName.parse("postgres:15.3").nn)
    ).flatTap(cont => IO(cont.start()))

    Resource.make(start)(cont => IO(cont.stop()))

  def createRepositoryResource: Resource[IO, Repository[IO]] =

    given Logger[IO] = NoOpLogger[IO]

    postgresContainer
      .evalMap(parseConfig)
      .flatMap: config =>
        DbResources.instance[IO](config)
          .evalTap(_.flyway.migrate)
          .map(x => Repository.instance[IO](x.postgres))
