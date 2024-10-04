//import org.apache.hadoop.fs.Path
//import org.apache.hadoop.conf.*
//import org.apache.hadoop.io.*
//import org.apache.hadoop.util.*
//import org.apache.hadoop.mapred.*
//
//import java.io.IOException
//import java.util
//import scala.jdk.CollectionConverters.*
//
//import com.knuddels.jtokkit.Encodings
//import com.knuddels.jtokkit.api.{Encoding, EncodingRegistry, EncodingType, ModelType, IntArrayList}
//
//// MapReduce program for BPE tokenization
//object BPEMapReduceProgram {
//
//  // Initialize JTokkit BPE tokenizer
//  private val registry: EncodingRegistry = Encodings.newDefaultEncodingRegistry()
//  private val enc = registry.getEncodingForModel(ModelType.GPT_4)//registry.getEncoding(EncodingType.P50K_BASE)
//
//  // Mapper class
//  class Map extends MapReduceBase with Mapper[LongWritable, Text, Text, IntWritable] {
//    private final val one = new IntWritable(1)
//    private val word = new Text()
//
//    @throws[IOException]
//    override def map(key: LongWritable, value: Text, output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit = {
//      val line: String = value.toString
//
//      // Encode the line using BPE (Byte Pair Encoding)
//      val encoded: IntArrayList = enc.encode(line)
//
//      // Convert IntArrayList to List[Int]
//      val encodedList: List[Int] = (0 until encoded.size()).map(encoded.get).toList
//
//      // Collect tokens as output
//      encodedList.foreach { token =>
//        word.set(token.toString) // Convert token to string
//        output.collect(word, one) // Emit token and count of 1
//      }
//    }
//  }
//
//  // Reducer class
//  class Reduce extends MapReduceBase with Reducer[Text, IntWritable, Text, IntWritable] {
//    override def reduce(key: Text, values: util.Iterator[IntWritable], output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit = {
//      val sum = values.asScala.reduce((valueOne, valueTwo) => new IntWritable(valueOne.get() + valueTwo.get()))
//      output.collect(key, new IntWritable(sum.get())) // Emit token and its total count
//    }
//  }
//
//  @main def runMapReduceBPE(inputPath: String, outputPath: String): Unit = {
//    val conf: JobConf = new JobConf(this.getClass)
//    conf.setJobName("BPETokenizer")
//
//    // Configuration settings for Hadoop
//    conf.set("fs.defaultFS", "file:///")
//    conf.set("mapreduce.job.maps", "20")
//    conf.set("mapreduce.job.reduces", "1")
//    conf.setOutputKeyClass(classOf[Text])
//    conf.setOutputValueClass(classOf[IntWritable])
//    conf.setMapperClass(classOf[Map])
//    conf.setCombinerClass(classOf[Reduce])
//    conf.setReducerClass(classOf[Reduce])
//    conf.setInputFormat(classOf[TextInputFormat])
//    conf.setOutputFormat(classOf[TextOutputFormat[Text, IntWritable]])
//
//    // Set input and output paths
//    FileInputFormat.setInputPaths(conf, new Path(inputPath))
//    FileOutputFormat.setOutputPath(conf, new Path(outputPath))
//
//    // Run the job
//    JobClient.runJob(conf)
//  }
//}