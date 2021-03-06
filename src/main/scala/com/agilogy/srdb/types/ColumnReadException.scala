package com.agilogy.srdb.types

import java.sql.ResultSet

import scala.collection.immutable.IndexedSeq
import scala.util.Try

trait ColumnReadException extends RuntimeException {
  val columnName: String
  val availableColumns: Option[Seq[(String, Any)]]
}

case class ColumnReadExceptionWithCause(columnName: String, cause: Throwable, availableColumns: Option[Seq[(String, Any)]])
  extends RuntimeException(ColumnReadExceptionWithCause.getMessage(columnName, cause, availableColumns), cause) with ColumnReadException {

  override def getMessage: String =
    s"""
       |Exception reading column $columnName
       |  Cause message: ${cause.getMessage}""".stripMargin +
      availableColumns.map(_.mkString("\n  Available columns are ", ", ", "."))

  override def getCause: Throwable = cause
}

object ColumnReadExceptionWithCause {

  private[types] def getMessage(columnName: String, cause: Throwable, availableColumns: Option[Seq[(String, Any)]]): String =
    s"""
       |Exception reading column $columnName
       |  Cause message: ${cause.getMessage}""".stripMargin +
      availableColumns.map(_.mkString("\n  Available columns are ", ", ", "."))

  private[types] def apply(columnName: String, cause: Throwable, rs: ResultSet): ColumnReadExceptionWithCause =
    ColumnReadExceptionWithCause(columnName, cause, Utilities.getColumnNames(rs))
}

private[types] object Utilities {
  def getColumnNames(rs: ResultSet): Option[IndexedSeq[(String, AnyRef)]] = Try {
    val md = rs.getMetaData
    for {
      i <- 1.to(md.getColumnCount)
    } yield {
      val columnName = md.getColumnName(i)
      columnName -> rs.getObject(i)
    }
  }.toOption
}

case class NullColumnReadException(columnName: String, availableColumns: Option[Seq[(String, Any)]])
  extends RuntimeException(NullColumnReadException.getMessage(columnName, availableColumns)) with ColumnReadException {
}

object NullColumnReadException {

  private[types] def getMessage(columnName: String, availableColumns: Option[Seq[(String, Any)]]): String = {
    lazy val columnNamesMsg = availableColumns
      .map(_.mkString("Available columns are ", ", ", "."))
      .getOrElse("Available columns could not be determined.")
    s"Null found reading column supposedly not null column $columnName. $columnNamesMsg"
  }

  private[types] def apply(columnName: String, rs: ResultSet): NullColumnReadException = {
    NullColumnReadException(columnName, Utilities.getColumnNames(rs))
  }

}

