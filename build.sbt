name := "findingMyHouse"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.jsoup" % "jsoup" % "1.7.3",
  "org.json" % "json" % "20131018"
)     

play.Project.playJavaSettings
