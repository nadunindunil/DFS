package org.cse13.ds.dfs;

/**
 * Created by nadunindunil on 11/5/17.
 */
public class Constants {

    public static final String BOOTSERVER_IP = "127.0.0.1";
    public static final int BOOTSERVER_PORT = 55555;

    public static final String REGISTER_FORMAT = "REG %s %d %s";
    public static final String UNREGISTER_FORMAT = "UNREG %s %d %s";
    public static final String MSG_FORMAT = "%04d %s";
    public static final String JOIN_FORMAT = "JOIN %s %d";
    public static final String JOINOK_FORMAT = "JOINOK %d";
    public static final String LEAVE_FORMAT = "LEAVE %s %d";
    public static final String SER_FORMAT = "JOIN %s %s %d";

    public static final String JOIN = "JOIN";
    public static final String REGOK = "REGOK";
    public static final String UNROK = "UNROK";
    public static final String JOINOK = "JOINOK";
    public static final String SER = "SER";
    public static final String SEROK = "SEROK";
    public static final String LEAVE = "LEAVE";
    public static final String LEAVEOK = "LEAVEOK";
    public static final String ERROR = "ERROR";

}
