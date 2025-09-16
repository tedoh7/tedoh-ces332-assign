name := "objsets"
scalaVersion := "2.12.18"
ThisBuild / scalacOptions ++= Seq("-deprecation", "-feature")

lazy val root = (project in file("."))
  .configs(Test)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)

testFrameworks := Seq(new TestFramework("org.scalatest.tools.Framework"))
