package config

import com.typesafe.config.{Config, ConfigFactory}

object ConfigLoader {
  // Load the configuration from application.conf
  private val config: Config = ConfigFactory.load()

  // Access specific configurations
  val appName: String = config.getString("app.name")
  val appVersion: String = config.getString("app.version")

  // Default input/output paths (can be overridden by terminal args)
  var inputPath: String = config.getString("app.inputPath")
  var outputPath: String = config.getString("app.outputPath")
  var corpusPath: String = config.getString("app.corpusPath")

  // Initialized shards of 64MB
  val shardSize: Long = config.getLong("app.shardSize")

  // Function to override paths with command-line arguments
  def overridePaths(input: String, output: String, corpus: String): Unit = {
    inputPath = input
    outputPath = output
    corpusPath = corpus
  }

  // Print out the loaded configuration (for debugging purposes)
  def printConfig(): Unit = {
    println(s"App Name: $appName")
    println(s"App Version: $appVersion")
    println(s"Input Path: $inputPath")
    println(s"Output Path: $outputPath")
  }
}
