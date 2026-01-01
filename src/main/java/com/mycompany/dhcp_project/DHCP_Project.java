 package com.mycompany.dhcp_project;
 

//import com.mycompany.dhcpprojject.DHCP_Client;
//import com.mycompany.dhcpprojject.DHCP_GUI;
//import com.mycompany.dhcpprojject.DHCP_Server;
//import com.mycompany.dhcpprojject.Lease;
import java.util.Map;
import java.util.Scanner;

public class DHCP_Project {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("=== DHCP PROJECT ===");
        System.out.println("1. Start DHCP Server (GUI)");
        System.out.println("2. Start DHCP Client (request IP)");
        System.out.println("3. Show current leases and reservations");
        System.out.print("Select option: ");
        int option = sc.nextInt();
        sc.nextLine(); // consume newline

        // Create server object once, shared with GUI
        DHCP_Server server = new DHCP_Server();

        switch (option) {

            case 1:
                System.out.println("Launching DHCP Server GUI...");
                DHCP_GUI.main(null); // GUI has buttons to add reservations
                break;

            case 2:
                System.out.print("Enter MAC address for client: ");
                String mac = sc.nextLine().trim();
                DHCP_Client client = new DHCP_Client(mac);
                client.requestIp();
                System.out.println("Client received IP: " + client.getIp());
                break;

            case 3:
                System.out.println("\n=== Static Reservations ===");
                Map<String, String> reservations = server.getReservations();
                if (reservations.isEmpty()) {
                    System.out.println("No static reservations yet.");
                } else {
                    reservations.forEach((macAddr, ip) ->
                            System.out.println("MAC: " + macAddr + " -> IP: " + ip));
                }

                System.out.println("\n=== Active Leases ===");
                Map<String, Lease> leases = server.getLeaseDB().getAll();
                if (leases.isEmpty()) {
                    System.out.println("No active leases yet.");
                } else {
                    leases.forEach((ipAddr, lease) ->
                            System.out.println("IP: " + lease.getIp() +
                                    " | MAC: " + lease.getMac() +
                                    " | Expiry: " + lease.getExpiryTime()));
                }
                break;

            default:
                System.out.println("Invalid option.");
        }
        
    }
}
