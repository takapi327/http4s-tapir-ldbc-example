
package infrastructure.mysql.custom

import cats.effect.IO

import ldbc.core.*
import ldbc.core.model.*
import ldbc.sql.*

trait CategoryCustom:

  enum Color(val code: Byte) extends Enum:
    case RED extends Color(1)
    case BLUE extends Color(2)
    case YELLOW extends Color(3)
  object Color extends EnumDataType[Color]
  given EnumDataType[Color] = Color

  given Conversion[TINYINT[Byte], DataType[Color]] =
    DataType.mapping[TINYINT[Byte], Color]

  given ResultSetReader[IO, Color] =
    ResultSetReader.mapping[IO, Byte, Color](n => Color.values.find(_.code == n).get)
