import bintray.Keys._

organization := "com.agilogy"

name := "srdb-types"

version := "1.0.0"

crossScalaVersions := Seq("2.10.4","2.11.5")

resolvers += Resolver.url("Agilogy Scala",url("http://dl.bintray.com/agilogy/scala/"))(Resolver.ivyStylePatterns)

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "test",
  "com.agilogy" %% "srdb-core" % "1.0.0" % "test"
)

ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages := "<empty>"

ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting := false

Boilerplate.settings

publishMavenStyle := false

// --> bintray

seq(bintrayPublishSettings:_*)

repository in bintray := "scala"

bintrayOrganization in bintray := Some("agilogy")

packageLabels in bintray := Seq("scala")

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

// <-- bintray
