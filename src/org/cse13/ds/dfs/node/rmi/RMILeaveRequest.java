package org.cse13.ds.dfs.node.rmi;

import org.cse13.ds.dfs.node.Neighbour;
import org.cse13.ds.dfs.node.Node;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by nadunindunil on 11/7/17.
 */
public class RMILeaveRequest extends RMIRequest {

    public RMILeaveRequest(String fromIP, int fromPort, String toIP, int toPort) {
        super(fromIP, fromPort, toIP, toPort);
    }

    @Override
    public void handle(Node node) throws RemoteException, NotBoundException, MalformedURLException {

        System.out.println("Removing......");

        Neighbour neighbour = new Neighbour(getFromIP(),getFromPort(), (float)0.0);
        node.removeNeighbour(getFromIP(),getFromPort());

        neighbour.rmiConnector.nodeLeaveOkRequest(new RMILeaveOKRequest(getToIP(),getToPort(),getFromIP(),getFromPort
                ()));
    }
}
