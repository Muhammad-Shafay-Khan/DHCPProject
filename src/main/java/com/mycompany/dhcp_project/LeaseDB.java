 package com.mycompany.dhcp_project;

 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

 import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LeaseDB {

    private final Map<String, Lease> leases = new HashMap<>();

    private static final String LEASE_FILE = "leases.txt";

    public LeaseDB() {
        loadLeases();
    }

    public synchronized void addLease(Lease lease) {
        leases.put(lease.getIp(), lease);
        saveLeases();
    }

    public synchronized Lease getLeaseByMac(String mac) {
        return leases.values().stream()
                .filter(l -> l.getMac().equals(mac) && !l.isExpired())
                .findFirst().orElse(null);
    }

    public synchronized boolean isIpFree(String ip) {
        Lease lease = leases.get(ip);
        return lease == null || lease.isExpired();
    }

    public synchronized String allocateIp(String mac, String poolStart, String poolEnd) {
        // 1. Check if client already has an active lease
        Lease existing = getLeaseByMac(mac);
        if (existing != null) {
            return existing.getIp();
        }

        // 2. Linear IP search
        String[] s = poolStart.split("\\.");
        String[] e = poolEnd.split("\\.");

        int start = Integer.parseInt(s[3]);
        int end = Integer.parseInt(e[3]);

        for (int i = start; i <= end; i++) {
            String ip = s[0] + "." + s[1] + "." + s[2] + "." + i;
            if (isIpFree(ip)) {
                Lease lease = new Lease(ip, mac, System.currentTimeMillis() + 3600_000);
                addLease(lease);
                return ip;
            }
        }

        return null; // No IP available
    }

    private synchronized void saveLeases() {
        try (PrintWriter out = new PrintWriter(LEASE_FILE)) {
            for (Lease l : leases.values()) {
                out.println(l.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void loadLeases() {
        try {
            Path p = Paths.get(LEASE_FILE);
            if (!Files.exists(p)) return;

            for (String line : Files.readAllLines(p)) {
                Lease l = Lease.fromString(line);
                if (l != null && !l.isExpired()) {
                    leases.put(l.getIp(), l);
                }
            }

            System.out.println("Loaded leases: " + leases.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Lease> getAll() {
        return leases;
    }
}
