 package com.mycompany.dhcp_project;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class DHCP_Server {

    private final int port = 6767;
    private DatagramSocket socket;

    private final LeaseDB leaseDB = new LeaseDB();
    private final Map<String, String> staticReservations = new HashMap<>();

    private static final String RES_FILE = "reservations.txt";

    // IP Pool
    private final String POOL_START = "192.168.1.10";
    private final String POOL_END   = "192.168.1.50";

    public DHCP_Server() {
        loadReservations();
    }

    public void start() {
        try {
            socket = new DatagramSocket(port);
            System.out.println("DHCP Server running...");

            while (true) {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                processPacket(packet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processPacket(DatagramPacket packet) throws IOException {
        String msg = new String(packet.getData(), 0, packet.getLength());
        String mac = msg.trim();

        System.out.println("Received DISCOVER from MAC: " + mac);

        // Static reservation check
        if (staticReservations.containsKey(mac)) {
            sendOffer(staticReservations.get(mac), packet);
            return;
        }

        // Normal lease allocation
        String ip = leaseDB.allocateIp(mac, POOL_START, POOL_END);
        if (ip != null) {
            sendOffer(ip, packet);
        }
    }

    private void sendOffer(String ip, DatagramPacket req) throws IOException {
        byte[] data = ip.getBytes();
        DatagramPacket offer = new DatagramPacket(data, data.length,
                req.getAddress(), req.getPort());

        socket.send(offer);
        System.out.println("Offered IP: " + ip);
    }

    // ------------------------------
    // Static reservation persistence
    // ------------------------------

    public void addStaticReservation(String mac, String ip) {
        staticReservations.put(mac, ip);
        saveReservations();
        System.out.println("Added static reservation: " + mac + " -> " + ip);
    }

    private void saveReservations() {
        try (PrintWriter out = new PrintWriter(RES_FILE)) {
            for (var e : staticReservations.entrySet()) {
                out.println(e.getKey() + "=" + e.getValue());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadReservations() {
        try {
            Path p = Paths.get(RES_FILE);
            if (!Files.exists(p)) return;

            for (String line : Files.readAllLines(p)) {
                if (line.contains("=")) {
                    String[] s = line.split("=");
                    staticReservations.put(s[0], s[1]);
                }
            }

            System.out.println("Loaded reservations: " + staticReservations.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getReservations() {
        return staticReservations;
    }

    public LeaseDB getLeaseDB() { return leaseDB; }
}
