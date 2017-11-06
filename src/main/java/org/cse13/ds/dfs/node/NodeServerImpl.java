package org.cse13.ds.dfs.node;

import org.cse13.ds.dfs.node.utils.NodeServer;
import org.cse13.ds.dfs.node.utils.Request;

import java.io.IOException;
import java.net.*;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.cse13.ds.dfs.Constants.*;


/**
 * Created by nadunindunil on 11/6/17.
 */
public class NodeServerImpl implements NodeServer {

    private Node node;
    private ExecutorService executorService;

    public NodeServerImpl(Node node){
        this.node = node;
    }

    @Override
    public void start() {

        executorService = Executors.newCachedThreadPool();

        executorService.submit(() -> {
            try {
                listen();
            } catch (Exception e) {
                System.out.println(e);
            }
        });

    }

    @Override
    public void stop() {
        executorService.shutdownNow();
    }

    @Override
    public void listen() {

        try(DatagramSocket datagramSocket = new DatagramSocket(this.node.getNode_port())){
            System.out.println("started Listening........");

            while(true){
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(incoming);

                byte[] data = incoming.getData();
                String request = new String(data, 0, incoming.getLength());

                executorService.submit(() -> {
                    try {
                        requestHandler(request,incoming,datagramSocket);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                });
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void requestHandler(String request,DatagramPacket incoming,DatagramSocket datagramSocket) throws IOException {

        StringTokenizer st = new StringTokenizer(request, " ");
        String command = "", length = "";

        String ip = incoming.getAddress().getHostAddress();
        int port = incoming.getPort();

        length = st.nextToken();
        command = st.nextToken();

        System.out.println( "RECEIVED <= " + request + " from " + ip + "," + port);

        String ip_address;
        int port_num;

        switch (command){
            case JOIN:
                ip_address = st.nextToken();
                port_num = Integer.parseInt(st.nextToken());
                try {
                    joinHandler(ip_address,port_num,datagramSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case JOINOK:
                System.out.println("join OK");
                joinOKHandler(ip,port,Integer.parseInt(st.nextToken()));
                break;
            case SER:
                searchHandler();
                break;
            case LEAVE:
                ip_address = st.nextToken();
                port_num = Integer.parseInt(st.nextToken());
                leaveHandler(ip_address,port_num,datagramSocket);
                break;
            case LEAVEOK:
                leaveOKHandler();
                break;
            default:
                System.out.println("not a valid request");
                break;
        }

    }

    private void joinOKHandler(String ip, int port,int code){

        switch (code) {
            case 0:
                System.out.println("Successful");
                this.node.addNeighbour(new Neighbour(ip,port));
                break;
            case 9999:
                System.out.println("error while adding new node to routing table");
            default:
                break;
        }
    }

    private void joinHandler(String ip, int port, DatagramSocket datagramSocket) throws IOException {

        this.node.addNeighbour(new Neighbour(ip,port));

        System.out.println("node added in handler");

        String msg = String.format(JOINOK_FORMAT,0);
        String request = Request.create(msg);

        DatagramPacket out = new DatagramPacket(request.getBytes(), request.getBytes().length,
                InetAddress.getByName(ip), port);

        System.out.println("SENDING... => " + request + " to " + port);

        datagramSocket.send(out);

    }

    private void searchHandler(){

        System.out.println("inside search");
        /* TODO : search should be here */

    }

    private void leaveHandler(String ipAddress, int port, DatagramSocket datagramSocket) throws IOException {

        this.node.removeNeighbour(ipAddress,port);

        String msg = String.format(LEAVEOK,0);
        String request = Request.create(msg);

        DatagramPacket out = new DatagramPacket(request.getBytes(), request.getBytes().length,
                InetAddress.getByName(ipAddress), port);

        System.out.println("SENDING... => " + request + " to " + port);

        datagramSocket.send(out);
    }

    private void leaveOKHandler(){
        System.out.println("inside leaveOK");
        // TODO
    }
}
