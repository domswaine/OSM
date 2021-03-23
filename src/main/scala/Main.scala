import Iterators.bz2_url_to_iterator
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

object Main {

  val resource_url: String = "https://download.geofabrik.de/europe/great-britain/england/west-sussex-latest.osm.bz2"

  case class OsmNode(id: Long, lat: Double, lon: Double){}
  case class OsmWay(id: Long, nodes: List[Long]){}

  @scala.annotation.tailrec
  def get_data(succeedingTagUtility: SucceedingTagUtility): Unit = {
    succeedingTagUtility.get_succeeding_tag() match {
      case Some(line) if line.startsWith("<way") =>
        println(decode_way_encoding(line))
        get_data(succeedingTagUtility)
      case Some(line) if line.startsWith("<node") =>
        println(decode_node_encoding(line))
        get_data(succeedingTagUtility)
      case None => ()
    }
  }

  def decode_node_encoding(node_encoding: String): OsmNode = {
    val node = Jsoup.parseBodyFragment(node_encoding).select("node")
    OsmNode(
      node.attr("id").toLong,
      node.attr("lat").toDouble,
      node.attr("lon").toDouble
    )
  }

  def decode_way_encoding(way_encoding: String): OsmWay = {
    val way = Jsoup.parseBodyFragment(way_encoding).select("way")
    OsmWay(
      way.attr("id").toLong,
      way.select("nd").toArray.toList.map(_.asInstanceOf[Element].attr("ref").toLong)
    )
  }

  def main(args: Array[String]): Unit = {
    get_data(new SucceedingTagUtility(bz2_url_to_iterator(resource_url), Set("node", "way")))
  }

}