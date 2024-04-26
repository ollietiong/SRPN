import java.util.*;
import java.math.*;
/**
 * Program class for an SRPN calculator. 
 */

public class SRPN {

  Stack<Integer> stack = new Stack<Integer>();
  int count = 0; int limit = 0;
  
  /*-------------------------------------*/
  
  public void processCommand(String s) {
    s = s.strip(); // strip spaces
    
    // determine input type: number, operator, return, unknown
    if(s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/") || s.equals("%") || s.equals("^") || s.equals("^")){ // operator
      processOperator(s);
    }
    else if(s.equals("=") || s.equals("d")){ // = or d
      processReturns(s);
    }
    else if(s.equals("r")){ // r
      random(s);
    }
    else if(isParsable(s) == 0){ // integer
      processNumber(s);
    }
    else if(isParsable(s) == 1){ // big integer
      processNumber(s);
    }
    else if(isParsable(s) == 2){ // input on one line
      oneLineProcess(s);
    }
    else{
      System.out.println("Unrecognised operator or operand " + "\"" + s + "\".");
    }
  }

  
  /*---------------------------------------*/
  
  // METHODS

  // METHOD TO PROCESS OPERATOR
  
  public void processOperator(String s){ 
    //create big integer objects for handling saturation
    BigInteger num1Big = new BigInteger("0");
    BigInteger num2Big = new BigInteger("0");
    BigInteger resultBig = new BigInteger("0");
    BigInteger compareMax = new BigInteger("2147483647");
    BigInteger compareMin = new BigInteger("-2147483648");
    // create result variable and fill with stack
    int result = stack.peek();
    
    try{
      // assign values from stack to new variables
      
      int num1 = stack.pop();
      int num2 = stack.pop();
      
      // convert int to BigInteger
      num1Big = BigInteger.valueOf(num1);
      num2Big = BigInteger.valueOf(num2);
      
      // process operator
      if(s.equals("+")){ 
        resultBig = num2Big.add(num1Big);
      }
      else if(s.equals("-")){
        resultBig = num2Big.subtract(num1Big);
      }
      else if(s.equals("*")){
        resultBig = num2Big.multiply(num1Big);
      }
      else if(s.equals("/") && num1 == 0){ // handle divide by zero
        System.out.println("Divide by 0.");
      }
      else if(s.equals("/")){
        resultBig = num2Big.divide(num1Big);
      }
      else if(s.equals("%")){
        resultBig = num2Big.mod(num1Big);
      }
      else if(s.equals("^")){
        resultBig = num2Big.pow(num1);
      }
      
      // handle saturation
      if (resultBig.compareTo(compareMax) == 1){
        result = Integer.MAX_VALUE;
      }
      else if(resultBig.compareTo(compareMin) == -1){
        result = Integer.MIN_VALUE;
      }
      else{ // convert to int
        result = resultBig.intValue();
      }
      
      // push result to stack
      stack.push(result);
    }
    // handle stack exceptions, ie. not enough values in stack to calculate
    catch(EmptyStackException e){ 
      System.out.println("Stack underflow.");
      stack.push(result);
    }
  }

    // METHOD TO PROCESS = OR D
  
  public void processReturns(String s){
    // = statement
    if(s.equals("=")){
      System.out.println(stack.peek());
    }
    else if (s.contains("=")){ // use of contains handles digits next to =
      System.out.println(stack.peek());
    }
    // d statement
    else{ // iterates through stack and returns
      Iterator values = stack.iterator();
      while(values.hasNext()){
        System.out.println(values.next()); 
      }
    }
  }
  
  // METHOD TO PROCESS INTEGER
  
  public void processNumber(String s){ 
    try{
      int num = Integer.parseInt(s);
      stack.push(num);
    }
    catch(NumberFormatException e){
      
        // handle numbers inputted to s greater than INTEGER MAX VALUE or MIN VALUE
        BigInteger bigS = new BigInteger(s);
        BigInteger max = new BigInteger("2147483647");
        BigInteger min = new BigInteger("-2147483648");
        
        if(bigS.compareTo(max) == 1){ // set to max value
          int num = Integer.MAX_VALUE;
          stack.push(num);
        }
        else if(bigS.compareTo(min) == -1){ // set to min value
          int num = Integer.MIN_VALUE;
          stack.push(num);
        }
        // print error message
        else{
          System.out.println("Unrecognised operator or operand " + "\"" + s + "\".");
        }      
    }
  }

   
  // METHOD TO CHECK IF INTEGER
  
  public int isParsable(String s){
    try{ // try parsing
      int num = Integer.parseInt(s);
      return 0;
    }
    catch(NumberFormatException e){ // catches non parsable input
      try{ // tests for big integer
        BigInteger bigS = new BigInteger(s);
        BigInteger max = new BigInteger("2147483647");
        BigInteger min = new BigInteger("-2147483648");
      
        if(bigS.compareTo(max) == 1 || bigS.compareTo(min) == -1){
          return 1;
        }
        else{
          return 3;
        }
      }
      catch(NumberFormatException f){ // otherwise returns 2
        return 2;
      }
    } 
    
  }
  /* ------------------------------------ */
  
  // METHODS TO PROCESS INPUT ON ONE LINE

  // METHOD TO PROCESS ONE LINE
  public void oneLineProcess(String s){
    // call method to filter comments
    s = filterComments(s);
    // split string into a local arraylist
    ArrayList<String> local = splitString(s);

    // iterate over string going through same commands as processCommand
    for(int i = 0; i < local.size(); i++){
    
      if(local.get(i).equals("+") || local.get(i).equals("-") || local.get(i).equals("*") || local.get(i).equals("/") || local.get(i).equals("%") || local.get(i).equals("^") || local.get(i).equals("^")){ // operator
        processOperator(local.get(i));
      }
      else if(local.get(i).contains("=") || local.get(i).equals("d")){ // = or d
        processReturns(local.get(i));
      }
      else if(local.get(i).equals("r")){ // r
        random(s);
      }
      else if(isParsable(local.get(i)) == 0){ // integer
        processNumber(local.get(i));
      }
      else if(isParsable(local.get(i)) == 1){ // big integer
        processNumber(local.get(i));
      }
      else if(s.length() == 0){ // empty string i.e. if comments only
        break;
      }
      else{
        System.out.println("Unrecognised operator or operand " + "\"" + s + "\".");
      }
    }
  }


  // METHOD TO SPLIT STRING
  public ArrayList<String> splitString(String s){
    
    ArrayList<String> local = new ArrayList<String>(); // local arraylist
    
    // if string contain spaces split by space as delimiter
    if (s.contains(" ")){
      String[] localArray = s.split(" ");
      
      for (int i = 0; i < localArray.length; i++){
        local.add(localArray[i]);
      }
    }
      
    // if no spaces split string via operators, and join if numbers are multiple digits
    else if(operatorCheck(s) == true){
      String[] localArray = s.split("|"); 

      String locString = ""; // create local string 
      
      // add up single digits if neccessary
      for (int i = 0; i < localArray.length; i++){
        if(isParsable(localArray[i]) ==0){ // is digit?
          locString = locString.concat(localArray[i]); // add to local s
        }
        else if(isParsable(localArray[i]) != 0){ // if operator
          if(locString.length() > 0){
            local.add(locString);
            local.add(localArray[i]);
            locString = "";
          }
          else{
            local.add(localArray[i]);
          }
        }
      }
    }
    // split by digits
    else{
      String[] localArray = s.split("\\D"); 
      
      for (int i = 0; i < localArray.length; i++){
        local.add(localArray[i]);
      }
    }
    return local;
  }

  // METHOD TO CHECK FOR OPERATORS
  public boolean operatorCheck(String s){
    // create array of operators
  String[] operators = {"+","-","*","/","%","^"};
  boolean returnValue = false;
  // compare string to see if contains operators
  for(String o : operators){
    if(s.contains(o)){
      returnValue= true;
      break;
    }
    else{
      returnValue = false;
    }
  }
  return returnValue;
  }

  // METHOD TO HANDLE R
  public void random(String s){

  // array of random numbers
    int[] rand = {1804289383, 846930886, 1681692777, 1714636915, 1957747793, 424238335, 719885386, 1649760492, 596516649, 1189641421, 1025202362, 1350490027, 783368690, 1102520059, 2044897763, 1967513926, 1365180540, 1540383426, 304089172, 1303455736, 35005211, 521595368};
  // if r called, push to global stack from array
    try{
    stack.push(rand[count]);
    }
    catch(ArrayIndexOutOfBoundsException e){ // handles calls to method outside of array
      if(limit < 2){
      System.out.println("Stack overflow.");
      }
      limit++;
      if(limit == 2){
        count= -1;
      }
    }
    count++;
  }

  //METHOD TO FILTER COMMENTS
  public String filterComments(String s){
    
    int length = s.length(); boolean comment = false; String localS= "";
    //loop over string checking for hash key, and rebuilding string without comments
    for(int i = 0; i < length; i++){
     
      char current = s.charAt(i);
      if(current == '#' && comment == false){
        comment = true;
      }
      else if(current == '#' && comment == true){
        comment = false;
      }
      else if(comment== false){
      localS = localS.concat(String.valueOf(current));
      }
    }
    return localS;
  }
  

} 