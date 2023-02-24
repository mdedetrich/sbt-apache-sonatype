ThisBuild / scalaVersion                 := "2.13.10"
ThisBuild / apacheSonatypeProjectProfile := "project"

lazy val root = Project(id = "root", base = file("."))

val customLicense = "Custom License"

lazy val subProject = Project(id = "sub", base = file("sub"))
  .settings(
    apacheSonatypeLicenseFile := (LocalRootProject / baseDirectory).value / "legal" / "ANOTHER-LICENSE",
    licenses += customLicense -> url("https://www.mycustomlicense.org")
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
