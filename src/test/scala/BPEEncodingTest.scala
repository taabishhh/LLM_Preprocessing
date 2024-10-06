import BPE.BPEEncoding
import com.knuddels.jtokkit.api.IntArrayList
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BPEEncodingTest extends AnyFlatSpec with Matchers {

  "BPEEncoding" should "encode text correctly" in {
    val inputText = "hello"
    val encoded: IntArrayList = BPEEncoding.encode(inputText)

    encoded.size() should be > 0 // Ensure something is encoded
    // Additional checks can be added based on the expected output
  }

  it should "return an empty list when encoding fails" in {
    // Simulate a failure in encoding
    // This will depend on how you want to trigger a failure (e.g., with an invalid model type)
    // You might need to mock the behavior of the encoding library if needed
    val encoded: IntArrayList = BPEEncoding.encode("") // Assuming empty string triggers error
    encoded.size() should be(0)
  }
}
