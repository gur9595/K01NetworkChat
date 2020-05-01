package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiServer extends ConnectDB{
	public MultiServer() {
		super();
	}
	public static void main(String[] args) {

		ServerSocket serverSocket = null;
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		String s = "";
		String name = "";
		
		MultiServer ser= new MultiServer();
		
		try {
			serverSocket = new ServerSocket(9999);
			System.out.println("서버가 시작되었습니다.");

			socket = serverSocket.accept();

			/*** ① ***/
			//서버에서 클라이언트 측으로 메세지를 전송(출력) 하기위한 스트림을 생성
			//클라이언트로부터 메세지를 받기위한 스트림을 생성
	        out = new PrintWriter(socket.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			if(in != null) {
				name = in.readLine();
				System.out.println(name +" 접속");
				out.println("> "+ name +"님이 접속했습니다.");
			}

			while(in != null) {
				s = in.readLine();
				if(s==null) {
					break;
				}
				System.out.println(name +" ==> "+ s);
				out.println(">  "+ name +" ==> "+ s);
				

				String query="insert into chatting_tb values(chatting_seq.nextval,?,?,sysdate)";
				psmt =con.prepareStatement(query);
								
				psmt.setString(1, name);
				psmt.setString(2, s);

				psmt.executeUpdate();
			}
			
			System.out.println("Bye...!!!");
		}
		catch (Exception e) {
			System.out.println("예외1:"+ e);
		}
		finally {
			try {
				in.close();
				out.close();
				socket.close();
				serverSocket.close();
			}
			catch (Exception e) {
				System.out.println("예외2:"+ e);
			}
		}
	}

	@Override
	void execute() {
		// TODO Auto-generated method stub
		
	}

}
