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
//   private double mutationUpper;
//   private double mutationLower;
//   private double crossoverUpper;
//   private double crossoverLower;
   
   public GA_vs_Ants(double cLower, double cUpper, double mLower, double mUpper) {
      
//      ga = new GA(8245, 500);
      ga = new GA(1000, 200, cLower, cUpper, mLower, mUpper);
//      ant = new Ant_Colony(1000, 1, 5, 500, 0.5);
      loadData();
      
   }
   
   private void loadData() {
      
      //File file = new File("berlin52.txt");
      //File file = new File("dj38.tsp");
      File file = new File("pr76.tsp");
      ArrayList<String> cities = new ArrayList<>();
      
      
      try {
         
         BufferedReader read = new BufferedReader(new FileReader(file));
         String line;
         
         while ((line = read.readLine()) != null) {
            cities.add(line);
         }
         
         ga.add(cities);
//         ant.add(cities);
         ga.evolve();
//         ant.findBest();
         
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
