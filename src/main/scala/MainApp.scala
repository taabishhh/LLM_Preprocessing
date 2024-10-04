import BPE.BPEMapReduceJob

object MainApp {

  def main(args: Array[String]): Unit = {
//    println(args(0))
//    println(args(1))

    if (args.length < 2) {
      println("Usage: BPEMapReduceJob <input path> <output path>")
      System.exit(-1)
    }

    val inputPath = args(1)
    val outputPath = args(2)

    // Run the MapReduce job
    BPEMapReduceJob.runJob(inputPath, outputPath)
  }
}
