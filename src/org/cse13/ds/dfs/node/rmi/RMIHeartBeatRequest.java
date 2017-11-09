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
import org.cse13.ds.dfs.node.Neighbour;
import org.cse13.ds.dfs.node.Node;

/**
 *
 * @author nadunindunil
 */
public class RMIHeartBeatRequest extends RMIRequest {

    public RMIHeartBeatRequest(String fromIP, int fromPort, String toIP, int toPort) {
        super(fromIP, fromPort, toIP, toPort);
    }

    @Override
    public void handle(Node node) throws RemoteException, NotBoundException, MalformedURLException, ConnectException {
        System.out.println("a HB received from" + this.getFromIP() + " , " + this.getFromPort());
        
        Neighbour neighbour = new Neighbour(getFromIP(),getFromPort());
        neighbour.rmiConnector.nodeHBSendOKRequest(new RMIHeartBeatOKRequest(getToIP(),getToPort(),getFromIP(),getFromPort
                ()));
    }
    
}
