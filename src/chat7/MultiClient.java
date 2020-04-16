package chat7;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.io.*;
import java.net.*;
import java.sql.SQLException;

public class MultiClient extends ConnectDB{
	@Override
	void execute() {
		// TODO Auto-generated method stub
	}

	public MultiClient() {
		super();
	}

	
	public void chating_user(String s_name) {
		String query_user="insert into chating_user values(?)";

		try {
			psmt = con.prepareStatement(query_user);
			psmt.setString(1, s_name);
			psmt.executeUpdate();
		} catch (SQLException e1) {
			System.out.println("이름이 중복되었습니다.");
			System.exit(0);
		}
	}

	public static void main(String[] args) {

		MultiClient client =new MultiClient();
		System.out.println("이름을 입력하세요:");
		Scanner scanner = new Scanner(System.in);
		String s_name = scanner.nextLine();	

		client.chating_user(s_name);

		try {
			String ServerIP = "localhost";
			if(args.length>0) {
				ServerIP = args[0];
			}
			Socket socket = new Socket(ServerIP, 9999);
			System.out.println("서버와 연결되었습니다...");

			//서버에서 보내는 Echo메세지를 클라이언트에 출력하기 위한 쓰레드 생성
			Thread receiver= new Receiver(socket);
			receiver.start();


			//클라이언트의 메세지를 서버로 전송해주는 쓰레드 생성
			Thread sender = new Sender(socket, s_name);
			sender.start();

		}
		catch (Exception e) {
			System.out.println("예외발생[MultiClient]"+e);
		}
	}
}