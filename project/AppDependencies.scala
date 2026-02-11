import sbt._

private object AppDependencies {
  import play.sbt.PlayImport._

  val bootstrapVersion = "10.5.0"

  private val playV = "play-30"

  val hmrcMongoVersion = "2.12.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc.mongo"    %% s"hmrc-mongo-$playV"         % hmrcMongoVersion,
    "at.yawk.lz4"          %  "lz4-java"                   % "1.10.3",
    "org.typelevel"        %% "cats-core"                  % "2.10.0",
    "com.github.fge"       %  "json-schema-validator"      % "2.2.14" exclude("org.mozilla", "rhino"),
    "org.mozilla"          %  "rhino"                      % "1.8.1",
    "ch.qos.logback"       % "logback-core"                % "1.5.21",
    "com.beachape"         %% "enumeratum"                 % "1.7.3",
    "uk.gov.hmrc"          %% s"bootstrap-backend-$playV"  % bootstrapVersion exclude("org.apache.commons", "commons-lang3"),
    "org.apache.commons" % "commons-lang3" % "3.18.0"
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
        "uk.gov.hmrc"             %% s"bootstrap-test-$playV"    % bootstrapVersion         % scope,
        "uk.gov.hmrc.mongo"       %% s"hmrc-mongo-test-$playV"   % hmrcMongoVersion                  % scope,
        "org.scalamock" %% "scalamock" % "5.2.0" % scope
      )
    }.test
  }

  def apply(): Seq[sbt.ModuleID] = compile ++ Test()
}