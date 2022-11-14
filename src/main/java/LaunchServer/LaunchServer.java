package LaunchServer;

import Server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LaunchServer extends Thread {//Поток, ожидающий новых клиентов
    ServerSocket server;
    Socket client;
    Server serverThread;
    public LaunchServer(){
        try {
            server=new ServerSocket(1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        start();
    }

    @Override
    public void run() {
        while (true){
            try {
                client=server.accept();
                serverThread=new Server(client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
