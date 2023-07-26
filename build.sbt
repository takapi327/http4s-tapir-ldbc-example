ThisBuild / organization := "com.github.takapi327"
ThisBuild / startYear := Some(2023)

lazy val root = (project in file("."))
  .settings(
    name := "http4s-tapir-ldbc-example",
    scalaVersion := "3.3.0",
    run / fork := true,
    scalacOptions ++= Seq(
      "-Xfatal-warnings",
      "-deprecation",
      "-Wunused:all"
    ),
    libraryDependencies ++= Seq(
      "com.google.inject" % "guice" % "5.1.0",
      "mysql" % "mysql-connector-java" % "8.0.33",
      "ch.qos.logback" % "logback-classic" % "1.4.7",
      "org.http4s" %% "http4s-dsl" % "0.23.18",
      "org.http4s" %% "http4s-ember-server" % "0.23.18"
    ),
    Compile / parseDirectories := List(
      baseDirectory.value / "mysql" / "data"
    ),
    Compile / excludeFiles := List("mysql_user.sql"),
    Compile / customYamlFiles := List(
      baseDirectory.value / "mysql" / "custom" / "example.yml"
    )
  )
  .enablePlugins(Ldbc)
