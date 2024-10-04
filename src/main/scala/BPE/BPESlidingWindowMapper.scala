package BPE

import org.apache.hadoop.io.{IntWritable, LongWritable, Text}
import org.apache.hadoop.mapred.{MapReduceBase, Mapper, OutputCollector, Reporter}
import java.io.IOException
import scala.jdk.CollectionConverters._

class BPESlidingWindowMapper extends MapReduceBase with Mapper[LongWritable, Text, Text, IntWritable] {
  private final val one = new IntWritable(1)
  private val word = new Text()
  val windowSize = 3

  @throws[IOException]
  override def map(key: LongWritable, value: Text, output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit = {
    val line: String = value.toString

    // BPE encode the line
    val encoded = BPEEncoding.encode(line)
    val encodedList = BPEEncoding.toList(encoded)

    // Create sliding windows from the encoded tokens
    val slidingWindows = createSlidingWindows(encodedList, windowSize)

    // Output each sliding window (input sequence -> final token)
    slidingWindows.foreach { case (inputSeq, nextToken) =>
      word.set(inputSeq.mkString(" ")) // Join input tokens as space-separated string
      output.collect(word, new IntWritable(nextToken)) // Emit input sequence and next token
    }
  }

  // Function to create sliding windows (input sequence and next token)
  def createSlidingWindows(tokens: List[Int], windowSize: Int): List[(List[Int], Int)] = {
    tokens.sliding(windowSize + 1).toList.map { window =>
      (window.take(windowSize), window.last) // Input is first N tokens, Output is the last token
    }
  }
}
