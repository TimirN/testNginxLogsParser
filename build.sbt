
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.11.12",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "nginxLogs",
    libraryDependencies ++= {
      val scalaTestVer = "3.0.4"
      val sparkCoreVer = "2.2.1"
      val sparkSqlVer  = "2.2.1"
      val uapVer       = "0.2.0"
      val scallopVer   = "3.1.2"
      Seq(
        "org.scalatest" %% "scalatest" % scalaTestVer % "test",
        "org.apache.spark" %% "spark-core" % sparkCoreVer % Provided,
        "org.apache.spark" %% "spark-sql" % sparkSqlVer % Provided,
        "org.uaparser" %% "uap-scala" % uapVer,
        "org.rogach" %% "scallop" % scallopVer
      )
    }
  )
