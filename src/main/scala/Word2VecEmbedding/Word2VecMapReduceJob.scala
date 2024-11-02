package Word2VecEmbedding

import config.ConfigLoader
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapred._
import org.slf4j.LoggerFactory

object Word2VecMapReduceJob {
  private val logger = LoggerFactory.getLogger(getClass)

  def runJob(): Unit = {
    val conf = configureJob()
    logger.info(s"Running Word2Vec MapReduce job with input: $ConfigLoader.inputPath and output: $ConfigLoader.outputPath")
    JobClient.runJob(conf)
  }

  def configureJob(): JobConf = {
    val conf = new JobConf(getClass)
    conf.setJobName("Word2VecModelTraining")

    conf.set("fs.defaultFS", "file:///")
    conf.set("mapreduce.job.maps", "10")
    conf.set("mapreduce.job.reduces", "1")

    conf.setOutputKeyClass(classOf[Text])
    conf.setOutputValueClass(classOf[Text])

    conf.setMapperClass(classOf[Word2VecMapper])
    conf.setReducerClass(classOf[Word2VecReducer])

    conf.setInputFormat(classOf[TextInputFormat])
    conf.setOutputFormat(classOf[TextOutputFormat[Text, Text]])

    FileInputFormat.setInputPaths(conf, new Path(ConfigLoader.inputPath))
    FileOutputFormat.setOutputPath(conf, new Path(s"${ConfigLoader.outputPath}/Word2Vec/output.txt"))

    conf
  }
}
