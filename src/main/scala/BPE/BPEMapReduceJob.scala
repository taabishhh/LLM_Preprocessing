package BPE

import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapred.*
import org.slf4j.LoggerFactory

import java.io.{BufferedWriter, FileWriter}
import scala.io.Source

object BPEMapReduceJob {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def runJob(inputPath: String, outputPath: String, shardSize: Long): Unit = {
    val conf = configureJob(inputPath, outputPath, shardSize)
    logger.info(s"Running MapReduce job with input: $inputPath and output: $outputPath")
    JobClient.runJob(conf)
    // Collect vocabulary stats during the MapReduce job (as a Map[String, (List[Int], Int)])
    logger.info(s"Reading tokenized output from path: $outputPath")
    val vocab = readVocabularyFromOutput(outputPath)

    // Write vocabulary and frequencies to CSV
    writeVocabularyToCSV(vocab, outputPath)
  }

  // Configure the Hadoop MapReduce job
   def configureJob(inputPath: String, outputPath: String, shardSize: Long): JobConf = {
    logger.info(s"Configuring MapReduce job with input: $inputPath and output: $outputPath")
    val conf = new JobConf(this.getClass)
    conf.setJobName("BPETokenizer")

    // Set shard size
    conf.set("mapreduce.input.fileinputformat.split.minsize", shardSize.toString)
    conf.set("mapreduce.input.fileinputformat.split.maxsize", shardSize.toString)

    // Hadoop job configurations
    conf.set("fs.defaultFS", "file:///")
    conf.set("mapreduce.job.maps", "20")
    conf.set("mapreduce.job.reduces", "1")
    conf.setOutputKeyClass(classOf[Text])
    conf.setOutputValueClass(classOf[IntWritable])
    conf.setMapperClass(classOf[BPETokenMapper])
    conf.setReducerClass(classOf[BPETokenReducer])
    conf.setInputFormat(classOf[TextInputFormat])
    conf.setOutputFormat(classOf[TextOutputFormat[Text, IntWritable]])

    // Set input and output paths
    FileInputFormat.setInputPaths(conf, new Path(inputPath))
    FileOutputFormat.setOutputPath(conf, new Path(outputPath))

    conf
  }

  // Write vocabulary to CSV
  private def writeVocabularyToCSV(vocab: Map[String, (List[Int], Int)], outputPath: String): Unit = {
    val csvPath = outputPath + "/vocabulary.csv"
    val writer = new BufferedWriter(new FileWriter(csvPath))
    logger.info("Saving CSV file")
    writer.write("Word,Tokens,Frequency\n")

    for ((word, (tokens, freq)) <- vocab) {
      val tokenStr = tokens.mkString(" ")
      writer.write(s"$word,$tokenStr,$freq\n")
    }
    writer.close()
  }

  private def readVocabularyFromOutput(outputPath: String): Map[String, (List[Int], Int)] = {
    // This method should be customized based on the format of your output
    // Here we are assuming a text file output where each line has "word tokens frequency"
    val vocabFile = Source.fromFile(s"$outputPath/part-00000") // Change this based on your output
    val vocab = vocabFile.getLines().flatMap { line =>
      val parts = line.split("\t") // Assuming tab-separated values
      if (parts.length >= 3) { // Ensure we have at least 3 parts
        val word = parts(0)
        val tokens = parts(1).split(" ").map(_.toInt).toList
        val frequency = parts(2).toInt
        Some(word -> (tokens, frequency)) // Use Some to create an option
      } else {
        println(s"Skipping malformed line: $line") // Debugging output
        None // Skip this line if it's malformed
      }
    }.toMap
    vocabFile.close()
    vocab
  }
}

