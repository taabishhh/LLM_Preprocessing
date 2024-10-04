//import org.apache.hadoop.fs.Path
//import org.apache.hadoop.conf.*
//import org.apache.hadoop.io.*
//import org.apache.hadoop.util.*
//import org.apache.hadoop.mapred.*
//import com.knuddels.jtokkit.Encodings
//import com.knuddels.jtokkit.api.{Encoding, EncodingRegistry, EncodingType}
//import org.apache.hadoop.mapred.{JobConf, MapReduceBase, Mapper}
//
//import java.io.IOException
//import scala.jdk.CollectionConverters.*
//
//object MapReduceProgram:
//  // Mapper class for BPE Tokenization
//  class BPEMapper extends Mapper[LongWritable, Text, Text, Text] {
//
//    @throws[IOException]
//    @throws[InterruptedException]
//    override def map(key: LongWritable, value: Text, context : Mapper[LongWritable, Text, Text, Text]#Context): Unit = {
//      // Initialize the Byte Pair Encoding (BPE) from jtokkit
//      val registry: EncodingRegistry = Encodings.newDefaultEncodingRegistry()
//      val enc: Encoding = registry.getEncoding(EncodingType.P50K_BASE)
//
//      // Get the line from the input
//      val line: String = value.toString
//
//      // Perform BPE tokenization
//      // Encode the line (returns IntArrayList)
//      val encoded = enc.encode(line)
//
//      // Convert IntArrayList to Scala List[Int]
//      val encodedList: List[Int] = (0 until encoded.size()).map(encoded.get).toList
//
//      // Emit the line (text) and its tokenized form (encoded)
//      context.write(new Text(line), new Text(encodedList.mkString(",")))
//    }
//  }
//
//  class BPEReducer extends MapReduceBase with Reducer[Text, IntWritable, Text, IntWritable] {
//    @throws[IOException]
//    override def reduce(key: Text, values: java.lang.Iterable[Text], context: Reducer[Text, Text, Text, Text]#Context): Unit = {
//      val combinedTokens = values.asScala.mkString(" ")
//      context.write(key, new Text(combinedTokens))
//    }
//  }
//
//  object BPEMapReduceJob {
//
//    def main(args: Array[String]): Unit = {
//      //    // Configuration for the Hadoop Job
//      //    val conf: Configuration = new Configuration
//      //    val job: Job = Job.getInstance(conf, "BPE Tokenization Job")
//      //
//      //    job.setJarByClass(BPEMapReduceJob.getClass)
//      //
//      //    // Set Mapper and Reducer
//      //    job.setMapperClass(classOf[BPEMapper])
//      //    job.setReducerClass(classOf[BPEReducer])
//      //
//      //    // Set output key and value types for Mapper and Reducer
//      //    job.setOutputKeyClass(classOf[Text])
//      //    job.setOutputValueClass(classOf[Text])
//      //
//      //    // Set Input and Output file formats
//      //    FileInputFormat.addInputPath(job, new Path(args(0))) // Input path
//      //    FileOutputFormat.setOutputPath(job, new Path(args(1))) // Output path
//      //
//      //    // Wait for job completion
//      //    System.exit(if (job.waitForCompletion(true)) 0 else 1)
//      val conf: JobConf = new JobConf(this.getClass)
//      conf.setJobName("BPE Tokenization Job")
//      conf.set("fs.defaultFS", "local")
//      conf.set("mapreduce.job.maps", "1")
//      conf.set("mapreduce.job.reduces", "1")
//      conf.setOutputKeyClass(classOf[Text])
//      conf.setOutputValueClass(classOf[IntWritable])
//      conf.setJarByClass(BPEMapReduceJob.getClass)
//      conf.setMapperClass(classOf[BPEMapper])
//      conf.setCombinerClass(classOf[BPEReducer])
//      conf.setReducerClass(classOf[BPEReducer])
//      conf.setInputFormat(classOf[TextInputFormat])
//      conf.setOutputFormat(classOf[TextOutputFormat[Text, IntWritable]])
//      //    val logger = Logger("name")
//      //        FileInputFormat.setInputPaths(conf, new Path(inputPath))
//      //        FileOutputFormat.setOutputPath(conf, new Path(outputPath))
//      // Set Input and Output file formats
//      //    FileInputFormat.addInputPath(job, new Path(args(0))) // Input path
//      //    FileOutputFormat.setOutputPath(job, new Path(args(1))) // Output path
//      JobClient.runJob(conf)
//    }
//  }
//
