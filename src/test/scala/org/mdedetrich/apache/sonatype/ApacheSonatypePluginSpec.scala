package org.mdedetrich.apache.sonatype

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import scala.xml._

class ApacheSonatypePluginSpec extends AnyFlatSpec with Matchers {
  "ApacheSonatypePlugin" must "replace node in prefixNameInPom" in {
    val before = XML.load(getClass.getResource("/before.pom.xml").getPath)
    val after  = XML.load(getClass.getResource("/after.pom.xml").getPath)

    ApacheSonatypePlugin.processNameInPom(ApacheSonatypePlugin.processArtifactName)(before).text mustEqual after.text
  }
}
