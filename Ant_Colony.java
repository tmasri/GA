package ga_vs_ants;

import java.util.ArrayList;
import java.util.Random;

public class Ant_Colony {
   
   private int num; // number of ants in the colony
   private int city_size;
   private int gens; // number of generations to go through
   private double pheromone; // amount of pheromone released every step
   private double evaporate; // amount of pheromone evaporation each step
   private double alpha;
   private double beta;
   private Graph graph;
   
   private Random r;
   private double trail[][];
   private double prob[];
   private int curIndex;
   private Ant[] ants;
   private Ant globalBest;
   
   private double antFactor = 0.8;
   private double randSelect = 0.001;
   
   public Ant_Colony(int g, double alph, double bet, double pher, double evap) {
      
      gens = g;
      pheromone = pher;
      evaporate = evap;
      
      alpha = alph;
      beta = bet;
      
      r = new Random(90);
      
   }
   
   public void add(ArrayList<String> cities) {
      graph = new Graph(cities);
      city_size = graph.size();
      
      int n = city_size;
      num = (int)(n * antFactor);
      
      
      trail = new double[n][n];
      prob = new double[n];
      ants = new Ant[num];
      for (int i = 0; i < num; i++) {
         ants[i] = new Ant(alpha, beta, graph);
      }
      
   }
   
   // This will go through gens until it finds the best
   // solution
   public void findBest() {
      
      for (int i = 0; i < trail.length; i++) {
         for (int j = 0; j < trail.length; j++) {
            trail[i][j] = 1.0;
         }
      }
      
      int iteration = 0;
      while (iteration < gens) {
//         System.out.println("iteration number "+iteration);
         
         // DONE
         initialize(); // reset everything and place ants at a random starting point
         
         // MOSTLY DONE, TRY TO ADD THE RANDOM VARIABLE SEE WHAT HAPPENS
         moveAnts(); // move ants to the end and gather data
         
         updatePath(); // applies evaporation and pheromones to path
         
         updateGlobalBest();
         
         
         iteration++;
//         System.out.println("");
//         System.out.println("");
//         System.out.println("");
      }
      
      System.out.println("");
      System.out.println("");
      System.out.println("");
      System.out.println("global best length = "+ globalBest.routeLength());
      globalBest.printRoute();
      
   }
   
   /*
    * Initializes everything, so it makes sure that it resets
    * the visited array to false, and gets a random starting point
    * for all ants to add it to route array
    */
   private void initialize() {
      
      curIndex = -1;
      for (int i = 0; i < ants.length; i++) {
         ants[i] = new Ant(alpha, beta, graph);
         ants[i].clear();
         ants[i].visitCity(curIndex, r.nextInt(city_size));
      }
      
      curIndex++;
      
   }
   
   private void moveAnts() {
      
      while (curIndex < city_size - 1) {
         for (Ant a: ants) {
            a.visitCity(curIndex, selectNextCity(a));
         }
         curIndex++;
      }
      
   }
   
   private int selectNextCity(Ant a) {
      
      if (r.nextDouble() < randSelect) {
         int s = r.nextInt(city_size - curIndex);
         int j = 0;
         for (int i = 0; i < city_size; i++) {
            if (!a.visited(i))
               j++;
            if (j == s) {
               return i;
            }
         }
      }
      
      // if above condition isnt true
      // get probabilities for all the cities
      calcProb(a);
      double rand = r.nextDouble();
      double total = 0;
      int index = 0;
      
      
      
      // go through all the cities
      // double check this method look at the pdf
      for (int i = 0; i < prob.length; i++) {
         total += prob[i];
         if (total >= rand) {
//            index = i;
//            total = prob[i];
            return i;
         }
      }
      
      return index;
      
   }
   
   /*
    * This will calculate the probability of an ant going to each of
    * the unvisited cities
    */
   private void calcProb(Ant a) {
      
      // get the city where the ant is at right now
      int i = a.getCityNum(curIndex);
      
      double denominator = 0;
      // calculate the denominator
      // equation is sum( attractivenessOfPastTransitions.toPower(alpha) * (1/addAttractiveness).toPower(beta) )
      for (int j = 0; j < city_size; j++) {
         // if city was NOT visited then do the calculation
         if (!a.visited(j)) {
            denominator += calculate(i,j);
         }
      }
      
      // calculate the probabilities
      for (int j = 0; j < city_size; j++) {
         if (a.visited(j)) {
            prob[j] = 0;
         } else {
            prob[j] = calculate(i,j) / denominator;
         }
      }
      
      // full equation to this method can be found here
      // http://www.ef.uns.ac.rs/mis/archive-pdf/2011%20-%20No4/MIS2011_4_2.pdf
      // end of page 2 "Design of the Solution"
      
   }
   
   private double calculate(int i, int j) {
      
      return Math.pow(trail[i][j], alpha) * Math.pow(
                                                      1/graph.getVal(i, j),
                                                      beta);
      
   }
   
   /*
    * Goes through all the trails, applies the evaporation rate to them
    * and adds pheromones from ants to the trail that it went through
    */
   private void updatePath() {
      
      // apply evaporation
      for (int i = 0; i < trail.length; i++) {
         for (int j = 0; j < trail.length; j++) {
            trail[i][j] *= (1-evaporate);// * trail[i][j];
         }
      }
      
      // add pheromones from ants to the paths they took
      // go through all the ants
      double pher;
      for (Ant a: ants) {
         pher = pheromone / a.routeLength();
//         System.out.println("pheromone amount = "+ pher);
         for (int i = 0; i < city_size - 1; i++) {
            trail[a.getCityNum(i)][a.getCityNum(i+1)] += pher;
         }
//         trail[a.getCityNum(city_size - 1)][a.getCityNum(0)] += pher;
//         break;
      }
      
   }

   private void updateGlobalBest() {
      
      if (globalBest == null) {
         globalBest = ants[0];
      }
      
      for (Ant a: ants) {
         if (a.routeLength() < globalBest.routeLength()) {
//            System.out.println("a length = "+a.routeLength() +", global = "+globalBest.routeLength());
            globalBest = a;
            System.out.println("global best length = "+ globalBest.routeLength());
            globalBest.printRoute();
         }
      }
      
   }
   
}
