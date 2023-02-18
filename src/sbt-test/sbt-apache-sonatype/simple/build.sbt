ThisBuild / scalaVersion                 := "2.13.10"
ThisBuild / apacheSonatypeProjectProfile := "project"

TaskKey[Unit]("check-organization-name") := {
  val name = "Apache Software Foundation"
  if (organizationName.value != name && (ThisBuild / organizationName).value != name)
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