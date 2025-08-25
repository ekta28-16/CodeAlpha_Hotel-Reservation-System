import java.io.*;
import java.util.*;

class Room implements Serializable {
    int roomNo;
    String type;
    boolean booked;
    double price;

    Room(int roomNo, String type, double price) {
        this.roomNo = roomNo;
        this.type = type;
        this.price = price;
        this.booked = false;
    }

    public String toString() {
        return "Room " + roomNo + " [" + type + "] - $" + price + " - " + (booked ? "Booked" : "Available");
    }
}

class Reservation implements Serializable {
    String customer;
    Room room;

    Reservation(String customer, Room room) {
        this.customer = customer;
        this.room = room;
    }

    public String toString() {
        return "Reservation: " + customer + " -> Room " + room.roomNo + " (" + room.type + ")";
    }
}

public class HotelReservationSystem {
    static ArrayList<Room> rooms = new ArrayList<>();
    static ArrayList<Reservation> reservations = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);
    static final String FILE_NAME = "reservations.dat";

    public static void main(String[] args) {
        // Initialize rooms
        rooms.add(new Room(101, "Standard", 100));
        rooms.add(new Room(102, "Deluxe", 150));
        rooms.add(new Room(201, "Suite", 250));

        loadReservations();  // Load from file if exists

        while (true) {
            System.out.println("\n--- Hotel Reservation System ---");
            System.out.println("1. View Rooms\n2. Book Room\n3. Cancel Reservation\n4. View Reservations\n5. Exit");
            System.out.print("Choose: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> viewRooms();
                case 2 -> bookRoom();
                case 3 -> cancelReservation();
                case 4 -> viewReservations();
                case 5 -> { saveReservations(); System.out.println("Goodbye!"); return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    static void viewRooms() {
        System.out.println("\nAvailable Rooms:");
        for (Room r : rooms) {
            System.out.println(r);
        }
    }

    static void bookRoom() {
        viewRooms();
        System.out.print("Enter Room Number: ");
        int roomNo = sc.nextInt(); sc.nextLine();

        for (Room r : rooms) {
            if (r.roomNo == roomNo && !r.booked) {
                System.out.print("Enter Customer Name: ");
                String name = sc.nextLine();
                r.booked = true;
                Reservation res = new Reservation(name, r);
                reservations.add(res);
                System.out.println("Payment of $" + r.price + " successful! Booking confirmed.");

                saveReservations();
                generateReceipt(res);  // Booking receipt
                return;
            }
        }
        System.out.println("Room not available!");
    }

    static void cancelReservation() {
        System.out.print("Enter Customer Name to Cancel: ");
        String name = sc.nextLine();
        Iterator<Reservation> it = reservations.iterator();
        while (it.hasNext()) {
            Reservation res = it.next();
            if (res.customer.equalsIgnoreCase(name)) {
                res.room.booked = false;
                it.remove();
                System.out.println("Reservation cancelled for " + name);

                saveReservations();
                generateCancellationSlip(res);  // Cancellation slip
                return;
            }
        }
        System.out.println("No reservation found for " + name);
    }

    static void viewReservations() {
        if (reservations.isEmpty()) {
            System.out.println("No reservations yet.");
        } else {
            for (Reservation r : reservations) {
                System.out.println(r);
            }
        }
    }

    // Save reservations to file
    static void saveReservations() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(reservations);
        } catch (IOException e) {
            System.out.println("Error saving reservations: " + e.getMessage());
        }
    }

    // Load reservations from file
    static void loadReservations() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            reservations = (ArrayList<Reservation>) ois.readObject();
            // Mark booked rooms
            for (Reservation res : reservations) {
                for (Room r : rooms) {
                    if (r.roomNo == res.room.roomNo) {
                        r.booked = true;
                    }
                }
            }
            System.out.println("Reservations loaded from file.");
        } catch (Exception e) {
            System.out.println("No previous reservations found.");
        }
    }

    // Generate booking receipt
    static void generateReceipt(Reservation res) {
        String fileName = "Receipt_" + res.customer.replaceAll("\\s+", "_") + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.println("========== Hotel Reservation Receipt ==========");
            pw.println("Customer Name : " + res.customer);
            pw.println("Room Number   : " + res.room.roomNo);
            pw.println("Room Type     : " + res.room.type);
            pw.println("Price Paid    : $" + res.room.price);
            pw.println("Status        : Confirmed");
            pw.println("===============================================");
            System.out.println("Receipt generated: " + fileName);
        } catch (IOException e) {
            System.out.println("Error generating receipt: " + e.getMessage());
        }
    }

    // Generate cancellation slip
    static void generateCancellationSlip(Reservation res) {
        String fileName = "Cancellation_" + res.customer.replaceAll("\\s+", "_") + ".txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            pw.println("========== Hotel Reservation Cancellation ==========");
            pw.println("Customer Name : " + res.customer);
            pw.println("Room Number   : " + res.room.roomNo);
            pw.println("Room Type     : " + res.room.type);
            pw.println("Refund Amount : $" + res.room.price);
            pw.println("Status        : Cancelled");
            pw.println("====================================================");
            System.out.println("Cancellation slip generated: " + fileName);
        } catch (IOException e) {
            System.out.println("Error generating cancellation slip: " + e.getMessage());
        }
    }
}

