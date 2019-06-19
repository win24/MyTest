package com.huabao.mytest.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


public class TcpClient {

    private static PrintWriter pw = null;
    private static BufferedReader br = null;
    private static Socket s;
    static Scanner scanner = new Scanner(System.in);

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            s = new Socket(InetAddress.getLocalHost(), 5500);
            pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            while (true) {
                System.out.println("Client端请输入：");
                String str = scanner.next();
                pw.println(str);
                pw.flush();
                String string = br.readLine();
                System.out.println("Client读到：" + string);
                if (str.equals("exit")) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if(br != null){
                br.close();
            }
            if(pw != null){
                pw.close();
            }
            if(s != null){
                s.close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}