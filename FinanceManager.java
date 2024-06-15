import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
public class FinanceManager {
	public static void main(String[] args){
		Connection conn=null;
		Statement stmt=null;
		ResultSet results=null;
		Scanner sc =new Scanner(System.in);
		// Step 1 registers 
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		}catch (ClassNotFoundException e) {
			System.out.println("JDBC DRIVER  NOT REGISTERED.");
		}
		System.out.println("JDBC DRIVER REGISTETED");
		//Step 2 connects
		try {
			conn=DriverManager.getConnection("jdbc:mysql://localhost:3307/finanfdatabase","root","");
		}catch(SQLException e) {
			System.out.println(e);
		}
		if(conn!=null) {
			System.out.println("CONNECTION SUCCESSFUL");
		}
		System.out.println("CREATING A TABLE");
		try{
			stmt=conn.createStatement();
			String sql="DROP TABLE IF EXISTS FinanceManager;";
			stmt.execute(sql);
			sql="CREATE TABLE FinanceManager ("
					+ "    TransactionID INTEGER NOT NULL PRIMARY KEY,"
					+ "    AccountHolder VARCHAR(255) NOT NULL,"
					+ "    Balance DOUBLE NOT NULL,"
					+ "    Ts TIMESTAMP NOT NULL"
					+ ");";
			stmt.executeUpdate(sql);
			System.out.println("TABLE CREATED");
			boolean a=true;
			int IDNumber=0;
			double AccountBalance=0.0;
			while(a) {
				try {
					LocalDateTime myDateObj = LocalDateTime.now();
					DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					System.out.println("\n***FINANCE MANAGER***\n1. Add info, 2. Search query, 3. Print All, 4. Remove info, 5. Export table to CVS, 6. Exit");
					int answer=sc.nextInt();
					switch (answer) {
						case 1: {
							System.out.println("Account holder name:");
							String Name=sc.nextLine();
							Name=sc.next();
							System.out.println("1.Withdraw or 2.Deposit");
							int anw=sc.nextInt();
							double WD=0;
							if(anw==1) {
								System.out.println("Withdraw amount:");
								WD=sc.nextDouble()*(-1);
							}else {
								System.out.println("Deposit amount:");
								WD=sc.nextDouble();
							}
							String CurrantTime = myDateObj.format(myFormatObj);
							sql="INSERT INTO FinanceManager (TransactionID,AccountHolder,Balance,Ts)"
								+ "VALUES ("+IDNumber+","+"'"+Name+"'"+","+(AccountBalance+=WD)+","+"'"+CurrantTime+"'"+");";
							stmt.execute(sql);
							System.out.println("Info added!");
							IDNumber++;
							break;
						}case 2: {
							System.out.println("Search query");
							sc.nextLine();
							sql=sc.nextLine();
							
							results=stmt.executeQuery(sql);
							boolean records=results.next();
							if(!records) {
								System.out.println("No valid rows!");
								break;
							}do {
								int TID=0;
								String AH="";
								double B=0.0;
								Timestamp TS=null;
								int col=1;
								if(sql.matches("select(.*)AccountHolder(.*)from(.*)")) {
									AH=results.getString(col);
									col++;
									System.out.print(AH+" ");
								}
								if(sql.matches("select(.*)Balance(.*)from(.*)")) {
									B=results.getDouble(col);
									col++;
									System.out.print(B+" ");
								}
								if(sql.matches("select(.*)Ts(.*)from(.*)")) {
									TS=results.getTimestamp(col);
									col++;
									System.out.print(TS+" ");
								}				
								if(sql.contains("*")) {
									TID=results.getInt(1);
									AH=results.getString(2);
									B=results.getDouble(3);
									TS=results.getTimestamp(4);
									System.out.println(TID+" "+AH+" "+B+" "+TS);
								}
								
							}while(results.next());
							break;
						}case 3:{
							results=stmt.executeQuery("select * from FinanceManager");
							boolean records=results.next();
							if(!records) {
								System.out.println("No valid rows!");
								break;
							}do {
								int TID=results.getInt(1);
								String AH=results.getString(2);
								double B=results.getDouble(3);
								Timestamp TS=results.getTimestamp(4);
								System.out.println(Integer.toString(TID)+", "+AH+", "+Double.toString(B)+", "+TS+"\n");
							}while(results.next());
							break;
						}case 4:{
							System.out.println("1.Drop table or 2.Delete condition");
							int answer3=sc.nextInt();
							if(answer3==1) {
								sql="DROP TABLE FinanceManager;";
								stmt.executeUpdate(sql);
								System.out.println("Table dropped");
							}else {
								System.out.println("Enter condition:");
								sc.nextLine();
								sql=sc.nextLine();
								stmt.executeUpdate("DELETE FROM FinanceManager WHERE "+sql);
								System.out.println("Condition deleted");
								break;
							}
							
						}case 5:{
							File fileio=new File("C:\\Users\\fagan\\eclipse-workspace\\Personal\\src\\FMTable.csv");
							 try {
								 FileWriter FW=new FileWriter(fileio);
						            if (fileio.createNewFile()) {
						                System.out.println("File created successfully.");
						            } else {
						            	System.out.println("File created successfully.");
						                fileio.delete();
						                fileio.createNewFile();
						            }
						            results=stmt.executeQuery("select * from FinanceManager");
						            FW.append("Transaction ID,Account Holder, Balance, Timestamp\n");
						            boolean records=results.next();
									if(!records) {
										System.out.println("No valid rows!");
										break;
									}do {
										int TID=results.getInt(1);
										String AH=results.getString(2);
										double B=results.getDouble(3);
										Timestamp TS=results.getTimestamp(4);
										FW.append(Integer.toString(TID)+", "+AH+", "+Double.toString(B)+", "+TS+"\n");
									}while(results.next());
									FW.close();
						        }catch (IOException e) {
						            System.out.println("An error occurred."+e);
						        }
							 break;
						}case 6:{
							sc.close();
							a=false;
							System.out.println("Table closed!");
							break;
						}default:{
							System.out.println("Invalid Input!");
						}
					}
				}catch(SQLException e) {
					System.out.println(e);
				}
			}
		}catch(SQLException e){
			System.out.println(e);
		}
	}
}
