package BPE

import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapred.{MapReduceBase, OutputCollector, Reducer, Reporter}
import org.slf4j.LoggerFactory

import java.io.{BufferedWriter, FileWriter}
import scala.jdk.CollectionConverters.*

class BPETokenReducer extends MapReduceBase with Reducer[Text, IntWritable, Text, IntWritable] {
  private val logger = LoggerFactory.getLogger(this.getClass)

  // File writer for outputting vocabulary statistics
  private val writer = new BufferedWriter(new FileWriter("vocabulary_stats.csv"))

  // Write header to the CSV file
  writer.write("Word,Token,Frequency\n")

  override def reduce(key: Text, values: java.util.Iterator[IntWritable], output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit = {
    logger.debug(s"Reducing token: $key")
    try {
      val sum = values.asScala.foldLeft(0)((acc, value) => acc + value.get())
      output.collect(key, new IntWritable(sum)) // Emit token sequence and total count

      // Write token statistics to the CSV file
      val wordWithToken = key.toString
      val frequency = sum.toString
      writer.write(s"$wordWithToken,$frequency\n")
    } catch
      case e: Exception => logger.error(s"Error during token reducing: ${e.getMessage}", e)
  }

  // Close the writer when the reducer finishes
  override def close(): Unit = {
    writer.close()
  }
}
