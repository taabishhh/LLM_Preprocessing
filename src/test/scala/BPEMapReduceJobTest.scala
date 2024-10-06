import BPE.BPEMapReduceJob
import org.apache.hadoop.mapred.JobConf
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BPEMapReduceJobTest extends AnyFlatSpec with Matchers {
  "BPEMapReduceJob" should "configure the Hadoop job correctly" in {
    val inputPath = "testInput.txt"
    val outputPath = "testOutput"
    val shardSize = 64 * 1024 * 1024

    val conf: JobConf = BPEMapReduceJob.configureJob(inputPath, outputPath, shardSize)

    conf.get("mapreduce.input.fileinputformat.split.minsize") should equal(shardSize.toString)
    conf.get("mapreduce.input.fileinputformat.split.maxsize") should equal(shardSize.toString)
    conf.get("fs.defaultFS") should equal("file:///")
  }
}
