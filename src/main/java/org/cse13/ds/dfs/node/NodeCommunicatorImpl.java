package org.cse13.ds.dfs.node;

import org.cse13.ds.dfs.node.utils.NodeCommunicator;
import org.cse13.ds.dfs.node.utils.Request;

import java.io.IOException;
import java.net.*;

import static org.cse13.ds.dfs.Constants.JOIN_FORMAT;
import static org.cse13.ds.dfs.Constants.LEAVE_FORMAT;

/**
 * Created by nadunindunil on 11/6/17.
 */
public class NodeCommunicatorImpl implements NodeCommunicator {

    private Node node;

    public NodeCommunicatorImpl(Node node){
        this.node = node;
    }

    @Override
    public void connect(String ipAddress, int port) throws IOException{
        String msg = String.format(JOIN_FORMAT, this.node.getIp_address(), this.node.getNode_port());
        String request = Request.create(msg);
        sendRequest(request,ipAddress,port);

    }

    @Override
    public void disconnect(String ipAddress, int port) throws IOException {
        String msg = String.format(LEAVE_FORMAT, this.node.getIp_address(), this.node.getNode_port());
        String request = Request.create(msg);
        sendRequest(request,ipAddress,port);
    }

    private void sendRequest(String request, String ipAddress, int port) throws IOException {

        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        DatagramPacket out = new DatagramPacket(request.getBytes(), request.getBytes().length,
                InetAddress.getByName(ipAddress), port);

        System.out.println("SENDING... => " + request + " to " + port);

        if (datagramSocket != null) {
            datagramSocket.send(out);
        }
    }

}
