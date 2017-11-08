package org.cse13.ds.dfs.node.rmi;

import org.cse13.ds.dfs.node.Node;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by kjtdi on 11/7/2017.
 */
public class RMIFileSearchRequest extends RMIRequest {

    private String fileName;
    private int originatorPort;
    private String originatorIP;
    private int hops;

    public RMIFileSearchRequest(String fileName, int hops, String fromIP, int fromPort, String toIP, int toPort, String originatorIP, int originatorPort) {
        super(fromIP, fromPort, toIP, toPort);
        this.fileName = fileName;
        this.hops = hops;
        this.originatorIP = originatorIP;
        this.originatorPort = originatorPort;
    }

    //handle file request here.
    @Override
    public void handle(Node node) throws RemoteException, NotBoundException, MalformedURLException {
        System.out.println("Request received!!!");
        ArrayList<String> searchResults =  node.searchFiles(fileName); //search file in the node
        hops--;
        if(searchResults.size() > 0) {
            System.out.println("File Found!!!");
            node.forwardFileSearchOKResponse(searchResults, hops, originatorIP, originatorPort);
        } else{
            if(hops > 0){
                node.forwardFileSearchRequest(fileName, hops, originatorIP, originatorPort); //forward request to a neighbour
            }else {
                System.out.println("File Couldn't found!!!");
            }
        }
    }
}
