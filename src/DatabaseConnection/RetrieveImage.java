package DatabaseConnection;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;

public class RetrieveImage {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mydatabasep";
        String username = "root";
        String password = "Sohan@20082002";
        String folder_path = "D:\\aktu\\imagedb\\";
        String query = "SELECT image_data FROM images_table WHERE image_id = (?)";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver Uploaded Successfully!!");
        }catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{
            Connection con = DriverManager.getConnection(url,username,password);
            System.out.println("Connection Established Successfully");
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1,1);
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                byte[] image_data = rs.getBytes("image_data");
                String image_path = folder_path+"extractedImage.jpg";
                OutputStream os = new FileOutputStream(image_path);
                os.write(image_data);
                System.out.println("Image Retrieved Successfully");
            }else {
                System.out.println("Image Not Found");
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
