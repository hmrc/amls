import sbt._

private object AppDependencies {
  import play.sbt.PlayImport._

  val bootstrapVersion = "10.5.0"

  private val playV = "play-30"

  val hmrcMongoVersion = "2.12.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc.mongo"    %% s"hmrc-mongo-$playV"         % hmrcMongoVersion,
    "at.yawk.lz4"          %  "lz4-java"                   % "1.11.0",
    "org.typelevel"        %% "cats-core"                  % "2.13.0",
    "com.github.fge"       %  "json-schema-validator"      % "2.2.14" exclude("org.mozilla", "rhino"),
    "org.mozilla"          %  "rhino"                      % "1.9.1",
    "ch.qos.logback"       % "logback-core"                % "1.5.34",
    "ch.qos.logback"       % "logback-classic"             % "1.5.34",
    "com.beachape"         %% "enumeratum"                 % "1.9.8",
    "uk.gov.hmrc"          %% s"bootstrap-backend-$playV"  % bootstrapVersion exclude("org.apache.commons", "commons-lang3"),
    "org.apache.commons"   % "commons-lang3"               % "3.20.0"
  )

  trait TestDependencies {
    val scope: String = "test"
    val test : Seq[ModuleID]
  }

  object Test {
    def apply(): Seq[sbt.ModuleID] = new TestDependencies {
      override val test: Seq[sbt.ModuleID] = Seq(
        "org.mockito"             %% "mockito-scala"             % "2.2.1"                % scope,
        "org.scalatestplus"       %% "scalacheck-1-17"           % "3.2.18.0"             % scope,
        "uk.gov.hmrc"             %% s"bootstrap-test-$playV"    % bootstrapVersion       % scope,
        "uk.gov.hmrc.mongo"       %% s"hmrc-mongo-test-$playV"   % hmrcMongoVersion       % scope,
        "org.scalamock"           %% "scalamock"                 % "7.5.5"                % scope
      )
    }.test
  }

  def apply(): Seq[sbt.ModuleID] = compile ++ Test()
}