import BPE.BPEMapReduceJob
import DimensionalityEvaluator.DimensionalityMapReduceJob
import Word2VecEmbedding.Word2VecMapReduceJob
import config.ConfigLoader

import org.slf4j.LoggerFactory


object MainApp {
  private val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    logger.info("Starting MainApp...")

    logger.debug(s"Received arguments: ${args.mkString(", ")}")

    val inputPath = args(0)
//    val corpusPath = s"${args(0)}/data-1.csv"
    val corpusPath = s"${args(0)}/data.txt"
    val outputPath = args(1)

    // Override the paths in the config with the command-line arguments
    ConfigLoader.overridePaths(inputPath, outputPath, corpusPath)

    // Print the final configuration
    ConfigLoader.printConfig()

    // Run the MapReduce jobs
    logger.info("Running BPE MapReduce job...")
    BPEMapReduceJob.runJob()

    // Train Word2Vec embeddings MapReduce job
    logger.info("Running Word2Vec MapReduce job...")
    Word2VecMapReduceJob.runJob()//(corpusPath, outputPath)

    // Load the trained Word2Vec model
//    val word2Vec = WordVectorSerializer.readWord2VecModel(new File(s"${outputPath}/output.word2vec_model.bin"))

    // Perform dimensionality evaluation
//    DimensionalityEvaluator.determineOptimalDimensions(inputPath, outputPath)

    // Run Dimensionality Evaluation MapReduce job
    logger.info("Running Dimensionality Evaluation job...")
//    DimensionalityMapReduceJob.runJob()

    logger.info("MainApp finished.")
  }
}
