import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class PerformanceReport {
	public boolean st_flag = false;
	public boolean ed_flag = false;
	public boolean o_flag = false;
	public String st_date = null;
	public String ed_date = null;
	public String output_path = null;
	
	public void periodic_summary(String st_date, String ed_date, String output_path) {
		try{
			boolean isValid_st = st_date.matches("^\\d{4}-\\d{2}-\\d{2}$");						//regex for validating date format(YYYY-MM-DD)
			boolean isValid_ed = ed_date.matches("^\\d{4}-\\d{2}-\\d{2}$");						
			
			if(st_date != null && !st_date.isEmpty() && isValid_st == true){					//validating that input date is neither empty nor null 
				if(isValid_st == true && isValid_ed == true)  {
					st_flag = true;
				}
			}
			else {
				System.out.println("Please input starting date in valid format");
				st_flag = false;
			}
			
			if(ed_date != null && !ed_date.isEmpty() && isValid_ed == true){
					ed_flag = true;
			}
			else {
				System.out.println("Please input ending date in valid format");
				ed_flag = false;
			}
			
			if(output_path != null && !output_path.isEmpty()) {									//validating file name is neither empty nor null			
				o_flag = true;
			}
			else {
				System.out.println("Please enter a valid file name");			
				o_flag = false;
			}
			
			if(st_flag == true && ed_flag == true && o_flag == true) {
				try {
					Class.forName("com.mysql.cj.jdbc.Driver");  								//loading a connection using JDBC driver
					Connection con=DriverManager.getConnection("jdbc:mysql://db.cs.dal.ca:3306?serverTimezone=UTC", "htrivedi", "B00836700");			//connecting the database, including login credentials
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();			
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document doc = db.newDocument();
					
					Statement stmt=con.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);									//creating a statement to run					
					stmt.execute("use csci3901;");												//executing the statment
					
					PreparedStatement c_list = con.prepareCall("select c.customerName, c.addressLine1, c.city, c.postalCode, c.country, count(c.customerNumber) as orders_made, sum(od.quantityOrdered*od.priceEach) as Total_orderValue  from customers c join orders o on c.customerNumber = o.customerNumber join orderdetails od on od.orderNumber = o.orderNumber where orderDate between ? and ? and o.status != 'Cancelled' group by c.customerName order by c.customerName asc;");
				    PreparedStatement p_list = con.prepareCall("select p.productLine, p.productName, p.productVendor, od.quantityOrdered, (od.quantityOrdered*od.priceEach) from products p join orderdetails od on p.productCode = od.productCode join orders o on od.orderNumber = o.orderNumber where orderDate between ? and ? and o.status != 'Cancelled';");
				    PreparedStatement s_list = con.prepareCall("select e.firstName, e.lastName, o.city, count(distinct ord.customerNumber) as active_customers, sum(ordd.quantityOrdered*ordd.priceEach) from employees e join offices o on e.officeCode = o.officeCode join customers c on e.employeeNumber = c.salesRepEmployeeNumber join orders ord on c.customerNumber = ord.customerNumber join orderdetails ordd on ord.orderNumber = ordd.orderNumber where ord.orderDate between ? and ? and ord.status != 'Cancelled';");
				   
					c_list.setString(1, st_date);												//assigning dynamic values to prepared statement
					c_list.setString(2, ed_date);
					
					p_list.setString(1, st_date);
					p_list.setString(2, ed_date);
					
					s_list.setString(1, st_date);
					s_list.setString(2, ed_date);
					
				    Element root = doc.createElement("year_end_summary");						//root element
				    doc.appendChild(root);
				    
				    Element year = doc.createElement("year");
				    root.appendChild(year);
					
				    Element start_date = doc.createElement("start_date");
				    start_date.appendChild(doc.createTextNode(st_date));
		            year.appendChild(start_date);
		            
		            Element end_date = doc.createElement("end_date");
		            end_date.appendChild(doc.createTextNode(ed_date));
		            year.appendChild(end_date);
				    
		            //customer information
				    try {
				    	con.setAutoCommit(false);
				    	ResultSet rs=c_list.executeQuery();
				    	while(rs.next())														//iterating through result set values
						{
						
						Element customer_list = doc.createElement("customer_list");
					    root.appendChild(customer_list);
					    
					    Element customer=doc.createElement("customer");
						customer_list.appendChild(customer);
						
					    Element customer_name = doc.createElement("customer_name");
					    if(rs.getString(1) != null){											//if a field has value null, then corresponding empty XML element will be generated
					    	customer_name.appendChild(doc.createTextNode(rs.getString(1)));
					    }
			            customer.appendChild(customer_name);
			            
						Element address=doc.createElement("address");
						customer.appendChild(address);
						
						Element street_address=doc.createElement("street_address");
						if(rs.getString(2) != null){
							street_address.appendChild(doc.createTextNode(rs.getString(2)));
					    }
						address.appendChild(street_address);
						
						Element city=doc.createElement("city");
						if(rs.getString(3) != null){
							city.appendChild(doc.createTextNode(rs.getString(3)));
					    }
						address.appendChild(city);
						
						Element postal_code=doc.createElement("postal_code");
						if(rs.getString(4) != null){
							postal_code.appendChild(doc.createTextNode(rs.getString(4)));
					    }
						address.appendChild(postal_code);
						
						Element custcountry=doc.createElement("country");
						if(rs.getString(5) != null){
							custcountry.appendChild(doc.createTextNode(rs.getString(5)));
					    }
						address.appendChild(custcountry);
						
						Element custOrders=doc.createElement("num_orders");
						if(rs.getString(6) != null){
							custOrders.appendChild(doc.createTextNode(rs.getString(6)));
					    }
						customer.appendChild(custOrders);
						
						Element custTotal=doc.createElement("order_value");
						if(rs.getString(7) != null){
							custTotal.appendChild(doc.createTextNode(rs.getString(7)));
					    }
						customer.appendChild(custTotal);
						}
				    	rs.close();
				    	}
				    catch(SQLException e) {
				    	System.out.println(e);
				    }
				    
				    //product_information
				    try {
				    	ResultSet rs=p_list.executeQuery();
				    	while(rs.next())
						{
						
						Element product_list = doc.createElement("product_list");
					    root.appendChild(product_list);
					    
					    Element product_set=doc.createElement("product_set");
						product_list.appendChild(product_set);
						
					    Element product_line_name = doc.createElement("product_line_name");
					    if(rs.getString(1) != null){
					    	product_line_name.appendChild(doc.createTextNode(rs.getString(1)));
					    }
			            product_set.appendChild(product_line_name);
			            
						Element product=doc.createElement("product");
						product_set.appendChild(product);
						
						Element product_name=doc.createElement("product_name");
						if(rs.getString(2) != null){
							product_name.appendChild(doc.createTextNode(rs.getString(2)));
					    }
						product.appendChild(product_name);
						
						Element product_vendor=doc.createElement("product_vendor");
						if(rs.getString(3) != null){
							product_vendor.appendChild(doc.createTextNode(rs.getString(3)));
					    }
						product.appendChild(product_vendor);
						
						Element units_sold=doc.createElement("units_sold");
						if(rs.getString(4) != null){
							units_sold.appendChild(doc.createTextNode(rs.getString(4)));
					    }
						product.appendChild(units_sold);
						
						Element total_sales=doc.createElement("total_sales");
						if(rs.getString(5) != null){
							total_sales.appendChild(doc.createTextNode(rs.getString(5)));
					    }
						product.appendChild(total_sales);
						}
				    	rs.close();
				    }
				    catch(SQLException e) {
				    	System.out.println(e);
				    }
				    
				    //employee_information
				    try {
				    	ResultSet rs=s_list.executeQuery();
				    	while(rs.next())
						{
						
						Element staff_list = doc.createElement("staff_list");
					    root.appendChild(staff_list);
					    
					    Element employee=doc.createElement("employee");
						staff_list.appendChild(employee);
						
					    Element first_name = doc.createElement("first_name");
					    if(rs.getString(1) != null){
					    	first_name.appendChild(doc.createTextNode(rs.getString(1)));
					    }
			            employee.appendChild(first_name);
			            
						Element last_name=doc.createElement("last_name");
						if(rs.getString(2) != null){
							last_name.appendChild(doc.createTextNode(rs.getString(2)));
					    }
						employee.appendChild(last_name);
						
						Element office_city=doc.createElement("office_city");
						if(rs.getString(3) != null){
							office_city.appendChild(doc.createTextNode(rs.getString(3)));
					    }
						employee.appendChild(office_city);
						
						Element active_customers=doc.createElement("active_customers");
						if(rs.getString(4) != null){
							active_customers.appendChild(doc.createTextNode(rs.getString(4)));
					    }
						employee.appendChild(active_customers);
						
						Element total_sales=doc.createElement("total_sales");
						if(rs.getString(5) != null){
							total_sales.appendChild(doc.createTextNode(rs.getString(5)));
					    }
						employee.appendChild(total_sales);
						}
				    	rs.close();
				    }
				    catch(SQLException e) {
				    	System.out.println(e);
				    }
				    
				    try {
					 TransformerFactory transformerFactory = TransformerFactory.newInstance();				//create the XML file
			         Transformer transformer = transformerFactory.newTransformer();							//transform the DOM object to XML file
			         DOMSource domSource = new DOMSource(doc);
			         StreamResult streamResult = new StreamResult(output_path);
			         transformer.transform(domSource, streamResult);
			         System.out.println("XML file created !");
				    }		    	
				    
				    catch(TransformerException tfe) {
				    	tfe.printStackTrace();
				    }
			         stmt.close();
			         con.close(); 
				}
				catch(SQLException e) {
					System.out.println(e);
				}
		}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}