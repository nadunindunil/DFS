package org.cse13.ds.dfs.node.rmi;

import org.cse13.ds.dfs.node.Node;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by nadunindunil on 11/7/17.
 */
public class RMIServerImpl extends UnicastRemoteObject implements RMIServer {

    private Node node;

    public RMIServerImpl(Node node) throws RemoteException {
        this.node = node;
    }


    @Override
    public void nodeJoinRequestHandle(RMIRequest rmiRequest) throws RemoteException, MalformedURLException, NotBoundException {
        rmiRequest.handle(this.node);
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

    }

    @Override
    public void fileSearchOkHandle() {

    }
}
