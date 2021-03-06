package com.agilogy.srdb.test

import java.sql.{ Statement, ResultSet, PreparedStatement, Connection }

import com.agilogy.srdb.Srdb._
import com.agilogy.srdb.types._
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec

case class Name(v: String) extends AnyVal
case class Age(v: Int) extends AnyVal
case class Person(name: Name, age: Age)

class SrdbCoreIntegrationTest extends FlatSpec with MockFactory {

  val ps = mock[PreparedStatement]
  val rs = mock[ResultSet]

  val conn = mock[Connection]
  implicit val nameDbType: ColumnType[Name] = ColumnType[String].xmap[Name](Name.apply, _.v)
  implicit val ageDbType: ColumnType[Age] = ColumnType[Int].xmap[Age](Age.apply, _.v)
  implicit val personReader: NamedDbReader[Person] = reader(notNull[Name]("name"), notNull[Age]("age")).map[Person]((Person.apply _).tupled)

  it should "be able to use simple column readers and arguments in srdb.core select" in {
    val sql = "select name,dept from people where name = ? and age > ?"
    val selectPeopleByName = select(sql)(_.toSeq[Person])
    inSequence {
      (conn.prepareStatement(_: String, _: Int)).expects(sql, Statement.NO_GENERATED_KEYS).returning(ps)
      (ps.setString(_: Int, _: String)).expects(1, "Jordi")
      (ps.setInt(_: Int, _: Int)).expects(2, 18)
      (ps.executeQuery _).expects().returning(rs)
      (rs.next _).expects().returning(true)
      (rs.getString(_: String)).expects("name").returning("Jordi")
      (rs.wasNull _).expects().returning(false)
      (rs.getInt(_: String)).expects("age").returning(37)
      (rs.wasNull _).expects().returning(false)
      (rs.next _).expects().returning(false)
      (rs.close _).expects()
      (ps.close _).expects()
    }
    val res = selectPeopleByName(conn, Name("Jordi"), Age(18))
    assert(res === List(Person(Name("Jordi"), Age(37))))

  }

}
