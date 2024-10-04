//
//import org.apache.hadoop.fs.Path
//import org.apache.hadoop.conf.*
//import org.apache.hadoop.io.*
//import org.apache.hadoop.util.*
//import org.apache.hadoop.mapred.*
//
//import java.io.IOException
//import java.util
//import scala.jdk.CollectionConverters.*
//import com.knuddels.jtokkit.Encodings
//import com.knuddels.jtokkit.api.{Encoding, EncodingRegistry, EncodingType, ModelType}
//
//object sampleBPE extends App {
//  private val registry: EncodingRegistry = Encodings.newDefaultEncodingRegistry()
//
//  private val enc = registry.getEncoding(EncodingType.P50K_BASE)
//  private val encoded = enc.encode("This is a sample sentence.")
//  private val decoded = enc.decode(encoded)
//
//  println(s"Encoded Tokens: ${encoded}")
//  println(s"Decoded Text: $decoded")
//
//
//  //  private val enc2 = registry.getEncodingForModel(ModelType.GPT_4)
////  val secondEnc = registry.getEncodingForModel(ModelType.TEXT_EMBEDDING_ADA_002) // decoded = "This is a sample sentence."
////  private val encoded2 = enc2.encode("This is a sample sentence.")
////  println(s"Encoded Tokens: ${encoded2}")
//}
