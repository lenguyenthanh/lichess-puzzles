package puzzle.db

import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.types.net.UserPortNumber

case class FlywayConfig(
    url: String,
    user: Option[String],
    password: Option[Array[Char]],
    migrationsTable: String,
    migrationsLocations: List[String],
)

case class PostgresConfig(
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
