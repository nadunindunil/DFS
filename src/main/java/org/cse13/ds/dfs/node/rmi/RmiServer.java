package org.cse13.ds.dfs.node.rmi;

import java.rmi.Remote;

/**
 * Created by nadunindunil on 11/7/17.
 */
public interface RmiServer extends Remote {
    void handle();
}
