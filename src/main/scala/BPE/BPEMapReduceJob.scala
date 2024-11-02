package BPE

import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapred._
import org.slf4j.LoggerFactory
import config.ConfigLoader

import java.io.{BufferedWriter, FileWriter}
import scala.io.Source

object BPEMapReduceJob {
  private val logger = LoggerFactory.getLogger(getClass)

  def runJob(): Unit = {
    val conf = configureJob()
    logger.info(s"Running MapReduce job with input: ${ConfigLoader.inputPath} and output: ${ConfigLoader.outputPath}")
    JobClient.runJob(conf)

//    // Collect vocabulary stats during the MapReduce job
//    logger.info(s"Reading tokenized output from path: $ConfigLoader.outputPath")
//    val vocab = readVocabularyFromOutput()
//
//    // Write vocabulary and frequencies to CSV
//    writeVocabularyToCSV(vocab)
  }

  // Configure the Hadoop MapReduce job
  def configureJob(): JobConf = {
    val conf = new JobConf(getClass)
    conf.setJobName("BPETokenizer")

    // Set shard size
    conf.set("mapreduce.input.fileinputformat.split.minsize", ConfigLoader.shardSize.toString)
    conf.set("mapreduce.input.fileinputformat.split.maxsize", ConfigLoader.shardSize.toString)

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
    FileInputFormat.setInputPaths(conf, new Path(ConfigLoader.corpusPath))
    FileOutputFormat.setOutputPath(conf, new Path(s"${ConfigLoader.outputPath}/BPE/output.txt"))

    conf
  }

//  // Write vocabulary to CSV
//  private def writeVocabularyToCSV(vocab: Map[String, (List[Int], Int)]): Unit = {
//    val csvPath = s"${ConfigLoader.outputPath}/vocabulary.csv"
//    val writer = new BufferedWriter(new FileWriter(csvPath))
//    logger.info("Saving CSV file")
//    writer.write("Word,Tokens,Frequency\n")
//
//    for ((word, (tokens, freq)) <- vocab) {
//      val tokenStr = tokens.mkString(" ")
//      writer.write(s"$word,$tokenStr,$freq\n")
//    }
//    writer.close()
//  }
//
//  private def readVocabularyFromOutput(): Map[String, (List[Int], Int)] = {
//    // This method should be customized based on the format of your output
//    // Assuming a text file output where each line has "word tokens frequency"
//    val vocabFile = Source.fromFile(s"${ConfigLoader.outputPath}/BPE/output.txt/part-00000") // Change this based on your output
//    val vocab = vocabFile.getLines().flatMap { line =>
//      val parts = line.split("\t") // Assuming tab-separated values
//      logger.info(s"parts.length: ${parts.length}\n ${parts.mkString("")}")
//      if (parts.length >= 3) { // Ensure we have at least 3 parts
//        val word = parts(0)
//        val tokens = parts(1).split(" ").map(_.toInt).toList
//        val frequency = parts(2).toInt
//        Some(word -> (tokens, frequency)) // Use Some to create an option
//      } else {
//        logger.warn(s"Skipping malformed line: $line") // Use logger for debugging output
//        None // Skip this line if it's malformed
//      }
//    }.toMap
//    vocabFile.close()
//    vocab
//  }
}
