package org.cse13.ds.dfs.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;


public class BootstrapServer implements Runnable{
    private static Thread t1,t2;
    DatagramSocket sock = null;

    public static void main(String args[]) {
        BootstrapServer bs1=new BootstrapServer();

        //bs1.startServer();
    }

    @Override
    public void run(){
        System.out.println("server thread is running...");
        this.startServer();
    }

    //simple function to echo data to terminal
    private void echo(String msg) {
        System.out.println(msg);
    }

    public BootstrapServer(){
        try {
            sock = new DatagramSocket(55555);
        }catch(Exception e){
            echo("crashed..");
        }

        t1=new Thread(this);
        t1.start();
    }

    public void startServer() {
        String s;
        List<Neighbour> nodes = new ArrayList<Neighbour>();

        try {

            echo("Bootstrap Server created at 55555. Waiting for incoming data...");

            while (true) {
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                sock.receive(incoming);

                byte[] data = incoming.getData();
                s = new String(data, 0, incoming.getLength());

                //echo the details of incoming data - client ip : client port - client message
                echo(incoming.getAddress().getHostAddress() + " : " + incoming.getPort() + " - " + s);

                StringTokenizer st = new StringTokenizer(s, " ");

                String length="",command="";
                try{
                    length = st.nextToken();
                    command = st.nextToken();
                }catch(Exception e){

                }

                if (command.equals("REG")) {
                    String reply = "REGOK ";

                    String ip = st.nextToken();
                    int port = Integer.parseInt(st.nextToken());
                    String username = st.nextToken();
                    if (nodes.size() == 0) {
                        reply += "0";
                        nodes.add(new Neighbour(ip, port, username));
                    } else {
                        boolean isOkay = true;

                        for (int i = 0; i < nodes.size(); i++) {
                            if (nodes.get(i).getPort() == port) {
                                if (nodes.get(i).getUsername().equals(username)) {
                                    reply += "9998";
                                } else {
                                    reply += "9997";
                                }
                                isOkay = false;
                            }
                        }

                        /*
                            PORT IS SAME AS OF CURRENT NODE CODE. SO =>
                            EDIT: CHECK IP+PORT and Username?
                        */

                        if (isOkay) {
                            if (nodes.size() == 1) {
                                reply += "1 " + nodes.get(0).getIp() + " " + nodes.get(0).getPort();
                            } else if (nodes.size() == 2) {
                                reply += "2 " + nodes.get(0).getIp() + " " + nodes.get(0).getPort() + " "
                                        + nodes.get(1).getIp() + " " + nodes.get(1).getPort();
                            } else {
                                Random r = new Random();
                                int Low = 0;
                                int High = nodes.size();
                                int random_1 = r.nextInt(High - Low) + Low;
                                int random_2 = r.nextInt(High - Low) + Low;
                                while (random_1 == random_2) {
                                    random_2 = r.nextInt(High - Low) + Low;
                                }
                                echo(random_1 + " " + random_2);
                                reply += "2 " + nodes.get(random_1).getIp() + " " + nodes.get(random_1).getPort() + " "
                                        + nodes.get(random_2).getIp() + " " + nodes.get(random_2).getPort();
                            }
                            nodes.add(new Neighbour(ip, port, username));
                        }
                    }

                    reply = String.format("%04d", reply.length() + 5) + " " + reply;

                    DatagramPacket dpReply = new DatagramPacket(reply.getBytes(), reply.getBytes().length,
                            incoming.getAddress(), incoming.getPort());
                    sock.send(dpReply);
                } else if (command.equals("UNREG")) {
                    String ip = st.nextToken();
                    int port = Integer.parseInt(st.nextToken());
                    String username = st.nextToken();
                    for (int i = 0; i < nodes.size(); i++) {
                        if (nodes.get(i).getPort() == port) {
                            nodes.remove(i);
                            String reply = "0012 UNROK 0";
                            DatagramPacket dpReply = new DatagramPacket(reply.getBytes(), reply.getBytes().length,
                                    incoming.getAddress(), incoming.getPort());
                            sock.send(dpReply);
                        }
                    }
                } else if (command.equals("ECHO")) {
                    for (int i = 0; i < nodes.size(); i++) {
                        echo(nodes.get(i).getIp() + " " + nodes.get(i).getPort() + " " + nodes.get(i).getUsername());
                    }
                    String reply = "0012 ECHOK 0";
                    DatagramPacket dpReply = new DatagramPacket(reply.getBytes(), reply.getBytes().length,
                            incoming.getAddress(), incoming.getPort());
                    sock.send(dpReply);
                }else if(command.equals("JOIN")){
                    echo("join req came");
                }
                else{
                    // else returns the text where you can see and debug the code!
                    System.out.println("not something expected: " + command);
                }

            }
        } catch (Exception e) {
            System.err.println("IOException " + e);
            echo("\n***********************\nServer crashed! restarting...\n");
            //this.t2.start();
            // startServer();
        }
    }


}
