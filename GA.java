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
   
   public GA(int num_of_generations, int pop_size, double cLower, double cUpper, double mLower, double mUpper) {
      this.GEN_NUM = num_of_generations;
      this.POP_SIZE = pop_size;
      this.globalBest = Double.MAX_VALUE;
      crossoverLower = cLower;
      crossoverUpper = cUpper;
      mutationLower = mLower;
      mutationUpper = mUpper;
      rand = new Random(500);
      
   }
   
   public void add(ArrayList<String> file) {
      
      pop = new Population(file, this.POP_SIZE);
      
   }
   
   public void evolve() {
      
//      long start, end;
      
      int numberOfTimes = 1;
      mutation = mutationLower;
      crossover = crossoverLower;
//      start = System.currentTimeMillis();
      double[][] average = new double[30][1000];
      while (crossover <= crossoverUpper) {
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
            
            for (int count = 0; count < 30; count++) {
               int random_number = (int)(Math.random()*100);
               sb.append("Seed "+ random_number);
               sb.append(',');
               rand = new Random(random_number);
               Population newPop;
               int gens = 1;
               Route bestFit = null;
               Route[] children;
      //         System.out.println("Running... Please be patient");
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

      //            System.out.println(gens + "-> Global Best = " + globalBest);

                  gens++;
               }
               
               sb.append('\n');

               bestFit = endLocalSearch(bestFit);
               bestFit.calcFitness();
               globalBest = bestFit.getFitness();

   //            System.out.println("Best result is:");
   //            printChild(bestFit);
   //            System.out.println("");
   //            System.out.println("With fitness = "+ this.globalBest);
   //            System.out.println("Muataion = "+mutation);
   //            System.out.println("Crossover = "+crossover);
   //            System.out.println("Random seed = " + random_number);
            }
            
            pw.append(sb.toString());
            pw.close();
            
            pw = new PrintWriter(new File("c_"+crossover+"__m_"+mutation+".csv"));
            sb = new StringBuilder();
            
            double compute = 0;
            for (int i = 0; i < average[0].length; i++) {
               compute = 0;
               for (int j = 0; j < average.length; j++) {
                  compute += average[j][i];
               }
               compute = compute / 30;
               sb.append(compute+",");
               sb.append('\n');
            }
            
            pw.write(sb.toString());
            pw.close();
            
         } catch (FileNotFoundException ex) {
            Logger.getLogger(GA.class.getName()).log(Level.SEVERE, null, ex);
         }
         System.out.println("Changing crossover and mutation for the "+ numberOfTimes+" time");
         numberOfTimes++;
         
         mutation += 0.005;
         
         if (mutation  > mutationUpper) {
            mutation = mutationLower;
            crossover += 0.1;
         }
      }
//      end = System.currentTimeMillis();
//      long timeTook = end - start;
//      System.out.println("time it took in seconds = "+(timeTook / 1000));
      
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
//      for (int i = 0; i < POP_SIZE; i++) {
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
         
//         newPop.add(possible);
      
//      }
      
      // set the populations fitness values
//      newPop.setFitnesses();
      
      return possible;
      
   }
   
   private Route mutate(Route r) {
      
      Population newPop = new Population(POP_SIZE);
      Route newRoute;
      
//      for (int i = 0; i < POP_SIZE; i++) {
         
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
      
//      }
      
//      newPop.setFitnesses();
      
      return newRoute;
      
   }

   private Route endLocalSearch(Route bestFit) {
      
//      if (bestFit.size() > 4) {
         
         int s = 4;
         Population newPop = new Population(5040);
         City[] c = new City[s];
         Route newRoute;
         
         int start = rand.nextInt(bestFit.size());
//         System.out.println(start);
         
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
            
//            System.out.println("cities = [");
//            if (mainStart + s >= 52) System.out.println("main + s = " + (mainStart + s));
            for (int j = mainStart; j < mainStart + s; j++) {
               index = possibilities.get(k);
//               System.out.print(index + ", ");
               newRoute.replace(j, c[index]);
               k++;
            }
//            System.out.println("");
//            System.out.println("]");
            
            newPop.add(newRoute);
         }
         
         newPop.setFitnesses();
         newRoute = newPop.getFittest();
         
//      }

//      System.out.println("end");
//      System.out.println("best fit in end is = " + newPop.getFittest().getFitness());
//      System.out.println("new route is = "+ newRoute.getFitness());
//      
      if (newRoute.getFitness() < globalBest) {
//         System.out.println("its less");
         return newRoute;
      } else if (newRoute.getFitness() == globalBest) {
//         System.out.println("its equal");
         return bestFit;
      } else {
//         System.out.println("its more");
         return bestFit;
      }
      
   }
   
}
