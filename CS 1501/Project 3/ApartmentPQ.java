import java.util.*;
@SuppressWarnings("unchecked")
public class ApartmentPQ
{
    /***********************************************************************************************
     *                             Apartment Priority Queue Class                                  *    
     * <~-----------------------------------------------------------------------------------------~>
     * This class will serve to aid with the Driver...                                             *
     * - It will allow for an easier implementation of the Driver...                               *
     *                                                                                             *
     *                                                                                             *
     **********************************************************************************************/
     
     /**********************************************************************************************
      *                             Global Instance Variables                                      *
      * <~----------------------------------------------------------------------------------------~>
      *                                                                                            *
      * The following Priority Queues will be utilized to track the progress by Prices and Square  *
      * - MinPQ: PricePQ                                                                           *
      * - MaxPQ: sqFt                                                                              *
      *                                                                                            *
      * - sqFtMap: This will serve as the indirection table! This will tell where in the sqFtPQ    *
      * a given apartment is! A DLB is used here due to the expectation of sparse data, and        *
      * fast string fetch operations.                                                              *
      *                                                                                            *                                                                                            *
      * - priceMap: This will serve as the indirection table! This will tell where in the pricePQ  *
      * a given apartment is! A DLB is used here due to the expectation of sparse data, and        *
      * fast string fetch operations.                                                              *
      *                                                                                            *
      * - sizeCityMap: This will store the apartments based on sqFt, and a given city.             *
      * We must use a PQ in order to maintain the ordering of a subquery based on City.            *
      *                                                                                            *
      * - priceCityMap: This will store the apartments based on price, and a given city.           *
      * We must use a PQ in order to maintain the ordering of a subquery based on City.            *
      *                                                                                            *
      * initCapacity: This will be the size of the pricePQ and sqFtPQ                              *
      *                                                                                            *
      * aptNum: This will serve as the index in the pricePQ and the priceCityPQ, sqFtPQ,           *
      * and sizeCityPQ of a given apartment.                                                       *
      *********************************************************************************************/

    //IndexedMinMaxPQ<Apartment> pricePQ;
    IndexedMinMaxPQ<Apartment> pricePQ;
    IndexedMinMaxPQ<Apartment> sqFtPQ;

    //To aid with update and removals:
    DLB<Integer> sqFtMap; 
    DLB<Integer> priceMap; 

    //To aid with querying by city:
    DLB<IndexedMinMaxPQ<Apartment>> sizeCityMap; 
    DLB<IndexedMinMaxPQ<Apartment>> priceCityMap; 

    int initCapacity;
    int aptNum;

    //Default Constructor: The ApartmentPQ will call a Secondary Constructor that will initialize the Array-Backed Heaps to a Length of 100...
    public ApartmentPQ()
    {
        this(100);
    }
    //Secondary Constructor: Initializes both Priority Queues to a Size of 100...
    public ApartmentPQ(int initCapacity)
    {
        //Initialize the PricePQ and sqFtPQ with the passed in Initial Capacity! 
        //(The array-backed heap will be initialized to size "initCapcity")
        this.initCapacity = initCapacity;
        pricePQ = new IndexedMinMaxPQ<Apartment>("MIN", "REG", initCapacity);
        sqFtPQ = new IndexedMinMaxPQ<Apartment>("MAX", "REG", initCapacity);

        //priceCityPQ = new IndexedMinMaxPQ<Apartment>("MIN", initCapacity);
        //sizeCityPQ = new IndexedMinMaxPQ<Apartment>("MAX", initCapacity);

        //Initialize the TreeMaps...
        sqFtMap = new DLB<Integer>();
        priceMap = new DLB<Integer>();

        sizeCityMap = new DLB<IndexedMinMaxPQ<Apartment>>();
        priceCityMap = new DLB<IndexedMinMaxPQ<Apartment>>();
        
        aptNum = 0;
 
    }
    //Insert the apartment into the PQs...
    public void insertApartment(Apartment apt)
    {
        String searchContents = "" + apt.getAddress() + apt.getAptNum() + apt.getZipCode();        
        String sizeToString = "" + apt.getSize(), zipCodeToString = "" + apt.getZipCode(), rentToString = "" + apt.getRent();

        //First apartment:
        if(sizeCityMap.isEmpty() || sizeCityMap.isChildless())
        {
            sizeCityMap.put(apt.getAptCity(), new IndexedMinMaxPQ<Apartment>("MAX", "REG", initCapacity));
            IndexedMinMaxPQ<Apartment> tempMaxPQ = sizeCityMap.get(apt.getAptCity());
            tempMaxPQ.insert(aptNum, new Apartment(apt.getAddress(), apt.getAptNum(), apt.getAptCity(), zipCodeToString, rentToString, sizeToString));
            sizeCityMap.put(apt.getAptCity(), tempMaxPQ);
        }
        else if(sizeCityMap.containsKey(apt.getAptCity()))
        {
            IndexedMinMaxPQ<Apartment> tempMaxPQ = sizeCityMap.get(apt.getAptCity());
            tempMaxPQ.insert(aptNum, apt);
            sizeCityMap.put(apt.getAptCity(), tempMaxPQ);
        }
        //Otherwise, we need to insert into the Tree Map!!
        else
        {
            sizeCityMap.put(apt.getAptCity(), new IndexedMinMaxPQ<Apartment>("MAX", "REG", initCapacity));
            IndexedMinMaxPQ<Apartment> tempMaxPQ = sizeCityMap.get(apt.getAptCity());
            tempMaxPQ.insert(aptNum, new Apartment(apt.getAddress(), apt.getAptNum(), apt.getAptCity(), zipCodeToString, rentToString, sizeToString));
            sizeCityMap.put(apt.getAptCity(), tempMaxPQ);
        }
        //First apartment:
        if(priceCityMap.isEmpty() || priceCityMap.isChildless())
        {
            priceCityMap.put(apt.getAptCity(), new IndexedMinMaxPQ<Apartment>("MIN", "REG", initCapacity));
            IndexedMinMaxPQ<Apartment> tempMinPQ = priceCityMap.get(apt.getAptCity());
            tempMinPQ.insert(aptNum, new Apartment(apt.getAddress(), apt.getAptNum(), apt.getAptCity(), zipCodeToString, rentToString, sizeToString));
            priceCityMap.put(apt.getAptCity(), tempMinPQ);
        }
        else if(priceCityMap.containsKey(apt.getAptCity()))
        {
            IndexedMinMaxPQ<Apartment> tempMinPQ = priceCityMap.get(apt.getAptCity());
            tempMinPQ.insert(aptNum, apt);
            priceCityMap.put(apt.getAptCity(), tempMinPQ);
        }
        //Otherwise, we need to insert into the Tree Map!!
        else
        {
            priceCityMap.put(apt.getAptCity(), new IndexedMinMaxPQ<Apartment>("MIN", "REG", initCapacity));
            IndexedMinMaxPQ<Apartment> tempMinPQ = priceCityMap.get(apt.getAptCity());
            tempMinPQ.insert(aptNum, new Apartment(apt.getAddress(), apt.getAptNum(), apt.getAptCity(), zipCodeToString, rentToString, sizeToString));
            priceCityMap.put(apt.getAptCity(), tempMinPQ);
        }

        pricePQ.insert(aptNum, apt);
        sqFtPQ.insert(aptNum, apt);
        //We should not expect duplicates here, since each integer maps to a unique apartment
        sqFtMap.put(searchContents, aptNum);
        priceMap.put(searchContents, aptNum);

        aptNum++;
    }

    public Apartment fetchApartment(String streetAddress, String apartmentNumber, String zipCode)
    {
        String aptContents = "" + streetAddress + apartmentNumber + zipCode;
        // int priceIndex = priceMap.get(aptContents);
        int priceIndex = 0;
        if((priceMap.get(aptContents)) == null)
        {
            return null;
        }
        else
        {
            priceIndex = priceMap.get(aptContents);
        } 
        
        if(!pricePQ.contains(priceIndex))
        {
            //viewApts();
            //System.out.println("Error. Designated apartment does not exist...");
            return null;
        }
        else
        {
            return pricePQ.PQApartmentIndexOf(priceIndex);
        }
    }

    public void updateApartment(String address, String aptNum, String zipCode, int newRent)
    {
        String searchContents = "" + address + aptNum + zipCode;

        int priceIndex = priceMap.get(searchContents);
        int sizeIndex = sqFtMap.get(searchContents);

        System.out.println("Before:" +  priceIndex);
        Apartment toUpdatePrice = pricePQ.PQApartmentIndexOf(priceIndex);
        Apartment toUpdateSize = sqFtPQ.PQApartmentIndexOf(sizeIndex);

        IndexedMinMaxPQ<Apartment> priceCityPQ = priceCityMap.get(toUpdatePrice.getAptCity());
        IndexedMinMaxPQ<Apartment> sizeCityPQ = sizeCityMap.get(toUpdateSize.getAptCity()); 

        //Need to update in the DLB too???
        pricePQ.mutateAptRent_maxPQ(priceIndex, newRent); //update the Key in the PQ... but lol what?
        sqFtPQ.mutateAptSqFt_maxPQ(sizeIndex, newRent); //update the Key in the PQ... but lol what? 
        System.out.println("After: " + priceIndex);
        priceCityPQ.mutateAptRent_maxPQ(priceIndex, newRent);
        sizeCityPQ.mutateAptSqFt_maxPQ(sizeIndex, newRent);
    }
    public void removeApartment(String address, String aptNum, String zipCode)
    {
        String searchContents = "" + address + aptNum + zipCode;

        int priceIndex = priceMap.get(searchContents);
        int sizeIndex = sqFtMap.get(searchContents);

        Apartment toDeletePrice = pricePQ.PQApartmentIndexOf(priceIndex);
        Apartment toDeleteSize = sqFtPQ.PQApartmentIndexOf(sizeIndex);

        //priceMap.remove(searchContents);
        //sqFtMap.remove(searchContents);
        priceMap.put(searchContents, null);
        sqFtMap.put(searchContents, null);

        IndexedMinMaxPQ<Apartment> priceCityPQ = priceCityMap.get(toDeletePrice.getAptCity());
        IndexedMinMaxPQ<Apartment> sizeCityPQ = sizeCityMap.get(toDeleteSize.getAptCity()); 

        //Would this be reflected in the DLB PQ, would this need to be put back into the map?
        priceCityPQ.delete(priceIndex);
        sizeCityPQ.delete(sizeIndex);

        priceCityMap.put(toDeletePrice.getAptCity(), priceCityPQ);
        sizeCityMap.put(toDeleteSize.getAptCity(), sizeCityPQ);

        //Delete from overall price PQ and sqFt PQ as well
        pricePQ.delete(priceIndex);
        sqFtPQ.delete(priceIndex);

        viewApts();
        System.out.println("<~---------------------priceCity---Contents------------------------~>");
        priceCityPQ.printApartments();
        System.out.println("<~-----------------------------------------------------------------~>");
        //TEST has just confirmed that Aparmtents are added in the Correct Order...
        System.out.println("<~---------------------sizeCity---Contents-------------------------~>");
        sizeCityPQ.printApartments();
        System.out.println("<~-----------------------------------------------------------------~>");
    
        //Decrease the number of apartments! 
        this.aptNum--;

    }
    public void viewApts()
    {
        //TEST has just confirmed that Aparmtents are added in the Correct Order...
        System.out.println("<~---------------------PricePQ-----Contents------------------------~>");
        pricePQ.printApartments();
        System.out.println("<~-----------------------------------------------------------------~>");
        //TEST has just confirmed that Aparmtents are added in the Correct Order...
        System.out.println("<~---------------------sqFtPQ-----Contents-------------------------~>");
        sqFtPQ.printApartments();
        System.out.println("<~-----------------------------------------------------------------~>");

    }

    public String minPrice()
    {
        return pricePQ.minAptPrice().toSmallestString();
    }
    public String maxSize()
    {
        return sqFtPQ.maxAptSize().toSmallestString();
    }

    public String cheapCity(String city)
    {
        try
        {
            IndexedMinMaxPQ<Apartment> tempPQ = priceCityMap.get(city);
            return tempPQ.minAptPrice().toSmallestString();
        }
        catch(NullPointerException np)
        {
            System.out.println("Error, no apartments with the city: " + city + " exist at the moment");
            return null;
        }
    }
    public String sizeCity(String city)
    {
        try
        {
            IndexedMinMaxPQ<Apartment> tempPQ = sizeCityMap.get(city);
            return tempPQ.maxAptSize().toSmallestString();
        }
        catch(NullPointerException np)
        {
            System.out.println("Error, no apartments with the city: " + city + " exist at the moment");
            return null;
        }
    }

}
