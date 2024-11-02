package Word2VecEmbedding

import org.apache.hadoop.io.{Text, IntWritable}
import org.apache.hadoop.mapred.{MapReduceBase, OutputCollector, Reducer, Reporter}
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.word2vec.Word2Vec
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable
import org.deeplearning4j.models.word2vec.VocabWord
import org.slf4j.LoggerFactory

import java.io.{File, IOException, FileWriter}
import scala.jdk.CollectionConverters._

class Word2VecReducer extends MapReduceBase with Reducer[Text, Text, Text, Text] {
  private val logger = LoggerFactory.getLogger(classOf[Word2VecReducer])

  @throws[IOException]
  override def reduce(key: Text, values: java.util.Iterator[Text], output: OutputCollector[Text, Text], reporter: Reporter): Unit = {
    logger.info("Merging Word2Vec models from mappers...")

    var mergedWord2Vec: Word2Vec = null
    var vocabMerged = false

    while (values.hasNext) {
      val modelPath = values.next().toString
      val modelFile = new File(modelPath)
      val word2VecModel = WordVectorSerializer.readWord2VecModel(modelFile)

      if (mergedWord2Vec == null) {
        mergedWord2Vec = word2VecModel
      } else {
        mergeWord2VecModels(mergedWord2Vec, word2VecModel)
        vocabMerged = true
      }
    }

    if (vocabMerged) {
      val outputPath = "./final_output"  // Use appropriate output path here
      logger.info("Saving the final merged Word2Vec model...")
      WordVectorSerializer.writeWord2VecModel(mergedWord2Vec, new File(s"$outputPath/final_word2vec_model.bin"))

      logger.info("Saving similar words to CSV...")
      writeSimilarWords(mergedWord2Vec, outputPath)

      output.collect(new Text("Final Word2Vec model saved"), new Text(outputPath))
    } else {
      logger.error("No models to merge.")
    }
  }

  private def mergeWord2VecModels(baseModel: Word2Vec, newModel: Word2Vec): Unit = {
    val baseLookupTable = baseModel.lookupTable.asInstanceOf[InMemoryLookupTable[VocabWord]]
    val newLookupTable = newModel.lookupTable.asInstanceOf[InMemoryLookupTable[VocabWord]]

    for (word <- newModel.getVocab.words().asScala) {
      if (baseModel.getVocab.containsWord(word)) {
        val baseVec = baseLookupTable.vector(word)
        val newVec = newLookupTable.vector(word)

        // Use the length of the INDArray instead of indices
        for (i <- 0 until baseVec.length().toInt) {
          baseVec.putScalar(i, (baseVec.getDouble(i.toLong) + newVec.getDouble(i.toLong)) / 2)
        }
      } else {
        // If word is not in baseModel, add it with its vector from newModel
        baseLookupTable.putVector(word, newLookupTable.vector(word))
      }
    }
  }

  private def writeSimilarWords(word2Vec: Word2Vec, outputPath: String): Unit = {
    val writer = new FileWriter(s"$outputPath/similar_words.csv")
    writer.write("Word,Similar Words\n")

    val vocabWords = word2Vec.getVocab.words()
    for (word <- vocabWords.asScala) {
      val similarWords = word2Vec.wordsNearest(word, 10).toArray.mkString(", ")
      writer.write(s"$word,$similarWords\n")
    }
    writer.close()
  }
}
