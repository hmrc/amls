resolvers += Resolver.url("hmrc-sbt-plugin-releases", url("https://dl.bintray.com/hmrc/sbt-plugin-releases"))(Resolver.ivyStylePatterns)
resolvers += "HMRC Releases" at "https://dl.bintray.com/hmrc/releases"
resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("uk.gov.hmrc" % "sbt-auto-build" % "1.16.0")

addSbtPlugin("uk.gov.hmrc" % "sbt-git-versioning" % "1.19.0")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.5")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.19")

addSbtPlugin("uk.gov.hmrc" % "sbt-settings" % "3.11.0")

addSbtPlugin("uk.gov.hmrc" % "sbt-distributables" % "1.6.0")

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.5.0")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.8.0")

addSbtPlugin("org.brianmckenna" % "sbt-wartremover" % "0.11")

addSbtPlugin("uk.gov.hmrc" % "sbt-artifactory" % "0.19.0")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.4.0")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.2")
