package BPE

import org.deeplearning4j.models.word2vec.Word2Vec
import org.deeplearning4j.models.word2vec.Word2Vec.Builder
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory
import scala.jdk.CollectionConverters._
import java.io.{BufferedWriter, FileWriter, File}
import org.slf4j.LoggerFactory

object Word2VecEmbedding {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def trainWord2Vec(corpusPath: String, outputPath: String, embeddingDim: Int): Word2Vec = {
    // Load text data from the corpus
    val file = new File(corpusPath)
    val sentenceIterator = new LineSentenceIterator(file)

    // Tokenizer configuration
    logger.info("Tokenizer configured...")
    val tokenizerFactory = new DefaultTokenizerFactory()

    // Build the Word2Vec model
    logger.info("Building the Word2Vec model...")
    val word2Vec: Word2Vec = new Word2Vec.Builder()
      .minWordFrequency(5)
      .iterations(10)
      .layerSize(embeddingDim)
      .seed(42)
      .windowSize(5)
      .iterate(sentenceIterator)
      .tokenizerFactory(tokenizerFactory)
      .build()

    // Train the model
    logger.info("Training the model...")
    word2Vec.fit()

    // Save the model for later use
    logger.info("Saving the model...")
    WordVectorSerializer.writeWord2VecModel(word2Vec, new File(outputPath))

    // Save semantically close tokens for each word
    logger.info("Saving the csv...")
    writeSimilarWords(word2Vec, outputPath)

    word2Vec
  }

  // Write semantically close tokens to a CSV file
  private def writeSimilarWords(word2Vec: Word2Vec, outputPath: String): Unit = {
    val writer = new FileWriter(outputPath + "/similar_words.csv")
    writer.write("Word,Similar Words\n")

    val vocabWords = word2Vec.getVocab.words()
    for (word <- vocabWords.asScala) {
      val similarWords = word2Vec.wordsNearest(word, 10).toArray.mkString(", ")
      writer.write(s"$word,$similarWords\n")
    }

    writer.close()
  }

  // Get the embedding for a specific word
  def getEmbedding(word2Vec: Word2Vec, word: String): Option[Array[Double]] = {
    Option(word2Vec.getWordVector(word))
  }

  private def getNearestNeighbors(word2Vec: Word2Vec, word: String, topN: Int = 10): List[(String, Double)] = {
    val neighbors = word2Vec.wordsNearest(word, topN).asScala.toList
    neighbors.map { neighbor =>
      (neighbor, word2Vec.similarity(word, neighbor))
    }
  }

  def outputNearestNeighbors(word2Vec: Word2Vec, outputPath: String): Unit = {
    val writer = new BufferedWriter(new FileWriter(outputPath))
    writer.write("Word,Neighbor,Similarity\n")

    val vocab = word2Vec.getVocab.words().asScala
    vocab.foreach { word =>
      val neighbors = getNearestNeighbors(word2Vec, word)
      neighbors.foreach { case (neighbor, similarity) =>
        writer.write(s"$word,$neighbor,$similarity\n")
      }
    }

    writer.close()
  }
}
