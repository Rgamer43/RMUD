import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;

public class ConnectionAccepter extends Thread{
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(Integer.valueOf(System.getenv("PORT")));
            System.out.println("PORT is " + System.getenv("PORT"));
            ss.bind(new InetSocketAddress(ss.getInetAddress(), Integer.valueOf(System.getenv("PORT"))));

            while (true) {
                try {
                    Socket socket = null;
                    socket = ss.accept();
                    System.out.println("Accepted connection from " + socket);

                    Server.sessions.add(new UserSession(socket));

                    Server.sessions.get(Server.sessions.size() - 1).start();

                    //socket.close();
                }
                catch (EOFException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
