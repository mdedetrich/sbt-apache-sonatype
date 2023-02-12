ThisBuild / scalaVersion                 := "2.13.10"
ThisBuild / apacheSonatypeProjectProfile := "project"

lazy val root = Project(id = "root", base = file("."))

lazy val subProject = Project(id = "sub", base = file("sub"))
  .settings(
    apacheSonatypeLicenseFile := (LocalRootProject / baseDirectory).value / "legal" / "ANOTHER-LICENSE"
  )
