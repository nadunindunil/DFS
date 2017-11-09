package org.cse13.ds.dfs.node.rmi;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by nadunindunil on 11/7/17.
 */
public interface RMIServer extends Remote {

    void nodeJoinRequestHandle(RMIRequest rmiRequest) throws RemoteException, MalformedURLException, NotBoundException;
    void nodeJoinOkRequestHandle(RMIRequest rmiRequest) throws RemoteException, MalformedURLException, NotBoundException;
    void nodeLeaveRequestHandle(RMIRequest rmiRequest) throws RemoteException, NotBoundException, MalformedURLException;
    void nodeLeaveOkRequestHandle(RMIRequest rmiRequest) throws RemoteException, NotBoundException, MalformedURLException;
    void fileSearchRequestHandle(RMIRequest rmiRequest) throws RemoteException, NotBoundException, MalformedURLException;
    void fileSearchOkHandle(RMIRequest rmiRequest) throws RemoteException, NotBoundException, MalformedURLException;
    void nodeHBSendRequestHandle(RMIRequest rmiRequest) throws RemoteException, NotBoundException, MalformedURLException, ConnectException;
    void nodeHBSendOkRequestHandle(RMIRequest rmiRequest) throws RemoteException, NotBoundException, MalformedURLException, ConnectException;

}
