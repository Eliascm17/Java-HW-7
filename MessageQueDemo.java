/*
  Elias Moreno
  R11514847
  11/13/18
  HW 7
*/
import java.util.*;
/////////////////////////////////////////////////////////////////
//MessageQue Class
class MessageQue{

  LinkedList<String> linkedlist = new LinkedList<String>(); //link list to que messages
  boolean flag = false;   //flag to used to notify whether a thread can procede or not
  int num, maxCount = 3;
  String message;

      ///////////////////////////////////////////////////////////////////////////////
      //recieved message function
      public String receive(){
        while(!flag){
          try{
            wait();
          }catch(InterruptedException e){
            System.out.println("InterruptedException caught");
          }
        }

        flag = false;
        notify();
        return linkedlist.getFirst();
      }
      ///////////////////////////////////////////////////////////////////////////////
      //send message function
      public void send(String message, int num){

        while(flag){
          try{
            wait();
          }catch(InterruptedException e){
            System.out.println("InterruptedException caught");
          }
        }

        this.message = message;
        this.num = num;

        System.out.println("------------------------------");
        System.out.println("Sending Message: " + message + "," + num);
        linkedlist.addFirst(message + "," + Integer.toString(num));

        if(linkedlist.size() > maxCount){
          linkedlist.removeLast();
        }

        flag = true;
        notify();

      }

      //maybe add some more stuff here
  }
/////////////////////////////////////////////////////////////////
//Producer class that sends the message to the MessageQue class
class Producer implements Runnable{
    MessageQue mq;
    String name;
    Thread t;
    int i = 0;

    String[] MessageArray = {"Add", "Multiply", "Multiply", "Add", "Add","Add", "Multiply"};
    int[] NumArray = {4,1,8,2,3,99,52};

    Producer(MessageQue mq, String threadname){//initiating Producer thread
      name = threadname;
      this.mq = mq;
      t = new Thread(this, name);
      t.start();
    }

    public void run(){//sending message when asked to start running
      while(i < 7){
        synchronized(mq){//synchronizing block
          mq.send(MessageArray[i], NumArray[i]);
          i++;
        }
      }
    }
}
/////////////////////////////////////////////////////////////////
//Consumer Class that receives the message from MessageQue
class Consumer implements Runnable{
    SimpleCalculation sc;
    MessageQue mq;
    Thread t;
    String name;
    int i = 0;
    int MaxCount = 7;

    Consumer(MessageQue mq, SimpleCalculation sc, String threadname){
      name = threadname;
      this.sc = sc;
      this.mq = mq;
      t = new Thread(this, name);
      t.start();
    }

    public void run(){
        while(i<MaxCount){

          synchronized(mq){ //synchronizing block so that threads execute on

            String receivedString = mq.receive(); //setting the message to a variable
            String[] got = receivedString.split(","); //splitting the string into 2 parts
            String firstPart = got[0]; //setting the first index as a variable
            String secondPart = got[1]; //setting the second index as a variable

            System.out.println("Received Message: " + receivedString + "\n");

              //if statements to determine whether the message said to add or Multiply
              if(firstPart.equals("Add")){
                System.out.println("Output with Add: " + sc.add(Integer.parseInt(secondPart)));
              }
              else if(firstPart.equals("Multiply")){
                System.out.println("Output with Multiply: " + sc.multiply(Integer.parseInt(secondPart)));
                }
              i++;
            }
        }
    }
}
/////////////////////////////////////////////////////////////////
//class used by Consumer to perform either addition or multiplication
class SimpleCalculation{
  //add function
  public int add(int num){
    return num+10;
  }
  //multiply funciton
  public int multiply(int num){
    return num*10;
  }
}
/////////////////////////////////////////////////////////////////
//Main class that creates 4 objects with 2 of them being the Consumber and Producer Threads
class MessageQueDemo{
    public static void main(String[] args){

      SimpleCalculation sc = new SimpleCalculation();
      MessageQue mq = new MessageQue();
      Producer pro = new Producer(mq, "Producer");
      Consumer con = new Consumer(mq, sc, "Consumer");

        try{
          pro.t.join();
          con.t.join();
        }catch(InterruptedException e){
          System.out.println("Interrupted");
      }
      System.out.println("------------------------------");
      System.out.println("\nExiting main...\n");
    }
  }
