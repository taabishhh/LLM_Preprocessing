ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.5.0"

lazy val root = (project in file("."))
  .settings(
    name := "Exercises441",
  )

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) =>
    xs match {
      case "MANIFEST.MF" :: Nil => MergeStrategy.discard
      case  "services" :: _     => MergeStrategy.concat
      case _                    => MergeStrategy.discard
    }
  case "reference.conf" => MergeStrategy.concat
  case x if x.endsWith(".proto") => MergeStrategy.rename
  case x if x.contains("hadoop") => MergeStrategy.first
  case _ => MergeStrategy.first
}

// https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-common
libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "3.3.4"
// https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-mapreduce-client-core
libraryDependencies += "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "3.3.4"
libraryDependencies += "org.apache.hadoop" % "hadoop-mapreduce-client-jobclient" % "3.3.4"
libraryDependencies += "com.knuddels" % "jtokkit" % "1.1.0"
libraryDependencies += "org.deeplearning4j" % "deeplearning4j-core" % "1.0.0-M2.1"
libraryDependencies += "org.deeplearning4j" % "deeplearning4j-nlp" % "1.0.0-M2.1"
libraryDependencies += "org.nd4j" % "nd4j-native-platform" % "1.0.0-M2.1"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.5.6"
libraryDependencies += "org.slf4j" % "slf4j-api" % "2.0.12"
//libraryDependencies += "org.bytedeco" % "openblas" % "0.3.19-1.5.7" // Use the latest version available
//libraryDependencies += "org.nd4j" % "nd4j-native" % "1.0.0-M2.1"
//libraryDependencies += "org.nd4j" % "nd4j-backends" % "1.0.0-M2.1"
//libraryDependencies += "org.nd4j" % "nd4j-api" % "1.0.0-M2.1"
//libraryDependencies += "org.nd4j" % "nd4j-api-parent" % "1.0.0-M2.1"


//libraryDependencies += "org.nd4j" % "nd4j-native" % "1.0.0-M2.1" classifier "macosx-arm64"
//libraryDependencies += "org.nd4j" % "nd4j-native-preset" % "1.0.0-M1.1" classifier "macosx-arm64"
//libraryDependencies += "org.bytedeco" % "openblas-platform" % "0.3.19-1.5.7"
//libraryDependencies += "org.bytedeco" % "javacpp-platform" % "1.5.7"
//libraryDependencies += "org.bytedeco" % "mkl-platform" % "2022.0-1.5.7"
