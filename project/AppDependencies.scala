import sbt._

private object AppDependencies {
  import play.sbt.PlayImport._
  import play.core.PlayVersion

  val bootstrapVersion = "7.19.0"

  val compile = Seq(
    ws,
    "uk.gov.hmrc.mongo"    %% "hmrc-mongo-play-28"         % "0.74.0",
    "org.typelevel"        %% "cats-core"                  % "2.9.0",
    "com.github.fge"       %  "json-schema-validator"      % "2.2.14",
    "com.eclipsesource"    %% "play-json-schema-validator" % "0.9.5",
    "com.beachape"         %% "enumeratum"                 % "1.7.0",
    "uk.gov.hmrc"          %% "bootstrap-backend-play-28"  % bootstrapVersion,
    "com.github.ghik"      %  "silencer-lib"               % "1.7.11" % Provided cross CrossVersion.full,
    compilerPlugin("com.github.ghik" % "silencer-plugin"  % "1.7.11" cross CrossVersion.full)
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  private val scalatestVersion = "3.2.15"
  private val scalatestPlusPlayVersion = "5.1.0"
  private val pegdownVersion = "1.6.0"
  private val scalacheckVersion = "1.17.0"

  object Test {
    def apply() = new TestDependencies {
      override lazy val test = Seq(
        "org.mockito"             %% "mockito-scala"             % "1.17.12"                % scope,
        "org.scalatestplus"       %% "scalacheck-1-17"           % "3.2.15.0"               % scope,
        "uk.gov.hmrc"             %% "bootstrap-test-play-28"    % bootstrapVersion         % scope,
        "com.vladsch.flexmark"    %  "flexmark-all"              % "0.64.0"                 % scope

      )
    }.test
  }

  object IntegrationTest {
    def apply() = new TestDependencies {

      override lazy val scope: String = "it"

      override lazy val test = Seq(
        "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28" % "0.74.0"                 % scope,
        "uk.gov.hmrc"             %% "bootstrap-test-play-28"  % bootstrapVersion         % scope

      )
    }.test
  }

  def apply() = compile ++ Test() ++ IntegrationTest()
}
