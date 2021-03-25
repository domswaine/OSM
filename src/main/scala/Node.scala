import org.apache.sis.geometry.DirectPosition2D
import org.apache.sis.referencing.CRS
import org.opengis.referencing.crs.CoordinateReferenceSystem
import org.opengis.referencing.operation.{CoordinateOperation, MathTransform}

case class Node(id: Long, lat: Float, lon: Float) {
  val (easting: Int, northing: Int) = Node.converter(lat, lon)
  def distance(n: Int, e: Int): Float = Math.sqrt(Node.square(northing - n) + Node.square(easting - e)).toFloat
  def to_record: String = easting.toString + "," + northing.toString + "," + id.toString
}

case object Node {
  type CRS = CoordinateReferenceSystem
  val bng: CRS = CRS.forCode("EPSG:27700")
  val wgs84: CRS = CRS.forCode("EPSG:4326")

  def converter(a: Float, b: Float, source: CRS = Node.wgs84, target: CRS = Node.bng): (Int, Int) = {
    val operation: CoordinateOperation = CRS.findOperation(source, target, null)
    val transform: MathTransform = operation.getMathTransform
    val result = transform.transform(new DirectPosition2D(a, b), null).getCoordinate
    (Math.round(result(0)).toInt, Math.round(result(1)).toInt)
  }

  def square(n: Int): Int = n*n
}
