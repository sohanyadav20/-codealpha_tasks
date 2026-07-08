package DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class PreparedStatement_Insert {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mydatabasep";
        String username = "root";
        String password = "Sohan@20082002";
        String query = "INSERT INTO employee(id, name, job_title, salary) VALUES (?, ?, ?, ?)";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded Successfully!!");
        } catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection con = DriverManager.getConnection(url,username,password);
            System.out.println("Connection Established Successfully!!");
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter Id: ");
            int id = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter name: ");
            String name = sc.nextLine();
            System.out.print("Enter Job title: ");
            String job_title = sc.nextLine();
            System.out.print("Enter Salary: ");
            double salary = sc.nextDouble();
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, id);
            pst.setString(2, name);
            pst.setString(3, job_title);
            pst.setDouble(4, salary);
            int rowsAffected = pst.executeUpdate();
            if(rowsAffected>0){
                System.out.println("Data Inserted Successfully!!");
            } else{
                System.out.println("Failed");
            }
            pst.close();

            con.close();
            System.out.println();
            System.out.println("Connection closed Successfully!!");
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
