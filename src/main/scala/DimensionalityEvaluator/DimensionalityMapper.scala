package DimensionalityEvaluator

import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapred.{MapReduceBase, Mapper, OutputCollector, Reporter}
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.nd4j.linalg.factory.Nd4j
import org.slf4j.LoggerFactory
import config.ConfigLoader

import java.io.{File, IOException}

class DimensionalityMapper extends MapReduceBase with Mapper[LongWritable, Text, Text, Text] {
  private val word2Vec = WordVectorSerializer.readWord2VecModel(new File(s"${ConfigLoader.outputPath}/output.txt/word2vec_model.bin"))
  private val dimensionKey = new Text()
  private val resultValue = new Text()
  private val logger = LoggerFactory.getLogger(getClass)

  @throws[IOException]
  override def map(key: LongWritable, value: Text, output: OutputCollector[Text, Text], reporter: Reporter): Unit = {
    val line = value.toString
    val parts = line.split(",")

    if (parts.length == 4) {
      try {
        val dim = parts(0).toInt
        val (a, b, c, d) = (parts(1), parts(2), parts(3), parts(4))

        val aVec = word2Vec.getWordVector(a)
        val bVec = word2Vec.getWordVector(b)
        val cVec = word2Vec.getWordVector(c)

        if (aVec != null && bVec != null && cVec != null) {
          val resultVector = Nd4j.create(aVec).sub(Nd4j.create(bVec)).add(Nd4j.create(cVec))
          val predicted = word2Vec.wordsNearest(resultVector, 1)
          val analogyAccuracy = if (predicted.contains(d)) 1 else 0

          dimensionKey.set(dim.toString)
          resultValue.set(analogyAccuracy.toString)
          output.collect(dimensionKey, resultValue)
        }
      } catch {
        case e: Exception => logger.error(s"Error during analogy processing: ${e.getMessage}", e)
      }
    } else {
      logger.warn(s"Skipping malformed line: $line")
    }
  }
}
