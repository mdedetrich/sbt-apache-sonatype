ThisBuild / scalaVersion                 := "2.13.10"
ThisBuild / apacheSonatypeProjectProfile := "project"

lazy val root = Project(id = "root", base = file("."))
  .settings(
    name := "root-name"
  )

val customLicense = "Custom License"

lazy val subProject = Project(id = "sub", base = file("sub"))
  .settings(
    apacheSonatypeLicenseFile := (LocalRootProject / baseDirectory).value / "legal" / "ANOTHER-LICENSE",
    licenses += customLicense -> url("https://www.mycustomlicense.org"),
    name                      := "sub-name",
    TaskKey[Unit]("check-artifact-name") := {
      val file     = makePom.value
      val xml      = scala.xml.XML.loadFile(file)
      val nameNode = (xml \ "name").text
      if (nameNode != "Apache Sub Name")
        sys.error(s"expected artifact name to be Apache Sub Name, instead got ${nameNode}")
    }
  )

TaskKey[Unit]("check-pom-license-field") := {
  val apacheField = "Apache-2.0"
  if (
    !licenses.value.exists { case (field, _) => field == apacheField } && !(subProject / licenses).value.exists {
      case (field, _) => field == apacheField
    } && !(subProject / licenses).value.exists { case (field, _) =>
      field == customLicense
    }
  )
    sys.error("licenses not set")
  ()
}

TaskKey[Unit]("check-artifact-name") := {
  val file     = makePom.value
  val xml      = scala.xml.XML.loadFile(file)
  val nameNode = (xml \ "name").text
  if (nameNode != "Apache Root Name")
    sys.error(s"expected artifact name to be Apache Root Name, instead got ${nameNode}")
}
