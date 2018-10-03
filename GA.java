package ga_vs_ants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GA {
   
   private Population pop;
   private int GEN_NUM;
   private int POP_SIZE;
   private double globalBest;
   private double crossover;
   private double crossoverLower;
   private double crossoverUpper;
   private double mutation;
   private double mutationLower;
   private double mutationUpper;
   
   private Random rand;
   
   public GA(int num_of_generations, int pop_size, double c, double m) {
      this.GEN_NUM = num_of_generations;
      this.POP_SIZE = pop_size;
      this.globalBest = Double.MAX_VALUE;
      crossover = c;
      mutation = m;
      rand = new Random(500);
      
   }
   
   public void add(ArrayList<String> file) {
      
      pop = new Population(file, this.POP_SIZE);
      
   }
   
   public void evolve(ArrayList<String> file) {
      
      double[][] average = new double[30][1000];
	  
		 
      try {
         PrintWriter pw = new PrintWriter(new File("data_on_c_"+crossover+"__m_"+mutation+".csv"));
         StringBuilder sb = new StringBuilder();
            
         // build gens in file
         sb.append("");
         sb.append(',');
         for (int i = 1; i <= GEN_NUM; i++) {
            sb.append("gen "+i);
            sb.append(',');
         }
         sb.append('\n');
         
			long random_number;
			average = new double[30][1000];
         for (int count = 0; count < 30; count++) {
            random_number = System.currentTimeMillis();//(int)(Math.random()*100);
            sb.append("Seed "+ random_number);
            sb.append(',');
            rand = new Random(random_number);
            Population newPop;
            int gens = 1;
            Route bestFit = null;
            Route[] children;
			   newPop = null;
			   
			   pop = new Population(file, this.POP_SIZE);;
			   
            while (gens <= GEN_NUM) {

               newPop = new Population(this.POP_SIZE);
               pop.setFitnesses(); // evaluate populations fitness values
               bestFit = pop.getFittest(); // select most fit individual

               newPop.add(bestFit); // add most fit route to new population
               for (int i = 0; i < (this.POP_SIZE/2); i++) {
                  children = newChildren();
                  if (newPop.size() == (this.POP_SIZE/2)-1) newPop.add(children[0]);
                  else {
                     newPop.add(children[0]);
                     newPop.add(children[1]);
                  }
               }

               newPop.setFitnesses();

               if (newPop.getFittest().getFitness() < this.globalBest) {
                  bestFit = newPop.getFittest();
                  this.globalBest = bestFit.getFitness();
                  pop.copy(newPop);
               }
               
               average[count][gens-1] = globalBest;
               sb.append(globalBest+"");
               sb.append(',');

               gens++;
            }
               
            sb.append('\n');

            bestFit = endLocalSearch(bestFit);
            bestFit.calcFitness();
            globalBest = bestFit.getFitness();
			   globalBest = Double.MAX_VALUE;

         }
         
         pw.append(sb.toString());
         pw.close();
         
         pw = new PrintWriter(new File("c_"+crossover+"__m_"+mutation+".csv"));
         sb = new StringBuilder();
         
         double compute = 0;
			
			for (int i = 0; i < average[0].length; i++) { // rows
				compute = 0;
				for (int j = 0; j < average.length; j++) { // columns
					compute += average[j][i];
				}
				sb.append((compute / average.length)+"");
				sb.append('\n');
			}
            
         pw.write(sb.toString());
         pw.close();
            
      } catch (FileNotFoundException ex) {
         Logger.getLogger(GA.class.getName()).log(Level.SEVERE, null, ex);
      }
      
   }
   
   private void printChild(Route child) {
      
      for (int i = 0; i < child.size(); i++) {
         System.out.print(child.getCity(i).getNumber() + ", ");
      }
      System.out.println("");
      
   }
   
   private Route[] newChildren() {
      
      Route parent1, parent2, child1, child2;
      double r;
      
      // initialize variables
      child1 = new Route();
      child2 = new Route();

      parent1 = pop.tournamentSelector();
      parent2 = pop.tournamentSelector();

      child1.copy(parent1);
      child2.copy(parent2);

      // crossover
      r = rand.nextDouble();
      if (r < this.crossover) {
         child1 = crossover(parent1, parent2);
         child2 = crossover(parent2, parent1);
      }

      // mutation
      r = rand.nextDouble();
      if (r < this.mutation) {
         child1 = mutate(child1);
         child2 = mutate(child2);
      }

      return new Route[]{ child1, child2 };
      
   }
   
   // builds a new bitmask every time its called
   private boolean[] bitmask() {
      boolean[] bitmask = new boolean[this.pop.getRoute(0).size()];
      for (int i = 0; i < bitmask.length; i++) {
         if(rand.nextDouble() < 0.6)
            bitmask[i] = rand.nextBoolean();
         else
            bitmask[i] = false;
      }
      
      return bitmask;
   }
   
   private Route crossover(Route p1, Route p2) {
      
      Population newPop = new Population(POP_SIZE);
      boolean[] bitmask;
      
      Route possible;
      
      // get multiple possible crossover solutions
      bitmask = bitmask();
      possible = new Route();

      // step 1: copy values corresponding to 1 from parent 1
      for (int j = 0 ; j < p1.size(); j++) {
         if (bitmask[j]) {
            possible.add(p1.getCity(j));
         } else {
            possible.add(new City(-1, 0.0, 0.0));
         }
      }

      // step 2: copy the values from parent 2 that dont exist
      for (int j = 0; j < p2.size(); j++) {
         if (!possible.contains(p2.getCity(j))) {
            possible.replace(p2.getCity(j));
         }
      }
         
      return possible;
      
   }
   
   private Route mutate(Route r) {
      
      Population newPop = new Population(POP_SIZE);
      Route newRoute;
      
      newRoute = new Route();
      
      newRoute.copy(r);

      int ind1 = rand.nextInt(r.size());
      int ind2 = rand.nextInt(r.size());

      while (ind1 == ind2) {
         ind2 = rand.nextInt(r.size());
      }

      City c1, c2;
      c1 = r.getCity(ind1);
      c2 = r.getCity(ind2);

      newRoute.replace(ind1, c2);
      newRoute.replace(ind2, c1);
      
      newPop.add(newRoute);
      
      return newRoute;
      
   }

   private Route endLocalSearch(Route bestFit) {
      
      int s = 4;
      Population newPop = new Population(5040);
      City[] c = new City[s];
      Route newRoute;
      
      int start = rand.nextInt(bestFit.size());
      
      while (start + s >= bestFit.size()) {
         start = rand.nextInt(bestFit.size()/2);
      }
      int mainStart = start;
      
      ArrayList<Integer> possibilities = new ArrayList<>();
      for (int i = 0; i < s; i++) {
         possibilities.add(i);
         c[i] = bestFit.getCity(start);
         start++;
      }
      
      int k;
      int index;
      for (int i = 0; i < 5040; i++) {
         
         Collections.shuffle(possibilities);
         
         newRoute = new Route();
         newRoute.copy(bestFit);
         k = 0;
         
         for (int j = mainStart; j < mainStart + s; j++) {
            index = possibilities.get(k);
            newRoute.replace(j, c[index]);
            k++;
         }
         
         newPop.add(newRoute);
      }
      
      newPop.setFitnesses();
      newRoute = newPop.getFittest();
      
      if (newRoute.getFitness() < globalBest) {
         return newRoute;
      } else if (newRoute.getFitness() == globalBest) {
         return bestFit;
      } else {
         return bestFit;
      }
      
   }
   
}
