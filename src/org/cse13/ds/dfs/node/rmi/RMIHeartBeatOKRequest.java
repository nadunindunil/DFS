package org.cse13.ds.dfs.node.rmi;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import org.cse13.ds.dfs.node.Node;

/**
 *
 * @author nadunindunil
 */
public class RMIHeartBeatOKRequest extends RMIRequest {

    public RMIHeartBeatOKRequest(String fromIP, int fromPort, String toIP, int toPort) {
        super(fromIP, fromPort, toIP, toPort);
    }

    @Override
    public void handle(Node node) throws RemoteException, NotBoundException, MalformedURLException, ConnectException {
        System.out.println("a HBOK was received from " + this.getFromIP() + "," + this.getFromPort());
        
//        node.proccessHeartBeatReceive(this.getFromIP(),this.getFromPort());
            node.processHeartBeatOK(this.getFromIP(),this.getFromPort());
    }
    
}
