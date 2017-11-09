package org.cse13.ds.dfs.node;

import org.cse13.ds.dfs.node.rmi.RMIConnector;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import org.cse13.ds.dfs.heartBeater.HeartBeatImpl;
import org.cse13.ds.dfs.heartBeater.HeartBeater;

public class Neighbour {
    private String ip;
    private int port;
    private String username;
    private float probability;
    public RMIConnector rmiConnector;
    private HeartBeater heartBeater;

    public Neighbour(String ip, int port ) throws RemoteException, NotBoundException, MalformedURLException {
        this.ip = ip;
        this.port = port;
        this.rmiConnector = new RMIConnector(ip,port);
        this.heartBeater = new HeartBeatImpl();
    }

    public Neighbour(String ip, int port, String username) throws RemoteException, NotBoundException, MalformedURLException {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.probability = 1;
        this.rmiConnector = new RMIConnector(ip,port);
        this.heartBeater = new HeartBeatImpl();
    }

    public Neighbour(String ip, int port, String username, float probability) throws RemoteException, NotBoundException, MalformedURLException {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.probability = probability;
        this.rmiConnector = new RMIConnector(ip,port);
        this.heartBeater = new HeartBeatImpl();
    }
    
    public HeartBeater getHeartBeater(){
        return this.heartBeater;
    }

    public String getIp() {
        return this.ip;
    }

    public String getUsername() {
        return this.username;
    }

    public int getPort() {
        return this.port;
    }
}
