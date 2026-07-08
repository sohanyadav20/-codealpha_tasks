package DatabaseConnection;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ImageHandling {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mydatabasep";
        String username = "root";
        String password = "Sohan@20082002";
        String image_path = "C:\\Users\\SOHANYADAV\\OneDrive\\Pictures\\IMG_20230726_193808.jpg.jpeg";
        String query = "INSERT INTO images_table(image_data) VALUES (?)";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver Uploaded Successfully!!");
        } catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection con = DriverManager.getConnection(url,username,password);
            System.out.println("Connection Established Successfully!!");
            FileInputStream fis  =new FileInputStream(image_path);
            byte[] imageData = new byte[fis.available()];
            fis.read(imageData);
            PreparedStatement pst = con.prepareStatement(query);
            pst.setBytes(1, imageData);
            int affectedrows = pst.executeUpdate();
            if(affectedrows>0){
                System.out.println("Successfully Uploaded Image");
            } else {
                System.out.println("Failed Uploaded Image");
            }
            pst.close();
            con.close();
            System.out.println("Connection closed successfully!!");


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }catch (FileNotFoundException e){
            throw new RuntimeException(e);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
