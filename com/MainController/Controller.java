package com.MainController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import com.ActionItem.ActionItems;
import com.ActionItem.InsufficientCredentialsException;
import com.Database.Database;
import com.Members.Members;
import com.Team.Team;

public class Controller {
	@FXML
	private ComboBox<String> actionItemList;
	@FXML
	private TextField actionItemName;
	@FXML
	private TextArea actionItemDescription;
	@FXML
	private TextArea actionItemResolution;
	@FXML
	private Text actionItemCreationDate;
	@FXML
	private DatePicker actionItemDueDate;
	@FXML
	private ComboBox<String> actionItemMember;
	@FXML
	private ComboBox<String> actionItemStatus;
	@FXML
	private ComboBox<String> actionItemTeam;
	@FXML
	private ListView<String> consoleActionItems;
	@FXML
	private TextField consoleItemName;
	@FXML
	private TextArea consoleItemDesc;
	@FXML
	private TextArea consoleItemResolution;
	@FXML
	private Text consoleCreationDate;
	@FXML
	private Text consoleDueDate;
	@FXML
	private Text consoleStatus;
	@FXML
	private Text consoleMember;
	@FXML
	private Text consoleTeam;
	
	@FXML
	private TextField membersNewName;
	@FXML
	private ListView<String> membersKnown;
	@FXML
	private ListView<String> membersTeamsAvailable;
	@FXML
	private ListView<String> membersTeamsFor;
	
	@FXML
	private TextField teamNewName;
	@FXML
	private ListView<String> teamsKnown;
	@FXML
	private ListView<String> teamsMembersAvailable;
	@FXML
	private ListView<String> teamsMembersFor;
	
	
	private ActionItems actionItem = new ActionItems();
	private Members members = new Members();
	private Team teams = new Team();
	private Database db;
	private String selectedName;
	
	public void addAffiliation() {
		String member = membersKnown.getSelectionModel().getSelectedItem();
		String team = membersTeamsAvailable.getSelectionModel().getSelectedItem();
		String query = "insert into memberTeam (membername,teamname) value ('" + member + "', '" + team + "')";
		int i = db.update(query);
		if(i != 0) {
			membersTeamsAvailable.getItems().remove(team);
			membersTeamsFor.getItems().add(team);
		}
		
	}
	
	public void addAssociation() {
		String team = teamsKnown.getSelectionModel().getSelectedItem();
		String member = teamsMembersAvailable.getSelectionModel().getSelectedItem();
		String query = "insert into memberTeam (membername,teamname) value ('" + member + "', '" + team + "')";
		int i = db.update(query);
		if(i != 0) {
			teamsMembersAvailable.getItems().remove(member);
			teamsMembersFor.getItems().add(member);
		}
	}
	
	
	public void addMember() throws InsufficientCredentialsException {
		String name = membersNewName.getText();
		if(members.addMember(name) != 0) {
			membersNewName.setText("");
			membersKnown.getItems().add(name);
			actionItemMember.getItems().add(name);
		}
	}
	
	public void addTeam() throws InsufficientCredentialsException {
		String name = teamNewName.getText();
		if(teams.addTeam(name) != 0) {
			teamNewName.setText("");
			teamsKnown.getItems().add(name);
			actionItemTeam.getItems().add(name);
		}
	}
	
	
	public void clearTheForm() {
		try {
			actionItemList.getSelectionModel().clearSelection();
			actionItemName.clear();
			actionItemDescription.clear();
			actionItemResolution.clear();
			actionItemCreationDate.setText("");
			actionItemDueDate.getEditor().clear();
			actionItemMember.getSelectionModel().clearSelection();
			actionItemTeam.getSelectionModel().clearSelection();
			actionItemStatus.getSelectionModel().clearSelection();
		} catch(Exception ex) {
			
		}
	}
	
	public void createActionItem() throws InsufficientCredentialsException {
		String name = actionItemName.getText();
		String desc = actionItemDescription.getText();
		String res = actionItemResolution.getText();
		LocalDate lDue = actionItemDueDate.getValue();
		Instant instant = lDue.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
		Date due = Date.from(instant);
		String stat = actionItemStatus.getValue();
		int status;
		if(stat.charAt(0) == 'o' || stat.charAt(0) == 'O') status = 1;
		else status = 0;
		String member = actionItemMember.getValue();
		String team = actionItemTeam.getValue();
		actionItem.setName(name);
		actionItem.setDesc(desc);
		actionItem.setResolution(res);
		actionItem.setDueDate(due);
		actionItem.setMember(member);
		actionItem.setTeam(team);
		actionItem.setStatus(status);
		if(actionItem.storeActionItem()) {
			actionItemName.clear();
			actionItemDescription.clear();
			actionItemResolution.clear();
			actionItemDueDate.getEditor().clear();
			actionItemList.getItems().add(name);
		}
	}
	
	public void deleteThisActionItem() {
		String name = actionItemList.getValue();
		String query = "delete from actionitems where name = '" + name + "'";
		if(actionItem.delete(query)) {
			actionItemList.getItems().remove(name);
			try {
				actionItemList.getSelectionModel().clearSelection();
				actionItemName.clear();
				actionItemDescription.clear();
				actionItemResolution.clear();
				actionItemCreationDate.setText("");
				actionItemDueDate.getEditor().clear();
				actionItemMember.getSelectionModel().clearSelection();
				actionItemTeam.getSelectionModel().clearSelection();
				actionItemStatus.getSelectionModel().clearSelection();
				consoleActionItems.getItems().remove(name);
			} catch(Exception ex) {

			}
		}
	}
	
	public void displayMembers() {
		String name = teamsKnown.getSelectionModel().getSelectedItem();
		String query = "select membername from members where membername NOT IN (select membername from memberTeam where teamname='" + name + "')";
		System.out.println(query);
		teamsMembersAvailable.getItems().clear();
		ResultSet rs = db.query(query);
		try {
			while(rs.next()) {
				teamsMembersAvailable.getItems().add(rs.getString("membername"));
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		query = "select membername from memberTeam where teamname ='" + name + "'";
		teamsMembersFor.getItems().clear();
		rs = db.query(query);
		try {
			while(rs.next()) {
				teamsMembersFor.getItems().add(rs.getString("membername"));
			}
		}catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public void displayTeams() {
		String name = membersKnown.getSelectionModel().getSelectedItem();
		String query = "select teamname from teams where teamname NOT IN (select teamname from memberTeam where membername='"+name+"')";
		membersTeamsAvailable.getItems().clear();
		ResultSet rs = db.query(query);
		try {
			while(rs.next()) {
				membersTeamsAvailable.getItems().add(rs.getString("teamname"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		query = "select teamname from memberTeam where membername='" + name + "'";
		membersTeamsFor.getItems().clear();
		rs = db.query(query);
		try {
			while(rs.next()) {
				membersTeamsFor.getItems().add(rs.getString("teamname"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void doQuit() {
		System.exit(1);
	}
	
	public void initialize() {
		db = new Database();
		initializeActionItems();
		initializeMembers();
		initializeTeams();
	}
	
	public void initializeActionItems() {
		String query = "select name from actionitems";
		ResultSet rs = db.query(query);
		ArrayList<String> list = new ArrayList<String>();
		try {
			while(rs.next()) {
				list.add(rs.getString("name"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ObservableList<String> namesList = FXCollections.observableArrayList(list);
		actionItemList.setItems(namesList);
		actionItemList.getSelectionModel().clearAndSelect(0);
		consoleActionItems.setItems(namesList);
		selectedName = actionItemList.getValue();
	}
	
	public void initializeMembers() {
		String query = "select * from members";
		ArrayList<String> listM = new ArrayList<String>();
		ResultSet rs = db.query(query);
		try {
			while(rs.next()) {
				listM.add(rs.getString("membername"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ObservableList<String> memList = FXCollections.observableArrayList(listM);
		membersKnown.setItems(memList);
	}
	
	public void initializeTeams() {
		String query = "select * from teams";
		ArrayList<String> listT = new ArrayList<String>();
		ResultSet rs = db.query(query);
		try {
			while(rs.next()) {
				listT.add(rs.getString("teamname"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ObservableList<String> teamList = FXCollections.observableArrayList(listT);
		teamsKnown.setItems(teamList);
	}
	
	public void loadActionItem() {
		String name = actionItemList.getValue();
		String query = "select * from actionitems where name = '" + name + "'";
		ResultSet rs = db.query(query);
		try {
			if(rs.next()) {
				actionItemName.setText(rs.getString("name"));
				actionItemDescription.setText(rs.getString("description"));
				actionItemResolution.setText(rs.getString("resolution"));
				actionItemCreationDate.setText(rs.getString("creation"));
				LocalDate dueD = LocalDate.parse(rs.getString("due"));
				actionItemDueDate.setValue(dueD);
				actionItemMember.setValue(rs.getString("member"));
				actionItemTeam.setValue(rs.getString("team"));
				int sta = Integer.parseInt(rs.getString("status"));
				if(sta == 0) actionItemStatus.setValue("closed");
				else actionItemStatus.setValue("open");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		selectedName = name;
	}
	
	public void removeAffiliation() {
		String member = membersKnown.getSelectionModel().getSelectedItem();
		String team = membersTeamsFor.getSelectionModel().getSelectedItem();
		String query = "delete from memberTeam where membername='" + member + "' and teamname='" + team + "'";
		int i = db.update(query);
		if(i != 0) {
			membersTeamsAvailable.getItems().add(team);
			membersTeamsFor.getItems().remove(team);
		}
	}
	
	public void removeMember() {
		String name = membersKnown.getSelectionModel().getSelectedItem();
		if(members.removeMember(name) != 0) {
			membersNewName.setText(name);
			membersKnown.getItems().remove(name);
			actionItemMember.getItems().remove(name);
		}
	}
	
	public void removeAssociation() {
		String name = teamsKnown.getSelectionModel().getSelectedItem();
		if(teams.removeTeam(name) != 0) {
			teamNewName.setText(name);
			teamsKnown.getItems().remove(name);
			actionItemTeam.getItems().remove(name);
		}
	}
	
	public void removeTeam() {
		String name = teamsKnown.getSelectionModel().getSelectedItem();
		if(teams.removeTeam(name) != 0) {
			teamNewName.setText(name);
			teamsKnown.getItems().remove(name);
			actionItemTeam.getItems().remove(name);
		}
	}
	
	public void selectedListItem() {
		String listname = consoleActionItems.getSelectionModel().getSelectedItem();
		String query = "select * from actionitems where name ='" + listname + "'";
		ResultSet rs = db.query(query);
		try {
			if(rs.next()) {
				consoleItemName.setText(listname);
				consoleItemDesc.setText(rs.getString("description"));
				consoleItemResolution.setText(rs.getString("resolution"));
				consoleCreationDate.setText(rs.getString("creation"));
				consoleDueDate.setText(rs.getString("due"));
				consoleMember.setText(rs.getString("member"));
				consoleTeam.setText(rs.getString("team"));
				int i = Integer.parseInt(rs.getString("status"));
				if(i == 0) consoleStatus.setText("closed");
				else consoleStatus.setText("open");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateActionItem() {
		try {
			actionItem.setName(actionItemName.getText());
			actionItem.setDesc(actionItemDescription.getText());
			actionItem.setResolution(actionItemResolution.getText());
			LocalDate lDue = actionItemDueDate.getValue();
			Instant instant = lDue.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
			Date due = Date.from(instant);
			actionItem.setDueDate(due);
			String stat = actionItemStatus.getValue();
			int status;
			if(stat.charAt(0) == 'o' || stat.charAt(0) == 'O') status = 1;
			else status = 0;
			actionItem.setStatus(status);
			actionItem.setTeam(actionItemTeam.getValue());
			actionItem.setMember(actionItemMember.getValue());
			if(actionItem.updateActionItem(selectedName)) {
				actionItemList.getItems().remove(selectedName);
				actionItemList.getItems().add(actionItemName.getText());
			}
			
		} catch (InsufficientCredentialsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
