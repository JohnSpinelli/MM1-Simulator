import java.util.Collections;

class Event implements Comparable<Event> {
  //Type 0 = Birth, Type 1 = cpu1Death,  Type 2 = cpu2death
  //Type 3 = diskBirth, Type 4 = diskDeath, Type 5 = netBirth, Type 6 = netDeath


  public int type;
  public double time;
  
  
  
  public Event(int eventType, double eventTime) {
    type = eventType; 
    time = eventTime;
 
  }
  
  @Override
  public int compareTo(Event e) {
    double comparedTime = e.time;
    if (this.time > comparedTime) {
      return 1;
    } else if (this.time == comparedTime) {
      return 0;
    }else {
      return -1;
    }
  }
  
  public int getType() {
    return type;
  }
  
  public double getTime() {
    return time;
  }
  
  
  public void setTime(double t) {
    time = t;
  }
  
  public String toString() {
    return ("Time: " + this.time);
  }
}
  


    
    
  