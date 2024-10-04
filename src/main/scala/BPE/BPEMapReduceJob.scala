package BPE

import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapred.{FileInputFormat, FileOutputFormat, JobClient, JobConf, TextInputFormat, TextOutputFormat}

object BPEMapReduceJob {

  def configureJob(inputPath: String, outputPath: String): JobConf = {
    val conf = new JobConf(this.getClass)
    conf.setJobName("BPETokenizerWithSlidingWindow")

    // Configuration settings for Hadoop
    conf.set("fs.defaultFS", "file:///")
    conf.set("mapreduce.job.maps", "100")
    conf.set("mapreduce.job.reduces", "1")
    conf.setOutputKeyClass(classOf[Text])
    conf.setOutputValueClass(classOf[IntWritable])
    conf.setMapperClass(classOf[BPESlidingWindowMapper])
    conf.setReducerClass(classOf[BPETokenReducer])
    conf.setInputFormat(classOf[TextInputFormat])
    conf.setOutputFormat(classOf[TextOutputFormat[Text, IntWritable]])

    // Set input and output paths
    FileInputFormat.setInputPaths(conf, new Path(inputPath))
    FileOutputFormat.setOutputPath(conf, new Path(outputPath))

    conf
  }

  def runJob(inputPath: String, outputPath: String): Unit = {
    val conf = configureJob(inputPath, outputPath)
    JobClient.runJob(conf)
  }
}
