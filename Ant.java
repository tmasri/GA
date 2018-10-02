package ga_vs_ants;

import java.util.ArrayList;

public class Ant {
   
   private double alpha, beta;
   private Graph graph;
   
   // new
   private int[] route;
   private boolean[] visited;
   private double length;
   
   public Ant(double alp, double bet, Graph g) {
      
      this.alpha = alp;
      this.beta = bet;
      
      this.graph = g;
      
      // new
      this.route = new int[g.size()];
      this.visited = new boolean[g.size()];
      length = 0;
      
   }
   
   // adds town to route array
   // makes sure that ant knows that it visited the town
   public void visitCity(int i, int city) {
      this.route[i + 1] = city;
      this.visited[city] = true;
   }
   
   public void clear() {
      for (int i = 0; i < this.visited.length; i++) {
         this.visited[i] = false;
      }
      
      for (int i = 0; i < route.length; i++) {
         route[i] = 0;
      }
      length = 0;
   }
   
   public double routeLength() {
      
      if (length == 0) {
         int s = this.graph.size();

         for (int i = 0; i < s - 1; i++) {
            length += this.graph.getVal(this.route[i], this.route[i+1]);
         }
      }
      
      return length;
      
   }
   
   public void printRoute() {
      
      System.out.print("[ ");
      for (int i = 0; i < route.length; i++) {
         System.out.print(route[i]+", ");
      }
      System.out.println("]");
      
   }
   
   public boolean visited(int i) {
      return this.visited[i];
   }
   
   public int getCityNum(int i) {
      return this.route[i];
   }
   
}
