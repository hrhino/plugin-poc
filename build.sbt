organization := "org.scalaz"

name := "scalaz-plugin"

scalaVersion := "2.12.5"

val library = (project in file("library"))
  .settings(
    name := "scalaz-plugin-library",
  )

val plugin = (project in file("plugin"))
  .dependsOn(library)
  .settings(
    name := "scalaz-plugin",
    libraryDependencies ++= List(
      scalaOrganization.value % "scala-compiler" % scalaVersion.value % Provided,
    ),
  )

val test = (project in file("test"))
  .dependsOn(library, plugin)
  .settings(
    publishArtifact := false,
    libraryDependencies ++= List(
      scalaOrganization.value % "scala-compiler" % scalaVersion.value,
      partestDep(scalaVersion.value),
    ),

    fork in Test := true,
    javaOptions in Test += "-Xmx1G",
    testFrameworks += new TestFramework("scala.tools.partest.sbt.Framework"),
    definedTests in Test +=
      new sbt.TestDefinition(
        "partest",
        new sbt.testing.AnnotatedFingerprint {
          def isModule = true
          def annotationName = "partest"
        }, true, Array()
      ),
    testOptions in Test += Tests.Argument(
      s"-Dpartest.scalac_opts=-Xplugin:${(packageBin in Compile in plugin).value}"
    ),
  )

val repl = (project in file("repl"))
  .dependsOn(library)
  .settings(
    publishArtifact := false,
    scalacOptions ++= List(
      s"-Xplugin:${(packageBin in Compile in plugin).value}",
      s"-Jdummy=${System.currentTimeMillis()}",
    ),
  )

def partestDep(v: String): ModuleID = (CrossVersion.partialVersion(v): @unchecked) match {
  case Some((2L, 12L)) =>
    "org.scala-lang.modules" %% "scala-partest" % "1.1.8"
  case Some((2L, 13L)) =>
    "org.scala-lang"          % "scala-partest" % v
}