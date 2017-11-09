package org.cse13.ds.dfs.node.rmi;

import org.cse13.ds.dfs.node.Node;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
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
    public void nodeJoinOkRequestHandle(RMIRequest rmiRequest) throws RemoteException, MalformedURLException,
            NotBoundException {
        rmiRequest.handle(this.node);
    }

    @Override
    public void nodeLeaveRequestHandle(RMIRequest rmiRequest) throws RemoteException, NotBoundException, MalformedURLException {
        rmiRequest.handle(this.node);
    }


    @Override
    public void nodeLeaveOkRequestHandle(RMIRequest rmiRequest) throws RemoteException, NotBoundException, MalformedURLException {
        rmiRequest.handle(this.node);
    }

    @Override
    public void fileSearchRequestHandle(RMIRequest rmiRequest) throws RemoteException, NotBoundException, MalformedURLException {
        rmiRequest.handle(this.node);
    }

    @Override
    public void fileSearchOkHandle(RMIRequest rmiRequest) throws RemoteException, NotBoundException, MalformedURLException {
        rmiRequest.handle(this.node);
    }

    @Override
    public void nodeHBSendRequestHandle(RMIRequest rmiRequest) throws RemoteException, NotBoundException, MalformedURLException, ConnectException {
        rmiRequest.handle(this.node);
    }

    @Override
    public void nodeHBSendOkRequestHandle(RMIRequest rmiRequest) throws RemoteException, NotBoundException, MalformedURLException, ConnectException{
        rmiRequest.handle(this.node);
    }
}
