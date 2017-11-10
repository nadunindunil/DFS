package org.cse13.ds.dfs.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.NotBoundException;
import java.util.Enumeration;

/**
 * Created by nadunindunil on 11/6/17.
 */
public class NodeDriver {

    public static void main(String[] args) throws IOException, NotBoundException {

        String ip="";

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            int position =0;
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                //This filters out the interfaces
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    ip = addr.getHostAddress();
                    position++;
                    
                    if (position == 0 ) {
                        System.out.println(iface.getDisplayName() + " " + ip);
                    }

                    else{
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        
        System.out.println("IP Address:"+ip);
        Node n1 = new Node(ip);

        n1.start();

        //starting listening to keyboard input from a seperate thread
        Thread stdReadThread = new Thread(new Runnable() {
            public void run() {
                System.out.println("std listener started...");
                n1.readStdin();
            }
        });

        stdReadThread.start();

    }
}
