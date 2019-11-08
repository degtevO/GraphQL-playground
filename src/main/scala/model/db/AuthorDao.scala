package model.db

import model.Author

object AuthorDao {
  var idCounter = 2
  var db = Seq(
    Author(1, "Martin", "Odersky"),
    Author(2, "Cay", "Horstmann")
  )

  def getAll = db

  def getByIds(ids: Seq[Long]) = db.filter(author => ids.contains(author.id))

  def create(author: Author) = {
    idCounter += 1
    author.copy(id = idCounter)
    db :+= author
    author
  }
}
