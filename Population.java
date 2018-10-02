package ga_vs_ants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Population {
   
   private int POP_SIZE;
   private ArrayList<Route> pop;
   private Random rand;
   private ArrayList<City> city_list;
   private double[][] distance;
   private City[] closest;
   
   public Population(int pop_size) {
      // one will take in a list and initialize it
      this.pop = new ArrayList<>();
      this.POP_SIZE = pop_size;
      rand = new Random(500);
      
   }
   
   // Constructor to build population read from
   // file and shuffle that population
   public Population(ArrayList<String> file, int pop_size) {
      this.POP_SIZE = pop_size;
      rand = new Random(500);
      
      buildList(file);
      buildDistances(file);
      // only get 10% of the number of cities given
      int closest_size = (int)((double)(file.size()) * 0.25);
      closest = new City[closest_size];
      
      // build population
      this.pop = new ArrayList<>();
      Route r;
      int random;
      
//      System.out.println("BUILDING INITIAL POPULATION...");
      for (int i = 0; i < POP_SIZE; i++) {
         
         r = new Route();
         // first city is random
         r.add(city_list.get(rand.nextInt(file.size())));
         
         // fill up the rest
         for (int j = 0; j < file.size() - 1; j++) {
            
            closest = getClosestTo(r.getCity(j), closest_size, r);
            
            random = rand.nextInt(closest.length);
            // make sure that you dont choose a null point
            // in the array
            while (closest[random] == null) {
               random = rand.nextInt(closest.length );
               
            }
            
            r.add(new City(
                    closest[random].getNumber(),
                    closest[random].getX(),
                    closest[random].getY()
            ));
            
         }
         
         pop.add(r);
         
      }
    
//      System.out.println("INITIAL POPULATION");
      
//      for (int i = 0; i < POP_SIZE; i++) {
//         System.out.println((i+1) + "-> [");
//         for (int j = 0; j < pop.get(i).size(); j++) {
//            System.out.print(pop.get(i).getCity(j).getNumber()+", ");
//         }
//         System.out.println("]");
//      }
      
//      String[] parse;
//      for (int i = 0; i < POP_SIZE; i++) {
//         
//         r = new Route();
//         for (int j = 0; j < file.size(); j++) {
//            parse = file.get(j).split(" ");
//            r.add(new City(
//                    Integer.parseInt( parse[0] ),
//                    Double.parseDouble( parse[1] ),
//                    Double.parseDouble( parse[2] )
//                  ));
//            
//         }
//         
//         r.shuffle();
//         this.pop.add(r);
//         
//      }
      
   }
   
   public Route tournamentSelector() {
      
      Population newPop = new Population(this.POP_SIZE);
      int r;
      for (int i = 0; i < this.POP_SIZE; i++) {
         r = rand.nextInt(this.POP_SIZE);
         newPop.add(this.pop.get(r));
      }
      
      newPop.setFitnesses();
      
      return newPop.getFittest();
      
   }
   
   public void setFitnesses() {
      
      for (int i = 0; i < this.pop.size(); i++) {
         this.pop.get(i).calcFitness();
      }
      
   }
   
   public Route getFittest() {
      
      Route r = null;
      double best = Double.MAX_VALUE;
      
      for (int i = 0; i < this.pop.size(); i++) {
         if (this.pop.get(i).getFitness() < best) {
            r = this.pop.get(i);
            best = this.pop.get(i).getFitness();
         }
      }
      
      return r;
      
   }
   
   public void add(Route r) {
      this.pop.add(r);
   }
   
   public Route getRoute(int i) {
      return this.pop.get(i);
   }
   
   public void copy(Population p) {
      
      Route newRoute;
      for (int i = 0; i < p.size(); i++) {
         newRoute = new Route();
         newRoute.copy(p.getRoute(i));
         this.pop.set(i, newRoute);
      }
      
   }
   
   private City[] getClosestTo(City c, int s, Route r) {
      
      City[] next = new City[s];
      City[] newNext = null;
      int city_num = c.getNumber() - 1;
      
      double[] distances = new double[city_list.size()];
      
      // get the distances from city c to all the other cities
      // from the distances array
      for (int i = 0; i < distances.length; i++) {
         distances[i] = distance[city_num][i];
      }
      
      // sort array from closest to furthest
      Arrays.sort(distances);
      boolean city_added = false;
      
      // fill the next city array
      for (int i = 0; i < s; i++) {
         
         // go through distance array to see if number matches
         // to get the city
         for (int k = 0; k < distances.length; k++) {
            for (int j = 0; j < distance.length; j++) {
               if (distance[city_num][j] == distances[k] && !r.contains(city_list.get(j))) {
                  next[i] = city_list.get(j);
                  city_added = true;
                  j = distance.length;
               }
            }
         }
         
         if (!city_added) next[i] = null;
         
      }
      return next;
      
   }
   
   public int size() {
      return this.pop.size();
   }

   // will fill the city list to choose the city according to local
   // search algorithm
   private void buildList(ArrayList<String> file) {
      
      String[] parse;
      city_list = new ArrayList<>();
      
      for (int j = 0; j < file.size(); j++) {
         parse = file.get(j).split(" ");
         city_list.add(new City(
                 Integer.parseInt( parse[0] ),
                 Double.parseDouble( parse[1] ),
                 Double.parseDouble( parse[2] )
               ));

      }
      
   }

   private void buildDistances(ArrayList<String> file) {
      
      distance = new double[file.size()][file.size()];
      
      // get distances between all cities
      for (int i = 0; i < file.size(); i++) {
         for (int j = 0; j < file.size(); j++) {
            distance[i][j] = city_list.get(i).to( city_list.get(j) );
         }
      }
      
   }
   
}
