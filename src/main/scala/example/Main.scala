package example

import org.apache.spark.sql.SparkSession
import org.rogach.scallop._

class ParseArgs(arguments: Seq[String]) extends ScallopConf(arguments) {
  val logsPath: ScallopOption[String]  = opt[String](required = true, noshort = true, name="logs-path")
  val saveTo: ScallopOption[String]  = opt[String](required = true, noshort = true, name="save-to")
  val outputType: ScallopOption[Boolean] = toggle(noshort = true, name = "format-complex", default = Some(false))
  verify()
}

object Main {

  def main(rawArgs: Array[String]): Unit = {

    val args = new ParseArgs(rawArgs)

    val logsPath = args.logsPath()
    val savePath = args.saveTo()
    val isComplex = args.outputType()


    val spark = SparkSession.builder()
      .appName("nginxLogsToJson").getOrCreate()

    val parsedLogs = spark.sparkContext.textFile(logsPath).flatMap(row => NginxLogsParser.parse(row))

    import spark.implicits._

    if (isComplex) {
      val cl = parsedLogs.mapPartitions(p => p.flatMap(r => NginxLogsParser.covert2Complex(r)))
      cl.toDF().toJSON
        .mapPartitions(vals => Iterator("[" + vals.mkString(",") + "]"))
        .write
        .text(savePath)

    } else {
      parsedLogs.toDF().toJSON
        .mapPartitions(vals => Iterator("[" + vals.mkString(",") + "]"))
        .write
        .text(savePath)
    }
  }

}

