package BPE

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.nd4j.linalg.api.ndarray.INDArray
import org.slf4j.LoggerFactory
import org.nd4j.linalg.factory.Nd4j
import java.io._
import java.io.{BufferedWriter, File, FileWriter}
import scala.io.Source

object DimensionalityEvaluator {
  private val logger = LoggerFactory.getLogger(this.getClass)


  // Test different embedding dimensions and log results


  def determineOptimalDimensions(): Unit = {
    logger.info("Evaluating Dimensions, Analogy Accuracy and Similarity Score")
    val dimensionList = List(50, 100, 150, 200) // Example dimension sizes to experiment with

    val analogyPairs = loadAnalogyPairs("/Users/taabish10s/IdeaProjects/CS441_Fall2024/src/main/resources/input/google-analogies.csv")
    val similarityPairs = loadSimLex999("/Users/taabish10s/IdeaProjects/CS441_Fall2024/src/main/resources/input/simlex999.csv")

    val resultsWriter = new BufferedWriter(new FileWriter("dimension_evaluation.csv"))
    resultsWriter.write("Dimensions,Analogy Accuracy,Similarity Score\n")

    val word2Vec = WordVectorSerializer.readWord2VecModel(new File("word2vec_model.bin"))

    dimensionList.foreach { dim =>
      // Analogy task evaluation
      val analogyAccuracy = analogyPairs.count { case (a, b, c, d) =>
        val aVec: INDArray = Nd4j.create(word2Vec.getWordVector(a)) // Convert to INDArray
        val bVec: INDArray = Nd4j.create(word2Vec.getWordVector(b)) // Convert to INDArray
        val cVec: INDArray = Nd4j.create(word2Vec.getWordVector(c)) // Convert to INDArray

        if (aVec != null && bVec != null && cVec != null) {
          val resultVector: INDArray = aVec.sub(bVec).add(cVec) // Vector arithmetic
          val predicted = word2Vec.wordsNearest(resultVector, 1) // Use the resulting vector
          predicted.contains(d)
        } else {
          false // If any word vector is not found, return false
        }
      }.toDouble / analogyPairs.size

      // Word similarity task evaluation
      val similarityScore = similarityPairs.map { case (word1, word2, humanScore) =>
        val modelSimilarity = word2Vec.similarity(word1, word2)
        Math.abs(modelSimilarity - humanScore) // Calculate difference from human ratings
      }.sum / similarityPairs.size

      // Write results for this dimension size
      resultsWriter.write(s"$dim,$analogyAccuracy,$similarityScore\n")
    }

    resultsWriter.close()
  }



  private def loadAnalogyPairs(filePath: String): List[(String, String, String, String)] = {
    val source = Source.fromFile(filePath)
    try {
      val lines = source.getLines().toList
      lines.filterNot(_.startsWith(":")).map { line =>
        val Array(a, b, c, d) = line.split(" ")
        (a, b, c, d)
      }
    } finally {
      source.close() // Ensure the source is closed
    }
  }

  private def loadSimLex999(filePath: String): List[(String, String, Double)] = {
    val source = Source.fromFile(filePath)
    try {
      val lines = source.getLines().toList.drop(1) // Skip header
      lines.flatMap { line =>
        val parts = line.split("\t") // Split by tab
        if (parts.length >= 3) {
          Some((parts(0), parts(1), parts(2).toDouble)) // Only take the first 3 parts
        } else {
          None // Skip lines that don't have enough parts
        }
      }
    } finally {
      source.close() // Ensure the source is closed
    }
  }
}
