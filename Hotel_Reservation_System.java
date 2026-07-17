package Hotel_Reservation_System;

import java.sql.*;
import java.util.Scanner;

public class Hotel_Reservation_System {
    private static final String url = "jdbc:mysql://127.0.0.1:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "Sohan@20082002";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jbdc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try{
            Connection con = DriverManager.getConnection(url,username,password);
            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a Room");
                System.out.println("2. Insert Customer Details");
                System.out.println("3. Insert Reservation");
                System.out.println("4. Insert Payment");
                System.out.println("5. Update Reservation");
                System.out.println("6. View Reservation Details");
                System.out.println("7. Delete Reservation");
                System.out.println("8. CancelReservation");
                System.out.println("0. Exit");
                int choice = sc.nextInt();
                switch(choice){
                    case 1:
                        reserveRoom(con, sc);
                        break;
                    case 2:
                        insertCustomer_Details(con,sc);
                        break;
                    case 3:
                        insert_Reservation(con, sc);
                        break;
                    case 4:
                        insertPayment(con,sc);
                        break;
                    case 5:
                        updateReservation(con,sc);
                        break;
                    case 6:
                        viewReservationDetails(con,sc);
                        break;
                    case 7:
                        deleteReservation(con,sc);
                        break;
                    case 8:
                        CancelReservation(con, sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        } //catch(InterruptedException e){
            //throw new RuntimeException(e);
        //}
    }
    private static void reserveRoom(Connection con, Scanner sc){
//        System.out.println("Enter Room Id: ");
//        int room_id = sc.nextInt();
        System.out.println("Enter Room Number: ");
        int room_number = sc.nextInt();
        sc.nextLine();
        System.out.println("Enter Room Category: ");
        String category = sc.nextLine();
        System.out.println("Enter Room Price: ");
        double price = sc.nextDouble();
        System.out.println("Is Room Available: ");
        boolean is_available = sc.nextBoolean();

        String query = "INSERT INTO Room (room_number, category, price, is_available)" +
                "VALUES(" + room_number +", '"+ category+"', "+ price+", "+is_available+")";
        try(Statement stmt = con.createStatement()){
            int affectedRows = stmt.executeUpdate(query);

            if(affectedRows>0){
                System.out.println("Room reservation Successfull!!");
            }else{
                System.out.println("Reservation Failed!!");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    private static void insertCustomer_Details(Connection con, Scanner sc){
        try{
            sc.nextLine();
            System.out.println("Enter Your Name: ");
            String name = sc.nextLine();
            System.out.println("Enter Email Id: ");
            String email = sc.nextLine();
            System.out.println("Enter Your AdhaarNo: ");
            String adhaar = sc.nextLine();
            System.out.println("Enter Your Contact: ");
            String contact = sc.nextLine();

            String query = "INSERT INTO customers(name, email, adhaar, contact)" +
                    "VALUES('" +name +"', '"+email+"', '"+adhaar+"', '"+contact+"')";
            Statement stmt = con.createStatement();
            int rowAffected = stmt.executeUpdate(query);
            if(rowAffected>0){
                System.out.println("Customer inserted successfully!");
            } else{
                System.out.println("Failed to insert customer.");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void insert_Reservation(Connection con, Scanner sc){
        try{
            System.out.println("Enter Customer Id: ");
            int customer_id = sc.nextInt();

            System.out.println("Enter Room Id: ");
            int room_id = sc.nextInt();

            sc.nextLine(); // consume newline
            System.out.println("Enter Check-in Date (YYYY-MM-DD): ");
            String check_in_date = sc.nextLine();

            System.out.println("Enter Check-out Date (YYYY-MM-DD): ");
            String check_out_date = sc.nextLine();

            String query = "INSERT INTO Reservations(customer_id,room_id,check_in_date,check_out_date)"+
                    "VALUES("+customer_id+","+room_id+",'"+check_in_date+"','"+check_out_date+"')";

            Statement stmt = con.createStatement();
            int rowAffected = stmt.executeUpdate(query);
            if(rowAffected>0){
                System.out.println("ReservationSuccessfull!!");
            }else {
                System.out.println("Reservation Failed!!");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void insertPayment(Connection con, Scanner sc){
        try{
            System.out.println("Enter Reservation Id: ");
            int reservation_id = sc.nextInt();

            System.out.println("Enter Amount: ");
            double amount = sc.nextDouble();

            sc.nextLine(); // consume newline
            System.out.println("Enter Payment Date (YYYY-MM-DD): ");
            String payment_date = sc.nextLine();

            System.out.println("Enter Payment Status (Pending/Completed): ");
            String status = sc.nextLine();

            String query = "INSERT INTO Payments (reservation_id, amount, payment_date, payment_status) " +
                    "VALUES (" + reservation_id + ", " + amount + ", '" + payment_date + "', '" + status + "')";

            Statement stmt = con.createStatement();
            int rows = stmt.executeUpdate(query);

            if (rows > 0) {
                System.out.println("Payment inserted successfully!");
            } else {
                System.out.println("Failed to insert payment.");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    private static void viewReservationDetails(Connection con, Scanner sc){
        try{
            System.out.println("Reservation details: ");
            String query = "SELECT r.reservation_id,c.customer_id,c.name,c.email,c.contact,c.adhaar,rm.room_id,rm.room_number,rm.category,r.check_in_date,r.check_out_date, " +
                    "r.status,p.amount,p.payment_date,p.payment_status " +
                    "FROM Reservations r " +
                    "JOIN Customers c ON r.customer_id = c.customer_id " +
                    "JOIN Room rm ON r.room_id = rm.room_id " +
                    "LEFT JOIN Payments p ON r.reservation_id = p.reservation_id";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("Reservation details: ");
            System.out.println("+----------------+-----------+---------------+------------------------------+------------+--------------+--------+------------+----------+--------------+---------------+---------+---------+--------------+----------------+");
            System.out.println("| Reservation Id |Customer ID| Name          | Email                        |Contact     | Adhaar       |Room ID |Room Number | Category |Check_in_Date |Check_Out_Date | Status  | Amount  | Payment Date | Payment Status |");
            System.out.println("+----------------+-----------+---------------+------------------------------+------------+--------------+--------+------------+----------+--------------+---------------+---------+---------+--------------+----------------+");
            while(rs.next()){
                int reservationid = rs.getInt("reservation_id");
                int customerid = rs.getInt("customer_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String contact = rs.getString("contact");
                String adhaar = rs.getString("adhaar");
                int roomid = rs.getInt("room_id");
                int roomNumber = rs.getInt("room_number");
                String category = rs.getString("category");
                Date checkin = rs.getDate("check_in_date");
                Date checkout = rs.getDate("check_out_date");
                String status = rs.getString("status");
                double amount = rs.getDouble("amount");
                Date paydate = rs.getDate("payment_date");
                String paystatus = rs.getString("payment_status");

                System.out.printf("|%-16d|%-11d|%-15s|%-30s|%-12s|%-14s|%-8d|%-12d|%-10s|%-14s|%-15s|%-9s|%-9.2f|%-14s|%-16s|%n",
                        reservationid,customerid,name,email,contact,adhaar,roomid,roomNumber,category,checkin,checkout,status,amount,paydate,paystatus);

                System.out.println("+----------------+-----------+---------------+------------------------------+------------+--------------+--------+------------+----------+--------------+---------------+---------+---------+--------------+----------------+");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    private static void updateReservation(Connection con, Scanner sc){
        try{
            System.out.print("Enter Reservation Id: ");
            int resID = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Email: ");
            String email = sc.nextLine();
            System.out.print("Enter Adhaar: ");
            String adhaar = sc.nextLine();
            System.out.print("Enter Contact: ");
            String contact = sc.nextLine();
            System.out.print("Enter room Number: ");
            int roomnumber = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter Category(Standard/Delux/Suite):");
            String category = sc.nextLine();
            System.out.print("Enter price: ");
            double price = sc.nextDouble();
            sc.nextLine();
            System.out.print("Enter Check-in Date (YYYY-MM-DD): ");
            String checkin = sc.nextLine();
            System.out.print("Enter Check-out Date (YYYY-MM-DD): ");
            String checkout = sc.nextLine();
            System.out.print("Enter Status (Booked/Cancelled): ");
            String status = sc.nextLine();
            System.out.print("Enter Amount: ");
            double amount = sc.nextDouble();
            sc.nextLine();
            System.out.print("Enter Payment Date (YYYY-MM-DD): ");
            String paydate = sc.nextLine();
            System.out.print("Enter Payment Status (Pending/Completed): ");
            String paystatus = sc.nextLine();
            String query = "UPDATE Reservations r " +
                    "JOIN Customers c ON r.customer_id = c.customer_id " +
                    "JOIN Room rm ON r.room_id = rm.room_id " +
                    "LEFT JOIN Payments p ON r.reservation_id = p.reservation_id " +
                    "SET r.check_in_date = ?, r.check_out_date = ?, r.status = ?," +
                    "c.name = ?, c.email = ?, c.adhaar = ?,c.contact = ?,rm.room_number = ?, " +
                    "rm.category = ?,rm.price = ?, p.amount = ?, p.payment_date = ?, p.payment_status = ? " +
                    "WHERE r.reservation_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, checkin);
            pst.setString(2, checkout);
            pst.setString(3, status);
            pst.setString(4, name);
            pst.setString(5, email);
            pst.setString(6, adhaar);
            pst.setString(7, contact);
            pst.setInt(8, roomnumber);
            pst.setString(9, category);
            pst.setDouble(10, price);
            pst.setDouble(11,amount);
            pst.setString(12, paydate);
            pst.setString(13, paystatus);
            pst.setInt(14, resID);

            int rows = pst.executeUpdate();
            if(rows>0){
                System.out.println("Reservation Updated Successfully!");
            } else{
                System.out.println("No Reservation found with ID: "+resID);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    private static void CancelReservation(Connection con, Scanner sc){
        try{
            System.out.println("Enter Reservation Id to Cancel: ");
            int reservationId = sc.nextInt();

//            if(!reservationExists(con, reservationId)) {
//                System.out.println("Reservation not found for the given ID.");
//                return;
//            }

                String query = "UPDATE Reservations SET status='Cancelled' WHERE reservation_id=?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setInt(1, reservationId);
                pst.executeUpdate();
                // Free up room
                String roomSql = "SELECT room_id FROM Reservations WHERE reservation_id=?";
                PreparedStatement roomPst = con.prepareStatement(roomSql);
                roomPst.setInt(1, reservationId);
                ResultSet rs = roomPst.executeQuery();
                if(rs.next()){
                    int roomId = rs.getInt("room_id");
                    String updateSql = "UPDATE Room SET is_available=TRUE WHERE room_id=?";
                    PreparedStatement upst = con.prepareStatement(updateSql);
                    upst.setInt(1, roomId);
                    upst.executeUpdate();
                }
                System.out.println("Reservation Cancelled.");
            } catch (SQLException e){
               e.printStackTrace();
        }
    }
    private static boolean reservationExists(Connection con, int reservationId){
        try{
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = "+reservationId;
            try(Statement smt = con.createStatement();
            ResultSet resultSet = smt.executeQuery(sql)){
                return resultSet.next(); // if there is a result, the reservation exists
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false; // Handle database errors as needed
        }
    }
    private static void deleteReservation(Connection con, Scanner sc) {
        try {
            System.out.print("Enter ReservationId to delete: ");
            int reservationId = sc.nextInt();

            if (!reservationExists(con, reservationId)) {
                System.out.println("Reservation not found for the given Id: " + reservationId);
                return;
            }

            // Step 1: Delete payments linked to reservation
            String deletePayments = "DELETE FROM Payments WHERE reservation_id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(deletePayments)) {
                pstmt.setInt(1, reservationId);
                pstmt.executeUpdate();
            }

            // Step 2: Get customer_id before deleting reservation
            int customerId = -1;
            String getCustomer = "SELECT customer_id FROM Reservations WHERE reservation_id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(getCustomer)) {
                pstmt.setInt(1, reservationId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        customerId = rs.getInt("customer_id");
                    }
                }
            }

            // Step 3: Delete reservation itself
            String deleteReservation = "DELETE FROM Reservations WHERE reservation_id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(deleteReservation)) {
                pstmt.setInt(1, reservationId);
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Reservation Deleted Successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }

            // Step 4: Delete customer (optional, if you want to remove person completely)
            if (customerId != -1) {
                String deleteCustomer = "DELETE FROM Customers WHERE customer_id = ?";
                try (PreparedStatement pstmt = con.prepareStatement(deleteCustomer)) {
                    pstmt.setInt(1, customerId);
                    pstmt.executeUpdate();
                    System.out.println("Customer Deleted Successfully!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void exit() {
        try {
            System.out.print("Exiting System");
            int i = 5;
            while (i != 0) {
                System.out.print(".");
                Thread.sleep(450);
                i--;
            }
            System.out.println();
            System.out.println("Thank You for Using Hotel Reservation System!!!");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
