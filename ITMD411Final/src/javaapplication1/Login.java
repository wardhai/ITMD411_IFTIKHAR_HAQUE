package javaapplication1;

import java.awt.Color;

import java.awt.Font;
import java.awt.GridLayout; //useful for layouts
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.ImageIcon;
//controls-label text fields, button
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;   

@SuppressWarnings("serial")
public class Login extends JFrame {

	Dao conn;
	//global variables
	public static int adminid = 0;
	public static int userid = 0;

	public static String uname = null;
	public static String upass = null;
		
	private JComponent main;

	public Login() {

		super("SwiftHQ HELP DESK LOGIN");
		conn = new Dao();
		conn.createTables();
		setSize(600, 600);
		
		setLayout(new GridLayout(1, 1));
		
		// ADD SWIFTHQ LOGO
		JLabel lblLogo = new JLabel("", JLabel.CENTER);
		lblLogo.setHorizontalAlignment(JLabel.CENTER);
		//getContentPane().add(new JLabel(new ImageIcon("src/logo.jpeg")));
		lblLogo.setIcon(new ImageIcon(new ImageIcon("src/logo.jpeg").getImage().getScaledInstance(130,130, Image.SCALE_DEFAULT)));
		add(lblLogo);

		
		setLayout(new GridLayout(6, 3));
		setLocationRelativeTo(null); // centers window
		getContentPane().setBackground(new Color(10, 44, 92));		

		
		// SET UP CONTROLS
		JLabel lblSpacer = new JLabel("Help Desk", JLabel.RIGHT);
		JLabel lblUsername = new JLabel("Enter Username", JLabel.LEFT);
		JLabel lblPassword = new JLabel("Enter Password", JLabel.LEFT);
		JLabel lblType = new JLabel("Select Type", JLabel.LEFT);
		JLabel lblStatus = new JLabel(" ", JLabel.CENTER);
		//JLabel lblSpacer = new JLabel("", JLabel.CENTER);

		
		lblSpacer.setForeground(Color.WHITE); 
		lblUsername.setForeground(Color.WHITE);
		lblPassword.setForeground(Color.WHITE);
		lblStatus.setForeground(Color.WHITE);
		lblType.setForeground(Color.WHITE);
		lblSpacer.setFont(new Font("Monospaced", Font.BOLD, 23));
	    lblUsername.setFont(new Font("Garamond", Font.PLAIN, 18));
	    lblPassword.setFont(new Font("Garamond", Font.PLAIN, 18));
	    lblType.setFont(new Font("Garamond", Font.PLAIN, 18));
		

		JTextField txtUname = new JTextField(10);
		
		JTextField txtType = new JTextField(10);

		JPasswordField txtPassword = new JPasswordField();
		JButton btn = new JButton("Submit");
		JButton btnExit = new JButton("Exit");
	    btn.setFont(new Font("Garamond", Font.PLAIN, 18));
	    btnExit.setFont(new Font("Garamond", Font.PLAIN, 18));
	    btnExit.setBackground(Color.GRAY);
	    btnExit.setForeground(Color.WHITE);
	    btn.setBackground(Color.GRAY);
	    btn.setForeground(Color.WHITE);


		// constraints

		lblStatus.setToolTipText("Contact help desk to unlock password");
		lblSpacer.setHorizontalAlignment(JLabel.CENTER);
		lblUsername.setHorizontalAlignment(JLabel.CENTER);
		lblPassword.setHorizontalAlignment(JLabel.CENTER);
		lblType.setHorizontalAlignment(JLabel.CENTER);
		
		
		//SELECT DROPDOWN LIST
		String selection[]= { "Admin", "User" };
		JComboBox select = new JComboBox(selection);
		//select.addItemListener(this);
				
		// ADD OBJECTS TO FRAME
		add(lblSpacer);   // 1st row filler
		add(lblUsername);   // 2nd row
		add(txtUname);
		add(lblPassword);// 3rd row
		add(txtPassword);
		add(lblType);     // 4th row
		//add(txtType);	  
		add(select);
		add(btn);         // 5th row
		add(btnExit);
		add(lblStatus);   // 6th row
		
		btn.addActionListener(new ActionListener() {
			int count = 0; // count agent

			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean admin = false;
				count = count + 1;
				// verify credentials of user (MAKE SURE TO CHANGE TO YOUR TABLE NAME BELOW)

				String query = "SELECT * FROM swifthq_users WHERE uname = ? and upass = ? ;";
				try (PreparedStatement stmt = conn.getConnection().prepareStatement(query)) {
					stmt.setString(1, txtUname.getText());
					uname = txtUname.getText();
					stmt.setString(2, txtPassword.getText());
					upass = txtPassword.getText();
					
					ResultSet rs = stmt.executeQuery();
					if (rs.next()) {
						admin = rs.getBoolean("admin"); // get table column value
						new Tickets(admin);
						setVisible(false); // HIDE THE FRAME
						dispose(); // CLOSE OUT THE WINDOW
					} else
						lblStatus.setText("Try again! " + (3 - count) + " / 3 attempts left");
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
				
				//get user
				String query1 = "SELECT admin FROM swiftHQ_users WHERE uname = ? and upass = ?;";
				try (PreparedStatement stmt2 = conn.getConnection().prepareStatement(query1)) {
					stmt2.setString(1, uname);
					stmt2.setString(2, upass);
					ResultSet rs = stmt2.executeQuery();
					if(rs.next()) {
						adminid = rs.getInt("admin");
						//userid = rs.getInt("uid");
					} else
						lblStatus.setText("Try again! " + (3 - count) + " / 3 attempts left");
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
				}
 			 
			
		});
		btnExit.addActionListener(e -> System.exit(0));

		setVisible(true); // SHOW THE FRAME
	}
	

	public static void main(String[] args) {
		

		// obtain today's date
		Date date = new Date();

		// the program's header
		System.out.println("Today's Date: " + date);
		System.out.println("[ Programmed by Wardha Iftikhar & Kinza Haque ]\n");

		new Login();
	}
}
