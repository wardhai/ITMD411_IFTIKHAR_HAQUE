//test test 
package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

public class Dao {
	// instance fields
	static Connection connect = null;
	Statement statement = null;

	// constructor
	public Dao() { 
	  
	}

	public Connection getConnection() {
		// Setup the connection with the DB
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false"
							+ "&user=fp411&password=411");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() {
		// variables for SQL Query table creations
		final String createTicketsTable = "CREATE TABLE swifthq_tickets(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticket_description VARCHAR(200), start_date VARCHAR(30))";
		final String createUsersTable = "CREATE TABLE swifthq_users(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)";
		final String createResolvedTable = "CREATE TABLE swifthq_resolved(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticket_description VARCHAR(200), start_date VARCHAR(30), end_date VARCHAR(30))";
		//final String createNewTicketsTable = "ALTER TABLE swifthq_tickets ADD end_date date";
		//final String createNewResolvedTable = "ALTER TABLE swifthq_resolved ADD start_date date";
			
		
		try {

			// execute queries to create tables

			statement = getConnection().createStatement();
			
			//statement.executeUpdate(createNewResolvedTable);
			//statement.executeUpdate(createNewTicketsTable);

			statement.executeUpdate(createResolvedTable);
			statement.executeUpdate(createTicketsTable);
			statement.executeUpdate(createUsersTable);

			System.out.println("Created tables in given database...");

			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (Exception e) { 
			System.out.println(e.getMessage());
		}
		// add users to user table
		addUsers();
	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table

		// variables for SQL Query inserts
		String sql;

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)

		// read data from file
		try {
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));

			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}

		try {

			// Setup the connection with the DB

			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			for (List<String> rowData : array) {

				sql = "insert into swifthq_users(uname,upass,admin) " + "values('" + rowData.get(0) + "'," + " '"
						+ rowData.get(1) + "','" + rowData.get(2) + "');";
				statement.executeUpdate(sql);
			}
			System.out.println("Inserts completed in the given database...");

			// close statement object
			statement.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int insertRecords(String ticketName, String ticketDesc, LocalDate startdate) {
		int id = 0;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate("Insert into swifthq_tickets" + "(ticket_issuer, ticket_description, start_date) values(" + " '"
					+ ticketName + "','" + ticketDesc + "','" + Tickets.startdate +"')", Statement.RETURN_GENERATED_KEYS);

			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = null;
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				id = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;

	}

	//Admin View
	public ResultSet readRecords() {

		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("SELECT * FROM swifthq_tickets");
			System.out.println("Welcome Admin!");
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace(); 
		}
		return results;
	}
	
	//User View
	public ResultSet readUserRecords() {

		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("SELECT * FROM swifthq_tickets WHERE admin= '" + Login.adminid + "';");
			System.out.println("Welcome User!");
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace(); 
		}
		return results;
	}
	

	// continue coding for deleteRecords implementation
	public void deleteRecords(int id) throws SQLException {

		// Execute delete  query
	      System.out.println("Creating delete statement...");
	      statement = connect.createStatement();

	     String sql = "DELETE FROM swifthq_tickets  " + "WHERE ticket_id = '" + id + "'" ;
	     String sql1="DELETE FROM swifthq_resolved  " + "WHERE ticket_id = '" + id + "'" ;

	    
	     int response = JOptionPane.showConfirmDialog(null, "Delete ticket # " + id + "?", "Confirm",  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	     if (response == JOptionPane.NO_OPTION) {
	       System.out.println("No record deleted");
	       JOptionPane.showMessageDialog(null, "No ticket deleted.");
	       
	    } else if (response == JOptionPane.YES_OPTION) {
	      statement.executeUpdate(sql);
	      statement.executeUpdate(sql1);
	      JOptionPane.showMessageDialog(null, "Ticket deleted.");
	      System.out.println("Record deleted");
	      
	    } else if (response == JOptionPane.CLOSED_OPTION) {
	      JOptionPane.showMessageDialog(null, "Ticket cancelled.");
	      System.out.println("Request cancelled");
	    }
		
	}
	
	// continue coding for updateRecords implementation
	public void updateRecords(int id, String ticketDesc) throws SQLException {
		 // Execute update  query
	      System.out.println("Creating update statement...");
	      statement = connect.createStatement();
	      
	      String sql = "UPDATE swifthq_tickets " + "SET ticket_description = '" + ticketDesc + "' WHERE ticket_id = '" + id +"'";
	      
	      statement.executeUpdate(sql);
	      JOptionPane.showMessageDialog(null, "Update successful.");
	}
	
	//close ticket
	public void closeRecords(int id, LocalDate startdate) throws SQLException {
		// Execute close ticket  query
	      System.out.println("Creating close ticket statement...");
	      statement = connect.createStatement();
	      
	     String sql = "INSERT INTO swifthq_resolved SELECT * FROM swifthq_tickets WHERE ticket_id = '" + id + "'";
	     String sql2 = "UPDATE swifthq_resolved " + "SET end_date = '" + Tickets.startdate + "' WHERE ticket_id = '" + id +"'";
	     String sql1="DELETE FROM swifthq_tickets  " + "WHERE ticket_id = '" + id + "'" ;
	     
	     int response = JOptionPane.showConfirmDialog(null, "Close ticket # " + id + "?", "Confirm",  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	     if (response == JOptionPane.NO_OPTION) {
	       System.out.println("No record closed");
	       JOptionPane.showMessageDialog(null, "No ticket closed.");
	       
	    } else if (response == JOptionPane.YES_OPTION) {
	      statement.executeUpdate(sql);
	      statement.executeUpdate(sql2);
	      statement.executeUpdate(sql1);
	      
	      
	      JOptionPane.showMessageDialog(null, "Ticket closed.");
	      System.out.println("Ticket successfully closed.");
	      
	    } else if (response == JOptionPane.CLOSED_OPTION) {
	      JOptionPane.showMessageDialog(null, "Error: Ticket did not close.");
	      System.out.println("Request cancelled");
	    }
	}
	
	//Display resolved tickets
	public ResultSet readResolved() {
		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("SELECT * FROM swifthq_resolved");
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}
}


