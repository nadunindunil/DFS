package org.cse13.ds.dfs.node;

import org.cse13.ds.dfs.node.utils.BootstrapCommunicator;
import org.cse13.ds.dfs.node.utils.Request;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.List;

import static org.cse13.ds.dfs.Constants.*;

/**
 * Created by nadunindunil on 11/5/17.
 */
public class BootstrapCommunicatorImpl implements BootstrapCommunicator {
    @Override
    public List<Neighbour> register(String ipAddress, int port, String username) throws IOException, NotBoundException {
        String msg = String.format(REGISTER_FORMAT, ipAddress, port, username);
        String request = Request.create(msg);
        String response = Request.sendMessage(request, BOOTSERVER_IP, Integer.toString(BOOTSERVER_PORT));

        return Request.decodeRegisterResponse(response);
    }

    @Override
    public boolean unregister(String ipAddress, int port, String username) throws IOException {
        String msg = String.format(UNREGISTER_FORMAT, ipAddress, port, username);
        String request = Request.create(msg);
        String response = Request.sendMessage(request, BOOTSERVER_IP, Integer.toString(BOOTSERVER_PORT));

//        return Request.decodeUnregister(response);
        return true;
    }
}
