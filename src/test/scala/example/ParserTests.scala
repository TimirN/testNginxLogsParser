package example

import org.scalatest.FlatSpec

class ParserTests  extends FlatSpec {

  "Nginx logs parser" should "parse test log row" in {
    val testRow ="""127.0.0.1 - - [19/Jun/2012:09:16:22 +0100] "GET /GO.jpg HTTP/1.1" 499 0 "http://domain.com/htm_data/7/1206/758536.html" "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; SE 2.X MetaSr 1.0)""""
    val resSimple = NginxLogsParser.parse(testRow)
    assert(resSimple.isDefined)
    assert(resSimple.get.timeLocal == "19/Jun/2012:09:16:22 +0100")

    val resComples = NginxLogsParser.covert2Complex(resSimple.get)
    assert(resComples.isDefined)
    for {res <- resComples} {
      assert(res.httpUserAgent.os.family != "")
      assert(res.status == 499)
      assert(res.request.uri == "/GO.jpg")
    }
  }
  "Test date parser" should "parse dates" in {
    assert(NginxLogsParser.toISO8601UTC("11/Mar/2018:21:11:05 +0200") == "2018-03-11T19:11:05Z")
  }
  "Test invald row" should "produce none" in {
    val testRow ="""1 -  [19/Jun/2012:09:16:22 "GET /GO.jpg HTTP/1.1" 499 0 "http://domain.com/htm_data/7/1206/758536.html" "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; SE 2.X MetaSr 1.0)""""
    val resSimple = NginxLogsParser.parse(testRow)
    assert(resSimple.isEmpty)
  }
}
