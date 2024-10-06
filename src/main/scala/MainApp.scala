import BPE.{BPEMapReduceJob, Word2VecEmbedding, DimensionalityEvaluator}
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import java.io.File
import org.slf4j.LoggerFactory

object MainApp {
  private val logger = LoggerFactory.getLogger(this.getClass)
  
  def main(args: Array[String]): Unit = {
    logger.info("Starting MainApp...")
    
    if (args.length < 2) {
      logger.error("Insufficient arguments provided.")
      sys.exit(1)
    }
    logger.debug(s"Received arguments: ${args.mkString(", ")}")

    val inputPath = s"${args(1)}/data.csv"
    val outputPath = args(2)
//    val corpusPath = args(1) // Path to text corpus for Word2Vec
    val shardSize = 64 * 1024 * 1024

    // Run the MapReduce job
    logger.info("Running BPE MapReduce job...")
    BPEMapReduceJob.runJob(inputPath, outputPath, shardSize)

    // Train Word2Vec embeddings
    logger.info("Training Word2Vec model...")
    Word2VecEmbedding.trainWord2Vec(inputPath, "word2vec_model.bin", embeddingDim = 100)

    // Load the trained Word2Vec model
    val word2Vec = WordVectorSerializer.readWord2VecModel(new File("word2vec_model.bin"))

    // Example: get an embedding for a word
    val exampleWord = "hello" // Replace with your word
    logger.info("Evaluating embeddings...")
    val embedding = Word2VecEmbedding.getEmbedding(word2Vec, exampleWord)

    // Print the embedding
    embedding match {
      case Some(vec) =>
        logger.info(s"Embedding for '$exampleWord': ${vec.mkString(", ")}")
      case None =>
        logger.info("Word not in the vocabulary!")
    }

    // Perform dimensionality evaluation
    DimensionalityEvaluator.determineOptimalDimensions()

    logger.info("MainApp finished.")
  }
}
