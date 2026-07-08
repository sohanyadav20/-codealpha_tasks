package DatabaseConnection;

import java.sql.*;

public class PreparedStatementRetrieve {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mydatabasep";
        String username = "root";
        String password = "Sohan@20082002";
        String query = "SELECT * FROM employee WHERE name = ?";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver Loaded Successfully!!!");
        } catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection con = DriverManager.getConnection(url,username,password);
            System.out.println("Connection Established Successfully!!");

            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1,"SOHAN");
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String job_title = rs.getString("job_title");
                double salary = rs.getDouble("salary");
                System.out.println("Id "+id);
                System.out.println("Name "+name);
                System.out.println("Job "+job_title);
                System.out.println("Salary "+salary);
            }
            rs.close();
            pst.close();

            con.close();
            System.out.println();
            System.out.println("Connection closed Successfully!!");
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
