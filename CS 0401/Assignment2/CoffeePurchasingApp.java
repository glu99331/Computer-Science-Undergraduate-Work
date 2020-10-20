import java.io.*;
import java.util.Scanner;
 
public class CoffeePurchasingApp
{
    final static double[] prices = {1.50, 3.50, 3.25, 2.00, 2.50, 3.00};
    static boolean validCouponCode = false;
 
    public static void main(String[] args)throws IOException
    {
        int orderNum, choice, quantity, beverageDecision, pastryDecision, beverageChoice, pastryChoice, beverageQuantity, pastryQuantity;
        String name, couponCode;
        String[] validCoupon = {"COFFEE1", "LATTE23", "I<3JAVA", "SCONEZ", "MUFFIN!LOLOL"};
        boolean validChoice, validPastry, validBeverage;
 
        orderNum = 1;
        Scanner kbd = new Scanner(System.in);
        Scanner stringScanner = new Scanner(System.in);
        Scanner nameScanner = new Scanner(System.in);
 
        File status = new File("status.txt");
 
        while(orderNum <= 10)
        {
            System.out.println("\nWelcome to the Java Coffee Shop!");
 
            System.out.println("---Purchasing Menu---");
            System.out.println("1. Place an order");
            System.out.println("2. Check order status");
            System.out.println("3. Exit");
            System.out.println("Enter an action:");
 
            int input = kbd.nextInt();
 
            boolean invalidInput = !(input == 1 || input == 2 || input == 3);
 
            if(invalidInput)
            {
                System.out.println("Error. Please enter a valid input");
                System.exit(0);
            }
       
            switch(input)
            {
            case 1:
                printMenu();
 
                System.out.println("Enter a name for the order: ");
 
                name = nameScanner.nextLine();
 
                System.out.println("Enter the item number: ");
                choice = kbd.nextInt();
 
                validChoice = (choice == 1 || choice == 2 || choice == 3 || choice == 4 || choice == 5 || choice == 6);
               
                if(!validChoice)
                {
                    invalidInput();
                }
 
                System.out.println("Enter the quantity: ");
                quantity = kbd.nextInt();
 
                switch(choice)
                {
                case 1:
                    System.out.println("Would you like to add a pastry? (Enter 0 for no, or 1 for yes)");
                    pastryChoice = kbd.nextInt();
                    if(pastryChoice == 0)
                    {
                        System.out.println("Enter a coupon code (or press ENTER to skip): ");
                        couponCode = stringScanner.nextLine();
 
                        validCouponCode = (couponCode.equals(validCoupon[0]) || couponCode.equals(validCoupon[1]) || couponCode.equals(validCoupon[2]) || couponCode.equals(validCoupon[3]) || couponCode.equals(validCoupon[4]) );
 
                        couponCode(validCouponCode);
                        pastryStatements(orderNum, name, quantity, 0,0,0, 0 , 0);
                    }
                    else
                    {
                        System.out.println("Great! Which one would you like? (Enter 5 for a Scone, or 6 for a Muffin) ");
                        pastryDecision = kbd.nextInt();
                        validPastry = (pastryDecision == 5 || pastryDecision == 6);
 
                        invalidPastry(validPastry);
 
                        System.out.println("And how many?");
                        pastryQuantity = kbd.nextInt();
 
                        System.out.println("Enter a coupon code (or press ENTER to skip): ");
                        couponCode = stringScanner.nextLine();
 
                        validCouponCode = (couponCode.equals(validCoupon[0]) || couponCode.equals(validCoupon[1]) || couponCode.equals(validCoupon[2]) || couponCode.equals(validCoupon[3]) || couponCode.equals(validCoupon[4]) );
 
                        couponCode(validCouponCode);
 
                        pastryStatements(orderNum, name, quantity, 0,0,0, pastryQuantity, pastryDecision);
                    }
                    break;
 
                case 2:
                    System.out.println("Would you like to add a pastry? (Enter 0 for no, or 1 for yes)");
                    pastryChoice = kbd.nextInt();
 
                    validChoice = (pastryChoice == 0 || pastryChoice == 1);
                    if(!validChoice)
                    {
                        System.out.println("Error, please enter a valid input.");
                        System.exit(0);
                    }
 
                    if(pastryChoice == 0)
                    {
                        System.out.println("Enter a coupon code (or press ENTER to skip): ");
                        couponCode = stringScanner.nextLine();
 
                        validCouponCode = (couponCode.equals(validCoupon[0]) || couponCode.equals(validCoupon[1]) || couponCode.equals(validCoupon[2]) || couponCode.equals(validCoupon[3]) || couponCode.equals(validCoupon[4]) );
 
                        couponCode(validCouponCode);
                        pastryStatements(orderNum, name, 0, quantity, 0,0,0, 0);
 
                    }
                    else
                    {
                        System.out.println("Great! Which one would you like? (Enter 5 for a Scone, or 6 for a Muffin) ");
                        pastryDecision = kbd.nextInt();
                        validPastry = (pastryDecision == 5 || pastryDecision == 6);
 
                        invalidPastry(validPastry);
 
                        System.out.println("And how many?");
                        pastryQuantity = kbd.nextInt();
 
                        System.out.println("Enter a coupon code (or press ENTER to skip): ");
                        couponCode = stringScanner.nextLine();
 
                        validCouponCode = (couponCode.equals(validCoupon[0]) || couponCode.equals(validCoupon[1]) || couponCode.equals(validCoupon[2]) || couponCode.equals(validCoupon[3]) || couponCode.equals(validCoupon[4]) );
 
                        couponCode(validCouponCode);
 
                        pastryStatements(orderNum, name, 0, quantity, 0,0,pastryQuantity, pastryDecision);
                    }
                    break;
 
                case 3:
                    System.out.println("Would one you like to add a pastry? (Enter 0 for no, or 1 for yes)");
                    pastryChoice = kbd.nextInt();
                    validChoice = (pastryChoice == 0 || choice == 1);
                    if(!validChoice)
                    {
                        System.out.println("Error, please enter a valid input.");
                        System.exit(0);
                    }
 
                    if(pastryChoice == 0)
                    {
                        System.out.println("Enter a coupon code (or press ENTER to skip): ");
                        couponCode = stringScanner.nextLine();
 
                        validCouponCode = (couponCode.equals(validCoupon[0]) || couponCode.equals(validCoupon[1]) || couponCode.equals(validCoupon[2]) || couponCode.equals(validCoupon[3]) || couponCode.equals(validCoupon[4]) );
 
                        couponCode(validCouponCode);
                        pastryStatements(orderNum, name, 0,0,quantity, 0,0, 0);
 
                    }
                    else
                    {
                        System.out.println("Great! Which one would you like? (Enter 5 for a Scone, or 6 for a Muffin) ");
                        pastryDecision = kbd.nextInt();
                        validPastry = (pastryDecision == 5 || pastryDecision == 6);
 
                        if(!validPastry)
                        {
                            System.out.println("Error. Please enter a valid input.");
                            System.exit(0);
                        }
 
 
                        System.out.println("And how many?");
                        pastryQuantity = kbd.nextInt();
 
                        System.out.println("Enter a coupon code (or press ENTER to skip): ");
                        couponCode = stringScanner.nextLine();
 
                        validCouponCode = (couponCode.equals(validCoupon[0]) || couponCode.equals(validCoupon[1]) || couponCode.equals(validCoupon[2]) || couponCode.equals(validCoupon[3]) || couponCode.equals(validCoupon[4]) );
 
                        couponCode(validCouponCode);
 
                        pastryStatements(orderNum, name, 0,0,quantity, 0,pastryQuantity, pastryDecision);
                    }
                    break;
 
                case 4:
                    System.out.println("Would you like to add a pastry? (Enter 0 for no, or 1 for yes)");
                    pastryChoice = kbd.nextInt();
                    if(pastryChoice == 0)
                    {
                        System.out.println("Enter a coupon code (or press ENTER to skip): ");
                        couponCode = stringScanner.nextLine();
 
                        validCouponCode = (couponCode.equals(validCoupon[0]) || couponCode.equals(validCoupon[1]) || couponCode.equals(validCoupon[2]) || couponCode.equals(validCoupon[3]) || couponCode.equals(validCoupon[4]) );
 
                        couponCode(validCouponCode);
                        pastryStatements(orderNum, name,0,0,0,quantity, 0, 0);
                    }
                    else
                    {
                        System.out.println("Great! Which one would you like? (Enter 5 for a Scone, or 6 for a Muffin)");
                        pastryDecision = kbd.nextInt();
                        validPastry = (pastryDecision == 5 || pastryDecision == 6);
 
                        invalidPastry(validPastry);
 
                        System.out.println("And how many?");
                        pastryQuantity = kbd.nextInt();
 
                        System.out.println("Enter a coupon code (or press ENTER to skip): ");
                        couponCode = stringScanner.nextLine();
 
                        validCouponCode = (couponCode.equals(validCoupon[0]) || couponCode.equals(validCoupon[1]) || couponCode.equals(validCoupon[2]) || couponCode.equals(validCoupon[3]) || couponCode.equals(validCoupon[4]) );
 
                        couponCode(validCouponCode);
 
                        pastryStatements(orderNum, name,0,0,0,quantity, pastryQuantity,pastryDecision);
                    }
                    break;
 
                case 5:
                    System.out.println("Would you like to add a beverage? (Enter 0 for no, or 1 for yes)");
                    beverageChoice = kbd.nextInt();
                    if(beverageChoice == 0)
                    {
                        System.out.println("Enter a coupon code (or press ENTER to skip): ");
                        couponCode = stringScanner.nextLine();
 
                        validCouponCode = (couponCode.equals(validCoupon[0]) || couponCode.equals(validCoupon[1]) || couponCode.equals(validCoupon[2]) || couponCode.equals(validCoupon[3]) || couponCode.equals(validCoupon[4]) );
 
                        couponCode(validCouponCode);
                        beverageStatements(orderNum, name, 0, quantity, 0, 0);
 
                    }
                    else
                    {
                        System.out.println("Great! Which one would you like? (Enter 1 for a Coffee, 2 for a Latte, and so forth...)");
                        beverageDecision = kbd.nextInt();
                        validBeverage = (beverageDecision == 1 || beverageDecision == 2 || beverageDecision == 3 || beverageDecision == 4);
 
                        invalidBeverage(validBeverage);
 
                        System.out.println("And how many?");
                        beverageQuantity = kbd.nextInt();
 
                        System.out.println("Enter a coupon code (or press ENTER to skip): ");
                        couponCode = stringScanner.nextLine();
 
                        validCouponCode = (couponCode.equals(validCoupon[0]) || couponCode.equals(validCoupon[1]) || couponCode.equals(validCoupon[2]) || couponCode.equals(validCoupon[3]) || couponCode.equals(validCoupon[4]) );
 
                        couponCode(validCouponCode);
 
                        beverageStatements(orderNum, name, beverageQuantity, quantity, 0, beverageDecision);
                    }
                    break;
 
                case 6:
                    System.out.println("Would you like to add a beverage? (Enter 0 for no, or 1 for yes)");
                    beverageChoice = kbd.nextInt();
                    if(beverageChoice == 0)
                    {
                        System.out.println("Enter a coupon code (or press ENTER to skip): ");
                        couponCode = stringScanner.nextLine();
 
                        validCouponCode = (couponCode.equals(validCoupon[0]) || couponCode.equals(validCoupon[1]) || couponCode.equals(validCoupon[2]) || couponCode.equals(validCoupon[3]) || couponCode.equals(validCoupon[4]) );
 
                        couponCode(validCouponCode);
                        beverageStatements(orderNum, name, 0, 0, quantity, 0);
                    }
                    else
                    {
                        System.out.println("Great! Which one would you like? (Enter 1 for a Coffee, 2 for a Latte, and so forth...)");
                        beverageDecision = kbd.nextInt();
                        validBeverage = (beverageDecision == 1 || beverageDecision == 2 || beverageDecision == 3 || beverageDecision == 4);
 
                        invalidBeverage(validBeverage);
 
 
                        System.out.println("And how many?");
                        beverageQuantity = kbd.nextInt();
 
                        System.out.println("Enter a coupon code (or press ENTER to skip): ");
                        couponCode = stringScanner.nextLine();
 
                        validCouponCode = (couponCode.equals(validCoupon[0]) || couponCode.equals(validCoupon[1]) || couponCode.equals(validCoupon[2]) || couponCode.equals(validCoupon[3]) || couponCode.equals(validCoupon[4]) );
 
                        couponCode(validCouponCode);
 
                        beverageStatements(orderNum, name, beverageQuantity, 0, quantity, beverageDecision);
                    }
                    break;
 
                }
               
                getSummary();
                System.out.println("Enter 1 to confirm the order or 0 to cancel.");
                int confirm = kbd.nextInt();
                if(confirm == 1)
                {
                    System.out.println("Order " + orderNum + " has been placed.");
                    orderNum++;
                }
                else if (confirm == 0)
                {
                    System.exit(0);
                }
                break;
 
            case 2:
                String reading = "";
                if(!status.exists())
                {
                    System.out.println("No orders are in at this time.");
                }
                else if(status.exists())
                {
 
                    reading = "";
                    Scanner inputReader = new Scanner(status);
 
                    while(inputReader.hasNext())
                    {
                        System.out.println("Order Status: ");
                        reading = inputReader.nextLine();
                        System.out.println(reading);
                    }
                }
                break;
 
            case 3:
                System.out.println("Goodbye!");
                System.exit(0);
                break;
 
            }
 
        }
    }
 
    public static void getSummary()
    {
        Scanner orderReader = null;
        try {
            orderReader = new Scanner(new File("orders.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String lastOrder = "";
 
        lastOrder = orderReader.nextLine();
 
        orderReader.close();
        String[] order = lastOrder.split(", ");
 
        double price = 0, total = 0;
        String output = "\n--- Order " + order[0] + " Summary ---\n";
        int quantity = 0;
        output += "\t";
 
        for(int i = 2; i < order.length; i++)
        {
            quantity = Integer.parseInt(order[i].trim());
            switch(i)
            {
            case 2:
                if(quantity != 0)
                {
                    price = prices[0] * quantity;
                    output += "\n" + quantity + "\tCoffee\t" + "$" + price;
                }
                break;
            case 3:
                if(quantity != 0)
                {
                    price = prices[1] * quantity;
                    output += "\n" + quantity + "\tLatte\t" + "$" + price;
                }
                break;
            case 4:
                if(quantity != 0)
                {
                    price = prices[2] * quantity;
                    output += "\n" + quantity + "\tCappuccino\t" + "$" + price;
                }
                break;
            case 5:
                if(quantity != 0)
                {
                    price = prices[3] * quantity;
                    output += "\n" + quantity + "\tEspresso\t" + "$" + price;
                }
                break;
            case 6:
                if(quantity != 0)
                {
                    price = prices[4] * quantity;
                    output += "\n" + quantity + "\tScone(s)\t" + "$" + price;
                }
                break;
            case 7:
                if(quantity != 0)
                {
                    price = prices[5] * quantity;
                    output += "\n" + quantity + "\tMuffin(s)\t" + "$" + price;
                }
                break;
            }
            total += price;
            price = 0;
        }
 
        output += "\n\nTotal Cost:\t\t\t$" + String.format("%.2f", total) + "\n";
        double discount = 0;
        if(validCouponCode)
        {
            discount = 0.2 * total;
            output += "20% Discount:\t\t\t$";
            output += String.format("%.2f\n", discount);
        }
        output += "Final Cost:\t\t\t$";
        output += String.format("%.2f\n", total - discount);
        System.out.println(output);
    }
 
    public static void printMenu()
    {
        System.out.println("---Here is our menu---");
        System.out.println("1. Coffee \t $1.50");
        System.out.println("2. Latte \t $3.50");
        System.out.println("3. Cappuccino \t $3.25");
        System.out.println("4. Espresso \t $2.00");
        System.out.println("5. Scone \t $2.50");
        System.out.println("6. Muffin \t $3.00");
    }
    public static void invalidPastry(boolean validPastry)
    {
        if(!validPastry)
        {
            System.out.println("Error. Please enter a valid input.");
            System.exit(0);
        }
    }
    public static void invalidBeverage(boolean validBeverage)
    {
        if(!validBeverage)
        {
            System.out.println("Error. Please enter a valid input.");
            System.exit(0);
        }
    }
    public static void couponCode(boolean validCouponCode)
    {
        if(validCouponCode)
        {
            System.out.println("Your code has been accepted! You will receive 20% off!");
 
        }
        else
        {
            System.out.println("Error, that is not a a valid coupon code.");
        }  
    }
    public static void beverageStatements(int orderNum, String name, int beverageQuantity, int sconeQuantity, int muffinQuantity, int beverageDecision)throws IOException
    {
        FileWriter orders = new FileWriter("orders.txt", true);
        PrintWriter pwriter = new PrintWriter(orders);
 
        if(beverageDecision == 1)
        {
            int latteQuantity = 0;
            int cappuccinoQuantity = 0;
            int espressoQuantity = 0;
 
            pwriter.println(orderNum + ", " + name + ", " + beverageQuantity + ", " + latteQuantity + ", " + cappuccinoQuantity + ", " + espressoQuantity + ", " + sconeQuantity + ", " + muffinQuantity );
        }
        else if(beverageDecision == 2)
        {
            int coffeeQuantity = 0;
            int cappuccinoQuantity = 0;
            int espressoQuantity = 0;
 
            pwriter.println(orderNum + ", " + name + ", " + coffeeQuantity + ", " + beverageQuantity + ", " + cappuccinoQuantity + ", " + espressoQuantity + ", " + sconeQuantity + ", " + muffinQuantity );
        }
        else if(beverageDecision == 3)
        {
            int coffeeQuantity = 0;
            int latteQuantity = 0;
            int espressoQuantity = 0;
 
            pwriter.println(orderNum + ", " + name + ", " + coffeeQuantity + ", " + latteQuantity + ", " + beverageQuantity + ", " + espressoQuantity + ", " + sconeQuantity + ", " + muffinQuantity );
        }
        else if(beverageDecision == 4)
        {
            int coffeeQuantity = 0;
            int latteQuantity = 0;
            int cappuccinoQuantity = 0;
 
            pwriter.println(orderNum + ", " + name + ", " + coffeeQuantity + ", " + latteQuantity + ", " + cappuccinoQuantity + ", " + beverageQuantity + ", " + sconeQuantity + ", " + muffinQuantity );
 
        }
        else if(beverageDecision == 0)
        {
            pwriter.println(orderNum + ", " + name + ", " + 0 + ", " + 0 + ", " + 0 + ", " + 0 + ", " + sconeQuantity + ", " + muffinQuantity );
 
        }
 
        pwriter.close();
    }
    public static void pastryStatements(int orderNum, String name, int coffeeQuantity, int latteQuantity, int cappuccinoQuantity, int espressoQuantity, int pastryQuantity, int pastryDecision) throws IOException
    {
        FileWriter orders = new FileWriter("orders.txt", true);
        PrintWriter pwriter = new PrintWriter(orders);
        if(pastryDecision == 5)
        {
            int muffinQuantity = 0;
            pwriter.println(orderNum + ", " + name + ", " + coffeeQuantity + ", " + latteQuantity + ", " + cappuccinoQuantity + ", " + espressoQuantity + ", " + pastryQuantity + ", " + muffinQuantity );
        }
        else if(pastryDecision == 6)
        {
            int sconeQuantity = 0;
            pwriter.println(orderNum + ", " + name + ", " + coffeeQuantity + ", " + latteQuantity + ", " + cappuccinoQuantity + ", " + espressoQuantity + ", " + sconeQuantity + ", " + pastryQuantity );
        }
        else
        {
            pwriter.println(orderNum + ", " + name + ", " + coffeeQuantity + ", " + latteQuantity + ", " + cappuccinoQuantity + ", " + espressoQuantity + ", " + 0 + ", " + 0);
        }
        pwriter.close();
    }
    public static void invalidInput()
    {
        System.out.println("Error. Please enter a valid input.");
        System.exit(0);
    }
}