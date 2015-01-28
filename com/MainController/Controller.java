package com.MainController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.ActionItem.ActionItems;
import com.ActionItem.InsufficientCredentialsException;
import com.Database.Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.text.Text;

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
	
	private ActionItems actionItem = new ActionItems();
	private Database db;
	private String selectedName;
	public void initialize() {
		db = new Database();
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
		System.out.println(namesList);
		actionItemList.setItems(namesList);
		actionItemList.getSelectionModel().clearAndSelect(0);
		selectedName = actionItemList.getValue();
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
	
	public void deleteThisActionItem() {
		String name = actionItemList.getValue();
		String query = "delete from actionitems where name = '" + name + "'";
		db.update(query);
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
		} catch(Exception ex) {
			
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
	
	public void updateActionItem() {
		System.out.println("on click update");
		
		try {
			actionItem.setName(actionItemName.getText());
			actionItem.setDesc(actionItemDescription.getText());
			actionItem.setResolution(actionItemResolution.getText());
			DateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
			Date due = new Date();
			try {
				due = format.parse(actionItemDueDate.getValue().toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	
	public void doQuit() {
		System.exit(1);
	}
	
}
