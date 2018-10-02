package ga_vs_ants;

import java.util.ArrayList;
import java.util.Collections;

public class Route {
   
   private ArrayList<City> cities;
   private double fitness;
   
   public Route() {
      this.cities = new ArrayList<>();
      this.fitness = 0;
   }
   
   public void add(City c) {
      this.cities.add(c);
   }
   
   public void calcFitness() {
      
      this.fitness = 0;
      City c1, c2;
      for (int i = 0; i < this.cities.size() - 1; i++) {
         c1 = this.cities.get(i);
         c2 = this.cities.get(i+1);
         this.fitness += c1.to(c2);
      }
      
   }
   
   public Double getFitness() {
      return this.fitness;
   }
   
   public void shuffle() {
      Collections.shuffle(this.cities);
   }
   
   public City getCity(int i) {
      return this.cities.get(i);
   }
   
   // Goes through the route sent and copies the pointers
   // to the cities
   public void copy(Route r) {
      
      for (int i = 0; i < r.size(); i++) {
         cities.add(r.getCity(i));
      }
      
   }
   
   public void replace(City c) {
      for (int i = 0; i < this.cities.size(); i++) {
         if (this.cities.get(i).getNumber() == -1) {
            replace(i, c);
            return;
         }
      }
   }
   
   public void replace(int i, City c) {
//      if (i >= cities.size()) System.out.println("YES IT IS i = "+ i+" and size = "+cities.size());
      this.cities.set(i, c);
   }
   
   public boolean contains(City c) {
      
      for (int i = 0; i < this.cities.size(); i++) {
         if (c.getNumber() == this.cities.get(i).getNumber()) {
            return true;
         }
      }
      return false;
   }
   
   public int size() {
      return this.cities.size();
   }
   
}
