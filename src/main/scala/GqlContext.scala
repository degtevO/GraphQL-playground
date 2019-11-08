import model.db.{AuthorDao, BookDao}

case class GqlContext(authorDao: AuthorDao.type, bookDao: BookDao.type)
