#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <stdbool.h>
#include <dlfcn.h>

unsigned long (*compressBound)(unsigned long length); 
int (*compress)(void *dest, unsigned long* destLen, const void* source, unsigned long sourceLen);
int (*uncompress)(void *dest, unsigned long* destLen, const void* source, unsigned long sourceLen);

int streq_nocase(const char* a, const char* b) 
{
	for(; *a && *b; a++, b++) if(tolower(*a) != tolower(*b)) return 0;
	return *a == 0 && *b == 0;
}
int sufficient_args(int argc)
{
    if(argc < 3)
    {
        printf("insufficient arguments");
        exit(1);
    }
    return 1;
}

void* load_lib()
{
    void* lib = dlopen("libz.so", RTLD_NOW);
    if(lib == NULL)
    {   
        printf("Uh oh, I couldn't find the library!");
    }   
    return lib;
}
void load_compressBound(void* lib)
{
	compressBound = dlsym(lib, "compressBound");
	
	if(compressBound == NULL)
	{
		printf("compressBound could not load!\n");
		exit(1);
	}
}
void load_compress(void* lib)
{
	compress = dlsym(lib, "compress");
	
	if(compress == NULL)
	{
		printf("compress could not load!\n");
		exit(1);
	}
}
void load_uncompress(void* lib)
{
	uncompress = dlsym(lib, "uncompress");
	
	if(uncompress == NULL)
	{
		printf("uncompress could not load!\n");
		exit(1);
	}
}

FILE* validFile(const char* file_name, const char* mode)
{
    FILE* file = fopen(file_name, mode);

	if(file == NULL) 
	{
		fprintf(stderr, "Could not open file '%s'\n", file_name);
		exit(1);
	}
	return file;
}

int main(int argc, char* argv[])
{
    
    if(sufficient_args(argc))
    {
        void* zlib = load_lib();
        if(zlib != NULL)
        {
            load_compressBound(zlib); //compressBound
            load_compress(zlib); //compress
            load_uncompress(zlib); //uncompress

            FILE* binaryFile = validFile(argv[2], "rb");

            unsigned long uncompressedFileSize;
            unsigned long compressedSize;
            int uncompressedFileResult;
            int compressedFileResult;
            char* input_buffer;
            char* output_buffer;
            if(streq_nocase(argv[1], "-c"))
            {
                fseek(binaryFile, 0, SEEK_END); 
                uncompressedFileSize = ftell(binaryFile);
                fseek(binaryFile,0, SEEK_SET);

                input_buffer = malloc(uncompressedFileSize);
                fread(input_buffer, uncompressedFileSize, 1, binaryFile);
            
                compressedSize = compressBound(uncompressedFileSize);
                output_buffer = malloc(compressedSize);

                compressedFileResult = compress(output_buffer, &compressedSize, input_buffer, uncompressedFileSize);
                if(compressedFileResult < 0)
                {
                    printf("Failed to compress the BMP file...");
                    exit(1);
                }
        
                fwrite(&uncompressedFileSize, sizeof(unsigned long), 1, stdout);
                fwrite(&compressedSize, sizeof(unsigned long), 1, stdout);
                fwrite(output_buffer, compressedSize, 1, stdout);

            }
            else if(streq_nocase(argv[1],"-d"))
            {   
                fseek(binaryFile, 0, SEEK_END); 
                uncompressedFileSize = ftell(binaryFile);
                fseek(binaryFile,0, SEEK_SET); 

                fread(&uncompressedFileSize, sizeof(unsigned long), 1, binaryFile);
                
                compressedSize = compressBound(uncompressedFileSize);
                fread(&compressedSize, sizeof(unsigned long), 1, binaryFile);
                
                input_buffer = malloc(compressedSize);
                fread(input_buffer, compressedSize, 1, binaryFile);

                output_buffer = malloc(uncompressedFileSize);
                
                uncompressedFileResult = uncompress(output_buffer, &uncompressedFileSize, input_buffer, compressedSize);
                if(uncompressedFileResult < 0)
                {
                    printf("Failed to uncompress the BMP file...");
                    exit(1);
                }

                fwrite(output_buffer, uncompressedFileSize, 1, stdout);

            }
            else
            {
                printf("Unrecognized flag");        
                return 0;
            }

            fflush(stdout);
            free(input_buffer);
			free(output_buffer);
			fclose(binaryFile);
        }
    }
    return 0;
    
}



    