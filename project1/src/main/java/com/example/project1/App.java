package com.example.project1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class App 
{

	public static String user = "postgres";
	public static String password = "barxas1234";
	
	
	public static String myDriver = "org.postgresql.Driver";
	public static String myUrl = "jdbc:postgresql://localhost:5432/db1";
	
	//Проверка строки на длину, пустоту, наличие специальных символов
	public static boolean strCheck(String str) {
		try {
		if(str.length() <= 30 && !str.trim().isEmpty()) {
			Pattern pt = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pt.matcher(str);
			boolean b = matcher.find();
			if (b) {
				return false;
			}
			else {
				return true;
			}
		}
		else
			return false;
        } catch(NullPointerException e) {
            return false;
        }
	}
		
	//Проверка на корректность даты(формат мм-дд-гг)
	public static boolean dateCheck(String word) {
	    try {
		DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		    df.setLenient(true);
            return df.format(df.parse(word)).equals(word);
	    } catch (ParseException e) {
	        return false;
        } catch(NullPointerException e) {
            return false;
	    }
	}
	
	//Проверка, является ли строка числом
    public static boolean isNumber(String num) {
        try {
            return Integer.parseInt(num)>0&&Integer.parseInt(num)<1000000;
        } catch(NumberFormatException e) { 
            return false; 
        }
    }
    
    //Запись в бд в таблицы newproduct, purchase и demand. Возвращает true если все хорошо, false если обнаружена ошибка
	public static boolean query(String[] words) throws ClassNotFoundException, SQLException {
		Class.forName(myDriver);
		Connection conn = DriverManager.getConnection(myUrl, user, password);
		Statement st = conn.createStatement();
		
		String queryCheck = "SELECT * from products WHERE product = '"+words[1]+"'";
		
		ResultSet rs = st.executeQuery(queryCheck);
		
		try {
			switch (words[0]) {
				case "NEWPRODUCT":
					if(words.length==2) {
						if(strCheck(words[1])) {
							if(rs.next()) {
								st.close();
								return false;
							}
							else {
								String queryInsertProduct = "INSERT INTO products (product) " + 
										"VALUES ('" + words[1] + "')";
								st.executeUpdate(queryInsertProduct);
								return true;
							}
						}
						else {
							return false;
						}
					}
					else {
						return false;
					}
						
				case "PURCHASE":
					if(rs.next()) {
						if(words.length==5) {
							if(strCheck(words[1])&&isNumber(words[2])&&isNumber(words[3])&&dateCheck(words[4])) {
								String queryInsertPurchase = "INSERT INTO purchase (products_id, purchase_amount, purchase_price, purchase_date) " + 
										"SELECT id, '" + words[2]  + "', '" + words[3] + "', '" + words[4] +"' FROM products WHERE product = '" + words[1]+"'";
								st.executeUpdate(queryInsertPurchase);
								st.close();
								return true;
							}
							else {
								return false;
							}
						}
						else {
							return false;
						}
					}
					else {
						return false;
					}
				case "DEMAND":
					if(rs.next()) {
						if(words.length==5) {
							if(strCheck(words[1])&&isNumber(words[2])&&isNumber(words[3])&&dateCheck(words[4])) {
								String queryInsertDemand = "INSERT INTO demand (products_id, demand_amount, demand_price, demand_date) " + 
										"SELECT id, '" + words[2]  + "', '" + words[3] + "', '" + words[4] +"' FROM products WHERE product = '" + words[1]+"'";
								st.executeUpdate(queryInsertDemand);
								return true;
							}
							else {
								return false;
							}
						}
						else {
							return false;
						}
					}
					else {
						return false;
						}
				default:
					return false;
			}
			
			}
		catch (NullPointerException | SQLException e) {
			e.printStackTrace();
	        return false;
        } finally {
	        if (rs != null) {
	            try {
	                rs.close();
	            } catch (SQLException e) {}
	        }
	        if (st != null) {
	            try {
	            	st.close();
	            } catch (SQLException e) {}
	        }
	        if (conn != null) {
	            try {
	                conn.close();
	            } catch (SQLException e) {}
	        }
	    }
	}

	//Расчет закупки или продажи товара. Возвращает общее количество полученных или потраченных денег для определенного товара
	public static int salesreport(String[] words, String whichTable) throws ClassNotFoundException, SQLException {
		int price = 0;
		Class.forName(myDriver);
		Connection conn = DriverManager.getConnection(myUrl, user, password);
		Statement st = conn.createStatement();
		String queryCheck = "SELECT * from products WHERE product = '"+words[1]+"'";
		ResultSet rs = st.executeQuery(queryCheck);
		try {
			
			
			//Запрос на расчет закупленных товаров
			String purchaseQuery = "SELECT distinct "
					+ "purchase.purchase_price, "
					+ "purchase.purchase_id, "
					+ "purchase.purchase_amount, "
					+ "purchase.purchase_date "
					+ "FROM "
					+ "products "
					+ "LEFT JOIN purchase "
					+ "ON "
					+ "purchase.products_id = (select distinct id from products where product = '" + words[1] + "') "
							+ "AND purchase.purchase_date <= '" + words[2] + "'";
			//Запрос на расчет проданных товаров
			String demandQuery = "SELECT distinct  "
					+ "demand.demand_price, "
					+ "demand.demand_id, "
					+ "demand.demand_amount, "
					+ "demand.demand_date "
					+ "FROM "
					+ "products "
					+ "INNER JOIN demand "
					+ "ON "
					+ "demand.products_id = (select distinct id from products where product = '" + words[1] + "') "
							+ "AND demand.demand_date <= '" + words[2] + "' ";
			

			if(rs.next()) {
				switch (whichTable) {
					case "demand":
						ResultSet rsQuery = st.executeQuery(demandQuery);
						while(rsQuery.next()) {
							String demandPrice = rsQuery.getString("demand_price");
							String demandAmount = rsQuery.getString("demand_amount");
							price=price+Integer.parseInt(demandAmount)*Integer.parseInt(demandPrice);
						}
						break;
					case "purchase":
						rsQuery = st.executeQuery(purchaseQuery);
						while(rsQuery.next()) {
							String purchasePrice = rsQuery.getString("purchase_price");
							String purchaseAmount = rsQuery.getString("purchase_amount");
							price=price+Integer.parseInt(purchaseAmount)*Integer.parseInt(purchasePrice);
						}
						break;
					default:
						System.out.println("Error");
						break;
				}
			}
			st.close();
		}
		catch (NullPointerException | SQLException e) {
		} finally {
	        if (rs != null) {
	            try {
	                rs.close();
	            } catch (SQLException e) {}
	        }
	        if (st != null) {
	            try {
	            	st.close();
	            } catch (SQLException e) {}
	        }
	        if (conn != null) {
	            try {
	                conn.close();
	            } catch (SQLException e) {}
	        }
		}
		return price;
	}
	public static void main(String args[])
	{
		Scanner in = new Scanner(System.in);
		String str = in.nextLine();
	        
		boolean message = false;
		boolean sales = false;
		String[] words = str.trim().split("\\s+");

		switch (words[0]) {
			case "NEWPRODUCT":
			case "PURCHASE":
			case "DEMAND":
				if(words.length>=2)
					if(strCheck(words[1]))
						try {
							message = query(words);
						} catch (ClassNotFoundException | SQLException e) {
							e.printStackTrace();
						}
					else 
						message = false;
					
				else
					message = false;
				break;
			case "SALESREPORT":
				if(words.length==3)
					if(strCheck(words[1])&&dateCheck(words[2])) {
						try {
							System.out.print(salesreport(words,"purchase") - salesreport(words,"demand"));
						} catch (ClassNotFoundException | SQLException e) {
							e.printStackTrace();
						}
						sales = true;
					}
					else 
						message = false;
				else
					message = false;
				break;
			default:
				message = false;
				break;
		}
		if(!sales)
			if(message)
				System.out.print("Ok");
			else
				System.out.print("Error");
		in.close();
	  }
	}
