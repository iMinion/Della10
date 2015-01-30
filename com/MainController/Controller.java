package com.MainController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

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
	private Button deleteActionItemB;
	@FXML
	private Button createActionItemB; 
	@FXML
	private Button clearForm;
	@FXML
	private Button updateActionItemB;
	
	@FXML
	private TextField membersNewName;
	@FXML
	private ListView<String> membersKnown;
	@FXML
	private ListView<String> membersTeamsAvailable;
	@FXML
	private ListView<String> membersTeamsFor;
	@FXML
	private Button membersAddAffiliation;
	@FXML
	private Button membersRemoveAffiliation;
	@FXML
	private Button membersAddNew;
	@FXML
	private Button membersRemove;
	
	@FXML
	private TextField teamNewName;
	@FXML
	private ListView<String> teamsKnown;
	@FXML
	private ListView<String> teamsMembersAvailable;
	@FXML
	private ListView<String> teamsMembersFor;
	@FXML
	private Button teamsAddNew;
	@FXML
	private Button teamsRemove;
	@FXML
	private Button teamsAddAssociation;
	@FXML
	private Button teamsRemoveAssociation;
	
	
	private ActionItems actionItem = new ActionItems();
	private Members members = new Members();
	private Team teams = new Team();
	private String selectedName;
	private boolean flag = false;
	Hashtable<String, ActionItems> contents;
	public static Database db;
	
	public void addAffiliation() {
		String member = membersKnown.getSelectionModel().getSelectedItem();
		String team = membersTeamsAvailable.getSelectionModel().getSelectedItem();
		String query = "insert into memberTeam (membername,teamname) values ('"+member+"', '"+team+"')";
		int i = db.update(query);
		if(i != 0) {
			membersTeamsAvailable.getItems().remove(team);
			membersTeamsFor.getItems().add(team);
			membersAddAffiliation.setDisable(true);
			membersAddNew.setDisable(true);
			membersRemove.setDisable(true);
			membersRemoveAffiliation.setDisable(true);
		}
		
	}
	
	public void addAssociation() {
		String team = teamsKnown.getSelectionModel().getSelectedItem().toString();
		String member = teamsMembersAvailable.getSelectionModel().getSelectedItem().toString();
		String query = "insert into memberTeam (membername,teamname) values ('" + member + "', '" + team + "')";
		int i = db.update(query);
		if(i != 0) {
			teamsMembersAvailable.getItems().remove(member);
			teamsMembersFor.getItems().add(member);
			teamsAddAssociation.setDisable(true);
			teamsAddNew.setDisable(true);
			teamsRemove.setDisable(true);
			teamsRemoveAssociation.setDisable(true);
		}
	}
	
	
	public void addMember() throws InsufficientCredentialsException {
		String name = membersNewName.getText();
		if(members.addMember(name) != 0) {
			membersNewName.setText("");
			String query = "select * from members";
			ResultSet rs = db.query(query);
			membersKnown.getItems().clear();
			actionItemMember.getItems().clear();
			ObservableList<String> mem = FXCollections.observableArrayList();
			try {
				while(rs.next()) {
					mem.add(rs.getString("membername"));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			membersKnown.setItems(mem);
			actionItemMember.setItems(mem);
			membersAddAffiliation.setDisable(true);
			membersAddNew.setDisable(true);
			membersRemove.setDisable(true);
			membersRemoveAffiliation.setDisable(true);
		}
	}
	
	public void addTeam() throws InsufficientCredentialsException {
		String name = teamNewName.getText();
		if(teams.addTeam(name) != 0) {
			teamNewName.setText("");
			String query = "select * from teams";
			ResultSet rs = db.query(query);
			teamsKnown.getItems().clear();
			actionItemTeam.getItems().clear();
			ObservableList<String> mem = FXCollections.observableArrayList();
			try {
				while(rs.next()) {
					mem.add(rs.getString("teamname"));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			teamsKnown.setItems(mem);
			actionItemTeam.setItems(mem);
			teamsAddAssociation.setDisable(true);
			teamsAddNew.setDisable(true);
			teamsRemove.setDisable(true);
			teamsRemoveAssociation.setDisable(true);
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
			actionItemMember.getSelectionModel().clearSelection();
			actionItemTeam.getSelectionModel().clearSelection();
		}
	}
	
	public void deleteThisActionItem() {
		String name = actionItemList.getValue();
		if(actionItem.delete(name)) {
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
		teamsAddNew.setDisable(true);
		String name = teamsKnown.getSelectionModel().getSelectedItem();
		String query = "select membername from members where membername NOT IN (select membername from memberTeam where teamname='" + name + "')";
		System.out.println(query);
		teamsMembersAvailable.getItems().clear();
		ResultSet rs = db.query(query);
		try {
			while(rs.next()) {
				System.out.println(rs.getString("membername"));
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
		teamsRemove.setDisable(false);
		teamsAddAssociation.setDisable(true);
		teamsRemoveAssociation.setDisable(true);
	}
	
	public void displayTeams() {
		membersAddNew.setDisable(true);
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
		membersRemove.setDisable(false);
		membersAddAffiliation.setDisable(true);
		membersAddNew.setDisable(true);
	}
	
	public void doQuit() {
		System.exit(1);
	}
	
	public void enableActionItemButtons() {
		deleteActionItemB.setDisable(true);
		createActionItemB.setDisable(false);
		updateActionItemB.setDisable(false);
		clearForm.setDisable(false);
	}
	
	public void initialize() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		db = new Database();
		flag = db.isDBReachable();
		initializeActionItems();
		initializeMembers();
		initializeTeams();
	}
	
	private void initializeActionItems() {
		ArrayList<String> list = new ArrayList<String>();
		ObservableList<String> namesList;
		if(flag) {
			String query = "select name from actionitems";
			ResultSet rs = db.query(query);
			try {
				while(rs.next()) {
					list.add(rs.getString("name"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			namesList = FXCollections.observableArrayList(list);
			actionItemList.setItems(namesList);
			consoleActionItems.setItems(namesList);
			updateActionItemB.setDisable(true);
			clearForm.setDisable(true);
			createActionItemB.setDisable(true);
			deleteActionItemB.setDisable(true);
		}
		else {
			contents = actionItem.deSerialize();
			Enumeration<String> names = contents.keys();
			while(names.hasMoreElements()) {
				list.add(names.nextElement());
			}
			namesList = FXCollections.observableArrayList(list);
			actionItemList.setItems(namesList);
			consoleActionItems.setItems(namesList);
			updateActionItemB.setDisable(true);
			clearForm.setDisable(true);
			createActionItemB.setDisable(true);
			deleteActionItemB.setDisable(true);
		}
	}
	
	private void initializeMembers() {
		if(flag) {
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
			actionItemMember.setItems(memList);
		}
		else {
			membersAddAffiliation.setDisable(true);
			membersAddNew.setDisable(true);
			membersRemove.setDisable(true);
			membersRemoveAffiliation.setDisable(true);
		}
	}
	
	private void initializeTeams() {
		if(flag) {
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
			actionItemTeam.setItems(teamList);
		}
		else {
			teamsAddAssociation.setDisable(true);
			teamsAddNew.setDisable(true);
			teamsRemove.setDisable(true);
			teamsRemoveAssociation.setDisable(true);
		}
	}
	
	public void loadActionItem() throws InsufficientCredentialsException {
		ActionItems aItem = new ActionItems();
		String name = actionItemList.getValue();
		if(flag) {
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
					aItem.setName(rs.getString("name"));
					aItem.setDesc(rs.getString("description"));
					aItem.setStatus(Integer.parseInt(rs.getString("status")));
					aItem.setResolution(rs.getString("resolution"));
					aItem.setDueDate(rs.getDate("due"));
					aItem.setMember(rs.getString("member"));
					aItem.setTeam(rs.getString("team"));
					aItem.setCreationDate(rs.getDate("creation"));
				}
				createActionItemB.setDisable(true);
				deleteActionItemB.setDisable(false);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			selectedName = name;
		}
		else {
			aItem = contents.get(name);
			actionItemName.setText(aItem.getName());
			actionItemDescription.setText(aItem.getDescription());
			actionItemResolution.setText(aItem.getResolution());
//			System.out.println(aItem.getCreationDate().toString());
			actionItemCreationDate.setText(aItem.getCreationDate().toString());
			LocalDate dueD = LocalDate.parse(aItem.getDueDate().toString());
			actionItemDueDate.setValue(dueD);
			actionItemMember.setValue(aItem.getMember());
			int sta = aItem.getStatus();
			if(sta == 0) actionItemStatus.setValue("closed");
			else actionItemStatus.setValue("open");
		}
		actionItem = aItem;
	}

	public void membersDisableAffiliation() {
		if(flag) {
			membersAddAffiliation.setDisable(true);
			membersAddNew.setDisable(true);
			membersRemove.setDisable(true);
			membersRemoveAffiliation.setDisable(false);
		}
		else {
			membersAddAffiliation.setDisable(true);
			membersAddNew.setDisable(true);
			membersRemove.setDisable(true);
			membersRemoveAffiliation.setDisable(true);
		}
	}
	
	public void memberAddListEnable() {
		if(flag) {
			membersAddAffiliation.setDisable(true);
			membersAddNew.setDisable(false);
			membersRemove.setDisable(true);
			membersRemoveAffiliation.setDisable(true);
		}
		else {
			membersAddAffiliation.setDisable(true);
			membersAddNew.setDisable(true);
			membersRemove.setDisable(true);
			membersRemoveAffiliation.setDisable(true);
		}		
	}
	
	public void memberEnableAffiliation() {
		if(flag) {
			membersAddNew.setDisable(true);
			membersAddAffiliation.setDisable(false);
			membersRemove.setDisable(true);
			membersRemoveAffiliation.setDisable(true);
		}
		else {
			membersAddAffiliation.setDisable(true);
			membersAddNew.setDisable(true);
			membersRemove.setDisable(true);
			membersRemoveAffiliation.setDisable(true);
		}
	}
	
	public void removeAffiliation() {
		if(flag) {
			String member = membersKnown.getSelectionModel().getSelectedItem();
			String team = membersTeamsFor.getSelectionModel().getSelectedItem();
			String query = "delete from memberTeam where membername='" + member + "' and teamname='" + team + "'";
			int i = db.update(query);
			if(i != 0) {
				membersTeamsAvailable.getItems().add(team);
				membersTeamsFor.getItems().remove(team);
				membersAddAffiliation.setDisable(true);
				membersAddNew.setDisable(true);
				membersRemoveAffiliation.setDisable(true);
				membersRemove.setDisable(true);
			}
		}
	}
	
	public void removeAssociation() {
		String team = teamsKnown.getSelectionModel().getSelectedItem();
		String member = teamsMembersFor.getSelectionModel().getSelectedItem();
		String query = "delete from memberTeam where membername='" + member + "' and teamname='" + team + "'";;
		int i = db.update(query);
		if(i != 0) {
			teamsMembersAvailable.getItems().add(member);
			teamsMembersFor.getItems().remove(member);
			teamsAddAssociation.setDisable(true);
			teamsAddNew.setDisable(true);
			teamsRemove.setDisable(true);
			teamsRemoveAssociation.setDisable(true);
		}
	}
	
	public void removeMember() {
		String name = membersKnown.getSelectionModel().getSelectedItem();
		if(members.removeMember(name) != 0) {
			membersNewName.setText(name);
			membersKnown.getItems().remove(name);
			actionItemMember.getItems().remove(name);
			membersAddAffiliation.setDisable(true);
			membersRemoveAffiliation.setDisable(true);
			membersRemove.setDisable(true);
			membersAddNew.setDisable(false);
		}
	}
	
	public void removeTeam() {
		String name = teamsKnown.getSelectionModel().getSelectedItem();
		if(teams.removeTeam(name) != 0) {
			teamNewName.setText(name);
			teamsKnown.getItems().remove(name);
			actionItemTeam.getItems().remove(name);
			teamsAddAssociation.setDisable(true);
			teamsAddNew.setDisable(false);
			teamsRemove.setDisable(true);
			teamsRemoveAssociation.setDisable(true);
		}
	}
	
	public void selectedListItem() {
		if(flag) {
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
		else {
			
		}
	}
	
	public void teamAddListEnable() {
		teamsRemove.setDisable(true);
		teamsAddAssociation.setDisable(true);
		teamsRemoveAssociation.setDisable(true);
		teamsAddNew.setDisable(false);
	}
	
	public void teamsDisableAssociation() {
		teamsAddAssociation.setDisable(true);
		teamsAddNew.setDisable(true);
		teamsRemoveAssociation.setDisable(false);
		teamsRemove.setDisable(true);
	}
	
	public void teamEnableAssociation() {
		teamsAddAssociation.setDisable(false);
		teamsRemoveAssociation.setDisable(true);
		teamsRemove.setDisable(true);
		teamsAddNew.setDisable(true);
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
			e.printStackTrace();
		}
	}
}