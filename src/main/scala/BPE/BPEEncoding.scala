package BPE

import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.EncodingRegistry
import com.knuddels.jtokkit.api.ModelType
import com.knuddels.jtokkit.api.IntArrayList

object BPEEncoding {

  private val registry: EncodingRegistry = Encodings.newDefaultEncodingRegistry()
  private val enc = registry.getEncodingForModel(ModelType.GPT_4)

  def encode(line: String): IntArrayList = {
    enc.encode(line)
  }

  def toList(encoded: IntArrayList): List[Int] = {
    (0 until encoded.size()).map(encoded.get).toList
  }
}
