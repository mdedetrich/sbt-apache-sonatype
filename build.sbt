name         := "sbt-apache-sonatype"
sbtPlugin    := true
organization := "org.mdedetrich"
scalacOptions ++= Seq(
  "-opt:l:inline",
  "-opt-inline-from:<sources>"
)

lazy val scala212 = "2.12.17"
crossScalaVersions := Seq(scala212)
scalaVersion       := scala212

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.21")
addSbtPlugin("com.github.sbt" % "sbt-pgp"      % "2.2.1")
enablePlugins(SbtPlugin)

ThisBuild / versionScheme          := Some("early-semver")
ThisBuild / test / publishArtifact := false
ThisBuild / pomIncludeRepository   := (_ => false)
ThisBuild / publishMavenStyle      := true
ThisBuild / licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))
ThisBuild / developers := List(
  Developer("mdedetrich", "Matthew de Detrich", "mdedetrich@gmail.com", url("https://github.com/mdedetrich"))
)
ThisBuild / homepage := Some(url("https://github.com/mdedetrich/sbt-apache-sonatype"))

ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/mdedetrich/sbt-apache-sonatype"), "git@github.com:mdedetrich/sbt-apache-sonatype.git")
)

ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches :=
  Seq(
    RefPredicate.StartsWith(Ref.Tag("v")),
    RefPredicate.Equals(Ref.Branch("main"))
  )
ThisBuild / githubWorkflowUseSbtThinClient := false
ThisBuild / githubWorkflowJavaVersions     := List(JavaSpec.temurin("8"))
ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    commands = List("ci-release"),
    name = Some("Publish project"),
    env = Map(
      "PGP_PASSPHRASE"    -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET"        -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)

ThisBuild / githubWorkflowBuild := Seq(
  WorkflowStep.Sbt(name = Some("Build project"), commands = List("test", "scripted"))
)

scriptedLaunchOpts += ("-Dplugin.version=" + version.value)

scriptedLaunchOpts := {
  scriptedLaunchOpts.value ++
    Seq("-Dplugin.version=" + version.value)
}

scriptedBufferLog := false
