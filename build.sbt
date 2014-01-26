name := "findingMyHouse"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.jsoup" % "jsoup" % "1.7.3",
  "com.graphhopper" % "graphhopper" % "0.2"
)     

play.Project.playJavaSettings
