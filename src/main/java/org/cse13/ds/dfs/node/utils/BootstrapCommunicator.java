package org.cse13.ds.dfs.node.utils;

import org.cse13.ds.dfs.node.Neighbour;
import java.io.IOException;
import java.util.List;

/**
 * Created by nadunindunil on 11/5/17.
 */
public interface BootstrapCommunicator {

    List<Neighbour> register(String ipAddress, int port, String username) throws IOException;

    boolean unregister(String ipAddress, int port, String username) throws IOException;
}
