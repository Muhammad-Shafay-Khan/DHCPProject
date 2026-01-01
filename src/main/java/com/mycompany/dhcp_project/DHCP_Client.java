 package com.mycompany.dhcp_project;

 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

 import java.net.*;

public class DHCP_Client {

    private final String mac;
    private String ip;

    public DHCP_Client(String mac) {
        this.mac = mac;
    }

    public void requestIp() {
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] data = mac.getBytes();

            DatagramPacket discover = new DatagramPacket(
                    data, data.length, InetAddress.getLocalHost(), 6767);

            socket.send(discover);

            byte[] buf = new byte[256];
            DatagramPacket offer = new DatagramPacket(buf, buf.length);
            socket.receive(offer);

            ip = new String(offer.getData(), 0, offer.getLength());
            System.out.println("Client received IP: " + ip);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getIp() {
        return ip;
    }
}
