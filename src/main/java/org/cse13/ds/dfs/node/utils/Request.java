package org.cse13.ds.dfs.node.utils;

import org.cse13.ds.dfs.Constants;
import org.cse13.ds.dfs.node.Neighbour;
import org.cse13.ds.dfs.node.Node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by nadunindunil on 11/5/17.
 */
public class Request {


    private static byte[] buf = new byte[1000];

    public static String sendMessage(String outString, String outAddress, String outPort) throws IOException {

        DatagramSocket datagramSocket = new DatagramSocket();

        DatagramPacket out = new DatagramPacket(outString.getBytes(), outString.getBytes().length,
                InetAddress.getByName(outAddress), Integer.parseInt(outPort));

        System.out.println("SENDING... => " + outString + " to " + outPort);

        datagramSocket.send(out);

        DatagramPacket incoming = new DatagramPacket(buf, buf.length);
        datagramSocket.receive(incoming);

        return new String(incoming.getData(), 0, incoming.getLength());

    }

    public static List<Neighbour> decodeRegisterResponse(String response){

        System.out.println(response);
        StringTokenizer st = new StringTokenizer(response, " ");
        String length = "", command = "";
        length = st.nextToken();
        command = st.nextToken();
        List<Neighbour> nodeList = null;


        if (command.equals(Constants.REGOK)) {

            int no_nodes = Integer.parseInt(st.nextToken());


            switch (no_nodes) {
                case 0:
                    System.out.println("no nodes in the system");
                    break;
                case 1:
                case 2:
                    nodeList = new ArrayList<>();
                    while (no_nodes > 0) {
                        nodeList.add(new Neighbour(st.nextToken(), Integer.parseInt(st.nextToken())));
                        no_nodes--;
                    }
                    break;
                case 9996:
                    System.out.println("failed, can’t register. BS full");
                    break;
                case 9997:
                    System.out.println("failed, registered to another user, try a different IP and port");
                    break;
                case 9998:
                    System.out.println("failed, already registered to you, unregister first");
                    break;
                case 9999:
                    System.out.println("failed, there is some error in the command");
                    break;
                default:
                    System.out.println("wrong request");

            }
        }

        return nodeList;
    }

    public static boolean decodeUnregister(String response){

        StringTokenizer st = new StringTokenizer(response, " ");
        String status = st.nextToken();

        if (!Constants.UNROK.equals(status)) {
            throw new IllegalStateException(Constants.UNROK + " not received");
        }

        int code = Integer.parseInt(st.nextToken());

        switch (code) {
            case 0:
                System.out.println("Successful");
                return true;
            case 9999:
                System.out.println("Error while un-registering. IP and port may not be in the registry or command is incorrect");
            default:
                return false;
        }
    }

    public static String create(String msg){
        return String.format(Constants.MSG_FORMAT, msg.length() + 5, msg);
    }
}
