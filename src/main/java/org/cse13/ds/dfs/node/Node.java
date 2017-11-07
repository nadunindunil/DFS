package org.cse13.ds.dfs.node;

import org.cse13.ds.dfs.node.rmi.RmiServerImpl;
import org.cse13.ds.dfs.node.utils.BootstrapCommunicator;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


public class Node {

    private final String ip_address;
    private final int node_port;
    private final String name;
    private List<Neighbour> MyNeighbours = new ArrayList<>();

    private BootstrapCommunicator bootstrapCommunicator = new BootstrapCommunicatorImpl();

    public Node(String ip_address){
        this.ip_address = ip_address;
        this.node_port = getPort();
        this.name = null;
    }

    public Node(String ip_address, String name){
        this.ip_address = ip_address;
        this.node_port = getPort();
        this.name = name;
    }

    private int getPort(){
        Random r = new Random();
        return Math.abs(r.nextInt()) % 6000 + 3000;
    }

    public String getIp_address() {
        return ip_address;
    }

    public int getNode_port() {
        return node_port;
    }

    public void addNeighbour(Neighbour neighbour){
        this.MyNeighbours.add(neighbour);
        printNeighbours();
    }

    public void removeNeighbour(String ipAddress, int port){
        if (MyNeighbours.size() != 0){
            for (Neighbour node : MyNeighbours){
                if (Objects.equals(node.getIp(), ipAddress) && node.getPort() == port){
                    MyNeighbours.remove(node);
                }
            }
        }
    }

    private void printNeighbours(){
        for (Neighbour n : MyNeighbours){
            System.out.println("Neighbour: " + n.getPort() + "," + n.getIp());
        }
    }

    public void start() throws IOException, NotBoundException {

        List<Neighbour> nodeList = register();

        System.setProperty("java.rmi.server.hostname", this.getIp_address());
        try {
            Registry registry = null;
            try {
                registry = LocateRegistry.createRegistry(this.getNode_port());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            registry.rebind("RMIServer", new RmiServerImpl());
            System.out.println("Server is Starting...");

        } catch (RemoteException ex) {
            ex.printStackTrace();
        }

        connect(nodeList);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    gracefulDeparture();
                    bootstrapCommunicator.unregister(ip_address,node_port,name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void connect(List<Neighbour> nodeList) throws IOException {

        if (nodeList != null){
            for (Neighbour node : nodeList){
                if (node.getPort() != this.node_port){
                    node.rmiConnector.nodeJoinRequest();
                }

            }
        }else{
            System.out.println("null in " + name);
        }

    }

    private void gracefulDeparture() throws IOException {
        for (Neighbour node : MyNeighbours){
        }
    }

    private List<Neighbour> register() throws IOException, NotBoundException {
         return bootstrapCommunicator.register(ip_address,node_port,name);
    }

}
