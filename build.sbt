lazy val currentProject = (project in file("."))
  .settings(
    scalaVersion := "2.12.7",
    organization := "com.cultureamp",
    name := "kafka-aws-config-provider",
    libraryDependencies ++= Seq(
      "org.apache.kafka" %% "kafka" % "2.3.1" % Provided,
      "software.amazon.awssdk" % "ssm" % "2.10.56",
      "org.scalatest" %% "scalatest" % "3.1.0" % Test,
      "org.scalamock" %% "scalamock" % "4.4.0" % Test
    )
  )
