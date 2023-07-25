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
    ),
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.4.7",
      "org.http4s" %% "http4s-dsl" % "0.23.18",
      "org.http4s" %% "http4s-ember-server" % "0.23.18",
    )
  )
