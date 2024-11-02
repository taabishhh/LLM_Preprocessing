package DimensionalityEvaluator

import org.apache.hadoop.io.{Text, IntWritable}
import org.apache.hadoop.mapred.{MapReduceBase, OutputCollector, Reducer, Reporter}
import org.slf4j.LoggerFactory

import scala.jdk.CollectionConverters._

class DimensionalityReducer extends MapReduceBase with Reducer[Text, IntWritable, Text, Text] {
  private val logger = LoggerFactory.getLogger(getClass)

  override def reduce(key: Text, values: java.util.Iterator[IntWritable], output: OutputCollector[Text, Text], reporter: Reporter): Unit = {
    var totalAccuracy = 0
    var count = 0

    try {
      // Summing up accuracy values
      values.asScala.foreach { value =>
        totalAccuracy += value.get()
        count += 1
      }

      // Calculate final accuracy as a percentage
      val finalAccuracy = totalAccuracy.toDouble / count
      logger.debug(s"Key: $key, Accuracy: $finalAccuracy, Count: $count")

      // Emit the key (dimension) and final accuracy as text
      output.collect(key, new Text(finalAccuracy.toString))
    } catch {
      case e: Exception => logger.error(s"Error during reduction: ${e.getMessage}", e)
    }
  }
}
