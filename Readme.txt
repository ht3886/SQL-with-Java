
Overview
--------

This program is used to generate a periodic summary of Mini-Me Toy Car company's operation.
The summary information is extracted from the database and is then stored in a file that 
follows XML format. A summary of the formatting requirements is in the CSCI 3901 
course assignment #5 information in the course's Brightspace space.

The program obtains three important input information in following order:

- Input 1:
  Starting date.
  This is the date from which the operation summary is to be calculated.

- Input 2:
  Ending date.
  This is the date upto which the operation summary is to be calculated.
  
- Input 3:
  Output file name.
  This is the name for the output file that will display the calculated summary.
  This file will be of XML format. 

Output file is designed in a way that : 
 - It uses a simple version of XML.
 - The starting and ending date for summary period are mentioned for reference purpose.
 - Data is extracted in 3 categories: customer information, product information and
   employee information.

Files and external data
-----------------------

There are two main files:

  - OperationSummary.java -- main for the program that prompts the user for input
  - PerformanceReport.java -- class that calculates the actual summary and generates 
			corresponding XML file.

Assumptions
-----------

  - All dates are to be in YYYY-MM-DD format.
  - The output file name will have a .xml extension. This is to incorporate
     proper human readability. 

Choices
-------

  - Address of the customer will display only the "addressLine 1" value.
  - Fields having "null" value will correspond to an empty XML element in output file.
  - I have used PreparedStatement to execute SQL queries since they are much faster 
    as compared to Statement object.

Strategy
---------------------------

The program prompts the user to enter starting and ending date for which
summary is to be calculated. The user then supplies name of file (along with .XML extension)
in which the actual summary information will be stored.
It then establishes a connection to the database using JDBC driver. The prepared statement 
then pre-compile the SQL and send the data to DBMS right away.

The document builder creates a document instance and then starts creating elements 
in the document with the root element being first. It then starts retrieving and modifying 
the values from result set. 

Firstly, customer information is retrieved. This section has <customer_list> as parent tag.
It then retrieves the customer name, address, number of orders in the period and total 
order value.

Secondly, product information is retrieved. This section has <product_list> as parent tag.
It further reports the product line name, and for each product in that product line it reports 
their name, vendor, units sold and total sales.

Finally, employee information is retrieved. This section has <staff_list> as parent tag.
This section includes name, office, number of active customers and total order value 
corresponding to each employee.

After extracting all of the above data, the program creates the XML file using TransformerFactory object.
A new transformer instance and a new DOMSource instance is created. After this, the program 
creates a new StreamResult to the output stream. Finally, it uses transform method to write the
DOM object to the specified XML file.

References
---------------------------

https://www.w3schools.com/sql/default.asp

https://examples.javacodegeeks.com/core-java/xml/parsers/documentbuilderfactory/create-xml-file-in-java-using-dom-parser-example/

https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html

Why this solution is ready to be deployed?
---------------------------------------------------

This program is thoroughly tested for various input combinations. Also, it comprises of a number of validations in order to avoid false inputs. Even the documentation is up to date and program is able to run in an error-free manner.