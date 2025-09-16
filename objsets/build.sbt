name := "objsets"
scalaVersion := "2.12.18"            // 2.13에서 사라진 JSON 파서 이슈 회피
ThisBuild / scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies += "junit" % "junit" % "4.13.2" % Test
// JSON 파서 필요 시(로컬 개발용). 실제 채점환경이 다르면 무시됨.
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
