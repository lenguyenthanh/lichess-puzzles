import sbt._

object Dependencies {

  val lilaMaven = "lila-maven" at "https://raw.githubusercontent.com/lichess-org/lila-maven/master"

  object V {
    val decline = "2.4.1"
    val fs2     = "3.10.2"
    val monocle = "3.2.0"
    val iron    = "2.0.0"
    val circe   = "0.14.6"
    val http4s  = "0.23.23"
    val fs2Data = "1.9.1"
    val ciris   = "3.4.0"
    val refined = "0.11.1"
  }

  def http4s(artifact: String): ModuleID = "org.http4s" %% s"http4s-$artifact"   % V.http4s
  def circe(artifact: String)            = "io.circe"   %% s"circe-$artifact"    % V.circe
  def fs2Data(artifact: String)          = "org.gnieh"  %% s"fs2-data-$artifact" % V.fs2Data

  val chess = "org.lichess" %% "scalachess" % "15.2.11"

  val catsCore = "org.typelevel" %% "cats-core" % "2.10.0"
  val kittens  = "org.typelevel" %% "kittens"   % "3.0.0"

  val catsEffect = "org.typelevel" %% "cats-effect" % "3.5.0"

  val fs2   = "co.fs2" %% "fs2-core" % V.fs2
  val fs2IO = "co.fs2" %% "fs2-io"   % V.fs2

  val monocleCore = "dev.optics"         %% "monocle-core" % V.monocle
  val ironCore    = "io.github.iltotore" %% "iron"         % V.iron
  val ironCats    = "io.github.iltotore" %% "iron-cats"    % V.iron
  val ironCirce   = "io.github.iltotore" %% "iron-circe"   % V.iron

  val circeCore    = circe("core")
  val circeParser  = circe("parser")
  val circeGeneric = circe("generic")
  val circeRefined = circe("refined")

  val fs2DataCsv        = fs2Data("csv")
  val fs2DataCsvGeneric = fs2Data("csv-generic")
  val fs2DataJson       = fs2Data("json")
  val fs2DataJsonCirce  = fs2Data("json-circe")
  val fs2Zstd           = "de.lhns" %% "fs2-compress-zstd" % "1.0.0"

  val cirisCore    = "is.cir" %% "ciris"         % V.ciris
  val cirisHtt4s   = "is.cir" %% "ciris-http4s"  % V.ciris
  val cirisRefined = "is.cir" %% "ciris-refined" % V.ciris

  val refinedCore = "eu.timepit" %% "refined"      % V.refined
  val refinedCats = "eu.timepit" %% "refined-cats" % V.refined

  val skunk = "org.tpolecat" %% "skunk-core" % "0.6.3"

  val flyway4s = "com.github.geirolz" %% "fly4s-core"  % "0.0.20"
  val flyway   = "org.flywaydb"        % "flyway-core" % "9.22.3"
  val postgres = "org.postgresql"      % "postgresql"  % "42.7.3"

  val http4sDsl    = http4s("dsl")
  val http4sServer = http4s("ember-server")
  val http4sClient = http4s("ember-client")
  val http4sCirce  = http4s("circe")

  val decline       = "com.monovore" %% "decline"        % V.decline
  val declineEffect = "com.monovore" %% "decline-effect" % V.decline

  val otel     = "org.typelevel" %% "otel4s-java"     % "0.4.0"
  val log4Cats = "org.typelevel" %% "log4cats-slf4j"  % "2.6.0"
  val logback  = "ch.qos.logback" % "logback-classic" % "1.5.3"

  val log4CatsNoop     = "org.typelevel"       %% "log4cats-noop"                   % "2.6.0"  % Test
  val testContainers   = "com.dimafeng"        %% "testcontainers-scala-postgresql" % "0.41.3" % Test
  val weaver           = "com.disneystreaming" %% "weaver-cats"                     % "0.8.4"  % Test
  val weaverScalaCheck = "com.disneystreaming" %% "weaver-scalacheck"               % "0.8.4"  % Test

}
