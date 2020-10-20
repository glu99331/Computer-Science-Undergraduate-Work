#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <stdbool.h>
#include <stdint.h>

typedef struct Wav{
   char riff_id[4];
   uint32_t file_size;
   char wave_id[4];
   char fmt_id[4];
   uint32_t fmt_size;
   uint16_t data_format;
   uint16_t number_of_channels;
   uint32_t samples_per_second;
   uint32_t bytes_per_second;
   uint16_t block_alignment;
   uint16_t bits_per_sample;
   char data_id[4];
   uint32_t data_size; 

}WAVHeader;
//ansi colors, underlining and bolding text
//im just tryna have fun with c, man...
void red () {
  printf("\033[0;31m");
}
void bold_red(){
   printf("\033[1;31m");
}
void blue () {
  printf("\033[0;34m");
}
void green () {
  printf("\033[0;32m");
}
void bold_green()
{
   printf("\033[1;32m");	
}
void cyan()
{
   printf("\033[0;36m");
}
void bold_underline_cyan()
{
      printf("\033[1;36m\e[4m");
}
void bold_underline_green()
{
   printf("\033[1;32m\e[4m");
}
void reset_plus_underline()
{
   printf("\033[0m\e");
}
void reset () {
  printf("\033[0m");
}
void displayMessage() //let's make the display menu colorful!!
{
   green();
   printf("----------------------------------------------------------------------------\n\t\t");
   bold_underline_green();
   printf("Usage:");
   cyan();
   printf(" ./wavedit ");
   red();
   printf("[FILE] ");
   green();
   printf("[OPTION] ");
   blue();
   printf("[RATE]\n");
   reset();
   printf("Perform simple operations on a WAV file, or simply read a WAV File.\n\n");
   bold_underline_cyan();
   printf("Flags:");
   green();
   printf("\n-reverse");
   reset();
   printf("\t\t\tReverse the audio of the WAV file.\n");
   green();
   printf("-rate");
   red();
   printf(" x\t\t\t\t");
   reset();
   printf("Modify the speed of the WAV file by 'x' Hz.\t\t\t\n");
   printf("\t\t\t\t");
   bold_underline_green();
   printf("Note:");
   reset_plus_underline();
   printf("  'x' must be an integer between:");
   printf("\033[1;32m"	);
   printf("\n\t\t\t\t\t1 and 192000 inclusive.");    
   printf("\033[0m");
   bold_underline_green();
   printf("\nExamples:");
   reset();
   printf(" \t\t\t\t\t\t\t\t\t\t\t     \n");
   cyan();
   printf("./wavedit ");
   red();
   printf("f.wav ");
   green();
   printf("-reverse");
   reset();
   printf("\tReverse the sound of f.\t\t\t\t\t\t     \n");
   cyan();
   printf("./wavedit ");
   red();
   printf("f.wav ");
   green();
   printf("-rate ");
   blue();
   printf("22350");
   reset();
   printf("\tModifies speed of f to 22350Hz.\t\t\t\t\t     \n");
   green();
   printf("----------------------------------------------------------------------------\n");
   reset();
   printf("With no file specified, display this chunk of information and exit.\t\t\t\t     \n\t\t\t\t\t\t\t\t\t\t\t\t     \n");
   cyan();
   printf("With no operations specified...\t\t\t\t\t\t\t\t     \n");
   red();
   printf("-Check that the file exists.\t\t\t\t\t\t\t\t\t     \n");
   green();
   printf("-Verify it is a WAV file.\t\t\t\t\t\t\t\t\t     \n");
   blue();
   printf("-Display the file's information.\t\t\t\t\t\t\t\t     \n");
   printf("\t\t\t\t\t\t\t\t\t\t\t\t     \n");
   bold_underline_green();
   printf("Examples:");
   reset();
   printf("\t\t\t\t\t\t\t\t\t\t\t     \n");
   cyan();
   printf("./wavedit");
   reset();
   printf("\t\t\tDisplay this chunk of information.\t\t\t\t     \n");
   cyan();
   printf("./wavedit ");
   red();
   printf("f.wav\t\t");
   red();
   printf("\tCheck that f exists, ");
   green();
   printf("verify it is a WAV file,\n");
   blue();
   printf("\t\t\t\t\tand display f's content.\n");
   green();
   printf("----------------------------------------------------------------------------\n");
   reset();
}
void printErrorMsg()
{
    red();
    printf("Error. Invalid argument.");
    green();
    printf("\nEnter ");
    cyan();
    printf("./wavedit ");
    green();
    printf("to see a list of valid inputs.\n");
    reset();
}
bool file_exists(FILE* file)
{
   if(file == NULL)
   {
      printErrorMsg();
      return false;
   }
   return true;
} 

bool contains_correct_id(WAVHeader file)
{
   if(strncmp(file.riff_id, "RIFF", 4) == 0 && strncmp(file.wave_id, "WAVE", 4) == 0 && strncmp(file.fmt_id, "fmt ", 4) == 0 && strncmp(file.data_id, "data", 4) == 0)
   {
      return true;
   }
  return false;
}
bool contains_correct_other_values(WAVHeader file)
{
   if(((file.fmt_size == 16) && (file.data_format == 1) && (file.number_of_channels == 1 || file.number_of_channels == 2) && (file.samples_per_second > 0 && file.samples_per_second <= 192000) && (file.bits_per_sample == 8 || file.bits_per_sample == 16) && (file.bytes_per_second == (file.samples_per_second * ((file.bits_per_sample)/8)) * file.number_of_channels) && ((file.block_alignment == ((file.bits_per_sample)/8)*file.number_of_channels))))
   {
      return true;
   }
   
  return false;
}

bool is_wav_file(WAVHeader file)
{
   if(contains_correct_id(file) && contains_correct_other_values(file))
   {
      return true;
   }
   return false;
} 

void print_wav_file_info(WAVHeader file)
{
   int samples = (file.data_size)/(file.block_alignment);
   float seconds = (float)(samples)/(file.samples_per_second);
   if(file.number_of_channels == 1)
   {
      printf("This is a %u-bit %uHz mono sound.\nIt is %d samples (%.3f seconds) long.\n", file.bits_per_sample, file.samples_per_second, samples, seconds);
   }
   else
   {
      printf("This is a %u-bit %uHz stereo sound.\nIt is %d samples (%.3f seconds) long.\n", file.bits_per_sample, file.samples_per_second, samples, seconds);
   }
}
void calculateInputRate(int rate, WAVHeader wav, FILE* file, char** argv)
{
    fread(&wav, sizeof(wav),1, file);
    if(is_wav_file(wav) == true)
    {
       printf("Initial sample rate: %d\n", wav.samples_per_second);
       wav.samples_per_second = rate;
       printf("New sample rate: %d\n", wav.samples_per_second);
       printf("Initial byte rate: %d\n", wav.bytes_per_second);
       wav.bytes_per_second = (wav.samples_per_second)*((wav.bits_per_sample)/8) * wav.number_of_channels;
       printf("New byte rate: %d\n", wav.bytes_per_second);
       fseek(file, 0, SEEK_SET);
       fwrite(&wav, sizeof(wav), 1, file);
       fclose(file);
    }
    else
    {
       printf("Error. %s is not a valid file.", argv[1]);
    }
}
void reverseAudio(WAVHeader wav, FILE* wav_file, int samples)
{
   if(wav.bits_per_sample == 8 && wav.number_of_channels == 1) 
      {
         uint8_t arr[samples]; //treat the data as an array of 8-bit integers!!
         fread(&arr, sizeof(uint8_t)*samples,1, wav_file); //read the data into the 8-bit array!
         int j = samples - 1; //start j-index at the end of the array -1
         for(int i =0; i < samples; i++) //starting from i = 0 ...
         {
            
            if(i >= j){break;} //if i and j are equal or if i crosses j ...
            
            uint8_t swapr = arr[i]; //create another unsigned int, "swapr" to hold the reversed values
            arr[i] = arr[j]; //swap values!!
            arr[j] = swapr; 
          
            j--; //decrement j
         }
         fseek(wav_file, sizeof(WAVHeader),SEEK_SET); //seek to the beginning of the file plus header, since header contains file properties
         fwrite(&arr, sizeof(uint8_t)*samples, 1, wav_file); //write array back into the file
         fclose(wav_file); //close it!

      }
      else if((wav.bits_per_sample == 16 && wav.number_of_channels == 1) || (wav.bits_per_sample == 8 && wav.number_of_channels == 2))
      {
         uint16_t arr[samples];
         fread(&arr, sizeof(uint16_t)*samples,1, wav_file);
         int j = samples - 1;
         for(int i =0; i < samples; i++)
         {
            if(i >= j){break;}
            uint16_t swapr = arr[i];
            arr[i] = arr[j];
            arr[j] = swapr;
           
            j--;
         }
         fseek(wav_file, sizeof(WAVHeader),SEEK_SET);
         fwrite(&arr, sizeof(uint16_t)*samples, 1, wav_file);
         fclose(wav_file);
      
      }
      else if((wav.bits_per_sample == 16 && wav.number_of_channels == 2))
      {
         uint32_t arr[samples];
         fread(&arr, sizeof(uint32_t)*samples,1, wav_file);
         int j = samples - 1;
         for(int i =0; i < samples; i++)
         {
            if(i >= j){break;}
            uint32_t swapr = arr[i];
            arr[i] = arr[j];
            arr[j] = swapr;
            
            j--;
         }
         fseek(wav_file, sizeof(WAVHeader),SEEK_SET);
         fwrite(&arr, sizeof(uint32_t)*samples, 1, wav_file);
         fclose(wav_file);
      
      }
}
int main( int argc, char *argv[] )  {
      FILE* wav_file;   
      WAVHeader wav;

   if(argc == 1)
   {
      displayMessage();
   }
   else if(argc == 2)
   {
      wav_file = fopen(argv[1], "rb");
      if(file_exists(wav_file) == false)
      {
         return 0;
      }
      else
      {
         fread(&wav, sizeof(wav),1, wav_file);
         if(is_wav_file(wav) == true)
         {
            print_wav_file_info(wav);
            fclose(wav_file);
         }
      }
   }
   else if(argc ==3)
   {
      if(strcmp(argv[2], "-reverse") == 0) 
      {
            wav_file = fopen(argv[1], "rb+"); //open the file for reading and writing a binary file
            fread(&wav, sizeof(wav),1, wav_file); //read it to the WAVHeader 
            
            if(is_wav_file(wav) == true) //if the file is a wav file...
            {
               int samples = (wav.data_size)/(wav.block_alignment); //calculare samples as before
               reverseAudio(wav, wav_file, samples);
            }
            else
            {
               printf("Error. %s is not a valid file.", argv[1]);
            }
      }
      else
      {
         printErrorMsg();
      }
   }
   else if(argc == 4)
   {
      int rate = atoi(argv[3]);
      if( strcmp(argv[2],"-rate") == 0 && (rate > 0 && rate <= 192000))
      {
            wav_file = fopen(argv[1], "rb+");
            calculateInputRate(rate, wav, wav_file, argv);  
      }
      else
      {
         printErrorMsg();
      }
     
   }
   
}
