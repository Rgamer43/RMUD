import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class ConnectionAccepter extends Thread{
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(8080);
            System.out.println("$PORT is " + System.getenv("$PORT"));

            while (true) {
                Socket socket = null;
                socket = ss.accept();
                System.out.println("Accepted connection from " + socket);

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                Server.sessions.add(new UserSession(socket, dos, dis));

                Server.sessions.get(Server.sessions.size()-1).start();

                //socket.close();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
