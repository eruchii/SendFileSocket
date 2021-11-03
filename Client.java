
/*
    Ho va ten: Nguyen Chi Thanh
    MSSV: 18020053
    Client
*/

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        final String serverHost = "localhost";

        Socket socketOfClient = null;
        DataOutputStream os = null;
        DataInputStream is = null;

        try {
            socketOfClient = new Socket(serverHost, 9999);
            os = new DataOutputStream(new BufferedOutputStream(socketOfClient.getOutputStream()));
            is = new DataInputStream(new BufferedInputStream(socketOfClient.getInputStream()));

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + serverHost);
            return;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + serverHost);
            return;
        }
        System.err.println("Connected to " + serverHost);
        Scanner sc = new Scanner(System.in);
        try {
            while (true) {
                String input = sc.nextLine();
                os.writeChars(input);
                os.writeChar('\n');
                os.flush();
                String resp = is.readUTF();
                System.out.println("FROM SERVER: " + resp);
                if (resp.equals("500 bye")) {
                    break;
                }
                if (resp.equals("210 File Info OK")) {
                    String x = sc.nextLine();
                    os.writeChars(x+"\n");                
                    os.flush();
                    String file_info = is.readUTF();
                    FileInfo fi = new FileInfo(file_info);
                    System.out.println(fi.getFileName());
                    System.out.println(fi.getFileSize());
                    OutputStream outputFile = new FileOutputStream("download/"+fi.getFileName());
                    int count = 0;
                    int remain = fi.getFileSize();
                    byte[] buffer = new byte[4096]; // or 4096, or more
                    while (true) {
                        count = is.read(buffer);
                        if(count <= 0){
                            break;
                        }
                        outputFile.write(buffer, 0, count);
                        remain -= count;
                        if(remain <= 0){
                            System.out.println("Finished");
                            break;
                        }
                    }
                    outputFile.close();
                }
            }
            os.close();
            is.close();
            socketOfClient.close();
            sc.close();
        } catch (UnknownHostException e) {
            System.err.println("Trying to connect to unknown host: " + e);
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }

    }

}