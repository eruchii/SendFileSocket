
/*
    Ho va ten: Nguyen Chi Thanh
    MSSV: 18020053
    Client
*/

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhap IP: ");
        String serverHost = sc.nextLine();

        Socket socketOfClient = null;
        OutputStream os = null;
        InputStream is = null;
        byte[] buff = new byte[4096];

        try {
            socketOfClient = new Socket(serverHost, 9999);
            os = socketOfClient.getOutputStream();
            is = socketOfClient.getInputStream();

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + serverHost);
            return;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + serverHost);
            return;
        }
        System.err.println("Connected to " + serverHost);
        
        boolean ready = false;
        try {
            while (true) {
                if(!ready){
                    os.write("HELO Server".getBytes());
                    os.flush();
                }
                else{
                    String input = sc.nextLine();
                    os.write(input.getBytes());
                    os.flush();
                }
                int recv_bytes = is.read(buff);
                String resp = new String(buff, StandardCharsets.UTF_8).substring(0, recv_bytes);
                System.out.println("FROM SERVER: " + resp);
                if (resp.equals("500 bye")) {
                    break;
                }
                if(resp.equals("200 Hello Client")){
                    ready = true;
                }
                if (resp.equals("210 Download Mode OK")) {
                    System.out.print("Nhap ten file: ");
                    String x = sc.nextLine();
                    os.write(x.getBytes());                
                    os.flush();
                    recv_bytes = is.read(buff);
                    String file_info = new String(buff, StandardCharsets.UTF_8).substring(0, recv_bytes);
                    if(file_info.equals("410 File Not Found")){
                        System.out.println(file_info);
                        continue;
                    }
                    FileInfo fi = new FileInfo(file_info);
                    System.out.println("Ten file: " + fi.getFileName());
                    System.out.println("Kich thuoc: " + fi.getFileSize());
                    OutputStream outputFile = new FileOutputStream("download/"+fi.getFileName());
                    int count = 0;
                    int remain = fi.getFileSize();
                    os.write("DOWNLOAD".getBytes());                
                    os.flush();
                    byte[] buffer = new byte[4096]; // or 4096, or more
                    while (true) {
                        if(remain <= 0){
                            System.out.println("Ket thuc");
                            break;
                        }
                        // System.out.println("Reading");
                        count = is.read(buffer);
                        // System.out.println(count);
                        if(count <= 0){
                            break;
                        }
                        outputFile.write(buffer, 0, count);
                        outputFile.flush();
                        remain -= count;
                        // System.out.println(remain);
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