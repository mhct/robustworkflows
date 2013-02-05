import sbt._
import Keys._
import akka.sbt.AkkaKernelPlugin
import akka.sbt.AkkaKernelPlugin.{ Dist, outputDirectory, distJvmOptions}
 
object HelloKernelBuild extends Build {
  val Organization = "iMinds-Distrinet"
  val Version      = "2.1.0"
  val ScalaVersion = "2.10.0"
 
  lazy val HelloKernel = Project(
    id = "robustworkflows-kernel",
    base = file("."),
    settings = defaultSettings ++ AkkaKernelPlugin.distSettings ++ Seq(
      libraryDependencies ++= Dependencies.helloKernel,
      distJvmOptions in Dist := "-Xms256M -Xmx1024M",
      outputDirectory in Dist := file("target/sorcerer-dist")
    )
  )
 
  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := Organization,
    version      := Version,
    scalaVersion := ScalaVersion,
    crossPaths   := false,
    organizationName := "KU Leuven iMinds-Distrinet",
    organizationHomepage := Some(url("http://www.kuleuven.be"))
  )
  
  lazy val defaultSettings = buildSettings ++ Seq(
    resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",

    resolvers += "gephi2-snapshop" at "http://nexus.gephi.org/nexus/content/repositories/snapshots/",
    resolvers += "gephi2-release" at "http://nexus.gephi.org/nexus/content/repositories/releases/",
 
    // compile options
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked"),
    javacOptions  ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")
 
  )
}
 
object Dependencies {
  import Dependency._
 
  val helloKernel = Seq(
    akkaKernel, akkaSlf4j, logback
  )
}
 
object Dependency {
  // Versions
  object V {
    val Akka      = "2.1.0"
  }
 
  val akkaKernel = "com.typesafe.akka" %% "akka-kernel" % V.Akka
  val akkaSlf4j  = "com.typesafe.akka" %% "akka-slf4j"  % V.Akka
  val logback    = "ch.qos.logback"    % "logback-classic" % "1.0.0"
}
