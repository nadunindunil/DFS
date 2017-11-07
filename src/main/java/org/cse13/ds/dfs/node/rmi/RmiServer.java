package org.cse13.ds.dfs.node.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by nadunindunil on 11/7/17.
 */
public interface RmiServer extends Remote {

    void nodeJoinRequestHandle() throws RemoteException;
    void nodeLeaveRequestHandle() throws RemoteException;
    void nodeJoinOkRequestHandle() throws RemoteException;
    void nodeLeaveOkRequestHandle() throws RemoteException;
    void fileSearchRequestHandle() throws RemoteException;
    void fileSearchOkHandle() throws RemoteException;

}
