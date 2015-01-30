package com.ActionItem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Date;
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
	private static Hashtable<String, ActionItems> recontents = new Hashtable<String, ActionItems>();
	
	@SuppressWarnings("unchecked")
	public Hashtable<String, ActionItems> deSerialize() {
		try {
			FileInputStream file = new FileInputStream("actionItem.ser");
			ObjectInputStream in = new ObjectInputStream(file);
			Object obj = in.readObject();
			recontents = (Hashtable<String, ActionItems>) obj;
			in.close();
			return recontents;
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
			return true;
		}
		
	}
	
	public void Serialize() {
		try {
			FileOutputStream file = new FileOutputStream("actionItems.ser");
			ObjectOutputStream out = new ObjectOutputStream(file);
			contents.put(this.name, this);
			out.writeObject(contents);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public boolean storeActionItem() {
		System.out.println("creating a new item");
		
		
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
		int i = db.update(query);
		System.out.println(i);
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
			return true;
		}
		
	}
}