package com

import java.sql.{Connection, DriverManager}

/* DDL:
  CREATE TABLE weather(
    id INT AUTO_INCREMENT PRIMARY KEY,
    high INT,
    low INT,
    weather VARCHAR(255),
    channel VARCHAR(255),
    time DATETIME,d
    days_out INT
   );
*/

object MysqlUtil {
  val url = sys.env.getOrElse("DB_URL", "jdbc:mysql://localhost:3306")
  val username = sys.env.getOrElse("DB_USER", "")
  val password = sys.env.getOrElse("DB_PASSWORD", "")
  println(s"Initializing DB connection with $url")

  def getConnection: Connection = {
    DriverManager.getConnection(url, username, password)
  }

  def runQuery(query: String): Unit = {
    val connection = getConnection
    val statement = connection.createStatement()
    statement.execute(query)
  }

  def closeConnection(): Unit = {
    val connection = getConnection
    if (connection != null && !connection.isClosed) {
      connection.close()
    }
  }
}