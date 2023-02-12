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

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.13")
enablePlugins(SbtPlugin)

ThisBuild / versionScheme := Some("early-semver")
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
