package com.example.project1;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AppTest {

    @BeforeClass
    public static void beforeClassFunction(){
		try {
		String myDriver = "org.postgresql.Driver";
			Class.forName(myDriver);
		} catch (ClassNotFoundException e) {
		}
    }

	@Test
	public void testStrCheck() {
		boolean result = App.strCheck("");
	    assertFalse(result);
	    
		result = App.strCheck(" ");
	    assertFalse(result);
	    
		result = App.strCheck("ddsfdsfdffsfdsfsdfddsfdsfdsfdsfdfdsfdsffsdgsfsd");
	    assertFalse(result);
	    
		result = App.strCheck("dsf-)(?d");
	    assertFalse(result);
	    
		result = App.strCheck(null);
	    assertFalse(result);
	    
		result = App.strCheck("dsfsd");
	    assertTrue(result);
	}
	

	@Test
	public void testDataCheck() {

		boolean result = App.dateCheck("11dsfa0");
	    assertFalse(result);

		result = App.dateCheck("11.11.2000");
	    assertFalse(result);

		result = App.dateCheck("11/11/2000");
	    assertFalse(result);

		result = App.dateCheck("20-02-2000");
	    assertFalse(result);
	    
		result = App.dateCheck("");
	    assertFalse(result);
	    
		result = App.dateCheck(" ");
	    assertFalse(result);
	    
		result = App.dateCheck(null);
	    assertFalse(result);

		result = App.dateCheck("02-29-2001");
	    assertFalse(result);

		result = App.dateCheck("02-29-2000");
	    assertTrue(result);

		result = App.dateCheck("11-20-2000");
	    assertTrue(result);
	}
	
	
	@Test
	public void testIsNumber() {
		
		boolean result = App.isNumber("10000000");
	    assertFalse(result);
	    
		result = App.isNumber("ds213");
	    assertFalse(result);
	    
		result = App.isNumber("-213");
	    assertFalse(result);
	    
		result = App.isNumber("23.23");
	    assertFalse(result);
	    
		result = App.isNumber("");
	    assertFalse(result);
	    
		result = App.isNumber(" ");
	    assertFalse(result);
	    
		result = App.isNumber(null);
	    assertFalse(result);
	    
		result = App.isNumber("12345");
	    assertTrue(result);
	}
	
	
	@Test
	public void testSaleseport() throws SQLException {
		String myUrl = "jdbc:postgresql://localhost:5432/db1";
		Connection conn = DriverManager.getConnection(myUrl, App.user, App.password);
		Statement st = conn.createStatement();
		try {
			int purchaseValue = 0;
			int demandValue = 0;
			
			String[] testStr  = {"SALESREPORT", "apple", "02-12-2000"};
			
			String purchaseQuery = "SELECT distinct "
										+ "purchase.purchase_price, "
										+ "purchase.purchase_id, "
										+ "purchase.purchase_amount, "
										+ "purchase.purchase_date "
								+ "FROM "
										+ "products "
								+ "LEFT JOIN purchase "
								+ "ON "
										+ "purchase.products_id = "
											+ "(select distinct id from products where product = '"+testStr[1]+"') "
								+ "AND purchase.purchase_date <= '"+testStr[2]+"'";
			String demandQuery = "SELECT distinct  "
										+ "demand.demand_price, "
										+ "demand.demand_id, "
										+ "demand.demand_amount, "
										+ "demand.demand_date "
								+ "FROM "
										+ "products "
								+ "INNER JOIN demand "
								+ "ON "
										+ "demand.products_id = "
											+ "(select distinct id from products where product = '"+testStr[1]+"') "
								+ "AND demand.demand_date <= '"+testStr[2]+"' ";
			
			
			ResultSet rs = st.executeQuery(demandQuery);
			
			while(rs.next()) {
				String demandPrice = rs.getString("demand_price");
				String demandAmount = rs.getString("demand_amount");
				demandValue=demandValue+Integer.parseInt(demandAmount)*Integer.parseInt(demandPrice);
			}
			
			
			rs= st.executeQuery(purchaseQuery);
			while(rs.next()) {
				String purchasePrice = rs.getString("purchase_price");
				String purchaseAmount = rs.getString("purchase_amount");
				purchaseValue=purchaseValue+Integer.parseInt(purchaseAmount)*Integer.parseInt(purchasePrice);
			}

			st.close();
			int expected = purchaseValue-demandValue;
			
			int actual;
				actual = App.salesreport(testStr,"purchase") - App.salesreport(testStr,"demand");
		    assertEquals(expected, actual);
		    
		    
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		 finally {
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
	
	 
	@Test
	public void testQuery() {

		String[] testStrProducts2  = {"NEWPRODUCT", "testProduct", "dsfds"};
		try {
			assertFalse(App.query(testStrProducts2));
			String[] testStrProducts  = {"NEWPRODUCT", "testProduct"};
			assertTrue(App.query(testStrProducts));
			assertFalse(App.query(testStrProducts));
			testStrProducts[0]  = "";
			assertFalse(App.query(testStrProducts));
			testStrProducts[0]  = null;
			testStrProducts[0]  = "NEWPRODUCT";
			assertFalse(App.query(testStrProducts));
			testStrProducts[1]  = "";
			assertFalse(App.query(testStrProducts));
			testStrProducts[1]  = null;
			assertFalse(App.query(testStrProducts));
			testStrProducts[1]  = "d5sf465)(";
			assertFalse(App.query(testStrProducts));
			testStrProducts[1]  = "testProductddsfdsfdffsfdsfsdfddsfdsfdsfdsfdfdsfdsffsdgsfsdddsfdsfdffsfdsfsdfddsfdsfdsfdsfdfdsfdsffsdgsfsdddsfdsfdffsfdsfsdfddsfdsfdsfdsfdfdsfdsffsdgsfsd";
			assertFalse(App.query(testStrProducts));

			String[] testStrPurchase  = {"PURCHASE", "testProduct", "3", "3000", "02-12-2000"};
			String[] testStrPurchase2  = {"PURCHASE", "testProduct", "3", "3000", "02-12-2000", "dsfdsfs"};
			assertTrue(App.query(testStrPurchase));
			assertFalse(App.query(testStrPurchase2));
			
			testStrPurchase[1]  = null;
			assertFalse(App.query(testStrPurchase));
			testStrProducts[1]  = "";
			assertFalse(App.query(testStrPurchase));
			testStrProducts[1]  = "d5sf465)(";
			assertFalse(App.query(testStrPurchase));
			testStrProducts[1]  = "testProductddsfdsfdffsfdsfsdfddsfdsfdsfdsfdfdsfdsffsdgsfsdddsfdsfdffsfdsfsdfddsfdsfdsfdsfdfdsfdsffsdgsfsdddsfdsfdffsfdsfsdfddsfdsfdsfdsfdfdsfdsffsdgsfsd";
			assertFalse(App.query(testStrPurchase));

			testStrPurchase[1] = "testProduct";
			testStrPurchase[2] = "ds213";
			assertFalse(App.query(testStrPurchase));
			testStrPurchase[2] = "-213";
			assertFalse(App.query(testStrPurchase));
			testStrPurchase[2] = "23.23";
			assertFalse(App.query(testStrPurchase));
			testStrPurchase[2] = "";
			assertFalse(App.query(testStrPurchase));
			testStrPurchase[2] = " ";
			assertFalse(App.query(testStrPurchase));

			testStrPurchase[2] = "3";
			testStrPurchase[3] = "ds213";
			assertFalse(App.query(testStrPurchase));
			testStrPurchase[3] = "-213";
			assertFalse(App.query(testStrPurchase));
			testStrPurchase[3] = "23.23";
			assertFalse(App.query(testStrPurchase));
			testStrPurchase[3] = "";
			assertFalse(App.query(testStrPurchase));
			testStrPurchase[3] = " ";
			assertFalse(App.query(testStrPurchase));

			testStrPurchase[2] = "3000";
			testStrPurchase[4]  = null;
			assertFalse(App.query(testStrPurchase));
			testStrPurchase[4] = "11dsfa0";
			assertFalse(App.query(testStrPurchase));
			testStrPurchase[4] = "11.11.2000";
			assertFalse(App.query(testStrPurchase));
			testStrPurchase[4] = "11/11/2000";
			assertFalse(App.query(testStrPurchase));
			testStrPurchase[4] = "20-02-2000";
			assertFalse(App.query(testStrPurchase));
			testStrPurchase[4] = "";
			assertFalse(App.query(testStrPurchase));
			testStrPurchase[4] = " ";
			assertFalse(App.query(testStrPurchase));
			testStrPurchase[4] = "02-29-2001";
			assertFalse(App.query(testStrPurchase));

			String[] testStrDemand  = {"DEMAND", "testProduct","2","2000", "03-12-2000"};
			String[] testStrDemand2  = {"DEMAND", "testProduct","2","2000", "03-12-2000", "dsfdsfs"};
			assertTrue(App.query(testStrDemand));
			assertFalse(App.query(testStrDemand2));
			
			testStrDemand[1]  = null;
			assertFalse(App.query(testStrDemand));
			testStrProducts[1]  = "";
			assertFalse(App.query(testStrDemand));
			testStrProducts[1]  = "d5sf465)(";
			assertFalse(App.query(testStrDemand));
			testStrProducts[1]  = "testProductddsfdsfdffsfdsfsdfddsfdsfdsfdsfdfdsfdsffsdgsfsdddsfdsfdffsfdsfsdfddsfdsfdsfdsfdfdsfdsffsdgsfsdddsfdsfdffsfdsfsdfddsfdsfdsfdsfdfdsfdsffsdgsfsd";
			assertFalse(App.query(testStrDemand));

			testStrDemand[1] = "testProduct";
			testStrDemand[2] = "ds213";
			assertFalse(App.query(testStrDemand));
			testStrDemand[2] = "-213";
			assertFalse(App.query(testStrDemand));
			testStrDemand[2] = "23.23";
			assertFalse(App.query(testStrDemand));
			testStrDemand[2] = "";
			assertFalse(App.query(testStrDemand));
			testStrDemand[2] = " ";
			assertFalse(App.query(testStrDemand));

			testStrDemand[2] = "3";
			testStrDemand[3] = "ds213";
			assertFalse(App.query(testStrDemand));
			testStrDemand[3] = "-213";
			assertFalse(App.query(testStrDemand));
			testStrDemand[3] = "23.23";
			assertFalse(App.query(testStrDemand));
			testStrDemand[3] = "";
			assertFalse(App.query(testStrDemand));
			testStrDemand[3] = " ";
			assertFalse(App.query(testStrDemand));

			testStrDemand[2] = "3000";
			testStrDemand[4]  = null;
			assertFalse(App.query(testStrDemand));
			testStrDemand[4] = "11dsfa0";
			assertFalse(App.query(testStrDemand));
			testStrDemand[4] = "11.11.2000";
			assertFalse(App.query(testStrDemand));
			testStrDemand[4] = "11/11/2000";
			assertFalse(App.query(testStrDemand));
			testStrDemand[4] = "20-02-2000";
			assertFalse(App.query(testStrDemand));
			testStrDemand[4] = "";
			assertFalse(App.query(testStrDemand));
			testStrDemand[4] = " ";
			assertFalse(App.query(testStrDemand));
			testStrDemand[4] = "02-29-2001";
			assertFalse(App.query(testStrDemand));
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMain() throws IOException,NullPointerException {
	    String[] args = null;
	    ByteArrayInputStream in = new ByteArrayInputStream("dsfd dsfsd  dsfds".getBytes());
	    System.setIn(in);

		    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		    PrintStream out = new PrintStream(outputStream);
		    PrintStream oldOut = System.out;
		    System.setOut(out);
		    App.main(args);
		    System.out.flush();
		    System.setOut(oldOut);
		    String actual = outputStream.toString();
		    assertEquals("Error", actual);
		    
		    outputStream = new ByteArrayOutputStream();
		    out = new PrintStream(outputStream);
		    in = new ByteArrayInputStream("NEWPRODUCT testProduct2".getBytes());
		    System.setIn(in);
		    System.out.flush();
		    System.setOut(out);
		    App.main(args);
		    System.out.flush();
		    System.setOut(oldOut);
		    actual = outputStream.toString();
		    assertEquals("Ok", actual);

		    outputStream = new ByteArrayOutputStream();
		    out = new PrintStream(outputStream);
		    in = new ByteArrayInputStream("NEWPRODUCT ^%&*&()_".getBytes());
		    System.setIn(in);
		    System.out.flush();
		    System.setOut(out);
		    App.main(args);
		    System.out.flush();
		    System.setOut(oldOut);
		    actual = outputStream.toString();
		    assertEquals("Error", actual);

		    outputStream = new ByteArrayOutputStream();
		    out = new PrintStream(outputStream);
		    in = new ByteArrayInputStream("NEWPRODUCT".getBytes());
		    System.setIn(in);
		    System.out.flush();
		    System.setOut(out);
		    App.main(args);
		    System.out.flush();
		    System.setOut(oldOut);
		    actual = outputStream.toString();
		    assertEquals("Error", actual);

		    
		    outputStream = new ByteArrayOutputStream();
		    out = new PrintStream(outputStream);
		    in = new ByteArrayInputStream("SALESREPORT testProduct 04-12-2001".getBytes());
		    System.setIn(in);
		    System.out.flush();
		    System.setOut(out);
		    App.main(args);
		    System.out.flush();
		    System.setOut(oldOut);
		    actual = outputStream.toString();
		    System.out.println(actual);
		    assertEquals("5000", actual);

		    outputStream = new ByteArrayOutputStream();
		    out = new PrintStream(outputStream);
		    in = new ByteArrayInputStream("SALESREPORT testProduct 04-12-2001 dfgfdg".getBytes());
		    System.setIn(in);
		    System.out.flush();
		    System.setOut(out);
		    App.main(args);
		    System.out.flush();
		    System.setOut(oldOut);
		    actual = outputStream.toString();
		    System.out.println(actual);
		    assertEquals("Error", actual);

		    outputStream = new ByteArrayOutputStream();
		    out = new PrintStream(outputStream);
		    in = new ByteArrayInputStream("SALESREPORT t#$%^&*()ct 04-12-2001".getBytes());
		    System.setIn(in);
		    System.out.flush();
		    System.setOut(out);
		    App.main(args);
		    System.out.flush();
		    System.setOut(oldOut);
		    actual = outputStream.toString();
		    System.out.println(actual);
		    assertEquals("Error", actual);

		    outputStream = new ByteArrayOutputStream();
		    out = new PrintStream(outputStream);
		    in = new ByteArrayInputStream("SALESREPORT testProduct 04-12-gv01".getBytes());
		    System.setIn(in);
		    System.out.flush();
		    System.setOut(out);
		    App.main(args);
		    System.out.flush();
		    System.setOut(oldOut);
		    actual = outputStream.toString();
		    System.out.println(actual);
		    assertEquals("Error", actual);
	}
	

    @AfterClass 
    public static void clearDatabase() throws SQLException {

		String myUrl = "jdbc:postgresql://localhost:5432/db1";
		Connection conn = DriverManager.getConnection(myUrl, App.user, App.password);
	
		Statement st = conn.createStatement();
		
		try {

		String deleteFromPurchase = "DELETE FROM purchase WHERE purchase.products_id = (SELECT id FROM products WHERE product = 'testProduct')";
		st.executeUpdate(deleteFromPurchase);
		
		String deleteFromDemand = "DELETE FROM demand WHERE demand.products_id = (SELECT id FROM products WHERE product = 'testProduct')";
		st.executeUpdate(deleteFromDemand);

		String deleteFromProducts = "DELETE FROM products WHERE product = 'testProduct'";
		st.executeUpdate(deleteFromProducts);
		
		String deleteFromProducts2 = "DELETE FROM products WHERE product = 'testProduct2'";
		st.executeUpdate(deleteFromProducts2);
		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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
    
}
