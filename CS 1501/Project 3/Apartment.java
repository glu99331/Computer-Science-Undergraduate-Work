public class Apartment implements Comparable<Apartment>
{
    /*******************************************************************************
     *                          Apartment Class                                    *
     * <~-------------------------------------------------------------------------~>
     * Create a class to store data about apartments.                              *
     *                                                                             *
     * Class must contain the following information...                             *
     * 1) Street Address (E.g. 4200 Forbes Ave.)                                   *
     * 2) Apartment Number (E.g. 3601)                                             *
     * 3) City the Apartment is in (E.g. Pittsburgh)                               *
     * 4) Apartment's Zip Code (E.g. 15213)                                        *
     * 5) Monthly Cost to Rent (in US Dollars)                                     *
     * 6) Square Footage of the Apartment                                          *
     *                                                                             *
     ******************************************************************************/

    String streetAddress;        //Instance variable to hold the Apartment's Street Address
    String apartmentNumber;      //Instance variable to hold the Apartment's Apartment Number
    String apartmentCity;        //Instance variable to hold the Apartment's City Name
    int apartmentZipCode;        //Instance variable to hold the Apartment's Zip Code
    int monthlyCostToRent;       //Instance variable to hold the Apartment's Monthly Cost to Rent
    int apartmentSquareFootage;  //Instance variable to hold the Apartment's Square Footage

    int DLBPriceIndex = -1;         //Instance variable to hold the appropriate price index from the DLB
    int DLBSizeIndex = -1;          //Instance variable to hold the appropriate price index from the DLB 
    int TST_PricePQIndex = -1;      //Instance variable to hold the appropriate price index from the DLB
    int TST_SizePQIndex = -1;       //Instance variable to hold the appropriate price index from the DLB

    int indexedField = 0;

    //Need a Constructor...
    //Program will pass in User Input... 
    public Apartment(String streetAddress, String apartmentNumber, String apartmentCity, String apartmentZipCode, String monthlyCostToRent, String apartmentSquareFootage)
    {
        //Initialize instance variable to User-Inputted Fields...
        this.streetAddress = streetAddress;
        this.apartmentNumber = apartmentNumber;
        this.apartmentCity = apartmentCity;
        this.apartmentZipCode = Integer.parseInt(apartmentZipCode);
        this.monthlyCostToRent = Integer.parseInt(monthlyCostToRent);
        this.apartmentSquareFootage = Integer.parseInt(apartmentSquareFootage);
    }
    //Determine if two Apartments hold the same Contents...
    public boolean equals(Apartment a)
    {
        boolean equalStreetAddresses = (this.streetAddress.equals(a.streetAddress));
        boolean equalApartmentNumbers = (this.apartmentNumber.equals(a.apartmentNumber));
        boolean equalApartmentCities = (this.apartmentCity.equals(a.apartmentCity));
        boolean equalZipCodes = (this.apartmentZipCode == a.apartmentZipCode);
        boolean equalRents = (this.monthlyCostToRent == a.monthlyCostToRent);
        boolean equalSize = (this.apartmentSquareFootage == a.apartmentSquareFootage);
        //If two apartments fields match line for line, return true, otherwise return false!
        return (equalStreetAddresses && equalApartmentNumbers && equalApartmentCities && equalZipCodes && equalRents && equalSize);
    }

    @Override
    //In order to use the Comparable interface, we need to override the compareTo function...
    public int compareTo(Apartment a)
    {
        return this.getRent() - a.getRent();
    }
    //Getters...
    public String getAddress()
    {
        return streetAddress;
    }
    public String getAptNum()
    {
        return apartmentNumber;
    }
    public String getAptCity()
    {
        return apartmentCity;
    }
    public int getZipCode()
    {
        return apartmentZipCode;
    }
    public int getRent()
    {
        return monthlyCostToRent;
    }
    public int getSize()
    {
        return apartmentSquareFootage;
    }

    //and Setters!!
    public void setAddress(String streetAddress)
    {
        this.streetAddress = streetAddress;
    }
    public void setAptNum(String apartmentNumber)
    {
        this.apartmentNumber = apartmentNumber;
    }
    public void setAptCity(String apartmenttCity)
    {
        this.apartmentCity = apartmenttCity;
    }
    public void setZipCode(int apartmentZipCode)
    {
        this.apartmentZipCode = apartmentZipCode;
    }
    public void setRent(int monthlyCostToRent)
    {
        this.monthlyCostToRent = monthlyCostToRent;
    }
    public void setSize(int apartmentSquareFootage)
    {
        this.apartmentSquareFootage = apartmentSquareFootage;
    }
    
    @Override
    //Print out the apartment contents, for aesthetic and testing purposes...
    public String toString()
    {
        StringBuilder fullContent = new StringBuilder();

        String formattedStreetAddress = String.format("|| Street Address: %-56s%s\n", streetAddress, "||");
        String formattedAptNumber = String.format("|| Apartment Number: %-54s%s\n", apartmentNumber, "||");
        String formattedCity = String.format("|| City: %-66s%s\n", apartmentCity, "||");
        String formattedZipCode = String.format("|| Zip Code: %-62d%s\n", apartmentZipCode, "||");
        String formattedMonthlyCost = String.format("|| Monthly Rent: $%-57d%s\n", monthlyCostToRent, "||");
        String formattedSize = String.format("|| Square Footage: %-56s%s\n", apartmentSquareFootage + " sqft", "||");

        fullContent.append("<~--------------------------Apartment---Contents---------------------------~>\n");
        fullContent.append(formattedStreetAddress);
        //fullContent.append("|| Street Address: " + streetAddress + "                                        ||\n");
        //fullContent.append("|| Apartment Number: " + apartmentNumber + "                                    ||\n");
        fullContent.append(formattedAptNumber);
        //fullContent.append("|| City: " + apartmentCity + "                                                  ||\n");
        fullContent.append(formattedCity);
        //fullContent.append("|| Zip Code: " + apartmentZipCode + "                                           ||\n");
        fullContent.append(formattedZipCode);
        fullContent.append(formattedMonthlyCost);
        //fullContent.append("|| Monthly Rent: $" + monthlyCostToRent + "                                     ||\n");
        fullContent.append(formattedSize);
        //fullContent.append("|| Square Footage: " + apartmentSquareFootage + " sqft                          ||\n");
        fullContent.append("<~-------------------------------------------------------------------------~>\n");

        return fullContent.toString();
    }

    public String enqueuedToString()
    {

        StringBuilder fullContent = new StringBuilder();

        String formattedStreetAddress = String.format("|| Street Address: %-56s%s\n", streetAddress, "||");
        String formattedAptNumber = String.format("|| Apartment Number: %-54s%s\n", apartmentNumber, "||");
        String formattedCity = String.format("|| City: %-66s%s\n", apartmentCity, "||");
        String formattedZipCode = String.format("|| Zip Code: %-62d%s\n", apartmentZipCode, "||");
        String formattedMonthlyCost = String.format("|| Monthly Rent: $%-57d%s\n", monthlyCostToRent, "||");
        String formattedSize = String.format("|| Square Footage: %-56s%s\n", apartmentSquareFootage +" sqft", "||");
        
        fullContent.append("<~-------------------------------------------------------------------------~>\n");
        fullContent.append(formattedStreetAddress);
        //fullContent.append("|| Street Address: " + streetAddress + "                                        ||\n");
        //fullContent.append("|| Apartment Number: " + apartmentNumber + "                                    ||\n");
        fullContent.append(formattedAptNumber);
        //fullContent.append("|| City: " + apartmentCity + "                                                  ||\n");
        fullContent.append(formattedCity);
        //fullContent.append("|| Zip Code: " + apartmentZipCode + "                                           ||\n");
        fullContent.append(formattedZipCode);
        fullContent.append(formattedMonthlyCost);
        //fullContent.append("|| Monthly Rent: $" + monthlyCostToRent + "                                     ||\n");
        fullContent.append(formattedSize);
        //fullContent.append("|| Square Footage: " + apartmentSquareFootage + " sqft                          ||\n");
        fullContent.append("<~-------------------------------------------------------------------------~>\n");

        return fullContent.toString();
    
    }
    public String toSmallestString()
    {
        StringBuilder fullContent = new StringBuilder();

        String formattedStreetAddress = String.format("|| Street Address: %-56s%s\n", streetAddress, "||");
        String formattedAptNumber = String.format("|| Apartment Number: %-54s%s\n", apartmentNumber, "||");
        String formattedCity = String.format("|| City: %-66s%s\n", apartmentCity, "||");
        String formattedZipCode = String.format("|| Zip Code: %-62d%s\n", apartmentZipCode, "||");
        String formattedMonthlyCost = String.format("|| Monthly Rent: $%-57d%s\n", monthlyCostToRent, "||");
        String formattedSize = String.format("|| Square Footage: %-56s%s\n", apartmentSquareFootage + " sqft", "||");

        fullContent.append("<~--------------------------Apartment---Contents---------------------------~>\n");
        fullContent.append(formattedStreetAddress);
        //fullContent.append("|| Street Address: " + streetAddress + "                                        ||\n");
        //fullContent.append("|| Apartment Number: " + apartmentNumber + "                                    ||\n");
        fullContent.append(formattedAptNumber);
        //fullContent.append("|| City: " + apartmentCity + "                                                  ||\n");
        fullContent.append(formattedCity);
        //fullContent.append("|| Zip Code: " + apartmentZipCode + "                                           ||\n");
        fullContent.append(formattedZipCode);
        fullContent.append(formattedMonthlyCost);
        //fullContent.append("|| Monthly Rent: $" + monthlyCostToRent + "                                     ||\n");
        fullContent.append(formattedSize);
        //fullContent.append("|| Square Footage: " + apartmentSquareFootage + " sqft                          ||\n");
        fullContent.append("<~-------------------------------------------------------------------------~>");

        return fullContent.toString();
    }
}