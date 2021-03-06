package com.agilogy.srdb.test

import java.sql.{ ResultSet, PreparedStatement }

import com.agilogy.srdb.types
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec

class DbTypesMapTest extends FlatSpec with MockFactory {

  import types._

  val ps = mock[PreparedStatement]
  val rs = mock[ResultSet]

  behavior of "column types xmap"

  case class Name(v: String) {
    require(v != null)
  }

  it should "create a new column type mapping over a function" in {
    implicit val nameColumnType = ColumnType[String].xmap[Name](Name.apply, _.v)
    inSequence {
      (ps.setString _).expects(1, "Jane")
      (ps.setNull(_: Int, _: Int)).expects(2, JdbcType.Varchar.code)
      (rs.getString(_: Int)).expects(1).returning("John")
      (rs.wasNull _).expects().returning(false)
      (rs.getString(_: Int)).expects(2).returning(null)
      (rs.wasNull _).expects().returning(true)
    }
    set(ps, (Name("Jane"), Option.empty[Name]))
    assert(get[(Name, Option[Name])](rs) === (Name("John") -> None))
    //TODO: Why the next line won't compile?
    //assert(get[(Name, Option[Name])](rs)(reader2(reader1[Name], optionalReader[Name])) === (Name("John") -> None))
  }

  behavior of "named db types xmap"

  it should "create a named dbtype from a mapped column type" in {
    implicit val nameColumnType = ColumnType[String].xmap[Name](Name.apply, _.v)
    inSequence {
      (ps.setString _).expects(1, "Jane")
      (rs.getString(_: String)).expects("name").returning("John")
      (rs.wasNull _).expects().returning(false)
    }
    set(ps, Name("Jane"))
    assert(get(rs)(notNull[Name]("name")) === Name("John"))
  }

  behavior of "combined db types xmap"

  case class Person(name: String, age: Int)

  it should "create a new compound db reads mapping over a function" in {
    implicit val personDbReader = reader[String, Int].map[Person](Person.tupled)
    inSequence {
      (rs.getString(_: Int)).expects(1).returning("John")
      (rs.wasNull _).expects().returning(false)
      (rs.getInt(_: Int)).expects(2).returning(23)
      (rs.wasNull _).expects().returning(false)
    }
    assert(get[Person](rs) === Person("John", 23))
  }

  it should "create compound db readers from compound db readers" in {
    implicit val personDbReader = reader[String, Int].map[Person](Person.tupled)
    inSequence {
      (rs.getInt(_: Int)).expects(1).returning(42)
      (rs.wasNull _).expects().returning(false)
      (rs.getString(_: Int)).expects(2).returning("John")
      (rs.wasNull _).expects().returning(false)
      (rs.getInt(_: Int)).expects(3).returning(23)
      (rs.wasNull _).expects().returning(false)
    }
    assert(get[(Int, Person)](rs) === (42 -> Person("John", 23)))
  }

  behavior of "combined named db readers map"

  it should "create a new reader mapping over a function" in {
    implicit val personReader = reader(notNull[String]("name"), notNull[Int]("age")).map[Person]((Person.apply _).tupled)
    inSequence {
      (rs.getString(_: String)).expects("name").returning("John")
      (rs.wasNull _).expects().returning(false)
      (rs.getInt(_: String)).expects("age").returning(23)
      (rs.wasNull _).expects().returning(false)
    }
    assert(get(rs)(personReader) === Person("John", 23))
    //    assert(get(rs)(reader(notNull[Int]("number"), personReader)) === (42 -> Person("John", 23)))
  }

}
