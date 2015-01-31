package com.ActionItem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import static com.MainController.Controller.db;

public class ActionItems implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2750687422355250894L;

	
	private String name;
	private String resolution;
	private String description;
	private int status;
	private Date creationDate;
	private Date dueDate;
	private String member;
	private String team;

	private static Hashtable<String, ActionItems> contents = new Hashtable<String, ActionItems>();
//	private static Hashtable<String, ActionItems> recontents = new Hashtable<String, ActionItems>();
	

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
	
	public void setCreationDate(Date date) {
		this.creationDate = date;
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
		int i = db.update(query);
		if(i == 0) {
			try {
				db.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		else {
			try {
				db.save();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			serializeActionItem();
			return true;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Hashtable<String, ActionItems> deSerialize() {
		try {
			FileInputStream file = new FileInputStream("actionItems.ser");
			ObjectInputStream in = new ObjectInputStream(file);
			Object obj = in.readObject();
			in.close();
			file.close();
			return (Hashtable<String, ActionItems>) obj;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void serializeActionItem() {
		try {
			FileOutputStream file = new FileOutputStream("actionItems.ser");
			ObjectOutputStream out = new ObjectOutputStream(file);
			String query = "select * from actionItems"; 
			ResultSet rs = db.query(query);
			ActionItems aItem = new ActionItems();
			contents.clear();
			while(rs.next()) {
				aItem.setName(rs.getString("name"));
				aItem.setDesc(rs.getString("description"));
				aItem.setStatus(Integer.parseInt(rs.getString("status")));
				aItem.setResolution(rs.getString("resolution"));
				aItem.setDueDate(rs.getDate("due"));
				aItem.setMember(rs.getString("member"));
				aItem.setTeam(rs.getString("team"));
				aItem.setCreationDate(rs.getDate("creation"));
				contents.put(aItem.getName(), aItem);
			}
			out.writeObject(contents);
			out.close();
			System.out.println(contents);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InsufficientCredentialsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean storeActionItem() {
		try {
			String query = "insert into actionitems (name, description, resolution, creation,"
					+ " due, status, member, team) values('" + name + "', '" + description + "', '" + resolution + "', '"+ 
					new java.sql.Date(new java.util.Date().getTime()) + "', '" +this.dueDate + "', '" + this.status + "', '" 
					+ this.member + "', '" + this.team + "')";
			int i = db.update(query);
			if(i == 0) {
				db.rollback();
				return false;
			}
			else {
				db.save();
				serializeActionItem();
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
	
	public String toString() {
		return "name = '" + this.name + "', description = '" + description + "', resolution= '" + resolution
				+ "', due = '" + this.dueDate + "', status = '" + this.status + "', member = '" + this.member + "', team ='" + this.team + "', creation = '" + this.creationDate;
	}
	
	public boolean updateActionItem(String name) {
		String query = "update actionitems set name = '" + this.name + "', description = '" + description + "', resolution= '" + resolution
						+ "', due = '" + this.dueDate + "', status = '" + this.status + "', member = '" + this.member + "', team ='" + this.team + "'"
						+ "where name = '" + name + "'";
		int i = db.update(query);
		if(i == 0) {
			try {
				db.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		else {
			try {
				db.save();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			serializeActionItem();
			return true;
		}
	}
}