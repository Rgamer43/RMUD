import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Client {

    public static Socket socket;

    public static ObjectOutputStream oos;
    public static ObjectInputStream ois;

    public static Scanner scn = new Scanner(System.in);

    public static JFrame jFrame;
    public static JLabel[] msgLabels = new JLabel[72];
    public static JTextField textField;
    public static JTextArea roomLabel;
    public static JLabel hpHUD;

    public static Font defaultFont = new Font("Monospaced", Font.PLAIN, 9);

    public static boolean graphicsInitted = false;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        socket = new Socket("192.168.4.22", 7777);
        //System.out.println("Connecting...");

        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());

        initGraphics();

        Thread sendInput = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    String msg = scn.nextLine();

                    try {
                        oos.writeObject(msg);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread readMsg = new Thread(new Runnable() {
            public boolean strInPaused = true;

            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    strInPaused = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (true){
                    try {
                        if (!strInPaused) {
                            String msg = (String) ois.readObject();
                            String[] args = msg.split(" ");

                            Thread.sleep(20);

                            if (msg.equals("quitApp")) {
                                System.out.println("Quitting application");
                                socket.close();
                                System.exit(0);
                            } else if (msg.equals("logOut")) {
                                //System.out.println("Logging out and exiting program");
                                socket.close();
                                System.exit(0);
                            } else if (args[0].equals("setRoomDesc")) {
                                msg = "";
                                for (int x = 1; x < args.length; x++) {
                                    msg += args[x];
                                    if (x < args.length - 1) msg += " ";
                                }
                                roomLabel.setText(msg);
                            } else if (args[0].equals("setHPHUD")) {
                                hpHUD.setText("HP: " + args[1]);
                            } else if (args[0].equals("psIncoming")) {
                                strInPaused = true;

                                PlayerSave ps = (PlayerSave) ois.readObject();

                                FileOutputStream fos = new FileOutputStream("save.save");
                                ObjectOutputStream o = new ObjectOutputStream(fos);

                                o.writeObject(ps);

                                o.close();
                                fos.close();

                                strInPaused = false;
                            } else {
                                if (graphicsInitted) {
                                    String[] lines = msg.split("\n");
                                    for (int y = 0; y < lines.length; y++) {
                                        for (int x = 1; x < msgLabels.length; x++) {
                                            msgLabels[x - 1].setText(msgLabels[x].getText());
                                        }
                                        msgLabels[msgLabels.length - 1].setText(lines[y]);
                                    }
                                }
                                //else System.out.println(msg);
                            }
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        sendInput.start();
        readMsg.start();

        if (!Files.exists(Paths.get(System.getProperty("user.dir") + "/save.save"))) {
            oos.writeObject("createPFile");
            //System.out.println("Creating PFile");
        }
        else {
            oos.writeObject("loadPFile");

            FileInputStream fis = new FileInputStream("save.save");
            ObjectInputStream o = new ObjectInputStream(fis);
            oos.writeObject(o.readObject());
            o.close();
            fis.close();

            //System.out.println("save.save exists");
            //System.out.println(ois.readObject());
        }
    }

    public static void initGraphics() {
        jFrame = new JFrame("RMUD");

        for (int x = 0; x < msgLabels.length; x++) {
            msgLabels[x] = new JLabel();
            msgLabels[x].setBounds(1, 0+(x*10), 690, 10);
            msgLabels[x].setFont(defaultFont);
            msgLabels[x].setBackground(Color.BLACK);
            msgLabels[x].setForeground(Color.WHITE);
            msgLabels[x].setOpaque(true);
            msgLabels[x].setForeground(Color.white);
            jFrame.add(msgLabels[x]);
        }

        textField = new JTextField(30);
        textField.setFont(defaultFont);
        textField.setOpaque(true);
        textField.setBackground(Color.BLACK);
        textField.setForeground(Color.white);
        textField.setBounds(0, 720, 690, 15);
        textField.addKeyListener(new EnterListener());
        jFrame.add(textField);

        roomLabel = new JTextArea(50, 50);
        roomLabel.setFont(defaultFont);
        roomLabel.setBounds(700, 0, 675, 300);
        roomLabel.setBackground(Color.black);
        roomLabel.setOpaque(true);
        roomLabel.setForeground(Color.white);
        jFrame.add(roomLabel);

        hpHUD = new JLabel();
        hpHUD.setFont(defaultFont);
        hpHUD.setBounds(700, 310, 680, 290);
        hpHUD.setBackground(Color.black);
        hpHUD.setOpaque(true);
        hpHUD.setForeground(Color.white);
        jFrame.add(hpHUD);

        jFrame.setSize(1375, 775);
        jFrame.setLayout(null);

        jFrame.getContentPane().setBackground(Color.BLACK);
        jFrame.getContentPane().setForeground(Color.white);

        jFrame.setVisible(true);
        graphicsInitted = true;

//        Dimension di = Toolkit.getDefaultToolkit().getScreenSize();
//        System.out.println(di.getWidth());
//        System.out.println(di.getHeight());
    }

}

class EnterListener extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent event) {
        int ch = event.getKeyCode();

        //Check if enter key is pressed
        if (ch == KeyEvent.VK_ENTER) {
            try {
                Client.oos.writeObject(Client.textField.getText());
                Client.textField.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

