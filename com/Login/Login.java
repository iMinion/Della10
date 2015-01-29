package com.Login;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
//import javafx.stage.Stage;
import static application.Main.stage; 



public class Login implements Initializable{
	
	final String url = "jdbc:mysql://10.10.11.53/della";
	final String driver = "com.mysql.jdbc.Driver";
	final String uName = "root";
	final String pWord = "root";
	
	@FXML
	private TextField userName;
	@FXML
	private PasswordField password;
	
	public void validateLogin() {
		String user = userName.getText().trim();
		String pass = password.getText().trim();
		if(!user.equals("") && !pass.equals("")) {
			if(verifyLogin(user,pass)) {
				loadPage();
				
			}
		}
	}
	public boolean verifyLogin(String user, String password) {
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url, uName, pWord);
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("select username from login where username='" + user + "' and password='" + password + "'");
			if(rs.next()) {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return false;
	}
	
	public void loadPage() {
		try {
			Pane root = (Pane) FXMLLoader.load(getClass().getResource("../../Layout/appPane.fxml"));
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
}