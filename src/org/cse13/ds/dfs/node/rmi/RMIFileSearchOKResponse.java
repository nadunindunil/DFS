package org.cse13.ds.dfs.node.rmi;

import org.cse13.ds.dfs.node.Node;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by kjtdi on 11/8/2017.
 */
public class RMIFileSearchOKResponse extends RMIRequest {

    private ArrayList<String> searchResults;
    private int numberOfHops;
    private String ownerIP;
    private int ownerPort;

    public RMIFileSearchOKResponse(String fromIP, int fromPort, String toIP, int toPort, ArrayList<String> searchResults, int numberOfHops, String ownerIP, int ownerPort) {
        super(fromIP, fromPort, toIP, toPort);
        this.searchResults = searchResults;
        this.numberOfHops = numberOfHops;
        this.ownerIP = ownerIP;
        this.ownerPort = ownerPort;
    }

    @Override
    public void handle(Node node) throws RemoteException, NotBoundException, MalformedURLException {
        System.out.println("File Found in....");
        System.out.println("Owner IP : " + ownerIP);
        System.out.println("Owner Port : " + ownerPort);
        System.out.println("Number of Hops : " + (3 - numberOfHops));

        ArrayList<String[]> searchResultsToDisplay = new ArrayList<String[]>();
        
        System.out.println("Files Found-----------------");
        for(String searchResult: searchResults) {
            System.out.println(searchResult);
            searchResultsToDisplay.add(new String[]{searchResult, ownerIP, String.valueOf(ownerPort)});
            node.saveSearchedResults(searchResult, ownerIP, ownerPort); //save searched results future lookups
        }
        
        node.setSearchResultsToDisplay(searchResultsToDisplay);
    }
}
