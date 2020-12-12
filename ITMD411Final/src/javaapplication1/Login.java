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

import javax.swing.ImageIcon;
//controls-label text fields, button
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;   

@SuppressWarnings("serial")
public class Login extends JFrame {

	Dao conn;

	public Login() {

		super("IIT HELP DESK LOGIN");
		conn = new Dao();
		conn.createTables();
		setSize(400, 400);
		
		setLayout(new GridLayout(1, 1));
		
		JLabel lblSpacer = new JLabel("", JLabel.CENTER);
		lblSpacer.setHorizontalAlignment(JLabel.CENTER);
		//getContentPane().add(new JLabel(new ImageIcon("src/logo.jpeg")));
		lblSpacer.setIcon(new ImageIcon(new ImageIcon("src/logo.jpeg").getImage().getScaledInstance(130,130, Image.SCALE_DEFAULT)));
		add(lblSpacer);
		
		setLayout(new GridLayout(5, 2));
		setLocationRelativeTo(null); // centers window
		getContentPane().setBackground(new Color(10, 44, 92));		
		
		// SET UP CONTROLS
		JLabel lblUsername = new JLabel("Username", JLabel.LEFT);
		JLabel lblPassword = new JLabel("Password", JLabel.LEFT);
		JLabel lblType = new JLabel("Select Type", JLabel.LEFT);
		JLabel lblStatus = new JLabel(" ", JLabel.CENTER);
		//JLabel lblSpacer = new JLabel("", JLabel.CENTER);

		
		lblUsername.setForeground(Color.WHITE);
		lblPassword.setForeground(Color.WHITE);
		lblStatus.setForeground(Color.WHITE);
		lblType.setForeground(Color.WHITE);
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
		lblUsername.setHorizontalAlignment(JLabel.CENTER);
		lblPassword.setHorizontalAlignment(JLabel.CENTER);
		lblType.setHorizontalAlignment(JLabel.CENTER);
		
		
		//lblSpacer.setHorizontalAlignment(JLabel.CENTER);
		//getContentPane().add(new JLabel(new ImageIcon("src/logo.jpeg")));

		//lblSpacer.setIcon(new ImageIcon(new ImageIcon("src/logo.jpeg").getImage().getScaledInstance(60, 60, Image.SCALE_DEFAULT)));

		// ADD OBJECTS TO FRAME
		//add(lblSpacer);
		add(lblUsername);  // 1st row filler
		add(txtUname);
		add(lblPassword); // 2nd row
		add(txtPassword);
		add(lblType);     // 3rd row
		add(txtType);	  
		add(btn);         // 4th row
		add(btnExit);
		add(lblStatus);   // 5th row

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
					stmt.setString(2, txtPassword.getText());
					//stmt.setString(3, txtType.getText());
					
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
 			 
			}
		});
		btnExit.addActionListener(e -> System.exit(0));

		setVisible(true); // SHOW THE FRAME
	}
	
	//adding combo box for select type (Admin

	public static void main(String[] args) {

		new Login();
	}
}
