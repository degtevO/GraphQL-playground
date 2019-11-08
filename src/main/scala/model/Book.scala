package model

case class Book(id: Long, authorId: Seq[Long], title: String)