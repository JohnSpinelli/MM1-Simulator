import java.util.Random;
import java.util.LinkedList;
import java.util.Collections;


public class MM1queue {
  
  public static Random rand = new Random();
  
  
  public static double generateExponential(double lambda) {
    
    double randU = rand.nextDouble();
    double exponential = -1 * ((Math.log(1 - randU)) / lambda);
    return exponential;
  }
  
  public static Event getNextEvent(LinkedList<Event> calendarInput) {
    Event event = calendarInput.remove();
    return event;
  
}
  
  
}
  
  