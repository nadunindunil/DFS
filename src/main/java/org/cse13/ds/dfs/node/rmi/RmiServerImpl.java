package org.cse13.ds.dfs.node.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by nadunindunil on 11/7/17.
 */
public class RmiServerImpl extends UnicastRemoteObject implements RmiServer {

    public RmiServerImpl() throws RemoteException {
    }

    @Override
    public void nodeJoinRequestHandle() {
        System.out.println("inside node join handler");
    }

    @Override
    public void nodeLeaveRequestHandle() {

    }

    @Override
    public void nodeJoinOkRequestHandle() {

    }

    @Override
    public void nodeLeaveOkRequestHandle() {

    }

    @Override
    public void fileSearchRequestHandle() {
        System.out.println("YEEEIIII");
    }

    @Override
    public void fileSearchOkHandle() {

    }
}
