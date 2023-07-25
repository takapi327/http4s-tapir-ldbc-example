lazy val root = (project in file("."))
  .settings(
    organization := "com.github.takapi327",
    name := "http4s-tapir-ldbc-example",
    startYear := Some(2023),
    scalaVersion := "3.2.0",
    run / fork := true,
    scalacOptions ++= Seq(
      "-Xfatal-warnings",
      "-deprecation",
      "-Wunused:all"
    )
  )
