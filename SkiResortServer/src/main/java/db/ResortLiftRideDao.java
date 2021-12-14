package db;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import model.LiftRideDetail;

public class ResortLiftRideDao {
  private static BasicDataSource dataSource;

  public ResortLiftRideDao() {
    dataSource = DBCPResortDataSource.getDataSource();
  }

  public void createLiftRide(LiftRideDetail newLiftRide) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    String insertQueryStatement = "INSERT INTO LiftRides (skierId, resortId, season, day, time, liftId) " +
            "VALUES (?,?,?,?,?,?)";
    try {
      conn = dataSource.getConnection();
      preparedStatement = conn.prepareStatement(insertQueryStatement);
      preparedStatement.setInt(1, newLiftRide.getSkierId());
      preparedStatement.setInt(2, newLiftRide.getResortId());
      preparedStatement.setInt(3, newLiftRide.getSeason());
      preparedStatement.setInt(4, newLiftRide.getDay());
      preparedStatement.setInt(5, newLiftRide.getTime());
      preparedStatement.setInt(6, newLiftRide.getLiftId());

      // execute insert SQL statement
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
        if (preparedStatement != null) {
          preparedStatement.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
  }

  public int getUniqueSkiers(int resortId, int season, int day) {
    String getQuery = "SELECT COUNT(distinct skierId) AS count FROM LiftRides WHERE " +
            "resortId=? AND season=? AND day=?;";
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      connection = dataSource.getConnection();
      preparedStatement = connection.prepareStatement(getQuery);
      preparedStatement.setInt(1, resortId);
      preparedStatement.setInt(2, season);
      preparedStatement.setInt(3, day);
      System.out.println(preparedStatement.toString());
      resultSet = preparedStatement.executeQuery();
//      printResultSet(resultSet);
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
    try {
      if(resultSet.next()){
        try {
          return resultSet.getInt(1);
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    } catch (SQLException e) {
      return 0;
    }
    return 0;
  }


  private void printResultSet(ResultSet resultSet) throws SQLException {
    ResultSetMetaData rsmd = resultSet.getMetaData();
    int columnsNumber = rsmd.getColumnCount();
    while (resultSet.next()) {
      for (int i = 1; i <= columnsNumber; i++) {
        if (i > 1) System.out.print(",  ");
        String columnValue = resultSet.getString(i);
        System.out.print(columnValue + " " + rsmd.getColumnName(i));
      }
      System.out.println("");
    }
  }

  public static void main(String[] args) {
    ResortLiftRideDao liftRideDao = new ResortLiftRideDao();
    int count = liftRideDao.getUniqueSkiers(1, 1993, 1);
    System.out.println("count: " + count);
    liftRideDao.createLiftRide(new LiftRideDetail(10, 2, 3, 5, 500, 20));
  }
}