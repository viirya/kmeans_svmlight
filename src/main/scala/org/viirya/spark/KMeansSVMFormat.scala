
package org.viirya.spark

import org.apache.log4j.Logger
import org.apache.log4j.Level
import scopt.OptionParser

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
 
object KMeansSVMFormat {

  object InitializationMode extends Enumeration {
    type InitializationMode = Value
    val Random, Parallel = Value
  }

  import InitializationMode._

  case class Params(
      input: String = null,
      k: Int = -1,
      numIterations: Int = 10,
      numDimensions: Int = -1,
      initializationMode: InitializationMode = Parallel)

  def main(args: Array[String]) {
    val defaultParams = Params()
 
    val parser = new OptionParser[Params]("KMeansSVMFormat") {
      head("KMeansSVMFormat: an example k-means app for dense data in svmlight format.")
      opt[Int]('k', "k")
        .required()
        .text(s"number of clusters, required")
        .action((x, c) => c.copy(k = x))
      opt[Int]("numIterations")
        .text(s"number of iterations, default; ${defaultParams.numIterations}")
        .action((x, c) => c.copy(numIterations = x))
      opt[Int]("numDimensions")
        .text(s"number of feature dimentions, required")
        .action((x, c) => c.copy(numDimensions = x))
      opt[String]("initMode")
        .text(s"initialization mode (${InitializationMode.values.mkString(",")}), " +
        s"default: ${defaultParams.initializationMode}")
        .action((x, c) => c.copy(initializationMode = InitializationMode.withName(x)))
      arg[String]("<input>")
        .text("input paths to examples")
        .required()
        .action((x, c) => c.copy(input = x))
    }

    parser.parse(args, defaultParams).map { params =>
      run(params)
    }.getOrElse {
      sys.exit(1)
    }
  }

  def run(params: Params) {
    val conf = new SparkConf().setAppName(s"KMeansSVMFormat with $params")
    val sc = new SparkContext(conf)

    Logger.getRootLogger.setLevel(Level.WARN)

    val numDims = params.numDimensions

    val examples = sc.textFile(params.input).map { line =>
      val pairs = line.split(' ').flatMap { n =>
        val p = n.split(":")
        if (p.length == 2) {
          Seq((p(0).toInt, p(1).toDouble))
        } else {
          Seq()
        }
      }
      val indices = pairs.map(_._1)
      val values = pairs.map(_._2)
      Vectors.sparse(numDims, indices, values)
    }.cache()

    val numExamples = examples.count()

    println(s"numExamples = $numExamples.")

    val initMode = params.initializationMode match {
      case Random => KMeans.RANDOM
      case Parallel => KMeans.K_MEANS_PARALLEL
    }

    val model = new KMeans()
      .setInitializationMode(initMode)
      .setK(params.k)
      .setMaxIterations(params.numIterations)
      .run(examples)

    val cost = model.computeCost(examples)

    println(s"Total cost = $cost.")

    sc.stop()
  }
}
