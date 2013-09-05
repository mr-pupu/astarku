package de.janmatuscheck;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * <p>See
 * <a href="http://JanMatuschek.de/LatitudeLongitudeBoundingCoordinates#Java">
 * http://JanMatuschek.de/LatitudeLongitudeBoundingCoordinates#Java</a>
 * for the GeoLocation class referenced from this code.</p>
 *
 * @author Jan Philip Matuschek
 * @version 26 May 2010
 */
public class GeoLocationDemo {

	/**
	 * @param radius radius of the sphere.
	 * @param location center of the query circle.
	 * @param distance radius of the query circle.
	 * @param connection an SQL connection.
	 * @return places within the specified distance from location.
	 */
	public static ResultSet findPlacesWithinDistance(
			double radius, GeoLocation location, double distance,
			Connection connection) throws SQLException {

		GeoLocation[] boundingCoordinates =
			location.boundingCoordinates(distance, radius);
		boolean meridian180WithinDistance =
			boundingCoordinates[0].getLongitudeInRadians() >
			boundingCoordinates[1].getLongitudeInRadians();
			
//		java.sql.PreparedStatement statement = connection.prepareStatement(
//			"SELECT * FROM NODE WHERE (LATITUDE >= ? AND LATITUDE <= ?) AND (LONGITUDE >= ? " +
//			(meridian180WithinDistance ? "OR" : "AND") + " LONGITUDE <= ?) AND " +
//			"acos(sin(?) * sin(LATITUDE) + cos(?) * cos(LATITUDE) * cos(LONGITUDE - ?)) <= ?");
//		statement.setDouble(1, boundingCoordinates[0].getLatitudeInRadians());
//		statement.setDouble(2, boundingCoordinates[1].getLatitudeInRadians());
//		statement.setDouble(3, boundingCoordinates[0].getLongitudeInRadians());
//		statement.setDouble(4, boundingCoordinates[1].getLongitudeInRadians());
//		statement.setDouble(5, location.getLatitudeInRadians());
//		statement.setDouble(6, location.getLatitudeInRadians());
//		statement.setDouble(7, location.getLongitudeInRadians());
//		statement.setDouble(8, distance / radius);
			
			PreparedStatement statement = connection.prepareStatement("select * from node");
		return statement.executeQuery();
	} 

	public static void main(String[] args) {

		double earthRadius = 6371.01;
		GeoLocation myLocation = GeoLocation.fromDegrees(-7.22035, 112.6483894);
		double distance = 1000;
//		java.sql.Connection connection = ...;
//
//		java.sql.ResultSet resultSet = findPlacesWithinDistance(
//				earthRadius, myLocation, distance, connection);
//
//		...;
		Connection c = null;
		
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager
					.getConnection("jdbc:sqlite:data/schema/astarDB-1");
			c.setAutoCommit(false);
			System.out.println("Open database successfully");
			
			ResultSet resultSet = findPlacesWithinDistance(
					earthRadius, myLocation, distance, c);
			System.out.println(resultSet.getRow());
			while (resultSet.next()) {
				System.out.println(resultSet.getString(1));
				
			}
		}catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

}
