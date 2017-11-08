package org.cse13.ds.dfs.node.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by nadunindunil on 11/7/17.
 */
public class RMIConnector {

    RMIServer rmiServer;

    public RMIConnector(String ip,int port) throws RemoteException, NotBoundException, MalformedURLException {

        String url = getRMIUrl(ip,port);
        rmiServer = (RMIServer) Naming.lookup(url);

    }

    private String getRMIUrl(String ip, int port){

        String URL = "rmi://"+ ip + ":"+ port + "/RMIServer";
        return URL;
    }


    public void nodeJoinRequest(RMIRequest rmiRequest) throws RemoteException, MalformedURLException, NotBoundException {
        rmiServer.nodeJoinRequestHandle(rmiRequest);
    }

    public void fileSearchRequest(RMIRequest rmiRequest) throws RemoteException, MalformedURLException, NotBoundException {
        rmiServer.fileSearchRequestHandle(rmiRequest);
    }

    public void nodeLeaveRequest() {

    }

    public void nodeJoinOkRequest(RMIRequest rmiRequest) throws RemoteException, MalformedURLException, NotBoundException {
        rmiServer.nodeJoinOkRequestHandle(rmiRequest);
    }

    public void nodeLeaveOkRequest() {

    }

    public void fileSearchOk(RMIRequest rmiRequest) throws RemoteException, NotBoundException, MalformedURLException {
        rmiServer.fileSearchOkHandle(rmiRequest);
    }

}
