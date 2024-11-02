package BPE

import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.{EncodingRegistry, IntArrayList, ModelType}
import org.slf4j.{Logger, LoggerFactory}

object BPEEncoding {

  private val registry: EncodingRegistry = Encodings.newDefaultEncodingRegistry()
  private val enc = registry.getEncodingForModel(ModelType.GPT_4)
  private val logger: Logger = LoggerFactory.getLogger(getClass)

  def encode(line: String): IntArrayList = {
    logger.debug(s"Encoding text: $line")

    try {
      val encoded = enc.encode(line)
      logger.debug(s"Encoded text: ${encoded.toArray.mkString(", ")}")
      encoded
    } catch {
      case e: Exception =>
        logger.error(s"Error during encoding: ${e.getMessage}", e)
        new IntArrayList()
    }
  }

  def toList(encoded: IntArrayList): List[Int] = {
    (0 until encoded.size()).map(encoded.get).toList
  }
}
