package org.cse13.ds.dfs.node;

import org.cse13.ds.dfs.node.utils.Request;
import org.junit.Assert;

import static org.cse13.ds.dfs.Constants.REGISTER_FORMAT;

/**
 * Created by nadunindunil on 11/5/17.
 */
public class DatagramBootstrapTest {
    @org.junit.Test
    public void register() throws Exception {

        String msg = String.format(REGISTER_FORMAT, "localhost", 5000, "nadun");
        String request = Request.create(msg);
        Assert.assertEquals("0029 REG localhost 5000 nadun",request);
    }



}