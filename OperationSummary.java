import java.util.Scanner;

public class OperationSummary {
	
	public static void main(String[] args) {
		
			System.out.println("Please enter starting date (yyyy-mm-dd):");
			Scanner sc = new Scanner(System.in);
			String starting_date = sc.nextLine();
			
			System.out.println("Please enter ending date (yyyy-mm-dd):");
			String ending_date = sc.nextLine();
			
			System.out.println("Please enter the name of the file for output:");
			String output_file = sc.nextLine();
			
			PerformanceReport report = new PerformanceReport();
			report.periodic_summary(starting_date, ending_date, output_file);
			sc.close();
	}
}