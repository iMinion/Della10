package com.Database;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;



public class Database {
	final String url = "jdbc:mysql://localhost/della";
	final String driver = "com.mysql.jdbc.Driver";
	final String uName = "root";
	final String pWord = "root";
	private java.sql.Connection con;
	private java.sql.Statement s;
	private Savepoint sp;
	
	
	public Database() {
		try {
			Class.forName(driver).newInstance();
			con = DriverManager.getConnection(url, uName, pWord);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
