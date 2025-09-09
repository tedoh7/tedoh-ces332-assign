name := "funsets"
version := "0.1"
scalaVersion := "2.13.12"

Compile / scalaSource := baseDirectory.value / "src" / "main" / "scala"
Test    / scalaSource := baseDirectory.value / "src" / "test" / "scala"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % Test
