package org.cse13.ds.dfs.node.rmi;

import org.cse13.ds.dfs.node.Neighbour;
import org.cse13.ds.dfs.node.Node;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by kjtdi on 11/7/2017.
 */
public class RMIFileSearchRequest extends RMIRequest {

    private String fileName;
    private int hops;

    public RMIFileSearchRequest(String fileName, int hops, String fromIP, int fromPort, String toIP, int toPort) {
        super(fromIP, fromPort, toIP, toPort);
        this.fileName = fileName;
        this.hops = hops;
    }

    @Override
    public void handle(Node node) throws RemoteException, NotBoundException, MalformedURLException {
        System.out.println("Request received!!!");
    }
}
