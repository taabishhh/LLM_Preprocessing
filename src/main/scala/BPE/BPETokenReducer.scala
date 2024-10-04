package BPE

import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapred.{MapReduceBase, Reducer, OutputCollector, Reporter}
import scala.jdk.CollectionConverters._

class BPETokenReducer extends MapReduceBase with Reducer[Text, IntWritable, Text, IntWritable] {

  override def reduce(key: Text, values: java.util.Iterator[IntWritable], output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit = {
    val sum = values.asScala.foldLeft(0)((acc, value) => acc + value.get())
    output.collect(key, new IntWritable(sum)) // Emit token sequence and total count
  }
}
