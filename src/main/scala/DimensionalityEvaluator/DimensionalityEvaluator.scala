package DimensionalityEvaluator

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.slf4j.LoggerFactory

import java.io._
import scala.io.Source

object DimensionalityEvaluator {
  private val logger = LoggerFactory.getLogger(getClass)

  // Test different embedding dimensions and log results
  def determineOptimalDimensions(inputPath: String, outputPath: String): Unit = {
    logger.info("Evaluating Dimensions, Analogy Accuracy and Similarity Score")
    val dimensionList = List(50, 100, 150, 200) // Example dimension sizes to experiment with

    val analogyPairs = loadAnalogyPairs(s"$inputPath/google-analogies.csv")
    val similarityPairs = loadSimLex999(s"$inputPath/simlex999.csv")


    val resultsWriter = new BufferedWriter(new FileWriter("dimension_evaluation.csv"))
    resultsWriter.write("Dimensions,Analogy Accuracy,Similarity Score\n")

    val word2Vec = WordVectorSerializer.readWord2VecModel(new File(s"$outputPath/word2vec_model.bin"))

    dimensionList.foreach { dim =>
      // Analogy task evaluation
//      val analogyAccuracy = analogyPairs.count { case (a, b, c, d) =>
//        logger.info(s"$a, $b, $c, $d")
//        val aVec: INDArray = Nd4j.create(word2Vec.getWordVector(a)) // Convert to INDArray
//        val bVec: INDArray = Nd4j.create(word2Vec.getWordVector(b)) // Convert to INDArray
//        val cVec: INDArray = Nd4j.create(word2Vec.getWordVector(c)) // Convert to INDArray
//
//        if (aVec != null && bVec != null && cVec != null) {
//          val resultVector: INDArray = aVec.sub(bVec).add(cVec) // Vector arithmetic
//          val predicted = word2Vec.wordsNearest(resultVector, 1) // Use the resulting vector
//          predicted.contains(d)
//        } else {
//          false // If any word vector is not found, return false
//        }
//      }.toDouble / analogyPairs.size

      val analogyAccuracy = analogyPairs.count { case (a, b, c, d) =>
        logger.info(s"Processing analogy: $a, $b, $c, $d")

        val aVecOption = Option(word2Vec.getWordVector(a))
        val bVecOption = Option(word2Vec.getWordVector(b))
        val cVecOption = Option(word2Vec.getWordVector(c))

        (aVecOption, bVecOption, cVecOption) match {
          case (Some(aVec), Some(bVec), Some(cVec)) =>
            val aNdArray = Nd4j.create(aVec)
            val bNdArray = Nd4j.create(bVec)
            val cNdArray = Nd4j.create(cVec)

            // Log the shapes of the vectors to ensure consistency
            logger.info(s"Shape of aVec: ${aNdArray.shape().mkString(",")}")
            logger.info(s"Shape of bVec: ${bNdArray.shape().mkString(",")}")
            logger.info(s"Shape of cVec: ${cNdArray.shape().mkString(",")}")

            val resultVector: INDArray = aNdArray.sub(bNdArray).add(cNdArray)
            // Log the shape of resultVector
            logger.info(s"Shape of resultVector before reshaping: ${resultVector.shape().mkString(",")}")

            val predicted = word2Vec.wordsNearest(resultVector, 1)
            predicted.contains(d)

          case _ =>
            logger.warn(s"One or more word vectors were not found for analogy: $a, $b, $c, $d")
            false
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

//  private def loadAnalogyPairs(filePath: String): List[(String, String, String, String)] = {
//    val source = Source.fromFile(filePath)
//    try {
//      val lines = source.getLines().toList
//      lines.filterNot(_.startsWith(":")).map { line =>
//        val Array(a, b, c, d) = line.split(" ")
//        (a, b, c, d)
//      }
//    } finally {
//      source.close() // Ensure the source is closed
//    }
//  }

//  private def loadAnalogyPairs(filePath: String): List[(String, String, String, String)] = {
//    val source = Source.fromFile(filePath)
//    try {
//      val lines = source.getLines().toList
//      lines.filterNot(_.startsWith(":")).flatMap { line =>
//        // Split by comma instead of space
//        val parts = line.split(",")
//        if (parts.length == 4) {
//          Some((parts(0).trim, parts(1).trim, parts(2).trim, parts(3).trim)) // Trim whitespace
//        } else {
//          logger.warn(s"Skipping line with unexpected format: $line")
//          None // Skip lines that don't have exactly 4 parts
//        }
//      }
//    } finally {
//      source.close() // Ensure the source is closed
//    }
//  }

//  private def loadAnalogyPairs(filePath: String): List[(String, String, String, String)] = {
//    val source = Source.fromFile(filePath)
//    try {
//      val lines = source.getLines().toList
//      lines.filterNot(_.startsWith(":")).flatMap { line =>
//        // Split by comma and trim whitespace
//        val parts = line.split(",").map(_.trim)
//        parts.length match {
//          case 6 => Some((parts(2), parts(3), parts(4), parts(5))) // Extracting relevant words
//          case _ =>
//            logger.warn(s"Skipping line with unexpected format: $line") // Log unexpected formats
//            None // Skip lines that don't have the expected number of parts
//        }
//      }
//    } finally {
//      source.close() // Ensure the source is closed
//    }
//  }

  private def loadAnalogyPairs(filePath: String): List[(String, String, String, String)] = {
    val source = Source.fromFile(filePath)
    try {
      val lines = source.getLines().drop(1).toList // Skip header

      lines.flatMap { line =>
        // Split by comma and trim whitespace
        val parts = line.split(",").map(_.trim)
        logger.info(s"Line: $line")
        logger.info(s"Parts: ${parts.mkString("Array(", ", ", ")")}")
        logger.info("------------------")
        parts.length match {
          case 6 => Some((parts(2), parts(3), parts(4), parts(5))) // Extracting word1, word2, word3, target
          case _ =>
            logger.warn(s"Skipping line with unexpected format: $line") // Log unexpected formats
            None // Skip lines that don't have the expected number of parts
        }
      }
    } finally {
      source.close() // Ensure the source is closed
    }
  }

  private def loadSimLex999(filePath: String): Seq[(String, String, Double)] = {
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
