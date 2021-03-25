import scala.collection.mutable.ListBuffer

case class Way(id: Long){
  var nodes: ListBuffer[Long] = ListBuffer.empty[Long]
  var isHighway: Boolean = false
  var name: String = ""
  def addNode(n: Long): Unit = nodes.addOne(n)
  def setIsHighway(): Unit = isHighway = true
  def setName(n: String): Unit = name = n
}
