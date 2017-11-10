package org.cse13.ds.dfs.node.rmi;

import org.cse13.ds.dfs.node.Neighbour;
import org.cse13.ds.dfs.node.Node;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by nadunindunil on 11/7/17.
 */
public class RMIJoinRequest extends RMIRequest{

    ArrayList<String[]> ownIPsPorts;

    public RMIJoinRequest(String fromIP, int fromPort, String toIP, int toPort, ArrayList<String[]> ownIPsPorts) {
        super(fromIP, fromPort, toIP, toPort);
        this.ownIPsPorts = ownIPsPorts;
    }

    @Override
    public void handle(Node node) throws RemoteException, NotBoundException, MalformedURLException {
        ArrayList<String[]> allConnectedNodes = node.getAllConnectedNodes();
        boolean isExists = false;

        float probablity = (float)1.0;

        //calculate probability based on past behavior of the node.
        for(String[] connectionDetails: allConnectedNodes) {
            System.out.println("Connected Nodes Details: "+connectionDetails[0]+" "+connectionDetails[1]);
            for(String[] ownDetails: ownIPsPorts) {
                System.out.println("Owner Details: "+ownDetails[0]+" "+ownDetails[1]+" "+connectionDetails[0]+" "+connectionDetails[1]);
                if((connectionDetails[0].equals(ownDetails[0])) && (connectionDetails[1].equals(ownDetails[1]))) {
                    System.out.println("Similar thing found!!!");
                    probablity -= 0.1f;
                }
            }
        }

        //record the neighbours
        allConnectedNodes.add(new String[]{getFromIP(), String.valueOf(getFromPort())});
        node.setAllConnectedNodes(allConnectedNodes);

        System.out.println("probability: "+probablity);

        Neighbour neighbour = new Neighbour(getFromIP(),getFromPort(), probablity);
        node.addNeighbour(neighbour);

        neighbour.rmiConnector.nodeJoinOkRequest(new RMIJoinOKRequest(getToIP(),getToPort(),getFromIP(),getFromPort(), node.getOwnIPsPorts()));
    }
}
