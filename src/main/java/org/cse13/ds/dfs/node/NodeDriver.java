package org.cse13.ds.dfs.node;

import java.io.IOException;
import java.rmi.NotBoundException;

/**
 * Created by nadunindunil on 11/6/17.
 */
public class NodeDriver {
    public static void main(String[] args) throws IOException, NotBoundException {
        String server_ip="10.8.112.6";
        Node n1 = new Node("localhost", "n1");

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
