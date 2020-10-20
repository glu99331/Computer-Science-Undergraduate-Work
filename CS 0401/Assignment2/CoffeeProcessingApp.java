import java.io.*;
import java.util.Scanner;
 
public class CoffeeProcessingApp
{
    public static void main(String[] args)throws IOException
    {
        Scanner stringReader = new Scanner(System.in);
        Scanner numberReader = new Scanner(System.in);
 
        String[][] arr = new String [15][10];
        int[] receivedOrders  = new int[20];
        int selection = 0;
        do{
            displayMenu();
            selection = numberReader.nextInt();
            switch(selection)
            {
            case 1:
                viewOrders();
                break;
            case 2:
                arr = receiveOrders(receivedOrders);
                break;
            case 3:
                initiateProcess(receivedOrders, numberReader, arr);
                break;
            case 4:
                finishProcessing(receivedOrders, numberReader, arr);
                break;
            case 5:
                viewHistory(arr);
                break;
            case 6:
                report(numberReader, stringReader);
                break;
            case 7:
                System.exit(0);
                break;
 
            }
        }while(selection >= 1 && selection <= 7 );
 
    }
 
    public static void displayMenu()
    {
        System.out.println("\n---Processing Menu---");
        System.out.println("1. View orders");
        System.out.println("2. Receive orders");
        System.out.println("3. Begin processing an order");
        System.out.println("4. Complete processing an order");
        System.out.println("5. View order history");
        System.out.println("6. Run a Report");
        System.out.println("7. Exit");
        System.out.println("\nEnter an action");
    }
 
    public static void viewOrders() throws IOException
    {
        int lineFile = 0;
        File ordersFile = new File("orders.txt");
        if(ordersFile.exists())
        {
            Scanner scanFiles = new Scanner(ordersFile);
            while(scanFiles.hasNext())
            {
                scanFiles.nextLine();
                lineFile++;
            }
            scanFiles.close();
 
            if(lineFile == 0)
            {
                System.out.println("No orders are in at this time.");
            }
            else
            {
                Scanner fileInput = new Scanner(ordersFile);
                System.out.println("---Orders---");
                while(fileInput.hasNext())
                {
                    System.out.println(fileInput.nextLine());
 
                }
                fileInput.close();
            }
        }
        else
        {
            System.out.println("File not found.");
        }
    }
    public static String[][] receiveOrders(int[] statusOrder)throws IOException
    {
        int lineFile = 0;
        int lineFileIndex = 0;
        File orders = new File("orders.txt");
        File status = new File("status.txt");
        PrintWriter output = new PrintWriter(status);
        String[][] arrLn = new String [10][8];
 
        if(orders.exists())
        {
            Scanner scan = new Scanner(orders);
            while(scan.hasNext())
            {
                lineFile++;
                lineFileIndex = lineFile-1;
                String ln = scan.nextLine();
                arrLn[lineFileIndex] = ln.split(", ");
                output.println("Order " + lineFile + " has been received!");
                System.out.println("Order " + lineFile + " has been received!");
                statusOrder[lineFileIndex] = 1;
            }
            scan.close();
            output.close();
        }
        PrintWriter delete = new PrintWriter(orders);
        delete.close();
 
        return arrLn;
    }
    public static void initiateProcess(int[] statusOrder, Scanner kbd, String[][] arrLn)throws IOException
    {
        int stay = 0;
        int process = 0;
 
        System.out.println("Choose one of the received orders to process: \n ");
 
        int count = 0;
        for(int i = 0; i < 10; i++)
        {
            if(statusOrder[i] == 1)
            {  
                count += 1;
                System.out.println(count + ". Order: " + arrLn[i][0]);
                stay++;
            }
        }
        if(stay != 0)
        {
            System.out.println("Which order would you like to start processing?");
            process = kbd.nextInt();
 
            count = 0;
            int orderIndex = 0;
            for(int i : statusOrder)
            {
                if(i == 1)
                {
                    count++;
                }
                if(count == process)
                    break;
                orderIndex++;
            }
 
            statusOrder[orderIndex] = 2;
 
            System.out.println("Your order has successfully begun processing!");
 
            File status = new File("status.txt");
            PrintWriter output = new PrintWriter(status);
 
            for(int i = 0; i < 10; i++)
            {
                if(statusOrder[i] == 1)
                {
                    count += 1;
                    output.println("Order" + i +" has been received!");
                }
                else if(statusOrder[i] == 2)
                {
                    count += count;
                    output.println("Order " + i + " is currently being processed!");
                }
            }
            output.close();
        }
        else
        {
            System.out.println("All your orders have been processed!");
        }
    }
 
    public static void finishProcessing(int[] statusOrder, Scanner kbd, String[][] arrLn) throws IOException
    {
        FileWriter writeHistory = new FileWriter("order_history.txt",true);
        PrintWriter orderHistory = new PrintWriter(writeHistory);
 
        int count = 1;
        System.out.println("Here are the orders that are processing:");
        for(int i = 0; i < statusOrder.length; i++)
        {
            if(statusOrder[i] == 2)
                System.out.println(count++ + ". Order " + (i + 1));
        }
 
        System.out.println("Which order would you like to finish processing?");
        int process = kbd.nextInt();
        int orderIndex = 0;
        count = 0;
        for(int i : statusOrder)
        {
            if(i == 2)
            {
                count++;
            }
            if(count == process)
                break;
            orderIndex++;
        }
 
        statusOrder[orderIndex] = 3;
        System.out.println("Your order has successfully finished processing!");
 
        for(int i = 0; i < arrLn[0].length; i++)
            orderHistory.print(arrLn[orderIndex][i] + ", ");
        orderHistory.println();
 
        FileWriter status = new FileWriter("status.txt", true);
        PrintWriter output = new PrintWriter(status);
 
        output.println("Order " + (orderIndex + 1) + " has been processed!");
 
        output.close();
 
        orderHistory.close();
 
    }
 
    public static void viewHistory(String [][] arr)
    {
        File history = new File("order_history.txt");
        Scanner reader = null;
        try {
            reader = new Scanner(history);
            System.out.println("\n------Order History------");
            System.out.println("Customer\tCoffee\t\tLatte\t\tCappuccino\tEspresso\tScone\t\tMuffin");
            System.out.println("---------\t-------\t\t-----\t\t----------\t-------\t\t-----\t\t-----");
 
            while(reader.hasNext())
            {
                String[] line = reader.nextLine().split(", ");
                for(int i = 1; i < line.length; i++)
                {
                    System.out.print(line[i] + "\t\t");
                }
                System.out.println();
            }
 
            reader.close();
 
        } catch (FileNotFoundException e) {
            System.out.println("No history found.");
        }
 
    }
 
    public static void report(Scanner kbd, Scanner nameReader)
    {
        String[][] orderHist = new String[10][8];
        File history = new File("order_history.txt");
        Scanner reader = null;
        try {
            reader = new Scanner(history);
            int count = 0;
 
            while(reader.hasNext())
            {
 
                orderHist[count] = reader.nextLine().split(", ");
                count++;
            }
 
            reader.close();
 
        } catch (FileNotFoundException e) {
            System.out.println("No history to report.");
        }
 
        System.out.println("--- Report Menu ---\n");
        System.out.println("1. Customer\n2. Coffee\n3. Latte\n4. Cappuccino"
                + "\n5. Espresso\n6. Scone\n7. Muffin\n");
        System.out.println("Choose a field: ");
        int choice = kbd.nextInt();
        kbd.nextLine();
 
        if(choice != 1)
        {
            System.out.println("\nItem: " + numToField(choice));
            int total = 0;
            for(int i = 0; i < orderHist.length; i++)
            {
                if(orderHist[i][choice] != null)
                    total += Integer.parseInt(orderHist[i][choice]);
            }
            System.out.println("Total Sold: " + total);
            System.out.println("Report Done.");
        }
        else
        {
            System.out.println("Enter the customer's name: ");
            String name = nameReader.nextLine();
            for(int i = 0; i < orderHist.length; i++)
            {
                if(orderHist[i][1] != null && orderHist[i][1].equals(name))
                {
                    System.out.println("Coffee\t\tLatte\t\tCappuccino\tEspresso\tScone\t\tMuffin");
                    System.out.println("-------\t\t-----\t\t----------\t-------\t\t-----\t\t-----");
                    for(int j = 2; j < orderHist[0].length; j++)
                    {
                        System.out.print(orderHist[i][j] + "\t\t");
                    }
                    System.out.println();
                }
            }
            System.out.println("Report Done.");
        }
 
 
 
    }
 
    private static String numToField(int i)
    {
        String s = "";
 
        switch(i)
        {
        case 1:
            s = "Customer";
            break;
        case 2:
            s = "Coffee";
            break;
        case 3:
            s = "Latte";
            break;
        case 4:
            s = "Cappuccino";
            break;
        case 5:
            s = "Espresso";
            break;
        case 6:
            s = "Scone";
            break;
        case 7:
            s = "Muffin";
            break;
        }
        return s;
    }
}