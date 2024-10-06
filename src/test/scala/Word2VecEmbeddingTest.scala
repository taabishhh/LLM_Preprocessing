import BPE.Word2VecEmbedding
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class Word2VecEmbeddingTest extends AnyFlatSpec with Matchers {
  "Word2VecEmbedding" should "train Word2Vec model correctly" in {
    val corpusPath = "/Users/taabish10s/IdeaProjects/CS441_Fall2024/src/main/resources/input/data.txt" // Make sure to create this test file with content
    val outputPath = "word2vec_model.bin"
    val embeddingDim = 100

    val word2Vec = Word2VecEmbedding.trainWord2Vec(corpusPath, outputPath, embeddingDim)

    word2Vec should not be null // Ensure model is trained
    // You can also verify if the output model file was created
    // Check the actual content of the model if needed
  }
}
