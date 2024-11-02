# CS441_Fall2024
Class repository for CS441 on Cloud Computing taught at the University of Illinois, Chicago in Fall, 2024 <br>
Name: Taabish Sutriwala
Email: tsutr@uic.edu
UIN: 673379837

---

# Homework 1

## Overview
This project implements a Byte Pair Encoding (BPE) tokenization approach along with a Word2Vec model to generate word embeddings from a text corpus. The implementation leverages Apache Hadoop for distributed processing and includes evaluation metrics for optimal dimensionality of embeddings.

## Project Structure
The project is structured as follows:
- **MainApp.scala**: The entry point of the application, orchestrating the execution of the BPE tokenization, Word2Vec training, and evaluation of embeddings.
- **BPEEncoding.scala**: Contains functions for encoding text using BPE.
- **BPEMapReduceJob.scala**: Manages the MapReduce job configuration and execution for BPE tokenization.
- **BPETokenMapper.scala**: Implements the mapper for the MapReduce job to perform tokenization.
- **BPETokenReducer.scala**: Implements the reducer to aggregate token counts and save vocabulary statistics.
- **DimensionalityEvaluator.scala**: Evaluates different dimensions of embeddings based on analogy and similarity tasks.
- **Word2VecEmbedding.scala**: Manages the training of the Word2Vec model and saving similar words to a CSV file.

## Requirements
- Apache Hadoop
- Scala (version 3.5.0)
- Java 11

## Scala and SBT Version
- **Scala Version:** 3.5.0
- **SBT Version:** 0.1.0-SNAPSHOT

## Dependencies
The project requires the following dependencies:

- **Hadoop:**
  - [hadoop-common](https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-common) (3.3.4)
  - [hadoop-mapreduce-client-core](https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-mapreduce-client-core) (3.3.4)
  - [hadoop-mapreduce-client-jobclient](https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-mapreduce-client-jobclient) (3.3.4)

- **Jtokkit:**
  - [jtokkit](https://mvnrepository.com/artifact/com.knuddels/jtokkit) (1.1.0)

- **Deeplearning4j:**
  - [deeplearning4j-core](https://mvnrepository.com/artifact/org.deeplearning4j/deeplearning4j-core) (1.0.0-M2.1)
  - [deeplearning4j-nlp](https://mvnrepository.com/artifact/org.deeplearning4j/deeplearning4j-nlp) (1.0.0-M2.1)

- **ND4J:**
  - [nd4j-native-platform](https://mvnrepository.com/artifact/org.nd4j/nd4j-native-platform) (1.0.0-M2.1)

- **Logging:**
  - [logback-classic](https://mvnrepository.com/artifact/ch.qos.logback/logback-classic) (1.5.6)
  - [slf4j-api](https://mvnrepository.com/artifact/org.slf4j/slf4j-api) (2.0.12)

- **Testing:**
  - [ScalaTest](https://mvnrepository.com/artifact/org.scalatest/scalatest) (3.2.19) for testing
  - [ScalaTest Plus Mockito](https://mvnrepository.com/artifact/org.scalatestplus/mockito-5-12) (3.2.19.0) for testing

## Setup Instructions
1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd <repository-directory>
   ```

2. **Build the Project**: 
   Ensure that you have all necessary dependencies specified in your `build.sbt` or equivalent file. Use the following command to build the project:
   ```bash
   sbt package
   ```

3. **Run the Program**:
   Execute the `MainApp` with the following command, replacing `<input-path>` and `<output-path>` with your respective paths:
   Eg. hadoop jar target/scala-3.5.0/HW1.jar MainApp src/resources/input src/resources/output
   ```bash
   hadoop jar target/scala-3.5.0/<your-jar-name>.jar MainApp <input-path> <output-path>
   ```
   - **Input Path**: Path to the text corpus for BPE tokenization.
   - **Output Path**: Path where results will be stored.

5. **View Results**:
   After execution, the output will include:
   - **Vocabulary Statistics**: Available in `vocabulary_stats.csv` within the output directory.
   - **Similar Words**: Found in `similar_words.csv`.
   - **Dimensionality Evaluation**: Results logged in `dimension_evaluation.csv`.

## Results and Evaluation
The results of the program execution will provide insights into:
- **Token Frequencies**: An aggregated view of token occurrences in the input text.
- **Similar Words**: A list of semantically similar words for each word in the vocabulary.
- **Dimensionality Analysis**: Evaluation metrics including analogy accuracy and similarity scores based on different embedding dimensions (e.g., 50, 100, 150, 200).

### Link to Video Demonstration
[Link to Video](<insert-video-link-here>)

## Limitations
- The performance of the Word2Vec model is sensitive to hyperparameters such as window size and minimum word frequency. The choice of parameters in this implementation may not be optimal for all datasets.
- The input corpus should be pre-processed adequately to remove noise and irrelevant characters for better results.
- The current implementation does not handle out-of-vocabulary (OOV) words in the analogy evaluation.

## Conclusion
This project showcases an efficient approach to text tokenization and word embedding generation using BPE and Word2Vec. The use of Hadoop enables scalable processing of large text corpora. Further optimizations and explorations can be conducted to enhance the model's performance based on the specific application requirements.
