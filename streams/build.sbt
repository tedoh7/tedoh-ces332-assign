ThisBuild / scalaVersion := "2.12.19"
ThisBuild / version := "0.1.0"
ThisBuild / organization := "edu.postech.csed332"

lazy val streams = (project in file("."))
  .settings(
    name := "streams",
    Test / fork := true,
    Test / parallelExecution := false,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.18" % Test
    )
  )
