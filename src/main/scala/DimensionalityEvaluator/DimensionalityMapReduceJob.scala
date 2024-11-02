package DimensionalityEvaluator

import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapred._
import org.slf4j.LoggerFactory
import config.ConfigLoader

object DimensionalityMapReduceJob {
  private val logger = LoggerFactory.getLogger(getClass)

  def runJob(): Unit = {
    val conf = configureJob()
    JobClient.runJob(conf)

    // After job completion, you can process results if needed
//    logger.info(s"Dimensionality evaluation job completed. Results stored at: $outputPath")
  }

  // Configure the Hadoop MapReduce job
  def configureJob(): JobConf = {
    val conf = new JobConf(getClass)
    conf.setJobName("DimensionalityEvaluator")

    // Hadoop job configurations
    conf.set("fs.defaultFS", "file:///")
    conf.set("mapreduce.job.maps", "20")
    conf.set("mapreduce.job.reduces", "1")

    // Set output key and value classes
    conf.setOutputKeyClass(classOf[Text])
    conf.setOutputValueClass(classOf[Text])

    // Set Mapper and Reducer classes
    conf.setMapperClass(classOf[DimensionalityMapper])
    conf.setReducerClass(classOf[DimensionalityReducer])

    // Set Input and Output formats
    conf.setInputFormat(classOf[TextInputFormat])
    conf.setOutputFormat(classOf[TextOutputFormat[Text, Text]])

    // Set input and output paths
    FileInputFormat.setInputPaths(conf, new Path(ConfigLoader.corpusPath))
    FileOutputFormat.setOutputPath(conf, new Path(s"${ConfigLoader.outputPath}/DimensionalityEvaluator/output.txt"))

    conf
  }
}
