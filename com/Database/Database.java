package com.Database;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;

public class Database {
	private final String url = "jdbc:mysql://10.10.11.53/della";
	private final String driver = "com.mysql.jdbc.Driver";
	private final String uName = "iminion";
	private final String pWord = "root";
	private java.sql.Connection con;
	private java.sql.Statement s;
	private Savepoint sp;
	
	
	public Database() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Class.forName(driver).newInstance();
		con = DriverManager.getConnection(url, uName, pWord);
		System.out.println("hello");
	}
	
	public boolean isDBReachable() throws SQLException {
			return con.isValid(10);
	}
	
	public ResultSet query(String message) {
		ResultSet rs;
		try {
			s = con.createStatement();
			rs = s.executeQuery(message);
			return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public int update(String updateQuery) {
		try {
			s = con.createStatement();
			return s.executeUpdate(updateQuery);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public void save() throws SQLException {
		sp = con.setSavepoint();
	}
	
	public void rollback() throws SQLException {
		if(sp != null)
		con.rollback(sp);
	}
}
