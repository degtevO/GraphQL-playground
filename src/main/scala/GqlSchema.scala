import model.{Author, Book}
import sangria.execution.deferred._
import sangria.macros.derive._
import sangria.schema._

import scala.concurrent.Future


object GqlSchema {
  val byAuthor = Relation[Book, Long]("authorId",_.authorId)

  val authorFetcher = Fetcher(
    (ctx: GqlContext, ids: Seq[Long]) => Future.successful(ctx.authorDao.getByIds(ids))
  )(HasId(_.id))

  val bookFetcher = Fetcher.rel(
    (ctx: GqlContext, ids: Seq[Long]) => Future.successful(ctx.bookDao.getByIds(ids)),
    (ctx: GqlContext, ids: RelationIds[Book]) => Future.successful(ctx.bookDao.getByAuthorIds(ids(byAuthor)))
  )(HasId(_.id))

  val resolver = DeferredResolver.fetchers(authorFetcher, bookFetcher)

  val AuthorType = deriveObjectType[Unit, Author](
    AddFields(
      Field("books", ListType(BookType), resolve = c => bookFetcher.deferRelSeq(byAuthor, c.value.id))
    )
  )

  val BookType: ObjectType[Unit, Book] = deriveObjectType[Unit, Book](
    ReplaceField("authorId",
      Field("author", ListType(AuthorType), resolve = c => authorFetcher.deferSeq(c.value.authorId))
    )
  )

  val QueryType = ObjectType(
    "Query",
    fields[GqlContext, Unit](
      Field(
        "Authors",
        ListType(AuthorType),
        arguments = List(Argument("id", OptionInputType(ListInputType(LongType)))),
        resolve = c => c.argOpt[Seq[Long]]("id") match {
          case None => c.ctx.authorDao.getAll
          case Some(ids) => authorFetcher.deferSeq(ids)
        }
      ),
      Field(
        "Books",
        ListType(BookType),
        arguments = List(Argument("id", OptionInputType(ListInputType(LongType)))),
        resolve = c => c.argOpt[Seq[Long]]("id") match {
          case None => c.ctx.bookDao.getAll
          case Some(ids) => bookFetcher.deferSeq(ids)
        }
      )
    )
  )

  val schema = Schema(QueryType, None)
}
