import com.amazonaws.regions.{ Region, Regions }
import ReleaseTransformations.*

ThisBuild / organization := "io.github.takapi327"
ThisBuild / startYear := Some(2023)

lazy val root = (project in file("."))
  .settings(
    name := "http4s-tapir-ldbc-example",
    scalaVersion := "3.3.1",
    run / fork := true,
    javaOptions ++= Seq(
      "-Dlogback.configurationFile=conf/logback.xml"
    ),
    scalacOptions ++= Seq(
      "-Xfatal-warnings",
      "-deprecation",
      "-Wunused:all",
      "-feature",
      "-language:implicitConversions"
    ),
    libraryDependencies ++= Seq(
      "com.google.inject" % "guice" % "5.1.0",
      "mysql" % "mysql-connector-java" % "8.0.33",
      "ch.qos.logback" % "logback-classic" % "1.4.7",
      "org.http4s" %% "http4s-dsl" % "0.23.18",
      "org.http4s" %% "http4s-ember-server" % "0.23.18",
      "org.http4s" %% "http4s-circe" % "0.23.19",
      "io.circe" %% "circe-generic" % "0.14.5",
      ldbcDsl
    ),
    Compile / parseDirectories := List(
      baseDirectory.value / "mysql" / "data"
    ),
    Compile / excludeFiles := List("mysql_user.sql"),
    Compile / customYamlFiles := List(
      baseDirectory.value / "mysql" / "custom" / "example.yml"
    )
  )
  .enablePlugins(Ldbc, DockerPlugin, EcrPlugin)

/** setting AWS Ecr */
Ecr / region := Region.getRegion(Regions.AP_NORTHEAST_1)
Ecr / repositoryName := "aurora-verification-server"
Ecr / repositoryTags := Seq(version.value, "latest")
Ecr / localDockerImage := (Docker / packageName).value + ":" + (Docker / version).value

releaseProcess := Seq[ReleaseStep](
  runClean,
  ReleaseStep(state => Project.extract(state).runTask(Docker / publishLocal, state)._1),
  ReleaseStep(state => Project.extract(state).runTask(Ecr / login, state)._1),
  ReleaseStep(state => Project.extract(state).runTask(Ecr / push, state)._1),
)
