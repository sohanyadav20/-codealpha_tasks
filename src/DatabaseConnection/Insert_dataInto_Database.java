package DatabaseConnection;
import java.sql.*;
public class Insert_dataInto_Database {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mydatabasep";
        String username = "root";
        String password = "Sohan@20082002";
        String query = "INSERT INTO employee(id, name, job_title, salary) VALUES (5, 'DHEERAJ YADAV', 'STUDENT', 5000);";
        try{
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded Successfully!!!");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try{
            Connection con = DriverManager.getConnection(url,username,password);
            System.out.println("Connection Established Successfully!!");
            Statement stmt = con.createStatement();
            int rowsaffected = stmt.executeUpdate(query);
            if(rowsaffected>0){
                System.out.println("insert Success"+rowsaffected);
            } else{
                System.out.println("Insert failed");
            }
            stmt.close();
            con.close();
            System.out.println("Connection closed Successfully!!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
