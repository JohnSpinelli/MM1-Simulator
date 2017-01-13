import java.util.LinkedList;
import java.util.Collections;
import java.util.Random;

public class networkController { 
  
  public static LinkedList<Event> calendar = new LinkedList<Event>();
  public static LinkedList<Event> cpuRequests = new LinkedList<Event>();
  public static LinkedList<Event> diskRequests = new LinkedList<Event>();
  public static LinkedList<Event> netRequests = new LinkedList<Event>();
  public static Random rand = new Random();
  public static double cpuTs = 0.020;
  public static double diskTs = 0.100;
  public static double netTs = 0.025;
  public static double simulationEndTime = 100;
  public static double lambda = 40.0;
  public static double controllerTime;
  public static double nextDeath;
  public static double nextBirth;
  public static double monitorT = 1;
  public static double monitorEvents = 0;
  public static double w;
  public static double totalTq;
  public static double totalTs;
  public static double requestCount;
  public static boolean cpu1serverBusy;
  public static boolean cpu2serverBusy;
  public static boolean diskServerBusy;
  public static boolean netServerBusy;
  
  
  
  public static void main(String[] args) {
    
    controllerTime = 0;
    calendar = initCalendar();
    cpuRequests = initRequests();
    diskRequests = initRequests();
    netRequests = initRequests();
    cpu1serverBusy = false;
    cpu2serverBusy = false;
    diskServerBusy = false;
    netServerBusy = false;
    
    
    while (controllerTime < (simulationEndTime)) {
      
      Event event = MM1queue.getNextEvent(calendar);
      controllerTime = event.getTime();
      
      if (event.getType() == 0) {
        handleBirthCpu(calendar,cpuRequests,event,controllerTime);
      }
      
      else if (event.getType() == 1) {
        handleDeathCpu1(calendar,cpuRequests,event,controllerTime);
      }
      
      else if (event.getType() == 2) {
        handleDeathCpu2(calendar,cpuRequests,event,controllerTime);
      }
      
      else if (event.getType() == 3) {
        handleBirthDisk(calendar,diskRequests,event,controllerTime);
      }
      
      else if (event.getType() == 4) {
        handleDeathDisk(calendar,diskRequests,event,controllerTime);
      }
      
      else if (event.getType() == 5) {
        handleBirthNet(calendar,netRequests,event,controllerTime);
      }
      
      else if (event.getType() == 6) {
        handleDeathNet(calendar,netRequests,event,controllerTime);
      }
      
      else if (event.getType() == 7) {
        handleMonitor(calendar,cpuRequests,event,controllerTime);
      }
      
      //System.out.println("Controller Time: " + controllerTime);
      //System.out.println("Event Type,time: (" + event.getType() + ", " + event.getTime() + ")\n");
      
    }
    
    System.out.println("Tq = " + totalTq/requestCount);
    //System.out.println("w = " + w/monitorEvents);
  }
  
  
  public static LinkedList<Event> initCalendar() {
    LinkedList<Event> calendar = new LinkedList<Event>();
    double birthTime = MM1queue.generateExponential(lambda);
    double monitorTime = MM1queue.generateExponential(monitorT);
    Event firstBirth = new Event(0,birthTime);
    Event monitor = new Event(7,monitorTime);
    calendar.add(firstBirth);
    calendar.add(monitor);
    
    return calendar;
  }
  
  public static LinkedList<Event> initRequests() {
    LinkedList<Event> requests = new LinkedList<Event>();
    return requests;
  }
  
  
  public static void handleBirthCpu(LinkedList<Event> cal, LinkedList<Event> req, Event e,double time) {
    
    if (cpu1serverBusy == false) { 
      cpu1serverBusy = true;
      nextDeath = MM1queue.generateExponential(1/cpuTs);
      Event death = new Event(1,time + nextDeath);
      cal.add(death);
      Collections.sort(cal);
      totalTq += nextDeath;
      requestCount++;
      
    }
    
    else if (cpu2serverBusy == false) {
      cpu2serverBusy = true;
      nextDeath = MM1queue.generateExponential(1/cpuTs);
      Event death = new Event(2,time + nextDeath);
      cal.add(death);
      Collections.sort(cal);
      totalTq += nextDeath;
      requestCount++;
    }
    
    else {
      req.add(e);
    }
    
    nextBirth = MM1queue.generateExponential(lambda);
    Event birth = new Event(0,time + nextBirth);
    cal.add(birth);
    Collections.sort(cal);
    
  }
  
  
  public static void handleDeathCpu1(LinkedList<Event> cal, LinkedList<Event> req, Event e, double time) {
    
    double prob = rand.nextDouble();
    if (prob <= 0.1) {
      Event diskBirth = new Event(3,time);
      cal.add(diskBirth);
      Collections.sort(cal);
    }
    
    else if (prob <= 0.5) {
      Event netBirth = new Event(5,time);
      cal.add(netBirth);
      Collections.sort(cal);
    }
    
    if (req.size() > 0) {
      Event birthEvent = req.remove();
      nextDeath = MM1queue.generateExponential(1/cpuTs);
      Event cpu1Death = new Event(1,time + nextDeath);
      cal.add(cpu1Death);
      Collections.sort(cal);
      totalTq += nextDeath;
      requestCount++;
      
      
    }
    else {
      cpu1serverBusy = false;
    }
  }
  
  public static void handleDeathCpu2(LinkedList<Event> cal, LinkedList<Event> req, Event e, double time) {
    
    double prob = rand.nextDouble();
    if (prob <= 0.1) {
      Event diskBirth = new Event(3,time);
      cal.add(diskBirth);
      Collections.sort(cal);
    }
    
    else if (prob <= 0.5) {
      Event netBirth = new Event(5,time);
      cal.add(netBirth);
      Collections.sort(cal);
    }
    
    if (req.size() > 0) {
      Event birthEvent = req.remove();
      nextDeath = MM1queue.generateExponential(1/cpuTs);
      Event cpu2Death = new Event(2,time + nextDeath);
      cal.add(cpu2Death);
      Collections.sort(cal);
      totalTq += nextDeath;
      requestCount++;
      
      
    }
    else {
      cpu2serverBusy = false;
    }
  }
  
  public static void handleBirthDisk(LinkedList<Event> cal, LinkedList<Event> req, Event e,double time) {
    
    if (diskServerBusy == false) { //if server is idle start service
      diskServerBusy = true;
      nextDeath = MM1queue.generateExponential(1/diskTs);
      Event death = new Event(4,time + nextDeath);
      cal.add(death);
      Collections.sort(cal);
      totalTq += nextDeath;
      requestCount++;
      
    }
    
    else {
      req.add(e);
    }
  }   
  
  
  public static void handleDeathDisk(LinkedList<Event> cal, LinkedList<Event> req, Event e, double time) {
    
    double prob = rand.nextDouble();
    if (prob > 0.5) { 
      Event cpuBirth = new Event(0,time);
      cal.add(cpuBirth);
      Collections.sort(cal);
    }
    
    else {
      Event netBirth = new Event(5,time);
      cal.add(netBirth);
      Collections.sort(cal);
    }
    
    if (req.size() > 0) {
      Event birthEvent = req.remove();
      nextDeath = MM1queue.generateExponential(1/diskTs);
      Event diskDeath = new Event(4,time + nextDeath);
      cal.add(diskDeath);
      Collections.sort(cal);
      totalTq += nextDeath;
      requestCount++;
      
      
    }
    else {
      diskServerBusy = false;
    }
  }
  
  public static void handleBirthNet(LinkedList<Event> cal, LinkedList<Event> req, Event e,double time) {
    
    if (netServerBusy == false) { //if server is idle start service
      netServerBusy = true;
      nextDeath = MM1queue.generateExponential(1/netTs);
      Event death = new Event(6,time + nextDeath);
      cal.add(death);
      Collections.sort(cal);
      totalTq += nextDeath;
      requestCount++;
    }
    
    else {
      req.add(e);
    }
  }
  
  
  
  public static void handleDeathNet(LinkedList<Event> cal, LinkedList<Event> req, Event e, double time) {
    Event nextBirth = new Event(0,time);
    cal.add(nextBirth);
    Collections.sort(cal);
    
    if (req.size() > 0) {
      Event birthEvent = req.remove();
      nextDeath = MM1queue.generateExponential(1/netTs);
      Event diskDeath = new Event(6,time + nextDeath);
      cal.add(diskDeath);
      Collections.sort(cal);
      totalTq += nextDeath;
      requestCount++;
      
      
    }
    else {
      netServerBusy = false;
    }
  }
  
  public static void handleMonitor(LinkedList<Event> cal, LinkedList<Event> req, Event e, double time) {
    double newMonitorT = MM1queue.generateExponential(monitorT);
    cal.add(new Event(7,time + monitorT));
    Collections.sort(cal); 
    System.out.println("time = " + time + ",CPUqueueSize = " + cpuRequests.size());
    
    monitorEvents += 1;
    
  }
  
  
}






