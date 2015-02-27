package com.agilogy.srdb.test

import java.sql._
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec

class AtomicDbTypesNotNullsTest extends FlatSpec with MockFactory {

  val ps = mock[PreparedStatement]
  val rs = mock[ResultSet]
  val db = new DummyDb(ps, rs)

  import com.agilogy.srdb.types._

  behavior of "not null ColumnType implicit conversions"
  
  it should "prepare statements with a Byte param and read resultsets with a Byte column" in {
    inSequence {
      (ps.setByte(_, _)).expects(1, 3.toByte)
      (rs.getByte(_: Int)).expects(1).returning(3.toByte)
      (rs.wasNull _).expects().returning(false)
      (rs.getByte(_: String)).expects("c").returning(3.toByte)
      (rs.wasNull _).expects().returning(false)
    }
    db.prepare(3.toByte)
    assert(db.read(notNull[Byte]) === 3.toByte)
    assert(db.read(notNull[Byte]("c")) === 3.toByte)
  }

  it should "prepare statements with a Short param and read resultsets with a Short column" in {
    inSequence {
      (ps.setShort(_, _)).expects(1, 3.toShort)
      (rs.getShort(_: Int)).expects(1).returning(3.toShort)
      (rs.wasNull _).expects().returning(false)
      (rs.getShort(_: String)).expects("c").returning(3.toShort)
      (rs.wasNull _).expects().returning(false)
    }
    db.prepare(3.toShort)
    assert(db.read(notNull[Short]) === 3.toShort)
    assert(db.read(notNull[Short]("c")) === 3.toShort)
  }

  it should "execute selects with an Int param" in {
    inSequence {
      (ps.setInt(_, _)).expects(1, 3)
      (rs.getInt(_: Int)).expects(1).returning(3)
      (rs.wasNull _).expects().returning(false)
      (rs.getInt(_: String)).expects("c").returning(3)
      (rs.wasNull _).expects().returning(false)
    }
    db.prepare(3)
    assert(db.read(notNull[Int]) === 3)
    assert(db.read(notNull[Int]("c")) === 3)
  }

  it should "prepare statements with a Long param and read resultsets with a Long column" in {
    inSequence {
      (ps.setLong(_, _)).expects(1, 3l)
      (rs.getLong(_: Int)).expects(1).returning(3l)
      (rs.wasNull _).expects().returning(false)
      (rs.getLong(_: String)).expects("c").returning(3l)
      (rs.wasNull _).expects().returning(false)
    }
    db.prepare(3l)
    assert(db.read(notNull[Long]) === 3l)
    assert(db.read(notNull[Long]("c")) === 3l)
  }

  it should "prepare statements with a Float param and read resultsets with a Float column" in {
    inSequence {
      (ps.setFloat _).expects(1, 3.0f)
      (rs.getFloat(_: Int)).expects(1).returning(3.0f)
      (rs.wasNull _).expects().returning(false)
      (rs.getFloat(_: String)).expects("c").returning(3.0f)
      (rs.wasNull _).expects().returning(false)
    }
    db.prepare(3.0f)
    assert(db.read(notNull[Float]) === 3.0f)
    assert(db.read(notNull[Float]("c")) === 3.0f)
  }

  it should "prepare statements with a Double param and read resultsets with a Double column" in {
    inSequence {
      (ps.setDouble _).expects(1, 3.0)
      (rs.getDouble(_: Int)).expects(1).returning(3.0)
      (rs.wasNull _).expects().returning(false)
      (rs.getDouble(_: String)).expects("c").returning(3.0)
      (rs.wasNull _).expects().returning(false)
    }
    db.prepare(3.0)
    assert(db.read(notNull[Double]) === 3.0)
    assert(db.read(notNull[Double]("c")) === 3.0)
  }

  it should "prepare statements with a String param and read resultsets with a String column" in {
    inSequence {
      (ps.setString _).expects(1, "hi!")
      (rs.getString(_: Int)).expects(1).returning("hi!")
      (rs.wasNull _).expects().returning(false)
      (rs.getString(_: String)).expects("c").returning("hi!")
      (rs.wasNull _).expects().returning(false)
    }
    db.prepare("hi!")
    assert(db.read(notNull[String]) === "hi!")
    assert(db.read(notNull[String]("c")) === "hi!")
  }

  it should "prepare statements with a Boolean param and read resultsets with a Boolean column" in {
    inSequence {
      (ps.setBoolean _).expects(1, false)
      (rs.getBoolean(_: Int)).expects(1).returning(false)
      (rs.wasNull _).expects().returning(false)
      (rs.getBoolean(_: String)).expects("c").returning(false)
      (rs.wasNull _).expects().returning(false)
    }
    db.prepare(false)
    assert(db.read(notNull[Boolean]) === false)
    assert(db.read(notNull[Boolean]("c")) === false)
  }

  it should "prepare statements with a java.util.Date param and read resultsets with a java.util.Date column" in {
    val d = new java.util.Date(123456l)
    val sqlDate = new java.sql.Date(d.getTime)
    inSequence {
      (ps.setDate(_: Int, _: java.sql.Date)).expects(1, sqlDate)
      (rs.getDate(_: Int)).expects(1).returning(sqlDate)
      (rs.wasNull _).expects().returning(false)
      (rs.getDate(_: String)).expects("c").returning(sqlDate)
      (rs.wasNull _).expects().returning(false)
    }
    db.prepare(d)
    assert(db.read(notNull[java.util.Date]) === d)
    assert(db.read(notNull[java.util.Date]("c")) === d)
  }

  it should "prepare statements with a BigDecimal param and read resultsets with a BigDecimal column" in {
    val value = BigDecimal("2.0")
    val javaBd = value.bigDecimal
    inSequence {
      (ps.setBigDecimal(_: Int, _: java.math.BigDecimal)).expects(1, javaBd)
      (rs.getBigDecimal(_: Int)).expects(1).returning(javaBd)
      (rs.wasNull _).expects().returning(false)
      (rs.getBigDecimal(_: String)).expects("c").returning(javaBd)
      (rs.wasNull _).expects().returning(false)
    }
    db.prepare(value)
    assert(db.read(notNull[BigDecimal]) === value)
    assert(db.read(notNull[BigDecimal]("c")) === value)
  }
  
  it should "throw if it reads a null value" in{
    inSequence {
      (rs.getString(_: Int)).expects(1).returning(null)
      (rs.wasNull _).expects().returning(true)
      (rs.getString(_: String)).expects("c").returning(null)
      (rs.wasNull _).expects().returning(true)
    }
    intercept[NullColumnReadException](db.read(notNull[String]))
    intercept[NullColumnReadException](db.read(notNull[String]("c")))
  }

}