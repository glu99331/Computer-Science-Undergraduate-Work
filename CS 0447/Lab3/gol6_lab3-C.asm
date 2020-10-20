.data
myArray: .word 0, 4, -3, 5, 2, -1, 6, 15, -8, 1

prompt1: .asciiz "Enter an index (between 0 - 9): "
prompt2: .asciiz "Enter another index (between 0 - 9): "
 
index1: .word 0
index2: .word 0 
sum: .asciiz "The sum of the two numbers in the specified array indices is: "
addition: .asciiz " + "
equals: .asciiz " = "

  # Declare your variables here
  # // add your code //

.text
.globl main
main:

la $s0, myArray #load array into register s0

la $a0, prompt1 #print messagex
li $v0, 4
syscall

li $v0, 11     # a = printChar('\n')
li $a0, '\n'
syscall

li $v0, 5
syscall
sw $v0,index1 #storing input into index1 variable

li $t1, 4
mult $t1, $v0 
mflo $t1	#t2 = 4*v0

li $v0, 11     # a = printChar('\n')
li $a0, '\n'
syscall

la $a0, prompt2 #print message
li $v0, 4
syscall


li $v0, 11     # a = printChar('\n')
li $a0, '\n'
syscall

li $v0, 5
syscall
sw $v0, index2 #storing input into index2 variable

li $t5, 4
mult $t5, $v0 
mflo $t5	#t2 = 4*v0

li $v0, 11     # a = printChar('\n')
li $a0, '\n'
syscall


add $t3, $t1, $s0
lw $t4, 0($t3)


add $t7, $t5, $s0
lw $t8, 0($t7)

la $a0, sum #print message
li $v0, 4
syscall

li $v0, 11     # a = printChar('\n')
li $a0, '\n'
syscall

add $a0, $t4, $zero
li $v0, 1
syscall

la $a0, addition #print message
li $v0, 4
syscall


add $a0, $t8, $zero
li $v0, 1
syscall

la $a0, equals #print message
li $v0, 4
syscall


add $t9, $t8, $t4
add $a0, $t9, $zero
li $v0, 1
syscall




  # Ask for two indices
  # Make it friendly!! Print out a prompt so people know what to do! :)
  # // add your code //

  # Write your code here to retrieve each value from the array and sum them
  # // add your code //

  # Print the result
  # // add your code //

  # Exit the program with the exit syscall
  li  $v0, 10
  syscall
