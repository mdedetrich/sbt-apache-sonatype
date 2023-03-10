ThisBuild / scalaVersion                 := "2.13.10"
ThisBuild / apacheSonatypeProjectProfile := "project"

TaskKey[Unit]("check-organization") := {
  val org = "org.apache.project"
  if (
    organization.value != org || (ThisBuild / organization).value != org || (sonatypeProfileName.value != org) || (ThisBuild / sonatypeProfileName).value != org
  )
    sys.error("apacheSonatypeProjectProfile not set")
  ()
}

TaskKey[Unit]("check-organization-name") := {
  val name = "Apache Software Foundation"
  if (organizationName.value != name || (ThisBuild / organizationName).value != name)
    sys.error("apacheSonatypeOrganizationName not set")
  ()
}

TaskKey[Unit]("check-publish-maven-style") := {
  val name = "Apache Software Foundation"
  if (!publishMavenStyle.value && !(ThisBuild / publishMavenStyle).value)
    sys.error("publishMavenStyle not set")
  ()
}

TaskKey[Unit]("check-pom-include-repository") := {
  val name = "Apache Software Foundation"
  if (pomIncludeRepository.value.apply(null) && (ThisBuild / pomIncludeRepository).value.apply(null))
    sys.error("pomIncludeRepository not set")
  ()
}

TaskKey[Unit]("check-pom-license-field") := {
  val apacheField = "Apache-2.0"
  if (!licenses.value.exists { case (field, _) => field == apacheField })
    sys.error("licenses not set")
  ()
}

TaskKey[Unit]("check-publish-to-field") := {
  val apacheSnapshotRepo =
    "https://repository.apache.org/content/repositories/snapshots"
  if (!publishTo.value.exists { case resolver: MavenRepository => resolver.root == apacheSnapshotRepo })
    ()
}
