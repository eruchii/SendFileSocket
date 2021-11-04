/*
    Ho va ten: Nguyen Chi Thanh
    MSSV: 18020053
    Server
*/


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import com.google.gson.Gson;

public class Server {
    public static void main(String args[]) {

        ServerSocket listener = null;
        String line;
        InputStream is;
        OutputStream os;
        Socket socketOfServer = null;

        final String FILE_NOT_FOUND = "410 File Not Found";
        final String DOWNLOAD_OK = "210 Download Mode OK";
        final String QUIT = "500 bye";
        final String HELLO = "200 Hello Client";
        final String COMMAND_NOT_FOUND = "400 Command not found";

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

            is = socketOfServer.getInputStream();
            os = socketOfServer.getOutputStream();

            while (true) {
                byte[] buff = new byte[4096];
                int cc = is.read(buff);
                if(cc < 0){
                    break;
                }
                line = new String(buff, StandardCharsets.UTF_8).substring(0, cc);                
                if (line.equals("QUIT")) {
                    System.out.println("client say quit");
                    os.write(QUIT.getBytes());
                    os.flush();
                    break;
                }
                if(line.equals("HELO Server")){
                    os.write(HELLO.getBytes());
                    os.flush();
                    continue;
                }
                if (line.equals("FILE")) {
                    os.write(DOWNLOAD_OK.getBytes());
                    os.flush();
                    int re = is.read(buff);
                    String fileName = new String(buff, StandardCharsets.UTF_8).substring(0, re);
                    System.out.println(fileName);
                    if (fileName == null) {
                        break;
                    }
                    File file = new File(fileName);
                    if(!file.exists()){
                        os.write(FILE_NOT_FOUND.getBytes());
                        os.flush();
                        continue;
                    }
                    FileInfo fi = new FileInfo(fileName, (int) file.length());
                    os.write((fi.toString()+"\n").getBytes());
                    os.flush();
                    re = is.read(buff);
                    String nextcmd = new String(buff, StandardCharsets.UTF_8).substring(0, re);
                    if(!nextcmd.equals("DOWNLOAD")){
                        continue;
                    }
                    System.out.println(nextcmd);
                    byte[] bytes = new byte[4096];
                    InputStream f = new FileInputStream(fileName);
                    int count;
                    while (true) {
                        count = f.read(bytes);
                        // System.out.println(count);
                        if(count < 0){
                            break;
                        }
                        os.write(bytes,0,count);
                        os.flush();
                    }
                    f.close();
                    continue;
                }
                os.write(COMMAND_NOT_FOUND.getBytes());
                os.flush();
            }

        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        System.out.println("Bye");
    }
}