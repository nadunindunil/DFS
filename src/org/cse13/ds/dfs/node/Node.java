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
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cse13.ds.dfs.heartBeater.HeartBeater;

public class Node {

    private final String ipAddress;
    private final int nodePort;
    private final String name;
    private volatile List<Neighbour> MyNeighbours = new ArrayList<>();
    private HeartBeater heartBeater;
    private BootstrapCommunicator bootstrapCommunicator = new BootstrapCommunicatorImpl();
    private ArrayList<String[]> searchResultsToDisplay;
    private NodeDriverUI ui;

    private HashMap<String, File> filesToStore = new HashMap<>();

    //to keep extra info about files stored in other nodes
    private HashMap<String, String[]> searchedResults = new HashMap<>();

    //to send fileOKresponse
    RMIConnector rmiConnector;
    
    private volatile HashMap<Neighbour, Integer> MyNeighbourHeartBeats = new HashMap<>();

    public Node(String ip_address) throws RemoteException, NotBoundException, MalformedURLException {
        this.ipAddress = ip_address;
        this.nodePort = generatePort();
        this.name = null;
    }

    public Node(String ip_address, String name, NodeDriverUI ui) throws RemoteException, NotBoundException, MalformedURLException {
        this.ipAddress = ip_address;
        this.nodePort = generatePort();
        this.name = name;
        this.ui = ui;
    }

    //to store files
    public HashMap<String, File> getFilesToStore() {
        return filesToStore;
    }
    
    public HeartBeater getHeartBeater(){
        return this.heartBeater;
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
    
    private void addNeighboursToHearBeatList(){
        MyNeighbours.stream().forEachOrdered((node) -> MyNeighbourHeartBeats.put(node, 0));
    }

    public synchronized void addNeighbour(Neighbour neighbour) {
        this.MyNeighbours.add(neighbour);
        printNeighbours();
    }

    public synchronized void removeNeighbour(String ipAddress, int port) {
        List<Neighbour> remove = new ArrayList<>();
        if (!MyNeighbours.isEmpty()) {
            MyNeighbours.stream().filter((node) -> (Objects.equals(node.getIp(), ipAddress) && node.getPort() == port)).forEachOrdered((node) -> {
                remove.add(node);
            });
        }
    }

    public void connect(List<Neighbour> nodeList) throws IOException, NotBoundException {

        if (nodeList != null) {
            for (Neighbour node : nodeList) {
                if (node.getPort() != this.nodePort) {
                    node.rmiConnector.nodeJoinRequest(new RMIJoinRequest(ipAddress, nodePort, node.getIp(),
                            node.getPort()));
                }

            }
        } else {
            System.out.println("null in " + name);
        }
    }

    private void gracefulDeparture() throws IOException, NotBoundException {
        for (Neighbour node : MyNeighbours) {
            node.rmiConnector.nodeLeaveRequest(new RMILeaveRequest(ipAddress, nodePort, node.getIp(),
                    node.getPort()));
        }

        MyNeighbours.clear();
    }
    
    //////////////////////// heartbeat /////////////////////////////////////////////
    
    public void processHeartBeatOK(String ipAddress, int port){
        MyNeighbourHeartBeats.forEach((index,value) -> {
            if (index.getIp() == ipAddress && index.getPort() == port){
                System.out.println("inside process heartbeatOK success");
                MyNeighbourHeartBeats.put(index,0);
            }
        });
    }
    
    private void processHeartBeatSend() throws RemoteException, NotBoundException, MalformedURLException, ConnectException, InterruptedException{
        if (!MyNeighbours.isEmpty()) {
            for (Neighbour node : MyNeighbours) {
                if (node.getPort() != this.nodePort) {
                    System.out.println("details :" + node.getIp() + "," + node.getPort());
                    node.rmiConnector.nodeHBSendRequest(new RMIHeartBeatRequest(ipAddress,nodePort,node.getIp(),
                    node.getPort()));
                }
            }
        }
    }
    
    private void proccessHeartBeatReceive() throws InterruptedException{
        
        System.out.println("inside hashmap function, length: " + MyNeighbourHeartBeats.size());
        
        MyNeighbourHeartBeats.forEach((index,value) -> {
            System.out.println(index + ", " + value);
            if (value < -5){
                try {
                    // heartbeat has been lost for three times!!!
                    removeNeighbour(index.getIp(),index.getPort());
                    removeHeartBeater(index.getIp(),index.getPort());
                    System.out.println("removing node :" + index.getPort() + " from system");
                } catch (InterruptedException ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else {
                System.out.println("decreasing 1 from node, value: " + value);
                MyNeighbourHeartBeats.put(index,MyNeighbourHeartBeats.get(index) - 1);
            }
        });
    }
    
    private synchronized void removeHeartBeater(String ipAddress, int port) throws InterruptedException{
        Neighbour neighbour = null;
        for (Neighbour key : MyNeighbourHeartBeats.keySet()) {
            if (key.getIp() == ipAddress && key.getPort() == port){
                neighbour = key;
            }  
        }
        MyNeighbourHeartBeats.remove(neighbour);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    
    
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
            if (registry == null) {
                throw new AssertionError();
            }
            registry.rebind("RMIServer", new RMIServerImpl(this));
            System.out.println("Server is Starting...");

        } catch (RemoteException ex) {
            ex.printStackTrace();
        }

        connect(nodeList);
        addNeighboursToHearBeatList();
        
        Runnable runnableHeartBeatSender = () -> {
            Timer timer = new Timer();

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        System.out.println("Sending Heart Beat");
                        processHeartBeatSend();
                    } catch (MalformedURLException | NotBoundException | RemoteException e) {
                        System.out.println("e");
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }, 2*1000, 2*1000);
        };
        Thread heartBeatSenderThread = new Thread(runnableHeartBeatSender);
        heartBeatSenderThread.start();
        
        ////////////////////////////////////////////////////////////////////////
        
        Runnable runnableHeartBeatReceiver = () -> {
            Timer timer = new Timer();

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        System.out.println("Receiveing Heart beat");
                        proccessHeartBeatReceive();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 10*1000, 10*1000);
        };
        Thread heartBeatReceiveThread = new Thread(runnableHeartBeatReceiver);
        heartBeatReceiveThread.start();
        
        

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
                    if (searchResults.size() > 0) {
                        System.out.println("File Found in My Node");
                    } else {
                        //check whether filename is already included in previous search results
                        String[] ownersDetailsOfFiles = searchPreviousSearchResults(outMessage.split(" ")[1]);
                        if (ownersDetailsOfFiles != null) {
                            //forward request to owner
                            System.out.println("File found from previous searched results. Request is forwarded directly to the owner.");
                            forwardFileSearchRequestToOwner(outMessage.split(" ")[1], 3, ownersDetailsOfFiles[0], Integer.parseInt(ownersDetailsOfFiles[1]), ipAddress, nodePort);
                        } else {
                            forwardFileSearchRequest(outMessage.split(" ")[1], 3, ipAddress, nodePort); //forward request to a neighbour
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

    public void searchInUI(String text) {
        ArrayList<String> searchResults = searchFiles(text); //search file in the own directory
        if (searchResults.size() > 0) {
            System.out.println("File Found in My Node");
        } else {
            //check whether filename is already included in previous search results
            String[] ownersDetailsOfFiles = searchPreviousSearchResults(text);
            if (ownersDetailsOfFiles != null) {
                try {
                    //forward request to owner
                    System.out.println("File found from previous searched results. Request is forwarded directly to the owner.");
                    forwardFileSearchRequestToOwner(text, 3, ownersDetailsOfFiles[0], Integer.parseInt(ownersDetailsOfFiles[1]), ipAddress, nodePort);
                } catch (RemoteException ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NotBoundException ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    forwardFileSearchRequest(text, 3, ipAddress, nodePort); //forward request to a neighbour
                } catch (RemoteException ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NotBoundException ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    //Randomly pick two files from the file list.
    public void initializeFiles() {

        HashMap<String, File> allFiles = new HashMap<String, File>();
        allFiles.put("Lord of the_Rings", new File("G:\\Films\\LR\\Lord_of_the_Rings.mov"));
        allFiles.put("Harry Porter 1", new File("G:\\Films\\HP\\Harry_Porter_1.mov"));
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
            if (fileNames.contains(fileNameToSearch)) {
                searchResults.add(fileNames);
            }
        }

        return searchResults;
    }

    public String[] searchPreviousSearchResults(String fileNameToSearch) {
        //search files in previous search results
        String[] ownerDetails = null;

        for (String fileNames : searchedResults.keySet()) {
            if (fileNames.contains(fileNameToSearch)) {
                ownerDetails = searchedResults.get(fileNames);
            }
        }

        return ownerDetails;
    }

    //send to owner of files to double check the existence of the file
    public void forwardFileSearchRequestToOwner(String fileNameToSearch, int hops, String ownerIP, int ownerPort, String originatorIP, int originatorPort) throws RemoteException, NotBoundException, MalformedURLException {
        Neighbour n = new Neighbour(ownerIP, ownerPort);
        if (n != null) {
            n.rmiConnector.fileSearchRequest(new RMIFileSearchRequest(fileNameToSearch, hops, ipAddress, nodePort, ownerIP, ownerPort, originatorIP, originatorPort));
        }
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
        randomSuccessor.rmiConnector.fileSearchRequest(new RMIFileSearchRequest(fileNameToSearch, hops, ipAddress, nodePort,
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
        System.out.println(originatorIP + " " + originatorPort);
        System.out.println(randomSuccessor.getIp() + " " + randomSuccessor.getPort());

        Neighbour n = new Neighbour(originatorIP, originatorPort);
        n.rmiConnector.fileSearchOk(new RMIFileSearchOKResponse(ipAddress, nodePort, originatorIP,
                originatorPort, searchResults, hops, ipAddress, nodePort));
    }

    //save searchedResults
    public void saveSearchedResults(String filename, String ip, int port) {
        ArrayList<String> keysAsArray = new ArrayList<String>(searchedResults.keySet());
        boolean isExist = false;

        //check whether filename exists already
        for (String key : keysAsArray) {
            if (key.equals(filename)) {
                isExist = true;
            }
        }

        //save search results
        if (!isExist) {
            searchedResults.put(filename, new String[]{ip, String.valueOf(port)});
        }

    }
    
    public void setSearchResultsToDisplay(ArrayList<String[]> searchResultsToDisplay) {
        this.searchResultsToDisplay = searchResultsToDisplay;
        ui.setSearchResults(searchResultsToDisplay);
    }

    public ArrayList<String[]> getSearchResultsToDisplay() {
        return searchResultsToDisplay;
    }

}
