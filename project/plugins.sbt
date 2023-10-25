resolvers += "HMRC-open-artefacts-maven" at "https://open.artefacts.tax.service.gov.uk/maven2"
resolvers += Resolver.url("HMRC-open-artefacts-ivy", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(Resolver.ivyStylePatterns)
resolvers += Resolver.jcenterRepo
resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/"

// To resolve a bug with version 2.x.x of the scoverage plugin - https://github.com/sbt/sbt/issues/6997
ThisBuild / libraryDependencySchemes ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
)

addSbtPlugin("uk.gov.hmrc"        %  "sbt-auto-build"         % "3.14.0")
addSbtPlugin("com.github.gseitz"  %  "sbt-release"            % "1.0.13")
addSbtPlugin("com.typesafe.play"  %  "sbt-plugin"             % "2.8.20")
addSbtPlugin("uk.gov.hmrc"        %  "sbt-distributables"     % "2.2.0")
addSbtPlugin("org.scoverage"      %% "sbt-scoverage"          % "2.0.5")
addSbtPlugin("org.scalastyle"     %% "scalastyle-sbt-plugin"  % "1.0.0")
addSbtPlugin("net.virtual-void"   %  "sbt-dependency-graph"   % "0.9.2")
