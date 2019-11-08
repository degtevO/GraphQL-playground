package model.db

import model.Book

object BookDao {
  var idCounter = 1
  var db = Seq(
    Book(1, Seq(1), "Programming in Scala"),
    Book(2, Seq(1), "Programming in Scala. SE"),
    Book(3, Seq(2), "Scala for impatient")
  )

  def getAll = db

  def getByIds(ids: Seq[Long]) = db.filter(book => ids.contains(book.id))

  def getByAuthorIds(ids: Seq[Long]) = db.filter(_.authorId.exists(aid => ids.contains(aid)))
}
