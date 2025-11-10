name := "nodescala"

version := "0.1"

scalaVersion := "2.13.12"   // 다른 과제 scalaVersion이랑 맞추고 싶으면 여기 숫자만 바꿔도 됨

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xasync"          // ← async/await를 위한 필수 옵션
)

// async / await 라이브러리
libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-async" % "1.0.1"
)
