import Dependencies.*

inThisBuild(
  Seq(
    scalaVersion  := "3.4.3",
    versionScheme := Some("early-semver"),
    run / fork    := true,
  )
)

val commonSettings = Seq(
  scalacOptions -= "-Xfatal-warnings",
  scalacOptions ++= Seq("-source:future", "-rewrite", "-indent", "-Yexplicit-nulls", "-explain", "-Wunused:all"),
  resolvers ++= Seq(Dependencies.lilaMaven),
  libraryDependencies ++= Seq(
    catsCore,
    monocleCore,
    log4Cats,
    refinedCore,
    refinedCats,
    log4CatsNoop,
    weaver,
    weaverScalaCheck,
  ),
)

lazy val core = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      chess,
      circeCore,
      circeGeneric,
      circeParser,
      circeRefined,
      http4sClient,
      fs2DataCsv,
      fs2DataCsvGeneric,
      fs2DataJson,
      fs2DataJsonCirce,
      fs2Zstd,
    ),
  )

lazy val database = (project in file("modules/database"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      skunk,
      postgres,
      flyway,
      flywayPostgres,
      flyway4s,
      otel,
      testContainers,
    ),
  )
  .dependsOn(core)

lazy val cli = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      fs2IO,
      decline,
      declineEffect,
    ),
  )
  .dependsOn(core)

lazy val backend = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      http4sDsl,
      http4sServer,
      http4sCirce,
      cirisCore,
      cirisRefined,
      cirisHtt4s,
      logback,
    ),
  )
  .dependsOn(core, database)

lazy val root = project
  .in(file("."))
  .settings(publish := {}, publish / skip := true)
  .aggregate(core, cli, backend, database)
