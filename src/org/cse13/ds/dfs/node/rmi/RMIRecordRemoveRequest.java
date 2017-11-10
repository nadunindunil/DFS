/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cse13.ds.dfs.node.rmi;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import org.cse13.ds.dfs.node.Node;

/**
 *
 * @author kjtdi
 */
public class RMIRecordRemoveRequest extends RMIRequest{

    private String fromIP;
    private int fromPort;
    
    public RMIRecordRemoveRequest(String fromIP, int fromPort, String toIP, int toPort) {
        super(fromIP, fromPort, toIP, toPort);
        this.fromIP = fromIP;
        this.fromPort = fromPort;
    }

    @Override
    public void handle(Node node) throws RemoteException, NotBoundException, MalformedURLException, ConnectException {
        System.out.println("Removing Search Records......");
        node.removeSearchResults(fromIP, String.valueOf(fromPort));
    }
    
}
