name := "KMeansWithSVMFormat"

version := "1.0"

scalaVersion := "2.11.12"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-feature")

libraryDependencies ++= {
  Seq(
    "org.scalatest"                 %%  "scalatest"                 % "2.2.5"       % "test",
    "com.github.scopt"              %%  "scopt"                     % "3.3.0",
    "org.apache.spark"              %%  "spark-core"                % "2.3.0"       % "provided",
    "org.apache.spark"              %%  "spark-mllib"               % "2.3.0"       % "provided"
  )
}

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) => {
    case PathList("META-INF", "ECLIPSEF.RSA" ) => MergeStrategy.discard
    case PathList("META-INF", "mailcap" ) => MergeStrategy.discard
    case PathList("com", "esotericsoftware", "minlog", ps ) if ps.startsWith("Log") => MergeStrategy.discard
    case PathList("plugin.properties" ) => MergeStrategy.discard
    case PathList("META-INF", ps @ _* ) => MergeStrategy.discard
    case PathList("javax", "activation", ps @ _* ) => MergeStrategy.first
    case PathList("org", "apache", "commons", ps @ _* ) => MergeStrategy.first
    case PathList("org", "apache", "hadoop", "yarn", ps @ _* ) => MergeStrategy.first
    case x => old(x)
  }
}

sparkVersion := "2.3.0"

sparkComponents ++= Seq("mllib")
