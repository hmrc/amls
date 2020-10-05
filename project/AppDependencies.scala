import sbt._

private object AppDependencies {
  import play.sbt.PlayImport._
  import play.core.PlayVersion

  private val domainVersion = "5.9.0-play-26"
  private val playReactivemongoVersion = "7.30.0-play-26"
  private val authVersion = "3.0.0-play-26"
  private val joda = "2.7.4"

  val compile = Seq(
    "uk.gov.hmrc" %% "simple-reactivemongo" % playReactivemongoVersion,
    ws,
    "uk.gov.hmrc" %% "domain" % domainVersion,
    "org.typelevel" %% "cats" % "0.9.0",
    "com.eclipsesource" %% "play-json-schema-validator" % "0.9.4",
    "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "2.2.0" % "test,it",
    "com.beachape" %% "enumeratum" % "1.6.1",
    "uk.gov.hmrc" %% "auth-client" % authVersion,
    "uk.gov.hmrc" %% "bootstrap-backend-play-26" % "2.24.0",
    "com.typesafe.play" %% "play-json-joda" % joda
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  private val scalatestVersion = "3.0.9"
  private val scalatestPlusPlayVersion = "3.1.2"
  private val pegdownVersion = "1.6.0"
  private val scalacheckVersion = "1.14.3"

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "org.scalatest" %% "scalatest" % scalatestVersion % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % scalatestPlusPlayVersion % scope,
        "org.scalacheck" %% "scalacheck" % scalacheckVersion % scope,
        "org.pegdown" % "pegdown" % pegdownVersion % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "org.mockito" % "mockito-core" % "1.10.19" % scope
      )
    }.test
  }

  object IntegrationTest {
    def apply() = new TestDependencies {

      override lazy val scope: String = "it"

      override lazy val test = Seq(
        "org.scalatest" %% "scalatest" % scalatestVersion % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % scalatestPlusPlayVersion % scope,
        "org.pegdown" % "pegdown" % pegdownVersion % scope,
        "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
        "org.mockito" % "mockito-core" % "1.10.19" % scope
      )
    }.test
  }

  def apply() = compile ++ Test() ++ IntegrationTest()
}

