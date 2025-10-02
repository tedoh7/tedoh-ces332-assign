ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "forcomp",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.18" % Test
    ),
    Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD")
  )
