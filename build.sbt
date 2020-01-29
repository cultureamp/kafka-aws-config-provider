lazy val currentProject = (project in file("."))
  .settings(
    scalaVersion := "2.12.7",
    organization := "com.cultureamp",
    name := "kafka-aws-config-provider",
    version := "0.1.0",

    libraryDependencies ++= Seq(
      "org.apache.kafka" %% "kafka" % "2.3.1" % Provided,
      "software.amazon.awssdk" % "ssm" % "2.10.56",
      "org.scalatest" %% "scalatest" % "3.1.0" % Test,
      "org.scalamock" %% "scalamock" % "4.4.0" % Test
    ),

    assemblyJarName in assembly :=
      s"jars/${organization.value}_${name.value}_${scalaVersion.value}_${version.value}.jar",

    assemblyMergeStrategy in assembly := {
      case "META-INF/MANIFEST.MF" => MergeStrategy.discard
      case "META-INF/io.netty.versions.properties" => MergeStrategy.discard
      case "module-info.class" => MergeStrategy.discard
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  )
