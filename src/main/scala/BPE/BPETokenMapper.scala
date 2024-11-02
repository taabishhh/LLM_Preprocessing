package BPE

import org.apache.hadoop.io.{IntWritable, LongWritable, Text}
import org.apache.hadoop.mapred.{MapReduceBase, Mapper, OutputCollector, Reporter}
import org.slf4j.LoggerFactory

import java.io.IOException

class BPETokenMapper extends MapReduceBase with Mapper[LongWritable, Text, Text, IntWritable] {
  private final val one = new IntWritable(1)
  private val wordAndToken = new Text()
  private val logger = LoggerFactory.getLogger(getClass)

  @throws[IOException]
  override def map(key: LongWritable, value: Text, output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit = {
    val line: String = value.toString

    // Preprocess the text by removing unwanted special characters
    val cleanedText = line.replaceAll("[=\"]", "")

    val words = cleanedText.split("\\s+")

    // BPE encode the line
    try {
      for (word <- words) {
        val encoded = BPEEncoding.encode(word)
        val encodedList = BPEEncoding.toList(encoded)

        for (token <- encodedList) {
          wordAndToken.set(s"$word\t$token")
          output.collect(wordAndToken, one)
        }
      }
    } catch {
      case e: Exception => logger.error(s"Error during token reducing: ${e.getMessage}", e)
    }
  }
}
