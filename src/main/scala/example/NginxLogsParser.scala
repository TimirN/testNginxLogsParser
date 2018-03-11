package example

import scala.util.matching.Regex
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.TimeZone

import org.uaparser.scala.{Client, Parser}



/*
log_format compression '$remote_addr - $remote_user [$time_local] '
                       '"$request" $status $bytes_sent '
                       '"$http_referer" "$http_user_agent"'
93.180.71.3 - - [17/May/2015:08:05:32 +0000] "GET /downloads/product_1 HTTP/1.1" 304 0 "-" "Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)"
 */
case class NginxLogSimpleRow(
                       remoteAddr: String,
                       remoteUser: String,
                       timeLocal :String,
                       request: String,
                       status: Int,
                       bytesSent: Int,
                       httpReferer: String,
                       httpUserAgent: String
                     )

case class Request(raw: String, method: String, uri: String, httpv: String)

case class NginxLogComplexRow(
                               remoteAddr: String,
                               remoteUser: String,
                               utcTime :String,
                               request: Request,
                               status: Int,
                               bytesSent: Int,
                               httpReferer: String,
                               httpUserAgent: Client
                             )

object NginxLogsParser extends Serializable {

  val rx: Regex = "^(\\d+.\\d+.\\d+.\\d+)\\s{1}-\\s{1}(\\S+)\\s{1}\\[(.+?)\\]\\s{1}\"(.+?)\"\\s{1}(\\d+)\\s{1}(\\d+)\\s{1}\"(.*?)\"\\s{1}\"(.*)\"$".r

  def parse(row: String): Option[NginxLogSimpleRow] = {
    rx.findFirstMatchIn(row) match {
      case Some(rx(remoteAddr, remoteUser, timeLocal, request, status, bytesSent, httpReferer, httpUserAgent)) =>
        Some(NginxLogSimpleRow(remoteAddr, remoteUser, timeLocal, request, status.toInt, bytesSent.toInt, httpReferer, httpUserAgent))
      case _ =>
        println(s"invalid row: $row")
        None
    }
  }

  def toISO8601UTC(date: String): String = {
    val inputFormat = new java.text.SimpleDateFormat("dd/MMM/yyyy:hh:mm:ss Z")
    val tz = TimeZone.getTimeZone("UTC")
    val outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    outputFormat.setTimeZone(tz)
    outputFormat.format(inputFormat.parse(date))
  }

  def parseRequest(request: String): Request = {
    val splitedRequest = request.split(" ")
    val method = splitedRequest(0)
    val uri = splitedRequest(1)
    val httpv = splitedRequest(2)
    Request(request, method, uri, httpv)
  }

  def covert2Complex(simpleRow: NginxLogSimpleRow): Option[NginxLogComplexRow] = {
    try {
      Some(
        NginxLogComplexRow(
          simpleRow.remoteAddr,
          simpleRow.remoteUser,
          toISO8601UTC(simpleRow.timeLocal),
          parseRequest(simpleRow.request),
          simpleRow.status,
          simpleRow.bytesSent,
          simpleRow.httpReferer,
          Parser.default.parse(simpleRow.httpUserAgent)
        )
      )
    } catch {
      case e: Exception =>
        println(s"invalid row: $simpleRow")
        None
    }
  }

}