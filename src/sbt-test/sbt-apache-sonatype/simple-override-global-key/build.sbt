ThisBuild / scalaVersion                 := "2.13.10"
ThisBuild / apacheSonatypeProjectProfile := "project"
lazy val orgName = "Apache Doppelgänger Software Foundation"
ThisBuild / apacheSonatypeOrganizationName := orgName

TaskKey[Unit]("check-apache-sonatype-organization-name") := {
  if (apacheSonatypeOrganizationName.value != orgName && (ThisBuild / apacheSonatypeOrganizationName).value != orgName)
    sys.error("apacheSonatypeOrganizationName not set")
  ()
}
