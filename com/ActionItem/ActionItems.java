package com.ActionItem;

import java.sql.Date;
import java.sql.SQLException;

import com.Database.Database;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

//import com.mysql.jdbc.Connection;

public class ActionItems {
	private Database dbActions;
	
	private String name;
	private String resolution;
	private String description;
	private int status;
	private Date creationDate;
	private Date dueDate;
	private String member;
	private String team;
	
	
	public ActionItems() {
		dbActions = new Database();
	}
	
	public void setName(String name) throws InsufficientCredentialsException {
		if(name.trim().length() == 0) {
			throw new InsufficientCredentialsException("Please enter all the details");
		}
		else this.name = name;
	}
	
	public void setDesc(String description) throws InsufficientCredentialsException {
		if(description.trim().length() == 0) {
			throw new InsufficientCredentialsException("Please enter all the details");
		}
		else this.description = description;
	}
	
	public void setResolution(String resolution) throws InsufficientCredentialsException {
		if(resolution.trim().length() == 0) {
			throw new InsufficientCredentialsException("Please enter all the details");
		}
		else this.resolution = resolution;
	}
	
	public void setDueDate(java.util.Date date) throws InsufficientCredentialsException {
		java.util.Date d = new java.util.Date();
		System.out.println(date);
		if(date.before(d)) {
			throw new InsufficientCredentialsException();
		}
		else {
			this.dueDate = new Date(date.getTime());
		}
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public void setMember(String member) {
		this.member = member;
	}
	
	public void setTeam(String team) {
		this.team = team;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public String getResolution() {
		return this.resolution;
	}

	public Date getCreationDate() {
		return this.creationDate;
	}
	
	public Date getDueDate() {
		return this.dueDate;
	}
	
	public int getStatus() {
		return this.status;
	}
	
	public String getMember() {
		return this.member;
	}
	
	public String getTeam() {
		return this.team;
	}
	
	
	public boolean delete(String name) {
		String query = "delete from actionitems where name = '" + name + "'";
		int i = dbActions.update(query);
		if(i == 0) {
			try {
				dbActions.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		else {
			try {
				dbActions.save();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		
	}
	
	public boolean storeActionItem() {
		System.out.println("creating a new item");
		
		
		try {
			String query = "insert into actionitems (name, description, resolution, creation,"
					+ " due, status, member, team) values('" + name + "', '" + description + "', '" + resolution + "', '"+ 
					new java.sql.Date(new java.util.Date().getTime()) + "', '" +this.dueDate + "', '" + this.status + "', '" 
					+ this.member + "', '" + this.team + "')";
			int i = dbActions.update(query);
			if(i == 0) {
				dbActions.rollback();
				return false;
			}
			else {
				dbActions.save();
				return true;
			}
			
		} catch(MySQLIntegrityConstraintViolationException ex) {
			System.out.println("Duplicate values are forbidden");
			return false;
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean updateActionItem(String name) {
		String query = "update actionitems set name = '" + this.name + "', description = '" + description + "', resolution= '" + resolution
						+ "', due = '" + this.dueDate + "', status = '" + this.status + "', member = '" + this.member + "', team ='" + this.team + "'"
						+ "where name = '" + name + "'";
		System.out.println(query);
		int i = dbActions.update(query);
		System.out.println(i);
		if(i == 0) {
			try {
				dbActions.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		else {
			try {
				dbActions.save();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		
	}
}