package DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;
public class DBConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mydatabasep";
        String username = "root";
        String password = "Sohan@20082002";
        String query = "SELECT * FROM employee;";

        try{
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver Loaded Successfully");
        } catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        try{
            Connection con = DriverManager.getConnection(url, username, password); // Connection Established
            System.out.println("Connection Created");
            Statement stmt = con.createStatement(); // Statement Created
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String job_title = rs.getString("job_title");
                double salary = rs.getDouble("salary");
                System.out.println("================");
                System.out.println("ID: "+id);
                System.out.println("Name: "+name);
                System.out.println("JobTitle: "+job_title);
                System.out.println("Salary: "+salary);
            }
            rs.close();
            stmt.close();
            con.close();
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
}