name := "ProgrammingAssignment"

scalaVersion := "2.10.2"

version :="1.0"

scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies += "org.scalatest" %% "scalatest" % "1.9.1" % "test"

libraryDependencies += "junit" % "junit" % "4.10" % "test"

libraryDependencies  ++= Seq(
            "org.scalanlp" % "breeze-math_2.10" % "0.4",
            "org.scalanlp" % "breeze-viz_2.10" % "0.4"
)

resolvers ++= Seq(
            // other resolvers here
            // if you want to use snapshot builds (currently 0.4-SNAPSHOT), use this.
            "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
            "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)