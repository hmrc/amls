import sbt._

private object AppDependencies {
  import play.sbt.PlayImport._

  val bootstrapVersion = "8.4.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc.mongo"    %% "hmrc-mongo-play-28"         % "1.7.0",
    "org.typelevel"        %% "cats-core"                  % "2.10.0",
    "com.github.fge"       %  "json-schema-validator"      % "2.2.14",
    "com.eclipsesource"    %% "play-json-schema-validator" % "0.9.5",
    "com.beachape"         %% "enumeratum"                 % "1.7.3",
    "uk.gov.hmrc"          %% "bootstrap-backend-play-28"  % bootstrapVersion
  )

  trait TestDependencies {
    val scope: String = "test"
    val test : Seq[ModuleID]
  }

  object Test {
    def apply(): Seq[sbt.ModuleID] = new TestDependencies {
      override val test: Seq[sbt.ModuleID] = Seq(
        "org.mockito"             %% "mockito-scala"             % "1.17.30"                % scope,
        "org.scalatestplus"       %% "scalacheck-1-17"           % "3.2.17.0"               % scope,
        "uk.gov.hmrc"             %% "bootstrap-test-play-28"    % bootstrapVersion         % scope,
        "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"   % "1.7.0"                  % scope,
        "com.vladsch.flexmark"    %  "flexmark-all"              % "0.64.8"                 % scope
      )
    }.test
  }

  def apply(): Seq[sbt.ModuleID] = compile ++ Test()
}
