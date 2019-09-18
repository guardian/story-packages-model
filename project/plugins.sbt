// Additional information on initialization
logLevel := Level.Warn

resolvers ++= Seq(
  Classpaths.typesafeReleases,
  Resolver.sonatypeRepo("releases"),
  Resolver.typesafeRepo("releases"),
  Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns),
  Resolver.bintrayRepo("twittercsl", "sbt-plugins"),
  "Guardian Github Releases" at "http://guardian.github.com/maven/repo-releases",
  "Spy" at "https://files.couchbase.com/maven2/"
)

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.18")

addSbtPlugin("com.gu" % "sbt-riffraff-artifact" % "1.1.9")

addSbtPlugin("com.twitter" %% "scrooge-sbt-plugin" % "19.9.0")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.11")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.2-1")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.5")
