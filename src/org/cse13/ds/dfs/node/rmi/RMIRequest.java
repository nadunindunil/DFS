package org.cse13.ds.dfs.node.rmi;

import org.cse13.ds.dfs.node.Node;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by nadunindunil on 11/7/17.
 */
public abstract class RMIRequest implements Serializable {

    private String fromIP;
    private int fromPort;
    private String toIP;
    private int toPort;


    public RMIRequest(String fromIP, int fromPort, String toIP, int toPort) {

        this.fromIP = fromIP;
        this.fromPort = fromPort;
        this.toIP = toIP;
        this.toPort = toPort;
    }


    public String getFromIP() {
        return fromIP;
    }

    public int getFromPort() {
        return fromPort;
    }

    public String getToIP() {
        return toIP;
    }

    public int getToPort() {
        return toPort;
    }

    public abstract void handle(Node node) throws RemoteException, NotBoundException, MalformedURLException, ConnectException;
}
