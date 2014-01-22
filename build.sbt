name := "findingMyHouse"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.jsoup" % "jsoup" % "1.7.3",
  "org.xerial" % "sqlite-jdbc" % "3.7.2"
)     

play.Project.playJavaSettings
