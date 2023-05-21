import Dependencies._

inThisBuild(
  Seq(
    scalaVersion := "3.3.0-RC6",
    versionScheme := Some("early-semver"),

    // Github Workflow
    githubWorkflowPublishTargetBranches := Seq(), // Don't publish anywhere
    githubWorkflowJavaVersions := Seq(JavaSpec.temurin("17")),
    githubWorkflowUseSbtThinClient := true,
    githubWorkflowEnv := Map("SBT_OPTS" -> "-Xmx2048M"),
    semanticdbEnabled := true,
  )
)

val commonSettings = Seq(
  scalacOptions ++= Seq("-source:future", "-rewrite", "-indent", "-Yexplicit-nulls", "-explain", "-Wunused:all"),
  resolvers ++= Seq(Dependencies.lilaMaven),
  libraryDependencies ++= Seq(
    chess,
    catsCore,
    ironCore,
    ironCats,
    ironCirce,
    monocleCore,
    circeCore,
    circeGeneric,
    circeParser,
    scribe,
    scribeCats,
    weaver,
    weaverScalaCheck,
  ),
)

lazy val core = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      skunk,
      otel,
    ),
  )

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

lazy val root = project
  .in(file("."))
  .settings(publish := {}, publish / skip := true)
  .aggregate(core, cli)
