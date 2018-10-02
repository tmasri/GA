package ga_vs_ants;

public class City {
   
   private int city_number;
   private Double xVal, yVal, fitness;
   
   public City(int num, double x, double y) {
      
      this.city_number = num;
      this.xVal = x;
      this.yVal = y;
      
   }
   
   public int getNumber() {
      return this.city_number;
   }
   
   public double getX() {
      return this.xVal;
   }
   
   public double getY() {
      return this.yVal;
   }
   
   // calculates the distance between this city and
   // the given city
   public double to(City c) {
      
      // x1 - x2 squared
      double x = this.xVal - c.getX();
      x *= x;
      
      // y1 - y2 squared
      double y = this.yVal - c.getY();
      y *= y;
      
      return Math.sqrt(x + y);
      
   }
   
}
