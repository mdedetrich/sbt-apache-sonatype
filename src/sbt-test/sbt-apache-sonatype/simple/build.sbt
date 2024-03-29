ThisBuild / scalaVersion                 := "2.13.10"
ThisBuild / apacheSonatypeProjectProfile := "project"
name                                     := "some-name"

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
    sys.error("publishTo not set")
}

TaskKey[Unit]("extract-packageSrc-contents") := {
  val sourcesJar =
    target.value / s"scala-${scalaBinaryVersion.value}" / s"${name.value}_${scalaBinaryVersion.value}-${version.value}-sources.jar"
  val targetDir = target.value / s"scala-${scalaBinaryVersion.value}" / "packageSrc"

  IO.unzip(sourcesJar, targetDir)
}

TaskKey[Unit]("extract-packageDoc-contents") := {
  val sourcesJar =
    target.value / s"scala-${scalaBinaryVersion.value}" / s"${name.value}_${scalaBinaryVersion.value}-${version.value}-javadoc.jar"
  val targetDir = target.value / s"scala-${scalaBinaryVersion.value}" / "packageDoc"

  IO.unzip(sourcesJar, targetDir)
}

TaskKey[Unit]("check-artifact-name") := {
  val file     = makePom.value
  val xml      = scala.xml.XML.loadFile(file)
  val nameNode = (xml \ "name").text
  if (nameNode != "Apache Some Name")
    sys.error(s"expected artifact name to be Apache Some Name, instead got ${nameNode}")
}
