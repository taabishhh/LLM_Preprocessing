import BPE.{BPEMapReduceJob, Word2VecEmbedding}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._

class MainAppTest extends AnyFlatSpec with Matchers with MockitoSugar {
  "MainApp" should "run without errors" in {
    val inputPath = "/Users/taabish10s/IdeaProjects/CS441_Fall2024/src/main/resources/input/data.txt"
    val outputPath = "output.txt"

    // Mocking the methods that interact with external resources
    val bpeJobMock = mock[BPEMapReduceJob.type]
    val word2VecMock = mock[Word2VecEmbedding.type]

    // Simulate job running
    when(word2VecMock.trainWord2Vec(inputPath, "word2vec_model.bin", 100)).thenReturn(null)

    // Call main method
    MainApp.main(Array("", inputPath, outputPath))

    // Verify interactions
    verify(bpeJobMock).runJob(inputPath, outputPath, 64 * 1024 * 1024)
    verify(word2VecMock).trainWord2Vec(inputPath, "word2vec_model.bin", 100)
  }
}
