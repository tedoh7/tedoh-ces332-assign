name := "RecFun"
version := "0.1"
scalaVersion := "2.13.12"

// 교수님 구조: src/main/scala 대신 main/ 과 test/ 을 바로 사용
Compile / scalaSource := baseDirectory.value / "main"
Test   / scalaSource := baseDirectory.value / "test"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % Test
