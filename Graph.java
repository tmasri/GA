package ga_vs_ants;

import java.util.ArrayList;

public class Graph {
   
   private ArrayList<City> cities;
   private double[][] distances;
   private double[][] pheromone;
   
   public Graph(ArrayList<String> c) {
      
      this.cities = new ArrayList<>();
      String[] val;
      
      // turn strings into cities
      for (int i = 0; i < c.size(); i++) {
         
         val = c.get(i).split(" ");
         this.cities.add( new City(
                 Integer.parseInt(val[0]),
                 Double.parseDouble(val[1]),
                 Double.parseDouble(val[2])
         ));
         
      }
      
      // get graph info
      graphInfo();
      
   }
   
   /*
    * This method will take a city, go through all the other
    * cities and get the distance from that city to all the
    * other cities
    */
   private void graphInfo() {
      
      this.distances = new double[this.cities.size()][this.cities.size()];
      
      City c1, c2;
      
      // take the city
      for (int i = 0; i < this.cities.size(); i++) {
         // go through all the other cities
         c1 = this.cities.get(i);
         for (int j = 0; j < this.cities.size(); j++) {
            c2 = this.cities.get(j);
            this.distances[i][j] = c1.to(c2);
         }
      }
      
   }
   
   public double getVal(int i, int j) {
      return this.distances[i][j];
   }
   
   public int size() {
      return this.cities.size();
   }
   
}
