package ga_vs_ants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

public class GA_vs_Ants {
   
   private GA ga;
   private Ant_Colony ant;
   
   public GA_vs_Ants(double cLower, double cUpper, double mLower, double mUpper) {
      
      loadData(cLower, cUpper, mLower, mUpper);
      
   }
   
   private void loadData(double cLower, double cUpper, double mLower, double mUpper) {
      
      File file = new File("berlin52.txt");
      ArrayList<String> cities = new ArrayList<>();
	  
	  double crossover = cLower;
	  double mutation = mLower;
      
      
      try {
         
         BufferedReader read = new BufferedReader(new FileReader(file));
         String line;
         
         while ((line = read.readLine()) != null) {
            cities.add(line);
         }
         
         
		 int numberOfTimes = 1;
		 while (crossover <= cUpper) {
			 ga = new GA(1000, 200, crossover, mutation);
			 ga.evolve(cities);
			
			 System.out.println("Changing crossover and mutation for the "+ numberOfTimes+" time");
			 numberOfTimes++;
			
			 mutation += 0.005;
         
			 if (mutation  > mUpper) {
				mutation = mLower;
				crossover += 0.1;
			 }
		 }
		 
		 System.out.println("================================================");
		 System.out.println("==                                            ==");
		 System.out.println("==                                            ==");
		 System.out.println("==                                            ==");
		 System.out.println("==                   DONE                     ==");
		 System.out.println("==                                            ==");
		 System.out.println("==                                            ==");
		 System.out.println("==                                            ==");
		 System.out.println("================================================");
         
      } catch (FileNotFoundException ex) {
         Logger.getLogger(GA_vs_Ants.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
         Logger.getLogger(GA_vs_Ants.class.getName()).log(Level.SEVERE, null, ex);
      }
      
   }

   public static void main(String[] args) {
      
      double cLower = 0, cUpper = 0, mLower = 0, mUpper = 0;
      
      for (int i = 0; i < args.length; i++) {
         switch (args[i]) {
            case "-c":
               i++;
               cLower = Double.parseDouble(args[i]);
               i++;
               cUpper = Double.parseDouble(args[i]);
               break;
            case "-m":
               i++;
               mLower = Double.parseDouble(args[i]);
               i++;
               mUpper = Double.parseDouble(args[i]);
         }
      }

      System.out.println("-c " + cLower + " -m " + mUpper);
      
      new GA_vs_Ants(cLower, cUpper, mLower, mUpper);
   }
   
}
