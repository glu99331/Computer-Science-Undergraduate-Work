
/*
Gordon Lu
CS401
Spring 2018
Assignment 1
*/

import java.util.Scanner;

public class JavaCodeCoffeeShop
{
        public static void main(String[] args)
        {
                //declare constants, such that if errors occur in program, it will not be due to the values of constants.
                final double COFFEE_PRICE = 1.50;
                final double LATTE_PRICE = 3.50;
                final double CAPPUCINO_PRICE = 3.25;
                final double ESPRESSO_PRICE = 2.00;
                final double SCONE_PRICE = 2.50;
                final double MUFFIN_PRICE = 3.00;

                Scanner kbd = new Scanner(System.in);
                System.out.println("Welcome to the Java Coffee Shop, What is your name?");
                
                String customer = kbd.nextLine();
                System.out.println("Welcome to the Coffee Shop, " + customer);
                System.out.println("Below is our menu! "+ "Please let me know when you're ready to order: ");

                System.out.println("=====================================");
                System.out.println("Item\t\t" + "Type\t\t" + " Cost\t\t");
                System.out.println("Coffee\t\t" + "drink\t\t" + "$1.50");
                System.out.println("Latte\t\t" + "drink\t\t" + "$3.50");
                System.out.println("Cappuccino\t" + "drink\t\t" + "$3.25");
                System.out.println("Espresso\t" + "drink\t\t" + "$2.00");
                System.out.println("Scone\t\t" + "pastry\t\t" + "$2.50");
                System.out.println("Muffin\t\t" + "pastry\t\t" + "$3.00");
                System.out.println("=====================================");

                System.out.println("What would you like to order?\n");
                System.out.println("Ordering here is special...");
                System.out.println("To order Coffee(s),    enter 0");
                System.out.println("To order Latte(s),     enter 1");
                System.out.println("To order Cappucino(s), enter 2");
                System.out.println("To order Espresso(s),  enter 3");
                System.out.println("To order Scone(s),     enter 4");
                System.out.println("To order Muffin(s),    enter 5");

                int customerInput = kbd.nextInt();

                if(customerInput > 5 && customerInput < 0)
                {
                    System.out.println("Error, you have not ordered anything avaiable on the menu.");
                    System.exit(0);
                }

                System.out.println("Please enter a quantity for the selected item: ");
                int quantity = kbd.nextInt();


                switch(customerInput)
                {
                        case 0:
                                System.out.println("Would you like a Scone or Muffin to go with your Coffee?");
                                System.out.println("To say no,  enter 0");
                                System.out.println("To say yes, enter 1");
                                int pastryOption = kbd.nextInt();
                                if(pastryOption == 0)
                                {
                                    double cost = COFFEE_PRICE * quantity;
                                    if(cost >= 10)
                                    {
                                        double discount = cost - (0.10*cost);
                                        System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                        
                                        double tax = discount + 0.07*(discount);
                                        System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                                    System.out.println("Please enter a payment amount: ");
                                                    double paymentAmount = kbd.nextDouble();

                                                    if(paymentAmount < tax)
                                                    {
                                                            System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                                    }
                                                    else if(paymentAmount > tax)
                                                    {
                                                        double change = paymentAmount - tax;
                                                        System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change);
                                                        
                                                    }
                                    }
                                }
                                else if(pastryOption == 1)
                                {
                                    System.out.println("As stated before, our ordering system is special...");
                                    System.out.println("To order Scone(s), enter 0");
                                    System.out.println("To order Muffin(s), enter 1");
                                    int pastryChoice = kbd.nextInt();
                                
                                    if(pastryChoice == 0) // if the user chooses coffee
                                    {
                                            System.out.println("How many Scones would you like?");
                                            int sconeQuantity = kbd.nextInt();
                                            double cost = (COFFEE_PRICE * quantity) + (SCONE_PRICE * sconeQuantity);
                                            if(cost >= 10)
                                            {
                                                    double discount = cost - (0.10*cost);
                                                    System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                                    
                                                    double tax = discount + (0.07*discount);
                                                    System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                                    System.out.println("Please enter a payment amount: ");
                                                    double paymentAmount = kbd.nextDouble();

                                                    if(paymentAmount < tax) // if amount paid is less than the amount with the cost with tax 
                                                    {
                                                            System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                                            System.exit(0);
                                                    }
                                                    else if(paymentAmount > tax) //if amount paid is greater than amount with cost with tax
                                                    {
                                                        double change = paymentAmount - tax;
                                                        System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change + " Have an awesome day!");

                                                    }
                                            }
                                    }
                                    else if(pastryChoice == 1)
                                    {
                                        System.out.println("How many Muffins would you like?");
                                        int muffinQuantity = kbd.nextInt();
                                        double cost = (COFFEE_PRICE * quantity) + (MUFFIN_PRICE* muffinQuantity);
                                        if(cost >= 10)
                                        {
                                            double discount = cost - (0.10*cost);
                                            System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                        
                                            double tax = discount + 0.07*(discount);
                                            System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                                    System.out.println("Please enter a payment amount: ");
                                                    double paymentAmount = kbd.nextDouble();

                                                    if(paymentAmount < tax)
                                                    {
                                                            System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                                    }
                                                    else if(paymentAmount > tax)
                                                    {
                                                        double change = paymentAmount - tax;
                                                        System.out.println("Thank you for your purchase! Your change is " + change + " Have an awesome day!");
                                                        
                                                    }
                                        }
                                    }
                            
                                }
                                break;
                        case 1:
                                System.out.println("Would you like a Scone or Muffin to go with your Latte?");
                                System.out.println("To say no,  enter 0");
                                System.out.println("To say yes, enter 1");
                                int pastryOption1 = kbd.nextInt();
                                if(pastryOption1 == 0)
                                {
                                    double cost = LATTE_PRICE * quantity;
                                    if(cost >= 10)
                                    {
                                        double discount = cost - (0.10*cost);
                                        System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);

                                    }
                                }
                                else if(pastryOption1 == 1)
                                {
                                    System.out.println("As stated before, our ordering system is special...");
                                    System.out.println("To order Scone(s), enter 0");
                                    System.out.println("To order Muffin(s), enter 1");
                                    int pastryChoice = kbd.nextInt();
                                
                                    if(pastryChoice == 0)
                                    {
                                            System.out.println("How many Scones would you like?");
                                            int sconeQuantity = kbd.nextInt();
                                            double cost = (LATTE_PRICE * quantity) + (SCONE_PRICE * sconeQuantity);
                                            if(cost >= 10)
                                            {
                                                    double discount = cost - (0.10*cost);
                                                    System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                                    double tax = discount + 0.07*(discount);
                                                    System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                                    System.out.println("Please enter a payment amount: ");
                                                    double paymentAmount = kbd.nextDouble();

                                                    if(paymentAmount < tax)
                                                    {
                                                            System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                                    }
                                                    else if(paymentAmount > tax)
                                                    {
                                                        double change = paymentAmount - tax;
                                                        System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change + " Have an awesome day!");
                                                        
                                                    }
                                            }
                                    }
                                    else if(pastryChoice == 1)
                                    {
                                        System.out.println("How many Muffins would you like?");
                                        int muffinQuantity = kbd.nextInt();
                                        double cost = (LATTE_PRICE * quantity) + (MUFFIN_PRICE* muffinQuantity);
                                        if(cost >= 10)
                                        {
                                            double discount = cost - (0.10*cost);
                                            System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                        
                                            double tax = discount + 0.07*(discount);
                                            System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                                    System.out.println("Please enter a payment amount: ");
                                                    double paymentAmount = kbd.nextDouble();

                                                    if(paymentAmount < tax)
                                                    {
                                                            System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                                    }
                                                    else if(paymentAmount > tax)
                                                    {
                                                        double change = paymentAmount - tax;
                                                        System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change + " Have an awesome day!");
                                                        
                                                    }
                                        }
                                    }
                                
                                }
                                break;
                        case 2:
                                System.out.println("Would you like a Scone or Muffin to go with your Cappucino?");
                                System.out.println("To say no,  enter 0");
                                System.out.println("To say yes, enter 1");
                                int pastryOption2 = kbd.nextInt();
                                if(pastryOption2 == 0)
                                {
                                    double cost = CAPPUCINO_PRICE * quantity;
                                    if(cost >= 10)
                                    {
                                        double discount = cost - (0.10*cost);
                                        System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                        double tax = discount + 0.07*(discount);
                                        System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                        System.out.println("Please enter a payment amount: ");
                                        double paymentAmount = kbd.nextDouble();

                                        if(paymentAmount < tax)
                                        {
                                                System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                        }
                                        else if(paymentAmount > tax)
                                        {
                                            double change = paymentAmount - tax;
                                            System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change + " Have an awesome day!");
                                            
                                        }
                                    }
                                }
                                else if(pastryOption2 == 1)
                                {
                                    System.out.println("As stated before, our ordering system is special...");
                                    System.out.println("To order Scone(s), enter 0");
                                    System.out.println("To order Muffin(s), enter 1");
                                    int pastryChoice = kbd.nextInt();
                                
                                if(pastryChoice == 0)
                                {
                                    System.out.println("How many Scones would you like?");
                                    int sconeQuantity = kbd.nextInt();
                                    double cost = (CAPPUCINO_PRICE * quantity) + (SCONE_PRICE * sconeQuantity);
                                    
                                    if(cost >= 10)
                                    {
                                            double discount = cost - (0.10*cost);
                                            System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                            double tax = discount + 0.07*(discount);
                                            System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                            System.out.println("Please enter a payment amount: ");
                                            double paymentAmount = kbd.nextDouble();

                                            if(paymentAmount < tax)
                                            {
                                                    System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                            }
                                            else if(paymentAmount > tax)
                                            {
                                                double change = paymentAmount - tax;
                                                System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change + " Have an awesome day!");
                                                
                                            }
                                        }
                                
                                    }
                                        else if(pastryChoice == 1)
                                {
                                    System.out.println("How many Muffins would you like?");
                                    int muffinQuantity = kbd.nextInt();
                                    double cost = (CAPPUCINO_PRICE * quantity) + (MUFFIN_PRICE* muffinQuantity);
                                    if(cost >= 10)
                                    {
                                        double discount = cost - (0.10*cost);
                                        System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                        double tax = discount + 0.07*(discount);
                                        System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                        System.out.println("Please enter a payment amount: ");
                                        double paymentAmount = kbd.nextDouble();

                                        if(paymentAmount < tax)
                                        {
                                                System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                        }
                                        else if(paymentAmount > tax)
                                        {
                                            double change = paymentAmount - tax;
                                            System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change + " Have an awesome day!");
                                            
                                        }
                                    }
                                }   
                            }
                        break;
                        
                        case 3:
                                System.out.println("Would you like a Scone or Muffin to go with your Espresso?");
                                System.out.println("To say no,  enter 0");
                                System.out.println("To say yes, enter 1");
                                int pastryOption3 = kbd.nextInt();
                                if(pastryOption3 == 0)
                                {
                                    double cost = ESPRESSO_PRICE * quantity;
                                    if(cost >= 10)
                                    {
                                        double discount = cost - (0.10*cost);
                                        System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                        double tax = discount + 0.07*(discount);
                                        System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                        System.out.println("Please enter a payment amount: ");
                                        double paymentAmount = kbd.nextDouble();

                                        if(paymentAmount < tax)
                                        {
                                                System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                        }
                                        else if(paymentAmount > tax)
                                        {
                                            double change = paymentAmount - tax;
                                            System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change + " Have an awesome day!");
                                            
                                        }
                                    }
                                }
                                else if(pastryOption3 == 1)
                                {
                                    System.out.println("As stated before, our ordering system is special...");
                                    System.out.println("To order Scone(s), enter 0");
                                    System.out.println("To order Muffin(s), enter 1");
                                    int pastryChoice = kbd.nextInt();
                                
                                    if(pastryChoice == 0)
                                    {
                                            System.out.println("How many Scones would you like?");
                                            int sconeQuantity = kbd.nextInt();
                                            double cost = (ESPRESSO_PRICE * quantity) + (SCONE_PRICE * sconeQuantity);
                                            if(cost >= 10)
                                            {
                                                    double discount = cost - (0.10*cost);
                                                    System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                                    double tax = discount + 0.07*(discount);
                                                    System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                                    System.out.println("Please enter a payment amount: ");
                                                    double paymentAmount = kbd.nextDouble();

                                                    if(paymentAmount < tax)
                                                    {
                                                            System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                                    }
                                                    else if(paymentAmount > tax)
                                                    {
                                                        double change = paymentAmount - tax;
                                                        System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change + " Have an awesome day!");
                                                        
                                                    }
                                            }
                                    }
                                    else if(pastryChoice == 1)
                                    {
                                        System.out.println("How many Muffins would you like?");
                                        int muffinQuantity = kbd.nextInt();
                                        double cost = (ESPRESSO_PRICE * quantity) + (MUFFIN_PRICE* muffinQuantity);
                                        if(cost >= 10)
                                        {
                                            double discount = cost - (0.10*cost);
                                            System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                            double tax = discount + 0.07*(discount);
                                            System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                            System.out.println("Please enter a payment amount: ");
                                            double paymentAmount = kbd.nextDouble();

                                            if(paymentAmount < tax)
                                            {
                                                    System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                            }
                                            else if(paymentAmount > tax)
                                            {
                                                double change = paymentAmount - tax;
                                                System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change);
                                                
                                            }
                                        }
                                    }
                                
                                }
                                break;

                        case 4: 
                                System.out.println("Would you like a beverage with your Scone?");
                                System.out.println("To say no,  enter 0");
                                System.out.println("To say yes, enter 1");

                                int decision = kbd.nextInt();

                                if(decision == 0)
                                {
                                        double cost = SCONE_PRICE * quantity;
                                        if(cost >= 10)
                                    {
                                        double discount = cost - (0.10*cost);
                                        System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                        double tax = discount + 0.07*(discount);
                                        System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                        System.out.println("Please enter a payment amount: ");
                                        double paymentAmount = kbd.nextDouble();

                                        if(paymentAmount < tax)
                                        {
                                                System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                        }
                                        else if(paymentAmount > tax)
                                        {
                                            double change = paymentAmount - tax;
                                            System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change + " Have an awesome day!");
                                            
                                        }
                                    }
                                }
                                else if(decision == 1)
                                {
                                    System.out.println("As stated before, our ordering system is special...");
                                    System.out.println("To order Coffee(s),    enter 0");
                                    System.out.println("To order Latte(s),     enter 1");
                                    System.out.println("To order Cappucino(s), enter 2");
                                    System.out.println("To order Espresso(s),  enter 3");
                                    int beverageChoice = kbd.nextInt();

                                    if(beverageChoice == 0)
                                    {
                                        System.out.println("How many Coffee(s) would you like?");
                                            int coffeeQuantity = kbd.nextInt();
                                            double cost = (SCONE_PRICE * quantity) + (COFFEE_PRICE * coffeeQuantity);
                                            if(cost >= 10)
                                            {
                                                    double discount = cost - (0.10*cost);
                                                    System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                                    double tax = discount + 0.07*(discount);
                                                    System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                                    System.out.println("Please enter a payment amount: ");
                                                    double paymentAmount = kbd.nextDouble();

                                                    if(paymentAmount < tax)
                                                    {
                                                            System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                                    }
                                                    else if(paymentAmount > tax)
                                                    {
                                                        double change = paymentAmount - tax;
                                                        
                                                        System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change);
                                                        
                                                    }
                                            }
                                    }
                                    if(beverageChoice == 1)
                                    {
                                        System.out.println("How many Latte(s) would you like?");
                                            int latteQuantity = kbd.nextInt();
                                            double cost = (SCONE_PRICE * quantity) + (LATTE_PRICE * latteQuantity);
                                            if(cost >= 10)
                                            {
                                                    double discount = cost - (0.10*cost);
                                                    System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                                    double tax = discount + 0.07*(discount);
                                                    System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                                    System.out.println("Please enter a payment amount: ");
                                                    double paymentAmount = kbd.nextDouble();

                                                    if(paymentAmount < tax)
                                                    {
                                                            System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                                    }
                                                    else if(paymentAmount > tax)
                                                    {
                                                        double change = paymentAmount - tax;
                                                        System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change + " Have an awesome day!");
                                                        
                                                    }
                                            }
                                    }
                                    if(beverageChoice == 2)
                                    {
                                        System.out.println("How many Cappucino(s) would you like?");
                                            int cappucinoQuantity = kbd.nextInt();
                                            double cost = (SCONE_PRICE * quantity) + (CAPPUCINO_PRICE * cappucinoQuantity);
                                            if(cost >= 10)
                                            {
                                                    double discount = cost - (0.10*cost);
                                                    System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                                    double tax = discount + 0.07*(discount);
                                                    System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                                    System.out.println("Please enter a payment amount: ");
                                                    double paymentAmount = kbd.nextDouble();

                                                    if(paymentAmount < tax)
                                                    {
                                                            System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                                    }
                                                    else if(paymentAmount > tax)
                                                    {
                                                        double change = paymentAmount - tax;
                                                        System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change + " Have an awesome day!");
                                                        
                                                    }
                                            }
                                    }
                                    if(beverageChoice == 3)
                                    {
                                        System.out.println("How many Espresso(s) would you like?");
                                            int espressoQuantity = kbd.nextInt();
                                            double cost = (SCONE_PRICE * quantity) + (ESPRESSO_PRICE * espressoQuantity);
                                            if(cost >= 10)
                                            {
                                                    double discount = cost - (0.10*cost);
                                                    System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                                    double tax = discount + 0.07*(discount);
                                                    System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                                    System.out.println("Please enter a payment amount: ");
                                                    double paymentAmount = kbd.nextDouble();

                                                    if(paymentAmount < tax)
                                                    {
                                                            System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                                    }
                                                    else if(paymentAmount > tax)
                                                    {
                                                        double change = paymentAmount - tax;
                                                        System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change + " Have an awesome day!");
                                                        
                                                    }
                                            }
                                    }
                                }
                        case 5:
                        System.out.println("Would you like a beverage with your Muffin?");
                        System.out.println("To say no,  enter 0");
                        System.out.println("To say yes, enter 1");

                        int decision2 = kbd.nextInt();

                        if(decision2 == 0)
                        {
                                double cost = MUFFIN_PRICE * quantity;
                                if(cost >= 10)
                            {
                                double discount = cost - (0.10*cost);
                                System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                double tax = discount + 0.07*(discount);
                                System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                System.out.println("Please enter a payment amount: ");
                                double paymentAmount = kbd.nextDouble();

                                if(paymentAmount < tax)
                                {
                                        System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                }
                                else if(paymentAmount > tax)
                                {
                                    double change = paymentAmount - tax;
                                    System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change + " Have an awesome day!");
                                    
                                }
                            }
                        }
                        else if(decision2 == 1)
                        {
                            System.out.println("As stated before, our ordering system is special...");
                            System.out.println("To order Coffee(s),    enter 0");
                            System.out.println("To order Latte(s),     enter 1");
                            System.out.println("To order Cappucino(s), enter 2");
                            System.out.println("To order Espresso(s),  enter 3");
                            int beverageChoice = kbd.nextInt();

                            if(beverageChoice == 0)
                            {
                                System.out.println("How many Coffee(s) would you like?");
                                    int coffeeQuantity = kbd.nextInt();
                                    double cost = (MUFFIN_PRICE * quantity) + (COFFEE_PRICE * coffeeQuantity);
                                    if(cost >= 10)
                                    {
                                            double discount = cost - (0.10*cost);
                                            System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                            double tax = discount + 0.07*(discount);
                                            System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                            System.out.println("Please enter a payment amount: ");
                                            double paymentAmount = kbd.nextDouble();

                                            if(paymentAmount < tax)
                                            {
                                                    System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                            }
                                            else if(paymentAmount > tax)
                                            {
                                                double change = paymentAmount - tax;
                                                System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change + " Have an awesome day!");
                                                
                                            }
                                    }
                            }
                            if(beverageChoice == 1)
                            {
                                System.out.println("How many Latte(s) would you like?");
                                    int latteQuantity = kbd.nextInt();
                                    double cost = (MUFFIN_PRICE * quantity) + (LATTE_PRICE * latteQuantity);
                                    if(cost >= 10)
                                    {
                                            double discount = cost - (0.10*cost);
                                            System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                            double tax = discount + 0.07*(discount);
                                            System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                            System.out.println("Please enter a payment amount: ");
                                            double paymentAmount = kbd.nextDouble();

                                            if(paymentAmount < tax)
                                            {
                                                    System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                            }
                                            else if(paymentAmount > tax)
                                            {
                                                double change = paymentAmount - tax;
                                                System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change + " Have an awesome day!");
                                                
                                            }
                                    }
                            }
                            if(beverageChoice == 2)
                            {
                                System.out.println("How many Cappucino(s) would you like?");
                                    int cappucinoQuantity = kbd.nextInt();
                                    double cost = (MUFFIN_PRICE * quantity) + (CAPPUCINO_PRICE * cappucinoQuantity);
                                    if(cost >= 10)
                                    {
                                            double discount = cost - (0.10*cost);
                                            System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                            double tax = discount + 0.07*(discount);
                                            System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                            System.out.println("Please enter a payment amount: ");
                                            double paymentAmount = kbd.nextDouble();

                                            if(paymentAmount < tax)
                                            {
                                                    System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                            }
                                            else if(paymentAmount > tax)
                                            {
                                                double change = paymentAmount - tax;
                                                System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change + " Have an awesome day!");
                                                
                                            }
                                    }
                            }
                            if(beverageChoice == 3) 
                            {
                                System.out.println("How many Espresso(s) would you like?");
                                    int espressoQuantity = kbd.nextInt();
                                    double cost = (MUFFIN_PRICE * quantity) + (ESPRESSO_PRICE * espressoQuantity);
                                    if(cost >= 10)
                                    {
                                            double discount = cost - (0.10*cost);
                                            System.out.println("Since you have spent $10 or more, you receive a 10% discount, your total is: " + discount);
                                            double tax = discount + 0.07*(discount);
                                            System.out.printf("Your total after calculating tax is: $%.2f\n", tax );

                                            System.out.println("Please enter a payment amount: ");
                                            double paymentAmount = kbd.nextDouble();

                                            if(paymentAmount < tax)
                                            {
                                                    System.out.println("Sorry, " + customer + "but your payment is insufficient.");
                                            }
                                            else if(paymentAmount > tax)
                                            {
                                                double change = paymentAmount - tax;
                                                System.out.printf("Thank you for your purchase! Your change is $%.2f\n", change + " Have an awesome day!" );
                                                
                                            }
                                    }
                            }
                        }
                        default: // provide an instance of the customer ordering an item that is not on the menu
                                System.out.println("You haven't ordered anything on the menu, but...");
                                System.out.println("Have a nice day!");
                }
                    kbd.close();    // close the Scanner 
                    System.exit(0);
                
        }
}