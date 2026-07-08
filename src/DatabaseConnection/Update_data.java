package DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Update_data {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mydatabasep";
        String username = "root";
        String password = "Sohan@20082002";
        String query = "UPDATE employee\n"+
                "SET job_title = 'Machanical Engineer', salary = '20000'\n"+
                "WHERE id = 2;";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver Loaded Successfully!!!");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try{
            Connection con = DriverManager.getConnection(url,username,password);
            System.out.println("Connection Estsblished Successfully!!");
            Statement stmt = con.createStatement();
            int rowsaffected = stmt.executeUpdate(query);
            if(rowsaffected>0){
                System.out.println("Updation Successfully!!"+" "+rowsaffected);
            } else{
                System.out.println("Updation failed");
            }
            stmt.close();
            con.close();
            System.out.println("Connection closed Successfully!!!");
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
