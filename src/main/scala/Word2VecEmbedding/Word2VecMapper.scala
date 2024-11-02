package Word2VecEmbedding

import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapred.{MapReduceBase, Mapper, OutputCollector, Reporter}
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.word2vec.Word2Vec
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory
import org.slf4j.LoggerFactory

import java.io.{File, IOException}

class Word2VecMapper extends MapReduceBase with Mapper[LongWritable, Text, Text, Text] {
  private val logger = LoggerFactory.getLogger(classOf[Word2VecMapper])

  @throws[IOException]
  override def map(key: LongWritable, value: Text, output: OutputCollector[Text, Text], reporter: Reporter): Unit = {
    val file = new File(value.toString)
    val sentenceIterator = new LineSentenceIterator(file)

    logger.info("Reading and tokenizing sentences...")
    val tokenizerFactory = new DefaultTokenizerFactory()

    // Train Word2Vec on the input split
    val word2Vec: Word2Vec = new Word2Vec.Builder()
      .minWordFrequency(1)
      .iterations(10)
      .layerSize(10)
      .seed(42)
      .windowSize(5)
      .iterate(sentenceIterator)
      .tokenizerFactory(tokenizerFactory)
      .build()

    logger.info("Training Word2Vec model...")
    word2Vec.fit()

    // Serialize the trained Word2Vec model
    val tempFile = File.createTempFile("word2vec_model_", ".bin")
    WordVectorSerializer.writeWord2VecModel(word2Vec, tempFile)

    // Emit the model file path as output key-value pair
    output.collect(new Text("model"), new Text(tempFile.getAbsolutePath))
  }
}
