//DBConnection
package db;
import java.sql.*;




public class DBConnection {
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/eee_formula",
                "root",
                "1234"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }





    // TEMPORARY TEST MAIN
    public static void main(String[] args) {
        Connection con = getConnection();
        if(con != null) {
            System.out.println("DB Connected Successfully");
        } else {
            System.out.println("DB Connection Failed");
        }
    }
}

//LoginFrame

package ui;

import db.DBConnection;
import javax.swing.*;
import java.sql.*;

public class LoginFrame extends JFrame {

    JTextField user;
    JPasswordField pass;

    public LoginFrame() {
        setTitle("Login");
        setSize(300,200);
        setLayout(null);

        JLabel l1 = new JLabel("Username:");
        JLabel l2 = new JLabel("Password:");
        user = new JTextField();
        pass = new JPasswordField();

        l1.setBounds(30,30,80,25);
        l2.setBounds(30,70,80,25);
        user.setBounds(120,30,120,25);
        pass.setBounds(120,70,120,25);

        JButton login = new JButton("Login");
        login.setBounds(100,120,100,30);

        add(l1); add(l2); add(user); add(pass); add(login);

        login.addActionListener(e -> checkLogin());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    void checkLogin() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, user.getText());
            ps.setString(2, new String(pass.getPassword()));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                new SubjectFrame();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Login");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}

//SubjectFrame
package ui;

import db.DBConnection;
import javax.swing.*;
import java.sql.*;

public class SubjectFrame extends JFrame {

    public SubjectFrame() {
        setTitle("Select Subject");
        setSize(300,250);
        setLayout(null);

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM subjects");
            ResultSet rs = ps.executeQuery();

            int y = 30;
            while(rs.next()) {
                JButton btn = new JButton(rs.getString("name"));
                int subjectId = rs.getInt("id");

                btn.setBounds(40, y, 200, 30);
                add(btn);

                btn.addActionListener(e -> new QuizFrame(subjectId));
                y += 50;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

//QuizFrame
package ui;
import db.DBConnection;
import javax.swing.*;
import java.sql.*;

public class QuizFrame extends JFrame {
    
    int score = 0;
    ResultSet rs;

    JRadioButton o1=new JRadioButton(), o2=new JRadioButton(),
                 o3=new JRadioButton(), o4=new JRadioButton();
    ButtonGroup bg = new ButtonGroup();
    JLabel qLabel = new JLabel();

    public QuizFrame(int subjectId) {
        setTitle("Quiz");
        setSize(500,300);
        setLayout(null);

        qLabel.setBounds(20,20,450,30);
        add(qLabel);

        o1.setBounds(20,60,300,25);
        o2.setBounds(20,90,300,25);
        o3.setBounds(20,120,300,25);
        o4.setBounds(20,150,300,25);

        add(o1); add(o2); add(o3); add(o4);

        bg.add(o1); bg.add(o2); bg.add(o3); bg.add(o4);

        JButton next = new JButton("Next");
        next.setBounds(150,200,100,30);
        add(next);

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM questions WHERE subject_id=?");
            ps.setInt(1, subjectId);
            rs = ps.executeQuery();
            loadQuestion();
        } catch(Exception e) {}

        next.addActionListener(e -> {
            checkAnswer();
            loadQuestion();
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    void loadQuestion() {
        try {
            if(rs.next()) {
                qLabel.setText(rs.getString("question"));
                o1.setText(rs.getString("option1"));
                o2.setText(rs.getString("option2"));
                o3.setText(rs.getString("option3"));
                o4.setText(rs.getString("option4"));
            } else {
                JOptionPane.showMessageDialog(this,"Quiz Over! Score = "+score);
                dispose();
            }
        } catch(Exception e) {}
    }

    void checkAnswer() {
        try {
            int correct = rs.getInt("answer");
            if((o1.isSelected() && correct==1) ||
               (o2.isSelected() && correct==2) ||
               (o3.isSelected() && correct==3) ||
               (o4.isSelected() && correct==4))
                score++;
        } catch(Exception e) {}
    }
}

