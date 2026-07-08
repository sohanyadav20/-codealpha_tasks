package DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Delete_data {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mydatabasep";
        String username = "root";
        String password = "Sohan@20082002";
        String query = "DELETE FROM employee WHERE id = 5;";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver Loaded Successfully!!!");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try{
            Connection con = DriverManager.getConnection(url,username,password);
            System.out.println("Connection Established!!!");
            Statement stmt = con.createStatement();
            int rowsaffected = stmt.executeUpdate(query);
            if(rowsaffected>0){
                System.out.println("Deletion successfull!!! ");
            } else{
                System.out.println("Deletion Failed!!!");
            }
            stmt.close();
            con.close();
            System.out.println("Connection closed Successfully!!!");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
