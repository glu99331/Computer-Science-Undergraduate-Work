.data
  str: .asciiz "What is the first value?\n"

  str2: .asciiz "What is the second value?\n"
  # First Input
  a:   .word 0
  a1:  .word 0
.text
.globl main
main:
  la $a0, str    # printString(str)
  li $v0, 4
  syscall

  li $v0, 5      # a = getInteger()
  syscall
  sw $v0, a

  li $v0, 11     # a = printChar('\n')
  li $a0, '\n'
  syscall

  li $v0, 1      # a = printInteger(a)
  lw $a0, a
  syscall

  li $v0, 11     # a = printChar('\n')
  li $a0, '\n'
  syscall
 
  li $v0, 4
  la $a0, str2
  syscall
  
  li $v0, 5
  la $a0, a1
  syscall
  
  li $v0, 11     # a = printChar('\n')
  li $a0, '\n'
  syscall
  
  li $v0, 1      # a = printInteger(a)
  lw $a0, a1
  syscall
  
   
  li $v0, 10     # exit() - stops the program
  syscall