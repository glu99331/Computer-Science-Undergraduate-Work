
/*************************************************************************************
* ==================== CS 1550: Project IV, FUSE File System. ====================== *
* ---------------------------------------------------------------------------------- *
* Author: Gordon Lu																				                           *
* Term: Spring 2020, Dr.Mosse's Operating Systems class		                           *
* ---------------------------------------------------------------------------------- *
*************************************************************************************/

/*	FUSE: Filesystem in Userspace
	Copyright (C) 2001-2007  Miklos Szeredi <miklos@szeredi.hu>
	This program can be distributed under the terms of the GNU GPL.
	See the file COPYING.
*/

#define	FUSE_USE_VERSION 26

#include <fuse.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>
#include <stdlib.h>
#include <stdbool.h>
#include <math.h>
//size of a disk block
#define	BLOCK_SIZE 512

//size of the .disk file
#define DISK_SIZE 5 * (long)pow(2,20)

//we'll use 8.3 filenames
#define	MAX_FILENAME 8
#define	MAX_EXTENSION 3

//How many files can there be in one directory?
#define MAX_FILES_IN_DIR (BLOCK_SIZE - sizeof(int)) / ((MAX_FILENAME + 1) + (MAX_EXTENSION + 1) + sizeof(size_t) + sizeof(long))

//The attribute packed means to not align these things
struct cs1550_directory_entry
{
	int nFiles;	//How many files are in this directory.
				//Needs to be less than MAX_FILES_IN_DIR

	struct cs1550_file_directory
	{
		char fname[MAX_FILENAME + 1];	//filename (plus space for nul)
		char fext[MAX_EXTENSION + 1];	//extension (plus space for nul)
		size_t fsize;					//file size
		long nIndexBlock;				//where the index block is on disk
	} __attribute__((packed)) files[MAX_FILES_IN_DIR];	//There is an array of these

	//This is some space to get this to be exactly the size of the disk block.
	//Don't use it for anything.
	char padding[BLOCK_SIZE - MAX_FILES_IN_DIR * sizeof(struct cs1550_file_directory) - sizeof(int)];
} ;

typedef struct cs1550_root_directory cs1550_root_directory;

#define MAX_DIRS_IN_ROOT (BLOCK_SIZE - sizeof(int)) / ((MAX_FILENAME + 1) + sizeof(long))

struct cs1550_root_directory
{
	int nDirectories;	//How many subdirectories are in the root
						//Needs to be less than MAX_DIRS_IN_ROOT
	struct cs1550_directory
	{
		char dname[MAX_FILENAME + 1];	//directory name (plus space for nul)
		long nStartBlock;				//where the directory block is on disk
	} __attribute__((packed)) directories[MAX_DIRS_IN_ROOT];	//There is an array of these

	//This is some space to get this to be exactly the size of the disk block.
	//Don't use it for anything.
	char padding[BLOCK_SIZE - MAX_DIRS_IN_ROOT * sizeof(struct cs1550_directory) - sizeof(int)];
} ;


typedef struct cs1550_directory_entry cs1550_directory_entry;

//How many entries can one index block hold?
#define	MAX_ENTRIES_IN_INDEX_BLOCK (BLOCK_SIZE/sizeof(long))

struct cs1550_index_block
{
      //All the space in the index block can be used for index entries.
			// Each index entry is a data block number.
      long entries[MAX_ENTRIES_IN_INDEX_BLOCK];
};

typedef struct cs1550_index_block cs1550_index_block;

//How much data can one block hold?
#define	MAX_DATA_IN_BLOCK (BLOCK_SIZE)

struct cs1550_disk_block
{
	//All of the space in the block can be used for actual data
	//storage.
	char data[MAX_DATA_IN_BLOCK];
};

typedef struct cs1550_disk_block cs1550_disk_block;

//Bit map sizes: Courtesy of Henrique Potter
static int MAX_SIZE = ((1 + MAX_DIRS_IN_ROOT * (1 + MAX_FILES_IN_DIR)) / 8) + 1;
static int MAX_BLOCK_SIZE =  1 + MAX_DIRS_IN_ROOT * (1 + MAX_FILES_IN_DIR);

  
static void mknod_update(unsigned char* bitmap, cs1550_directory_entry* subDirectory_temp, char* filename, char* extension, int curr_block, int location, FILE* fp)
{
  //Update bitmap
  fseek(fp, -sizeof(bitmap), SEEK_END);
  fwrite(bitmap, sizeof(bitmap), 1, fp);
  
  //Add file to subdirectory:
  strcpy(subDirectory_temp -> files[subDirectory_temp -> nFiles].fname, filename);
  strcpy(subDirectory_temp -> files[subDirectory_temp -> nFiles].fext, extension);
  subDirectory_temp -> files[subDirectory_temp -> nFiles].nIndexBlock = curr_block;
  subDirectory_temp -> files[subDirectory_temp -> nFiles].fsize = 0;
  subDirectory_temp -> nFiles++;
  //Update root directory in disk:
  fseek(fp, BLOCK_SIZE * location, SEEK_SET);
  fwrite(subDirectory_temp, sizeof(cs1550_directory_entry), 1, fp);
  //Update new file information:
  cs1550_disk_block* newFile = malloc(sizeof(cs1550_disk_block));
  memset(newFile, 0, BLOCK_SIZE);
  fseek(fp, BLOCK_SIZE * curr_block, SEEK_SET);
  //Write out new directory to disk:
  fwrite(newFile, sizeof(cs1550_disk_block), 1, fp);
}  

// static void mkdir_update_disk(FILE* fp, unsigned char* bitmap, cs1550_root_directory* rootDirectory, char* directory, int curr_block)
// {
//   fseek(fp, -sizeof(bitmap), SEEK_END);
//   fwrite(bitmap, sizeof(bitmap), 1, fp);
// 
//   //Update root directory values:
//   strcpy(rootDirectory -> directories[rootDirectory -> nDirectories].dname, directory);
//   rootDirectory -> directories[rootDirectory -> nDirectories].nStartBlock = curr_block;
//   rootDirectory -> nDirectories++;
//   //Update root directory in disk:
//   fseek(fp, 0, SEEK_SET);
//   fwrite(rootDirectory, sizeof(cs1550_root_directory), 1, fp);
// }

// static void perform_bitmap_calculation(int* bitmap_ptr, int curr_block)
// {
//   bitmap_ptr = malloc(MAX_BLOCK_SIZE);
//   bitmap_ptr[0] = curr_block / 8;
//   bitmap_ptr[1] = curr_block % 8;
// }
/*
 * Called whenever the system wants to know the file attributes, including
 * simply whether the file exists or not.
 *
 * man -s 2 stat will show the fields of a stat structure
 */
static int cs1550_getattr(const char *path, struct stat *stbuf)
{
  memset(stbuf, 0, sizeof(struct stat));

	unsigned long int file_length =  MAX_FILENAME + 1;
	unsigned long int extension_length = MAX_EXTENSION + 1;
	//How do we ge the directory, filenmae, and extension are valid
	//Before we do anything, check if the requested path is valid:
	char directory[file_length];
	char filename[file_length];
	char extension[extension_length];
	// printf("im not stuffed.\n");

	//Fill up the buffers:
	memset(directory, 0, file_length);
	memset(filename, 0, file_length);
	memset(extension, 0, extension_length);
	// printf("im stuffed.\n");

	bool found_file_flag = false;
	bool found_dir = false;
	size_t file_size = 0;
	//Search for the subdirectory
	struct cs1550_directory* subDirectory = (struct cs1550_directory*)malloc(sizeof(struct cs1550_directory));
	//Determine if path represents a valid directory:
	sscanf(path, "/%[^/]/%[^.].%s", directory, filename, extension);
	int return_type = 0;
  int return_value = 0;

	//is path the root dir?
	if (strcmp(path, "/") == 0) {
		// stbuf->st_mode = S_IFDIR | 0711;
		// stbuf->st_nlink = 2;
    //Set return type to type 1:
    return_type = 1; 
	} else {
	   //Search for directory:
     if(strlen(directory) > 0)
     {
       FILE* fp = fopen(".disk", "rb+");
       //Read in the root directory:
       cs1550_root_directory* rootDirectory = malloc(BLOCK_SIZE);
       fread(rootDirectory, BLOCK_SIZE, 1, fp);
       //Now search through the subdirectories:
       int curr_subdir;
       for(curr_subdir = 0; curr_subdir < rootDirectory -> nDirectories && found_dir == false; curr_subdir++)
       {
         //If we find a directory with the same name as the requested directory: YAY
         if(!strcmp(rootDirectory -> directories[curr_subdir].dname, directory))
         {
           subDirectory = &(rootDirectory -> directories[curr_subdir]);
           found_dir = true;
         }
         
       }
       //If we couldn't find the directory:
       if(found_dir == false)
       {
         //type 2: directory not found!
         return_type = 2;
       }
       //Are we fine with just finding the directory?
       //else if(num == 1)
       else if(found_dir == true && strlen(filename) == 0 && strlen(extension) == 0)
       {
         //type 3: directory found, no need to search for a file!
         return_type = 3;
       }
       //Otherwise, we need to look for a file:
       else
       {
         //progress file pointer correctly:
         fseek(fp, BLOCK_SIZE * subDirectory -> nStartBlock, SEEK_SET);
         //Let's find the file within this directory:
         cs1550_directory_entry* subDirectory_temp = malloc(BLOCK_SIZE);
         fread(subDirectory_temp, BLOCK_SIZE, 1, fp);
         //Now look for the file:
         struct cs1550_file_directory* searchFile = NULL;
         int curr_file;
         for(curr_file = 0; curr_file < MAX_FILES_IN_DIR && found_file_flag == false; curr_file++)
         {
           //Does the current file & extension match the requested file and extension?
           if(!strcmp(subDirectory_temp -> files[curr_file].fname, filename) && !strcmp(subDirectory_temp -> files[curr_file].fext, extension))
           {
             //We found the file:
             searchFile = &(subDirectory_temp -> files[curr_file]);
             file_size = searchFile -> fsize;
             found_file_flag = true;
           }
         }
         //If we couldn't find the file:
         if(found_file_flag == false)
         {
           //type 4: file not found!
           return_type = 4;
         }
         //Otherwise the file was found:
         else
         {
           //type 5: file found!
           return_type = 5;
         }
       }
       //Close the file:
       fclose(fp);
     }
     else
     {
       //Directory is empty:
       return_type = 6;
     }
       
  }
  //Determine what to return based on return types:
  if(return_type == 1)
  {
    //Requested the root directory!
    stbuf->st_mode = S_IFDIR | 0755;
		stbuf->st_nlink = 2;
    return_value = 0;
  }
  else if(return_type == 2)
  {
    //Requested directory not found!
    return_value = -ENOENT;
  }
  else if(return_type == 3)
  {
    //Directory found!
    stbuf->st_mode = S_IFDIR | 0755;
    stbuf->st_nlink = 2;
    return_value = 0;
  }
  else if(return_type == 4)
  {
    //File not found!
    return_value = -ENOENT;
  }
  else if(return_type == 5)
  {
    //File found!
    //We found the correct file, so return the appropriate permissions and the actual size:
 		stbuf->st_mode = S_IFREG | 0666;
 		stbuf->st_nlink = 1; //file links
 		stbuf->st_size = file_size; //file size - make sure you replace with real size!
    return_value = 0;
  }
  else if(return_type == 6)
  {
    //Empty directory:
    return_value = -ENOENT;
  }
  return return_value;
}

/*
 * Called whenever the contents of a directory are desired. Could be from an 'ls'
 * or could even be when a user hits TAB to do autocompletion
 */
static int cs1550_readdir(const char *path, void *buf, fuse_fill_dir_t filler,
			 off_t offset, struct fuse_file_info *fi)
{
  //printf("im in readdir!!\n");
 	//Since we're building with -Wall (all warnings reported) we need
 	//to "use" every parameter, so let's just cast them to void to
 	//satisfy the compiler
 	(void) offset;
 	(void) fi;
 	
  unsigned long int file_length =  MAX_FILENAME + 1;
  unsigned long int extension_length = MAX_EXTENSION + 1;
  //How do we ge the directory, filenmae, and extension are valid
  //Before we do anything, check if the requested path is valid:
  char directory[file_length];
  char filename[file_length];
  char extension[extension_length];
  // printf("im not stuffed.\n");

  //Fill up the buffers:
  memset(directory, 0, file_length);
  memset(filename, 0, file_length);
  memset(extension, 0, extension_length);
  // printf("im stuffed.\n");
  bool found_dir = false;
  //Search for the subdirectory
  struct cs1550_directory* subDirectory = (struct cs1550_directory*)malloc(sizeof(struct cs1550_directory));
  //Determine if path represents a valid directory:
  sscanf(path, "/%[^/]/%[^.].%s", directory, filename, extension);
  int return_type = 0;
  int return_value = 0;
  
  //the filler function allows us to add entries to the listing
	//read the fuse.h file for a description (in the ../include dir)
	filler(buf, ".", NULL, 0);
	filler(buf, "..", NULL, 0);
  
  //Are we at the root?
  if(!strcmp(path, "/"))
  {
    //Get root directory:
    FILE* fp = fopen(".disk", "rb+");
    //Read in the root directory:
    cs1550_root_directory* rootDirectory = malloc(BLOCK_SIZE);
    fread(rootDirectory, BLOCK_SIZE, 1, fp);
    
    //Print out all subdirectories:
    int curr_subdir;
    for(curr_subdir = 0; curr_subdir < MAX_DIRS_IN_ROOT; curr_subdir++)
    {
      //we can only print out the subdirectory if the current subdirectory name is not null!!!
      if(strlen(rootDirectory -> directories[curr_subdir].dname) > 0)
      {
        filler(buf, rootDirectory -> directories[curr_subdir].dname, NULL, 0);
      }
    }
    //Close the file!
    fclose(fp);
    //Type I: if we were at the root:
    return_type = 1;
  }
  //Otherwise look for the subdirectory:
  else
  {
    //Search for directory:
    if(strlen(directory) > 0 && strlen(filename) == 0 && strlen(extension) == 0)
    {
      FILE* fp = fopen(".disk", "rb+");
      //Read in the root directory:
      cs1550_root_directory* rootDirectory = malloc(BLOCK_SIZE);
      fread(rootDirectory, BLOCK_SIZE, 1, fp);
      
      int curr_subdir;
      for(curr_subdir = 0; curr_subdir < MAX_DIRS_IN_ROOT && found_dir == false; curr_subdir++)
      {
        //Search for the specified subdirectory:
        if(!strcmp(rootDirectory -> directories[curr_subdir].dname, directory))
        {
          subDirectory = &(rootDirectory -> directories[curr_subdir]);
          found_dir = true;
        }
      }
      //Did we find the subdirectory?
      if(!found_dir)
      {
        //Type II: Couldn't find the subdirectory!!
        return_type = 2;
      }
      //Otherwise try to look for a file!!
      else
      {
        //progress file pointer correctly:
        fseek(fp, BLOCK_SIZE * subDirectory -> nStartBlock, SEEK_SET);
        //Let's find the file within this directory:
        cs1550_directory_entry* subDirectory_temp = malloc(BLOCK_SIZE);
        //Set the number of files:
        // subDirectory_temp -> nFiles = 0;
        fread(subDirectory_temp, BLOCK_SIZE, 1, fp);
        //Look for all files within the directory:
        int curr_file;
        
        char* newPath = malloc(file_length + extension_length);
        for(curr_file = 0; curr_file < subDirectory_temp -> nFiles; curr_file++)
        {
          //Generate the full path to print out!
          strcpy(newPath, subDirectory_temp -> files[curr_file].fname);
          strcat(newPath, ".");
          strcat(newPath, subDirectory_temp -> files[curr_file].fext);
          //Display the contents:
          filler(buf, newPath, NULL, 0);
        }
        //Type III: Found the file!
        return_type = 3;
      }
      //Close the file!
      fclose(fp);
    }
    else
    {
      //Otherwise we didn't request a sole directory!!
      return_type = 4;
    }
  }
  if(return_type == 1)
  {
    //Succesfully displayed all the contents in the root directory!!
    return_value = 0;
  }
  else if(return_value == 2)
  {
    //Couldn't find the directory!!
    return_value = -ENOENT;
  }
  else if(return_value == 3)
  {
    //Found the file!!
    return_value = 0;
  }
  else if(return_value == 4)
  {
    //Requested more than a directory!!
    return_value = -ENOENT;
  }
  return return_value;
}

/*
 * Creates a directory. We can ignore mode since we're not dealing with
 * permissions, as long as getattr returns appropriate ones for us.
 */
static int cs1550_mkdir(const char *path, mode_t mode)
{
	(void) path;
	(void) mode;

  unsigned long int file_length =  MAX_FILENAME + 1;
  unsigned long int extension_length = MAX_EXTENSION + 1;
  //How do we ge the directory, filenmae, and extension are valid
  //Before we do anything, check if the requested path is valid:
  char directory[file_length];
  char filename[file_length];
  char extension[extension_length];
  // printf("im not stuffed.\n");

  //Fill up the buffers:
  memset(directory, 0, file_length);
  memset(filename, 0, file_length);
  memset(extension, 0, extension_length);
  // printf("im stuffed.\n");
  bool found_dir = false;
  bool full_dir = false;
  //bool found_free_space = false;
  //Search for the subdirectory
  struct cs1550_directory* subDirectory = (struct cs1550_directory*)malloc(sizeof(struct cs1550_directory));
  memset(subDirectory, 0, sizeof(struct cs1550_directory));
  //Determine if path represents a valid directory:
  int num_scanned = sscanf(path, "/%[^/]/%[^.].%s", directory, filename, extension);
  bool not_directly_under_root = (num_scanned != 1);
  int return_type = 0;
  int return_value = 0;
  
  bool invalid_dir_name = (strlen(directory) > MAX_FILENAME);
  
  //Check if directory is under root:
  if(not_directly_under_root)
  {
    //Type I: Not directly under root:
    return_type = 1;
  }
  //Check if directory name exceeds max length:
  else if(invalid_dir_name)
  {
    //Type II: Invalid directory name:
    return_type = 2;
  }
  else
  {
    //Now check if the directory already exists in the root:
    //Get root directory:
    FILE* fp = fopen(".disk", "rb+");
    //Read in the root directory:
    cs1550_root_directory* rootDirectory = malloc(BLOCK_SIZE);
    fread(rootDirectory, BLOCK_SIZE, 1, fp);
    //Are we at full capacity??
    full_dir = (rootDirectory -> nDirectories == MAX_DIRS_IN_ROOT);
    //Now search through the subdirectories:
    int curr_subdir;
    for(curr_subdir = 0; curr_subdir < rootDirectory -> nDirectories && found_dir == false; curr_subdir++)
    {
      //If we find a directory with the same name as the requested directory: YAY
      if(!strcmp(rootDirectory -> directories[curr_subdir].dname, directory))
      {
        subDirectory = &(rootDirectory -> directories[curr_subdir]);
        found_dir = true;
      }
    }
    //If we found a directory with the same name as the one we request:
    if(found_dir)
    {
      fclose(fp);
      //Type III: Found a directory with the same name
      return_type = 3;
    }
    //If we didn't find a directory with the same name, we need to try and allocate space for the directory via a bit map:
    else
    {
      //If the directory is full, we can't add anything to it:
      if(full_dir)
      {
        fclose(fp);
        //Type IV: Directory is a maximimum capacity:
        return_type = 4;
      }
      //If directory is not full yet, try to add using bitmap:
      else
      {
        //Create bit map:
        // char* bitmap = (char*) malloc(MAX_SIZE); //Size courtesy of Henrique
        unsigned char bitmap[MAX_SIZE];
        //Seek to the right position to properly append to be able to read bit map contents in:
        fseek(fp, -sizeof(bitmap), SEEK_END);
        //Read in bitmap:
        fread(bitmap, sizeof(bitmap), 1, fp);
        
        //Try to find free space within bitmap:
        int curr_block = 0, curr_byte = 0, curr_bit = 0; //Get byte -> bit, then form mask to find free space:
        char bit_mask = 0; //Build up mask based on bits:
        
        //Iterate through the bit map:
        for(curr_block = 1; curr_block <= MAX_BLOCK_SIZE; curr_block++)
        {
          // int* bitmap_ptr = NULL; 
          // perform_bitmap_calculation(bitmap_ptr, curr_block);
          // //Extract current byte:
          curr_byte = curr_block / 8;
          curr_bit = curr_block % 8;
          // curr_byte = bitmap_ptr[0];
          // //Extract current bit:
          // curr_bit = bitmap_ptr[1];
          
          //Now get the mask based on this position:
          bit_mask = 0x80 >> curr_bit;
          //If we find sufficient free space:
          //if((*(bitmap + curr_byte) & bit_mask) == 0)
          if((bitmap[curr_byte] & bit_mask) == 0)
          {
            break;
          }
        }
        //What if the bitmap is full???
        if(curr_block == (MAX_BLOCK_SIZE + 1))
        {
          //Type V: Full bitmap
          fclose(fp);
          return_type = 5;
        }
        //Otherwise we need to update the bitmap accordingly:
        else
        {
          //Mark bits as used:
          //*(bitmap + curr_byte) |= bit_mask;
          bitmap[curr_byte] |= bit_mask;
          //Now update bitmap:
          //mkdir_update_disk(fp, bitmap, rootDirectory, directory, curr_block);
          fseek(fp, -sizeof(bitmap), SEEK_END);
	        fwrite(bitmap, sizeof(bitmap), 1, fp);
          
          //Update root directory values:
          strcpy(rootDirectory -> directories[rootDirectory -> nDirectories].dname, directory);
          rootDirectory -> directories[rootDirectory -> nDirectories].nStartBlock = curr_block;
          rootDirectory -> nDirectories++;
          //Update root directory in disk:
          fseek(fp, 0, SEEK_SET);
          fwrite(rootDirectory, sizeof(cs1550_root_directory), 1, fp);
          //Update new directory information:
          cs1550_directory_entry* new_subDirectory = malloc(sizeof(cs1550_directory_entry));
          memset(new_subDirectory, 0, BLOCK_SIZE);
          fseek(fp, BLOCK_SIZE * curr_block, SEEK_SET);
          //Write out new directory to disk:
          fwrite(new_subDirectory, sizeof(cs1550_directory_entry), 1, fp);
          
          //Close the file:
          fclose(fp);
          //Type VI: Successful append to bitmap and creation of directory:
          return_type = 6;
        }
      }
    }
  }
  //Now determine what value to return based on return type:
  if(return_type == 1)
  {
    return_value = -EPERM;
  }
  else if(return_type == 2)
  {
    return_value = -ENAMETOOLONG;
  }
  else if(return_type == 3)
  {
    return_value = -EEXIST;
  }
  else if(return_type == 4)
  {
    return_value = -ENOSPC;
  }
  else if(return_type == 5)
  {
    return_value = -EPERM;
  }
  else if(return_value == 6)
  {
    return_value = 0;
  }
  return return_value;
}



/*
 * Removes a directory.
 */
static int cs1550_rmdir(const char *path)
{
	(void) path;
    return 0;
}

/*
 * Does the actual creation of a file. Mode and dev can be ignored.
 *
 */
static int cs1550_mknod(const char *path, mode_t mode, dev_t dev)
{
	(void) mode;
	(void) dev;
  unsigned long int file_length =  MAX_FILENAME + 1;
  unsigned long int extension_length = MAX_EXTENSION + 1;
  //How do we ge the directory, filenmae, and extension are valid
  //Before we do anything, check if the requested path is valid:
  char directory[file_length];
  char filename[file_length];
  char extension[extension_length];

  //Fill up the buffers:
  memset(directory, 0, file_length);
  memset(filename, 0, file_length);
  memset(extension, 0, extension_length);
  bool found_dir = false;
  bool found_file_flag = false;
  //bool found_free_space = false;
  //Search for the subdirectory
  struct cs1550_directory* subDirectory = (struct cs1550_directory*)malloc(sizeof(struct cs1550_directory));
  memset(subDirectory, 0, sizeof(struct cs1550_directory));

  //Determine if path represents a valid directory:
  sscanf(path, "/%[^/]/%[^.].%s", directory, filename, extension);
  // bool not_directly_under_root = (num_scanned != 1);
  int return_type = 0;
  int return_value = 0; 
  int location = 0; //location of subdirectory on disk
  
  bool invalid_file_name = (strlen(filename) + strlen(extension) > (MAX_EXTENSION + MAX_FILENAME));
  //Check if the requested file falls within the bounds:
  if(invalid_file_name)
  {
    //Type I: File name not within bounds
    return_type = 1;
  }
  else if(!strcmp(path, "/"))
  {
    //If requested root:
    //Type 2: Requested root!!
    return_type = 2;
  }
  //Check to see if directory exists!!
  else
  {
    //Now check if the directory already exists in the root:
    //Get root directory:
    FILE* fp = fopen(".disk", "rb+");
    //Read in the root directory:
    cs1550_root_directory* rootDirectory = malloc(BLOCK_SIZE);
    fread(rootDirectory, BLOCK_SIZE, 1, fp);

    //Now search through the subdirectories:
    int curr_subdir;
    for(curr_subdir = 0; curr_subdir < rootDirectory -> nDirectories && found_dir == false; curr_subdir++)
    {
      //If we find a directory with the same name as the requested directory: YAY
      if(!strcmp(rootDirectory -> directories[curr_subdir].dname, directory))
      {
        location = rootDirectory -> directories[curr_subdir].nStartBlock;
        subDirectory = &(rootDirectory -> directories[curr_subdir]);
        found_dir = true;
      }
    }
    if(!found_dir)
    {
      fclose(fp);
      //Type III: Directory not found:
      return_type = 3;
      //-ENOENT
    }
    else
    {
      //Now try to see if the file exists:
      //progress file pointer correctly:
      fseek(fp, BLOCK_SIZE * subDirectory -> nStartBlock, SEEK_SET);
      //Let's find the file within this directory:
      cs1550_directory_entry* subDirectory_temp = malloc(BLOCK_SIZE);
      fread(subDirectory_temp, BLOCK_SIZE, 1, fp);
      //Now look for the file:
      int curr_file;
      for(curr_file = 0; curr_file < MAX_FILES_IN_DIR && found_file_flag == false; curr_file++)
      {
        //Does the current file & extension match the requested file and extension?
        if(!strcmp(subDirectory_temp -> files[curr_file].fname, filename) && !strcmp(subDirectory_temp -> files[curr_file].fext, extension))
        {
          //We found the file:
          found_file_flag = true;
        }
      }
      //If we found a file with the same name:
      if(found_file_flag)
      {
        fclose(fp);
        //Type IV: Found a file with the same name!
        return_type = 4;
      }
      else
      {
        //Allocate file using bit map:
        //Create bit map:
        // char* bitmap = (char*) malloc(MAX_SIZE); //Size courtesy of Henrique
        unsigned char bitmap[MAX_SIZE];
        //Seek to the right position to properly append to be able to read bit map contents in:
        fseek(fp, -sizeof(bitmap), SEEK_END);
        //Read in bitmap:
        fread(bitmap, sizeof(bitmap), 1, fp);
        
        //Try to find free space within bitmap:
        int curr_block = 0, curr_byte = 0, curr_bit = 0; //Get byte -> bit, then form mask to find free space:
        char bit_mask = 0; //Build up mask based on bits:
        
        //Iterate through the bit map:
        for(curr_block = 1; curr_block <= MAX_BLOCK_SIZE; curr_block++)
        {
          // int* bitmap_ptr = NULL; 
          // perform_bitmap_calculation(bitmap_ptr, curr_block);
          // //Extract current byte:
          // curr_byte = bitmap_ptr[0];
          // //Extract current bit:
          // curr_bit = bitmap_ptr[1];
          curr_byte = curr_block / 8;
          curr_bit = curr_block % 8;
          //Now get the mask based on this position:
          bit_mask = 0x80 >> curr_bit;
          //If we find sufficient free space:
          //if((*(bitmap + curr_byte) & bit_mask) == 0)
          if((bitmap[curr_byte] & bit_mask) == 0)
          {
            break;
          }
        }
        //What if the bitmap is full???
        if(curr_block == (MAX_BLOCK_SIZE + 1))
        {
          //Type V: Full bitmap
          fclose(fp);
          return_type = 5;
        }
        //Otherwise we need to update the bitmap accordingly:
        else
        {
          //Mark bits as used:
          //*(bitmap + curr_byte) |= bit_mask;
          bitmap[curr_byte] |= bit_mask;
          //Now update bitmap:
          mknod_update(bitmap, subDirectory_temp, filename, extension, curr_block, location, fp);
          
          //Close the file:
          fclose(fp);
          //Type VI: Successful append to bitmap and creation of file:
          return_type = 6;
        }
      }
    }
  }
  //Determine return value based on return types:
  if(return_type == 1)
  {
    return_value = -ENAMETOOLONG;
  }
  else if(return_type == 2)
  {
    return_value = -EPERM;
  }
  else if(return_type == 3)
  {
    return_value = -ENOENT;
  }
  else if(return_type == 4)
  {
    return_value = -EEXIST;
  }
  else if(return_type == 5)
  {
    return_value = -EPERM;
  }
  else if(return_type == 6)
  {
    return_value = 0;
  }
  return return_value;
}

/*
 * Deletes a file
 */
static int cs1550_unlink(const char *path)
{
    (void) path;
    return 0;
}


/*
 * Read size bytes from file into buf starting from offset
 *
 */
static int cs1550_read(const char *path, char *buf, size_t size, off_t offset,
			  struct fuse_file_info *fi)
{
	(void) fi;
  unsigned long int file_length =  MAX_FILENAME + 1;
  unsigned long int extension_length = MAX_EXTENSION + 1;
  //How do we ge the directory, filenmae, and extension are valid
  //Before we do anything, check if the requested path is valid:
  char directory[file_length];
  char filename[file_length];
  char extension[extension_length];

  //Fill up the buffers:
  memset(directory, 0, file_length);
  memset(filename, 0, file_length);
  memset(extension, 0, extension_length);
  bool found_dir = false;
  bool found_file_flag = false;
  //bool found_free_space = false;
  //Search for the subdirectory
  struct cs1550_directory* subDirectory = (struct cs1550_directory*)malloc(sizeof(struct cs1550_directory));
  memset(subDirectory, 0, sizeof(struct cs1550_directory));

  //Determine if path represents a valid directory:
  sscanf(path, "/%[^/]/%[^.].%s", directory, filename, extension);
  // bool not_directly_under_root = (num_scanned != 1);
  int return_type = 0;
  int return_value = 0; 
  //int location = 0; //location of subdirectory on disk
    
  //Are we trying to read a directory??
  if(strlen(directory) > 0 && strlen(filename) == 0)
  {
    //Type I: Path is a directory
    return_type = 1;
  }
  else
  {
    //Now check if the directory already exists in the root:
    //Get root directory:
    FILE* fp = fopen(".disk", "rb+");
    //Read in the root directory:
    cs1550_root_directory* rootDirectory = malloc(BLOCK_SIZE);
    fread(rootDirectory, BLOCK_SIZE, 1, fp);

    //Now search through the subdirectories:
    int curr_subdir;
    for(curr_subdir = 0; curr_subdir < rootDirectory -> nDirectories && found_dir == false; curr_subdir++)
    {
      //If we find a directory with the same name as the requested directory: YAY
      if(!strcmp(rootDirectory -> directories[curr_subdir].dname, directory))
      {
        //location = rootDirectory -> directories[curr_subdir].nStartBlock;
        subDirectory = &(rootDirectory -> directories[curr_subdir]);
        found_dir = true;
      }
    }
    if(!found_dir)
    {
      fclose(fp);
      //Type II: Directory not found
      return_type = 2;
    }
    else
    {
      //Find the file:
      //Now try to see if the file exists:
      //progress file pointer correctly:
      fseek(fp, BLOCK_SIZE * subDirectory -> nStartBlock, SEEK_SET);
      //Let's find the file within this directory:
      cs1550_directory_entry* subDirectory_temp = malloc(BLOCK_SIZE);
      struct cs1550_file_directory* searchFile = (struct cs1550_file_directory*)malloc(sizeof(struct cs1550_file_directory));
      fread(subDirectory_temp, BLOCK_SIZE, 1, fp);
      //Now look for the file:
      int curr_file;
      for(curr_file = 0; curr_file < MAX_FILES_IN_DIR && found_file_flag == false; curr_file++)
      {
        //Does the current file & extension match the requested file and extension?
        if(!strcmp(subDirectory_temp -> files[curr_file].fname, filename) && !strcmp(subDirectory_temp -> files[curr_file].fext, extension))
        {
          //We found the file:
          searchFile = &(subDirectory_temp -> files[curr_file]);
          found_file_flag = true;
        }
      }
      if(!found_file_flag)
      {
        fclose(fp);
        //Type III: File not found
        return_type = 3;
      }
      else
      {
        //Check if the file is within the correct bounds:
        //Is the offset less than the file size??
        if(offset > searchFile -> fsize)
        {
          fclose(fp);
          //Type IV: Offset exceeds file size!!
          return_type = 4;
        }
        else
        {
          //Otherwise read data on disk, and return the size of the file:
          fseek(fp, searchFile -> nIndexBlock * BLOCK_SIZE + offset, SEEK_SET);
          //Update size:
          if(size > (searchFile -> fsize - offset))
          {
            size = searchFile -> fsize - offset;
          }
          //Read in the data:
          fread(buf, size, 1, fp);
        }
      }
      //CLose the file pointer!!
      fclose(fp);
      //Type V: Successful read of file, so return the size of the file:
      return_type = 5;
    }
  }
  if(return_type == 1)
  {
    return_value = -EISDIR;
  }
  else if(return_type == 2)
  {
    return_value = -ENOENT;
  }
  else if(return_type == 3)
  {
    return_value = -ENOENT;
  }
  else if(return_type == 4)
  {
    return_value = -EFBIG;
  }
  else if(return_type == 5)
  {
    return_value = size;
  }
  return return_value;
}

/*
 * Write size bytes from buf into file starting from offset
 *
 */
static int cs1550_write(const char *path, const char *buf, size_t size,
			  off_t offset, struct fuse_file_info *fi)
{
	(void) buf;
	(void) offset;
	(void) fi;
	(void) path;

  unsigned long int file_length =  MAX_FILENAME + 1;
  unsigned long int extension_length = MAX_EXTENSION + 1;
  //How do we ge the directory, filenmae, and extension are valid
  //Before we do anything, check if the requested path is valid:
  char directory[file_length];
  char filename[file_length];
  char extension[extension_length];

  //Fill up the buffers:
  memset(directory, 0, file_length);
  memset(filename, 0, file_length);
  memset(extension, 0, extension_length);
  bool found_dir = false;
  bool found_file_flag = false;
  //bool found_free_space = false;
  //Search for the subdirectory
  struct cs1550_directory* subDirectory = (struct cs1550_directory*)malloc(sizeof(struct cs1550_directory));
  memset(subDirectory, 0, sizeof(struct cs1550_directory));

  //Determine if path represents a valid directory:
  sscanf(path, "/%[^/]/%[^.].%s", directory, filename, extension);
  // bool not_directly_under_root = (num_scanned != 1);
  int return_type = 0;
  int return_value = 0; 
  int location = 0;
  //Look for the directory:
  //Now check if the directory already exists in the root:
  //Get root directory:
  FILE* fp = fopen(".disk", "rb+");
  //Read in the root directory:
  cs1550_root_directory* rootDirectory = malloc(BLOCK_SIZE);
  fread(rootDirectory, BLOCK_SIZE, 1, fp);

  //Now search through the subdirectories:
  int curr_subdir;
  for(curr_subdir = 0; curr_subdir < rootDirectory -> nDirectories && found_dir == false; curr_subdir++)
  {
    //If we find a directory with the same name as the requested directory: YAY
    if(!strcmp(rootDirectory -> directories[curr_subdir].dname, directory))
    {
      location = rootDirectory -> directories[curr_subdir].nStartBlock;
      subDirectory = &(rootDirectory -> directories[curr_subdir]);
      found_dir = true;
    }
  }
  if(!found_dir)
  {
    fclose(fp);
    //Type I: Couldn't find the directory
    return_type = 1;
  }
  else
  {
    //Search for the file:
    //Find the file:
    //Now try to see if the file exists:
    //progress file pointer correctly:
    fseek(fp, BLOCK_SIZE * subDirectory -> nStartBlock, SEEK_SET);
    //Let's find the file within this directory:
    cs1550_directory_entry* subDirectory_temp = malloc(BLOCK_SIZE);
    struct cs1550_file_directory* searchFile = (struct cs1550_file_directory*)malloc(sizeof(struct cs1550_file_directory));
    fread(subDirectory_temp, BLOCK_SIZE, 1, fp);
    //Now look for the file:
    int curr_file;
    for(curr_file = 0; curr_file < MAX_FILES_IN_DIR && found_file_flag == false; curr_file++)
    {
      //Does the current file & extension match the requested file and extension?
      if(!strcmp(subDirectory_temp -> files[curr_file].fname, filename) && !strcmp(subDirectory_temp -> files[curr_file].fext, extension))
      {
        //We found the file:
        searchFile = &(subDirectory_temp -> files[curr_file]);
        found_file_flag = true;
      }
    }
    if(!found_file_flag)
    {
      fclose(fp);
      //Type II: Couldn't find the file
      return_type = 2;
    }
    else
    {
      //Write out the file (figure out where it is using the bit map!!)
      //Check if the file is within the correct bounds:
      //Is the offset less than the file size??
      if(offset > searchFile -> fsize)
      {
        fclose(fp);
        //Type III: Offset exceeds file size!!
        return_type = 3;
      }
      else
      {
        //Search bitmap for file:
        // char* bitmap = (char*) malloc(MAX_SIZE); //Size courtesy of Henrique
        unsigned char bitmap[MAX_SIZE];
        //Seek to the right position to properly append to be able to read bit map contents in:
        fseek(fp, -sizeof(bitmap), SEEK_END);
        //Read in bitmap:
        fread(bitmap, sizeof(bitmap), 1, fp);
        
        
        
        //Can only go through bitmap if the offset and size don't exceed the file size:
        if(offset + size < searchFile -> fsize)
        {
          //Try to find free space within bitmap:
          int curr_block = 0, curr_byte = 0, curr_bit = 0; //Get byte -> bit, then form mask to find free space:
          char bit_mask = 0; //Build up mask based on bits:
          //Do we need a new block?
          for(curr_block = searchFile -> fsize/BLOCK_SIZE + 1; curr_block <= (offset + size)/BLOCK_SIZE; curr_block++)
          {
            int new_alloc_location = searchFile -> nIndexBlock + curr_block;
            // int* bitmap_ptr = NULL; 
            // perform_bitmap_calculation(bitmap_ptr, new_alloc_location);
            //Extract current byte:
            // curr_byte = bitmap_ptr[0];
            //Extract current bit:
            // curr_bit = bitmap_ptr[1];
            curr_byte = new_alloc_location / 8;
            curr_bit = new_alloc_location % 8;
            //Now get the mask based on this position:
            bit_mask = 0x80 >> curr_bit;
            bitmap[curr_byte] |= bit_mask;
          }
        }
          //Mark bits as used:
          //Update bit map:
          fseek(fp, -sizeof(bitmap), SEEK_END);
          fwrite(bitmap, sizeof(bitmap), 1, fp);
          
          //Update directory:
          searchFile -> fsize = offset + size;
          fseek(fp, location * BLOCK_SIZE, SEEK_SET);
          fwrite(subDirectory_temp, BLOCK_SIZE, 1, fp);
          //Update file:
          fseek(fp, searchFile -> nIndexBlock * BLOCK_SIZE + offset, SEEK_SET);
          fwrite(buf, size, 1, fp);
          
          fclose(fp);
          //Type IV: Properly wrote file and directory to disk
          return_type = 4;
          
        }
      
      }
    }
  //Determine return values based on return types:
  if(return_type == 1)
  {
    return_value = -ENOENT;
  }
  else if(return_type == 2)
  {
    return_value = -ENOENT;
  }
  else if(return_type == 3)
  {
    return_value = -EFBIG;
  }
  else if(return_type == 4)
  {
    return_value = size;
  }
  return return_value;
}

/******************************************************************************
 *
 *  DO NOT MODIFY ANYTHING BELOW THIS LINE
 *
 *****************************************************************************/

/*
 * truncate is called when a new file is created (with a 0 size) or when an
 * existing file is made shorter. We're not handling deleting files or
 * truncating existing ones, so all we need to do here is to initialize
 * the appropriate directory entry.
 *
 */
static int cs1550_truncate(const char *path, off_t size)
{
	(void) path;
	(void) size;

    return 0;
}


/*
 * Called when we open a file
 *
 */
static int cs1550_open(const char *path, struct fuse_file_info *fi)
{
	(void) path;
	(void) fi;
    /*
        //if we can't find the desired file, return an error
        return -ENOENT;
    */

    //It's not really necessary for this project to anything in open

    /* We're not going to worry about permissions for this project, but
	   if we were and we don't have them to the file we should return an error

        return -EACCES;
    */

    return 0; //success!
}

/*
 * Called when close is called on a file descriptor, but because it might
 * have been dup'ed, this isn't a guarantee we won't ever need the file
 * again. For us, return success simply to avoid the unimplemented error
 * in the debug log.
 */
static int cs1550_flush (const char *path , struct fuse_file_info *fi)
{
	(void) path;
	(void) fi;

	return 0; //success!
}

/* Thanks to Mohammad Hasanzadeh Mofrad (@moh18) for these
   two functions */
static void * cs1550_init(struct fuse_conn_info* fi)
{
	  (void) fi;
    printf("We're all gonna live from here ....\n");
		return NULL;
}

static void cs1550_destroy(void* args)
{
		(void) args;
    printf("... and die like a boss here\n");
}


//register our new functions as the implementations of the syscalls
static struct fuse_operations hello_oper = {
    .getattr	= cs1550_getattr,
    .readdir	= cs1550_readdir,
    .mkdir	= cs1550_mkdir,
		.rmdir = cs1550_rmdir,
    .read	= cs1550_read,
    .write	= cs1550_write,
		.mknod	= cs1550_mknod,
		.unlink = cs1550_unlink,
		.truncate = cs1550_truncate,
		.flush = cs1550_flush,
		.open	= cs1550_open,
		.init = cs1550_init,
    .destroy = cs1550_destroy,
};

//Don't change this.
int main(int argc, char *argv[])
{
	return fuse_main(argc, argv, &hello_oper, NULL);
}