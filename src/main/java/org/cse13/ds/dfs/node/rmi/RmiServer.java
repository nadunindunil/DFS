package org.cse13.ds.dfs.node.rmi;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by nadunindunil on 11/7/17.
 */
public interface RMIServer extends Remote {

    void nodeJoinRequestHandle(RMIRequest rmiRequest) throws RemoteException, MalformedURLException, NotBoundException;
    void nodeLeaveRequestHandle() throws RemoteException;
    void nodeJoinOkRequestHandle() throws RemoteException;
    void nodeLeaveOkRequestHandle() throws RemoteException;
    void fileSearchRequestHandle() throws RemoteException;
    void fileSearchOkHandle() throws RemoteException;

}
