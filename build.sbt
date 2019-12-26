name := "mpai"
 
version := "1.0" 
      
lazy val `mpai` = (project in file(".")).enablePlugins(PlayJava)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
scalaVersion := "2.11.11"


libraryDependencies ++= Seq( javaJdbc , cache , javaWs )

libraryDependencies += guice

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )