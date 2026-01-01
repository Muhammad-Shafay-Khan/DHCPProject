 package com.mycompany.dhcp_project;

public class Lease {
    private String ip;
    private String mac;
    private long expiryTime;

    public Lease(String ip, String mac, long expiryTime) {
        this.ip = ip;
        this.mac = mac;
        this.expiryTime = expiryTime;
    }

    public String getIp(){
        return ip; 
    }
    public String getMac(){
        return mac;
    }
    public long getExpiryTime(){
        return expiryTime; 
    }

    public boolean isExpired(){
        return System.currentTimeMillis() > expiryTime;
    }

    @Override
    public String toString() {
        return ip + "," + mac + "," + expiryTime;
    }

    public static Lease fromString(String line) {
        try {
            String[] p = line.split(",");
            return new Lease(p[0], p[1], Long.parseLong(p[2]));
        } catch (Exception e) {
            return null;
        }
    }
}
