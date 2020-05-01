package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//객체생성의 목적이 아닌 상속의 목적으로 정의된 추상클래스
public abstract class ConnectDB {
   
	/*
	 멤버변수 : 상속받은 하위클래스에서의 접근을 허용하기 위해 
	  		접근지정자는 protected로 선언
	 */
   protected static Connection con;
   protected Statement stmt;
   protected ResultSet rs;
   public static PreparedStatement psmt;

   
   //UpdateSQL에서 사용할 인자생성자
   public ConnectDB(String id, String pw) {
      try {
    	  //드라이버 로드
         Class.forName("oracle.jdbc.OracleDriver");
         
         //커넥션( 매개변수로 전달된 id, pw를 통해 연결 )
         //2. 커넥션 객체를 통해 DB연결
         con = DriverManager.getConnection
               ("jdbc:oracle:thin://@localhost:1521:orcl", 
                     id, pw);
         System.out.println("오라클 DB연결 성공");
      }
      catch (ClassNotFoundException e) {
         //ojdbc6.jar파일이 없거나 연동되지 않았을때 예외발생
         System.out.println("오라클 드라이버 로딩 실패");
         e.printStackTrace();
      }
      catch (SQLException e) {
         //커넥션 URL이나 계정명이 잘못되었을때 발생되는 예외
         System.out.println("DB 연결 실패");
         e.printStackTrace();
      }
      catch (Exception e) {
         System.out.println("알수 없는 예외 발생");
      }
   }
   
   public ConnectDB() {
      try {
         //1. 오라클 드라이버 로드
         Class.forName("oracle.jdbc.OracleDriver");
         //2. 커넥션 객체를 통해 연결
         con = DriverManager.getConnection
               ("jdbc:oracle:thin://@localhost:1521:orcl", 
                     "kosmo","1234"
               );
         System.out.println("오라클 DB 연결성공");
               
      }
      catch (ClassNotFoundException e) {
         //ojdbc6.jar파일이 없거나 연동되지 않았을때 예외발생
         System.out.println("오라클 드라이버 로딩 실패");
         e.printStackTrace();
      }
      catch (SQLException e) {
         //커넥션 URL이나 계정명이 잘못되었을때 발생되는 예외
         System.out.println("DB 연결 실패");
         e.printStackTrace();
      }
      catch (Exception e) {
         System.out.println("알수 없는 예외 발생");
      }
   }
   
   //자원반납을 위한 메소드
   public void close() {
      try {
         
         if(stmt!=null) stmt.close();//stmt 객체 자원반납
         if(con!=null) con.close(); //con 객체 자원 반납.
         if(psmt!=null) psmt.close();
         if(rs!=null) rs.close();
         System.out.println("자원반납완료");
         
      }
      catch(SQLException e) {
         System.out.println("자원 반납 시 오류가 발생하였습니다.");
      }
   }////end of close
   
   	/*
       오버라이딩을 목정으로 정의한 추상메소드
    */
   abstract void execute();

    
}















