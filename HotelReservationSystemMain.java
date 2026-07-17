package Hotel_Reservation_System;

import java.io.*;
import java.util.*;

// Room class
class Room {
    int roomId;
    String category; // Standard, Deluxe, Suite
    boolean isAvailable;

    Room(int roomId, String category) {
        this.roomId = roomId;
        this.category = category;
        this.isAvailable = true;
    }
}

// Reservation class
class Reservation {
    int reservationId;
    String customerName;
    Room room;
    String checkInDate;
    String checkOutDate;
    boolean isPaid;

    Reservation(int reservationId, String customerName, Room room,
                String checkInDate, String checkOutDate) {
        this.reservationId = reservationId;
        this.customerName = customerName;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.isPaid = false;
    }
}

// Hotel class (Manager)
class Hotel {
    ArrayList<Room> rooms = new ArrayList<>();
    ArrayList<Reservation> reservations = new ArrayList<>();
    int reservationCounter = 1;

    // Add rooms
    public void addRoom(Room room) {
        rooms.add(room);
    }

    // Search available room by category
    public Room searchRoom(String category) {
        for(Room r : rooms) {
            if(r.category.equalsIgnoreCase(category) && r.isAvailable) {
                return r;
            }
        }
        return null;
    }

    // Book room
    public Reservation bookRoom(String customerName, String category,
                                String checkIn, String checkOut) {
        Room room = searchRoom(category);
        if(room == null) {
            System.out.println("❌ No available " + category + " rooms.");
            return null;
        }
        room.isAvailable = false;
        Reservation res = new Reservation(reservationCounter++, customerName, room, checkIn, checkOut);
        reservations.add(res);
        saveReservation(res);
        System.out.println("✅ Reservation successful! ID: " + res.reservationId);
        return res;
    }

    // Cancel reservation
    public void cancelReservation(int reservationId) {
        for(Reservation r : reservations) {
            if(r.reservationId == reservationId) {
                r.room.isAvailable = true;
                reservations.remove(r);
                System.out.println("✅ Reservation cancelled.");
                return;
            }
        }
        System.out.println("❌ Reservation not found.");
    }

    // Payment simulation
    public void makePayment(int reservationId) {
        for(Reservation r : reservations) {
            if(r.reservationId == reservationId) {
                r.isPaid = true;
                System.out.println("💳 Payment successful for Reservation #" + reservationId);
                return;
            }
        }
        System.out.println("❌ Reservation not found.");
    }

    // View booking details
    public void viewReservations() {
        if(reservations.isEmpty()) {
            System.out.println("No reservations yet.");
            return;
        }
        for(Reservation r : reservations) {
            System.out.println("Reservation #" + r.reservationId +
                    " | Customer: " + r.customerName +
                    " | Room: " + r.room.category +
                    " | Dates: " + r.checkInDate + " to " + r.checkOutDate +
                    " | Paid: " + r.isPaid);
        }
    }

    // File I/O: Save reservations
    private void saveReservation(Reservation res) {
        try (FileWriter fw = new FileWriter("reservations.txt", true)) {
            fw.write(res.reservationId + "," + res.customerName + "," +
                    res.room.roomId + "," + res.room.category + "," +
                    res.checkInDate + "," + res.checkOutDate + "," +
                    res.isPaid + "\n");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

// Main Program
public class HotelReservationSystemMain {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Hotel hotel = new Hotel();

        // Add sample rooms
        hotel.addRoom(new Room(101, "Standard"));
        hotel.addRoom(new Room(102, "Deluxe"));
        hotel.addRoom(new Room(103, "Suite"));

        while(true) {
            System.out.println("\n🏨 Hotel Reservation System");
            System.out.println("1. Search & Book Room");
            System.out.println("2. Cancel Reservation");
            System.out.println("3. Make Payment");
            System.out.println("4. View Reservations");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch(choice) {
                case 1:
                    System.out.print("Enter Customer Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Room Category (Standard/Deluxe/Suite): ");
                    String category = sc.nextLine();
                    System.out.print("Enter Check-in Date (YYYY-MM-DD): ");
                    String checkIn = sc.nextLine();
                    System.out.print("Enter Check-out Date (YYYY-MM-DD): ");
                    String checkOut = sc.nextLine();
                    hotel.bookRoom(name, category, checkIn, checkOut);
                    break;

                case 2:
                    System.out.print("Enter Reservation ID to cancel: ");
                    int cancelId = sc.nextInt();
                    hotel.cancelReservation(cancelId);
                    break;

                case 3:
                    System.out.print("Enter Reservation ID for payment: ");
                    int payId = sc.nextInt();
                    hotel.makePayment(payId);
                    break;

                case 4:
                    hotel.viewReservations();
                    break;

                case 5:
                    System.out.println("Exiting... Goodbye!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}

