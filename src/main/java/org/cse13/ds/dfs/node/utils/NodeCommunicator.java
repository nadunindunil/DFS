package org.cse13.ds.dfs.node.utils;

import org.cse13.ds.dfs.node.Node;

import java.io.IOException;

/**
 * Created by nadunindunil on 11/5/17.
 */
public interface NodeCommunicator {

    void connect(String ipAddress, int port) throws IOException;

    void disconnect(String ipAddress, int port) throws IOException;

}
