/*
    Ho va ten: Nguyen Chi Thanh
    MSSV: 18020053
    Server
*/


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import com.google.gson.Gson;

public class Server {
    public static void main(String args[]) {

        ServerSocket listener = null;
        String line;
        BufferedReader is;
        OutputStreamWriter os;
        Socket socketOfServer = null;

        final String INFO_INPUT = "210 OK\n";
        final String FILE_NOT_FOUND = "400 File Not Found\n";
        final String INFO_OK = "210 File Info OK\n";
        final String DOWNLOAD_OK = "220 Download OK\n";
        final String QUIT = "500 bye\n";
        final String HELLO = "200 Hello Client\n";
        final String COMMAND_NOT_FOUND = "400 Command not found\n";

        try {
            listener = new ServerSocket(9999);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }

        try {
            System.out.println("Server is waiting to accept user...");
            socketOfServer = listener.accept();
            System.out.println("Accept a client!");

            is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
            os = new OutputStreamWriter(socketOfServer.getOutputStream());

            while (true) {
                line = is.readLine();
                if (line == null) {
                    break;
                }
                
                if (line.equals("QUIT")) {
                    System.out.println("client say quit");
                    os.write(QUIT);
                    os.flush();
                    break;
                }
                if(line.equals("HELO Server")){
                    os.write(HELLO);
                    os.flush();
                    continue;
                }
                if (line.equals("FILE INFO")) {
                    os.write(INFO_OK);
                    os.flush();
                    String fileName = is.readLine();
                    System.out.println(fileName);
                    if (fileName == null) {
                        break;
                    }
                    File file = new File(fileName);
                    if(!file.exists()){
                        os.write(FILE_NOT_FOUND);
                        os.flush();
                        continue;
                    }
                    FileInfo fi = new FileInfo(fileName, (int) file.length());
                    os.write(fi.toString());
                    os.write("\n");
                    os.flush();
                    byte[] bytes = new byte[4096];
                    FileInputStream f = new FileInputStream(file);
                    int count;
                    while ((count = f.read(bytes)) > 0) {
                        os.write(0);
                    }
                    continue;
                }
                os.write(COMMAND_NOT_FOUND);
                os.flush();
            }

        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        System.out.println("Bye");
    }
}