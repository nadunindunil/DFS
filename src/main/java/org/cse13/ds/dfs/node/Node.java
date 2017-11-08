package org.cse13.ds.dfs.node;

import org.cse13.ds.dfs.node.rmi.RMIFileSearchRequest;
import org.cse13.ds.dfs.node.rmi.RMIJoinRequest;
import org.cse13.ds.dfs.node.rmi.RMILeaveRequest;
import org.cse13.ds.dfs.node.rmi.RMIServerImpl;
import org.cse13.ds.dfs.node.utils.BootstrapCommunicator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;


public class Node {

    private final String ipAddress;
    private final int nodePort;
    private final String name;
    private List<Neighbour> MyNeighbours = new ArrayList<>();

    private BootstrapCommunicator bootstrapCommunicator = new BootstrapCommunicatorImpl();

    public Node(String ip_address) {
        this.ipAddress = ip_address;
        this.nodePort = generatePort();
        this.name = null;
    }

    public Node(String ip_address, String name) {
        this.ipAddress = ip_address;
        this.nodePort = generatePort();
        this.name = name;
    }

    private int generatePort() {
        Random r = new Random();
        return Math.abs(r.nextInt()) % 6000 + 3000;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getNodePort() {
        return nodePort;
    }

    public synchronized void addNeighbour(Neighbour neighbour) {
        this.MyNeighbours.add(neighbour);
        printNeighbours();
    }

    public synchronized void removeNeighbour(String ipAddress, int port) {
        List<Neighbour> remove = new ArrayList<Neighbour>();
        if (MyNeighbours.size() != 0) {
            for (Neighbour node : MyNeighbours) {
                if (Objects.equals(node.getIp(), ipAddress) && node.getPort() == port) {
                    remove.add(node);
                }
            }
            MyNeighbours.removeAll(remove);
            System.out.println("Print in remove neighbour");
            printNeighbours();
        }
    }

    public void connect(List<Neighbour> nodeList) throws IOException, NotBoundException {

        if (nodeList != null){
            for (Neighbour node : nodeList){
                if (node.getPort() != this.nodePort){
                    node.rmiConnector.nodeJoinRequest(new RMIJoinRequest(ipAddress, nodePort,node.getIp(),
                            node.getPort()));
                }

            }
        } else {
            System.out.println("null in " + name);
        }
    }

    private void gracefulDeparture() throws IOException, NotBoundException {
        for (Neighbour node : MyNeighbours){
            node.rmiConnector.nodeLeaveRequest(new RMILeaveRequest(ipAddress, nodePort,node.getIp(),
                    node.getPort()));
        }

        MyNeighbours.clear();
    }

    private List<Neighbour> register() throws IOException, NotBoundException {
        return bootstrapCommunicator.register(ipAddress, nodePort, name);
    }

    private void printNeighbours() {
        for (Neighbour n : MyNeighbours) {
            System.out.println("Neighbour: " + n.getPort() + "," + n.getIp());
        }
    }

    public void start() throws IOException, NotBoundException {

        List<Neighbour> nodeList = register();

        System.setProperty("java.rmi.server.hostname", this.getIpAddress());
        try {
            Registry registry = null;
            try {
                registry = LocateRegistry.createRegistry(this.getNodePort());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (registry == null) throw new AssertionError();
            registry.rebind("RMIServer", new RMIServerImpl(this));
            System.out.println("Server is Starting...");

        } catch (RemoteException ex) {
            ex.printStackTrace();
        }

        connect(nodeList);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                gracefulDeparture();
                bootstrapCommunicator.unregister(ipAddress, nodePort, name);
                Thread.sleep(4000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

    }

    //read keyboard input
    public void readStdin() { //get input from command line
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (true) {
                String outMessage = stdin.readLine();

                if (outMessage.contains("ser")) {
                    System.out.println("AAAA");
                    for (Neighbour node : MyNeighbours) {
                        if (node.getPort() != this.nodePort) {
                            System.out.println("BBBB");
                            node.rmiConnector.fileSearchRequest(new RMIFileSearchRequest("Test.txt", 3, ipAddress, nodePort,node.getIp(),
                                    node.getPort()));
                        }
                    }
                } else {
                    System.out.println("null in " + name);
                }
            }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

}
