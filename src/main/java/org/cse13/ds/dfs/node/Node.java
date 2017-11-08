package org.cse13.ds.dfs.node;

import org.cse13.ds.dfs.node.rmi.RMIFileSearchRequest;
import org.cse13.ds.dfs.node.rmi.RMIJoinRequest;
import org.cse13.ds.dfs.node.rmi.RMILeaveRequest;
import org.cse13.ds.dfs.node.rmi.RMIServerImpl;
import org.cse13.ds.dfs.node.rmi.*;
import org.cse13.ds.dfs.node.utils.BootstrapCommunicator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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

    //to store files
    private HashMap<String, File> filesToStore = new HashMap<String, File>();

    //to send fileOKresponse
    RMIConnector rmiConnector;

    public Node(String ip_address) throws RemoteException, NotBoundException, MalformedURLException {
        this.ipAddress = ip_address;
        this.nodePort = generatePort();
        this.name = null;
    }

    public Node(String ip_address, String name) throws RemoteException, NotBoundException, MalformedURLException {
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

        //initialize files
        initializeFiles();

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
                    ArrayList<String> searchResults = searchFiles(outMessage.split(" ")[1]); //search file in the own directory
                    if(searchResults.size() > 0) {
                        System.out.println("File Found in My Node");
                        forwardFileSearchRequest(outMessage.split(" ")[1], 3, ipAddress, nodePort);
                    } else {
                        forwardFileSearchRequest(outMessage.split(" ")[1], 3, ipAddress, nodePort);
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

    //Randomly pick two files from the file list.
    public void initializeFiles() {

        HashMap<String, File> allFiles = new HashMap<String, File>();
        allFiles.put("Lord_of_the_Rings", new File("G:\\Films\\LR\\Lord_of_the_Rings.mov"));
        allFiles.put("Harry_Porter_1", new File("G:\\Films\\HP\\Harry_Porter_1.mov"));
        allFiles.put("Fast_and_Furious", new File("G:\\Films\\FF\\Fast_and_Furious.mov"));
        allFiles.put("La_La_Land", new File("G:\\Films\\LR\\La_La_Land.mov"));
        allFiles.put("Transformers", new File("G:\\Films\\Transformers\\Transformers.mov"));
        allFiles.put("Spider_Man_1", new File("G:\\Films\\SP\\Spider_Man_1.mov"));
        allFiles.put("XXX", new File("G:\\Films\\XXX\\XXX.mov"));

        //generate 3 random indices to pick files from hashmap
        int[] randomIndices = new Random().ints(1, 6).distinct().limit(3).toArray();

        System.out.println("Initiated Files-----------------------");
        //pick files randomly
        ArrayList<String> keysAsArray = new ArrayList<String>(allFiles.keySet());
        for (int fileIndex : randomIndices) {
            filesToStore.put(keysAsArray.get(fileIndex), allFiles.get(keysAsArray.get(fileIndex)));
            System.out.println(keysAsArray.get(fileIndex));
        }
        System.out.println("End Initiated Files-----------------------");

    }

    //search files
    public ArrayList<String> searchFiles(String fileNameToSearch) {
        ArrayList<String> searchResults = new ArrayList<String>();

        for (String fileNames : filesToStore.keySet()) {
            System.out.println(fileNames+" "+fileNameToSearch);
            if (fileNames.contains(fileNameToSearch)) {
                searchResults.add(fileNames);
            }
        }

        return searchResults;
    }

    //send file search request to neighbours
    public void forwardFileSearchRequest(String fileNameToSearch, int hops, String originatorIP, int originatorPort) throws RemoteException, NotBoundException, MalformedURLException {
        //select random neighbour to forward request
        Random r = new Random();
        Neighbour randomSuccessor = null;

        while (true) {
            randomSuccessor = MyNeighbours.get(r.nextInt(MyNeighbours.size()));
            //check whether selected node is equal to myself.
            //TODO: check the ip also
            if (randomSuccessor.getPort() != this.nodePort) {
                break;
            }
        }

        System.out.println("File Couldn't found & File Search Request forwarded");
        randomSuccessor.rmiConnector.fileSearchRequest(new RMIFileSearchRequest(fileNameToSearch, hops, ipAddress,nodePort,
                randomSuccessor.getIp(),
                randomSuccessor.getPort(), originatorIP, originatorPort));
    }

    //send search results to query originator
    public void forwardFileSearchOKResponse(ArrayList<String> searchResults, int hops, String originatorIP, int originatorPort) throws RemoteException, NotBoundException, MalformedURLException {
        Random r = new Random();
        Neighbour randomSuccessor = null;

        while (true) {
            randomSuccessor = MyNeighbours.get(r.nextInt(MyNeighbours.size()));
            //check whether selected node is equal to myself.
            //TODO: check the ip also
            if (randomSuccessor.getPort() != this.nodePort) {
                break;
            }
        }
        randomSuccessor.rmiConnector.fileSearchOk(new RMIFileSearchOKResponse(ipAddress, nodePort, originatorIP,
                originatorPort, searchResults, hops, ipAddress, nodePort));
    }

}
