public class PTE implements Comparable<PTE>
{
  //Object to represent a single page table entry (PTE)

  //Each entry in the page table contains...
  //Valid bit: set if this logical page number has a corresponding physical frame in memory
  // - If not valid, remainder of PTE is irrelevant
  int valid_bit; 
  //Page frame number: page in physical memory
  int page_frame_number;
  //Referenced bit: set if data on the page has been accessed
  int referenced_bit;
  //Dirty (modified) bit: set if data on the page has been modified
  int dirty_bit;
  //Protection information
  int protection_bits;
  //Add another field for LRU: Keep counter in each page table entry
  //This counter will be incremented each time the PTE is referenced!
  int num_memory_references;
  //Default Constructor: When we create a PTE, initialize to default values:
  public PTE()
  {
    valid_bit = 0; //0 means that this is page number does not have a corresponding frame in memory
    referenced_bit = 0; //0 means not accessed yet
    dirty_bit = 0; //0 means not modified yet
    page_frame_number = -1; //0 actual indicates some value, but -1 doesn't...
    protection_bits = -1; //0 actual indicates some value, but -1 doesn't...
    num_memory_references = 0; //0 indicates that this memory has not been referenced yet
    
  }
  //Override the compareTo function from the Comparable interface:
  @Override
  public int compareTo(PTE other)
  {
    return (this.get_memory_references() - other.get_memory_references());
  }
  //Accessors:
  public int get_valid_bit()
  {
    return valid_bit;
  }
  public int get_page_frame_number()
  {
    return page_frame_number;
  }
  public int get_referenced_bit()
  {
    return referenced_bit;
  }
  public int get_dirty_bit()
  {
    return dirty_bit;
  }
  public int get_protection_bits()
  {
    return protection_bits;
  }
  public int get_memory_references()
  {
    return num_memory_references;
  }
  //Mutators
  public void set_valid_bit(int valid_bit)
  {
    this.valid_bit = valid_bit;
  }
  public void set_page_frame_number(int page_frame_number)
  {
    this.page_frame_number = page_frame_number;
  }
  public void set_referenced_bit(int referenced_bit)
  { 
    this.referenced_bit = referenced_bit;
  }
  public void set_dirty_bit(int dirty_bit)
  {
    this.dirty_bit = dirty_bit;
  }
  public void set_protection_bits(int protection_bits)
  {
    this.protection_bits = protection_bits;
  }
  public void set_num_references(int num_memory_references)
  {
    this.num_memory_references = num_memory_references;
  }
  public void printContents()
  {
    System.out.println("-------------Page Contents---------");
    System.out.println("Valid Bit: " + valid_bit);
    System.out.println("Referenced Bit: " + referenced_bit);
    System.out.println("Dirty Bit: " + dirty_bit);
    System.out.println("Page Frame Number: " + page_frame_number);
    System.out.println("Memory References: " + num_memory_references);
    System.out.println("-----------------------------------");

  }
}