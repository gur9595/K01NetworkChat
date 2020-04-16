package chat7;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class MultiServer  extends ConnectDB{
	static ServerSocket serverSocket =null;
	static Socket socket =null;
	//클라이언트 정보 저장을 위한 Map컬랙션 정의 
	Map<String , PrintWriter> clientMap;

	//생성자
	public MultiServer() {
		super();
		//클라이언트의 이름과 출력스트림을 저장할 HashMap생성
		clientMap =new HashMap<String, PrintWriter>();
		//HashMap동기화 설정. 쓰레드가 사용자정보에 동시에 접근하는것을 차단한다.
		Collections.synchronizedMap(clientMap);
	}

	public void init() {

		try {
			serverSocket = new ServerSocket(9999);
			System.out.println("서버가 시작되었습니다");

			while (true) {
				socket = serverSocket.accept();
				/*
				 클라이언트의 메세지를 모든 클라이언트에게 
				 전달하기 위한 쓰레드 생성 및 start. 
				 */
				Thread mst= new MultiServerT(socket);
				mst.start();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				serverSocket.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public static void main(String[] args){

		MultiServer ms =new MultiServer();
		ms.init();

	}

	//접속된 모든 클라이언트에게 메세지를 전달하는 역할의 메소드
	public void sendAllMsg(String name, String msg) {
		//Map에 저장된 객체의 키값(이름)을 먼저 얻어온다.
		Iterator<String> it=clientMap.keySet().iterator();
		//저장된 객체(클라이언트)의 갯수만큼 반복한다.
		while (it.hasNext()) {
			try {
				//각 클라이언트의 PrintWriter 객체를 얻어온다.
				PrintWriter it_out=(PrintWriter)clientMap.get(it.next());

				//클라이언트에게 메세지를 전달한다.
				/* 
				 매개변수 name이 있는 경우에는 이름+메세지
				 없는경우에는 메세지만 클라이언트로 전송한다.
				 */
				//URLEncoder.encode(msg,"UTF-8")
				if(name.equals("")) {
					it_out.println(msg);
				}
				else {
					it_out.println("["+name+"]:"+msg);
				}
			} catch (Exception e) {
				System.out.println("예외: " +e);
			}
		}

	}



	class MultiServerT extends Thread{
		//맴버변수
		Socket socket;
		PrintWriter out = null;
		BufferedReader in =null;
		//생성자 :Socket을 기반으로 입출력 스트림을 생성한다.
		public MultiServerT(Socket socket) {

			this.socket = socket;
			try {
				out = new PrintWriter(this.socket.getOutputStream(),true);
				in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(),"UTF-8"));
			} catch (Exception e) {
				System.out.println("예외"+e);
			}
		}

		public void sendMsg(String[] array,String name) {	

			PrintWriter arr_out = (PrintWriter) clientMap.get(array[1]);

			arr_out.print("["+name+"]:");
			for(int i=2; i<array.length;i++) {
				arr_out.print(array[i]+" ");
			}
			arr_out.println();
			
		}
		public void room() {
			
		}

		@Override
		public void run() {
			//클라이언트로부터 전송된 "대화명"을 저장할 변수
			String name= "";
			//메세지 저장용 변수
			String s="";
			boolean aa=true;

			String fname = null;

			try {
				//클라이언트의 이름을 읽어와서 저장
				name= in.readLine();
				name= URLDecoder.decode(name,"UTF-8");


				//접속한 클라이언트에게 새로운 사용자의 입장을 알림.
				//접속자를 제외한 나머지 클라리언트만 입장 메세지를 받는다.
				sendAllMsg("", name+"님이 입장하셨습니다.");

				//현재 접속한 클라이언트를 HashMap에 저장한다.
				clientMap.put(name, out);

				//HashMap에 저장된 객체의 수로 접속자수를 파악할수있다.
				System.out.println(name+" 접속");
				System.out.println("현재 접속자 수는"+clientMap.size()+"명 입니다");


				//입력한 메세지는 모든 클라이언트에게 Echo된다
				while (in!=null) {					
					s= in.readLine();
					s= URLDecoder.decode(s,"UTF-8");

					System.out.println(name+">> "+s);

					if(s==null)break;

					StringTokenizer st =new StringTokenizer(s, " ");
					String[] st_array = new String[st.countTokens()];		
					int i=0;
					while(st.hasMoreElements()) {
						st_array[i++]=st.nextToken();
					}

					if(st_array[0].equals("/q")) {
						aa=true;
						PrintWriter hi_out = (PrintWriter) clientMap.get(name);
						hi_out.println("고정해제 완료");
					}


					if(s.charAt(0)=='/') {
						switch (st_array[0].substring(1)) {
						case "list":

							Iterator<String> iterator = clientMap.keySet().iterator();
							while (iterator.hasNext()) {
								PrintWriter hi_out = (PrintWriter) clientMap.get(name);
								hi_out.println("접속자명 : "+iterator.next());
							}
							break;

						case "to":	

							if(st_array.length==2) {
								aa=false;
								fname=st_array[1];
								PrintWriter hi_out = (PrintWriter) clientMap.get(name);
								hi_out.println(fname+"에게 귓속말 고정완료 . 고정해제 하려면 /q입력");
							}else if(st_array.length>=3){
								sendMsg(st_array,name);				
							}

						}
					}else {
						if (aa==false) {
							PrintWriter it_out = (PrintWriter) clientMap.get(fname);
							it_out.println("["+name+"]:"+s);
						}else {
							sendAllMsg(name,s);							
						}
					}
					
//					String query_user="insert into chating_user values(?)";
//					
//					psmt =con.prepareStatement(query_user);	
//					psmt.setString(1, name);
//					psmt.executeUpdate();
					
					String query="insert into chating_tb values(seq_chating_tb.nextval,?,?,sysdate)";
					psmt =con.prepareStatement(query);
									
					psmt.setString(1, name);
					psmt.setString(2, s);

					psmt.executeUpdate();	
				}


			} catch (Exception e) {
				System.out.println("예외"+e);
			}finally {
				/*
				 클라이언트가 접속을 종료하면 예외가 발생하게 되어 finally로 넘어오게된다
				 이때 "대화명"을 통해 remove()시켜준다. 
				 */
				clientMap.remove(name);
				sendAllMsg("", name+"님이 퇴장하셨습니다.");
				//퇴장하는 클라이언트의 쓰레드명을 보여준다
				System.out.println(name+" ["+Thread.currentThread().getName()+"] 퇴장");
				System.out.println("현재 접속자 수는"+clientMap.size()+"명 입니다.");
				try {
					in.close();
					out.close();
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}


	}



	@Override
	void execute() {
		// TODO Auto-generated method stub

	}

}


//while (iterator.hasNext()) {
//String a = iterator.next();
//System.out.println(a);
//
//System.out.println(st_array[0]+"   "+st_array[1]);
//
//if(st_array[1].equals(a)){
//	System.out.println("if 들어옴 !!");
//	//sendMsg(st_array[1],st_array[2]);
//	PrintWriter st_out=(PrintWriter)clientMap.get(st_array[1]);
//	
//	st_out.print("["+name+"]: ");
//	for(int j=2; j<st_array.length; j++) {
//		st_out.print(st_array[j]+" ");
//	}
//	
//	st_out.println(s);
//	System.out.println("어디가 ?? ");
//	break;									
//}
//}

//if(arr[1].equals("")) {
//					for(int i=2; i<arr.length;i++) {
//						arr_out.println(URLEncoder.encode(arr[i],"UTF-8"));
//					}
//				}
//				else {


