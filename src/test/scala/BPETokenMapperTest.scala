import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import BPE.BPETokenMapper
import org.apache.hadoop.io.{IntWritable, LongWritable, Text}
import org.apache.hadoop.mapred.OutputCollector
import org.mockito.Mockito.*
import org.mockito.ArgumentCaptor

class BPETokenMapperTest extends AnyFlatSpec with Matchers {
  

  "BPETokenMapper" should "process a line and output token counts" in {
    val mapper = new BPETokenMapper
    val mockOutputCollector = mock(classOf[OutputCollector[Text, IntWritable]])
    val line = "hello world"
    val key = new LongWritable(1)
//    val value = new Text(line)

    mapper.map(key, line, mockOutputCollector, null)

    // Capture output values
    val wordCaptor = ArgumentCaptor.forClass(classOf[Text])
    val countCaptor = ArgumentCaptor.forClass(classOf[IntWritable])

    verify(mockOutputCollector, atLeastOnce()).collect(wordCaptor.capture(), countCaptor.capture())

    val outputWords = wordCaptor.getAllValues
    outputWords should contain(new Text("hello"))
    outputWords should contain(new Text("world"))

    val outputCounts = countCaptor.getAllValues
    outputCounts.forEach(count => count.get shouldBe 1)
  }
}
