main:
	lui	$a0,0x8000	# should be 31	31	1000 0000 0000 0000 0000 0000 0000 0000
	jal	first1pos
	jal	printv0
	lui	$a0,0x0001	# should be 16
	jal	first1pos
	jal	printv0
	li	$a0,1		# should be 0	0000 0000 0000 0000 0000 0000 0000 0001
	jal	first1pos
	jal	printv0
	add	$a0,$0,$0
	jal	first1pos
	jal	printv0
	li	$v0,10
	syscall


first1pos:	# first1pos
	addiu	$sp, $sp, -4
	sw	$ra, 0($sp)
	jal	first1posshift
	lw	$ra, 0($sp)
	addiu	$sp, $sp, 4
	jr	$ra


first1posshift:
	addi	$sp,$sp,-4
	sw	$s0,0($sp)	
	addi 	$s0,$zero,31
loop:
	beq	$a0,$zero,retNegOne
	slt	$t0,$a0,$zero
	bne	$t0,$zero,leftBitGot
	addi	$s0,$s0,-1
	sll	$a0,$a0,1
	j loop	
retNegOne:
	li	$v0,-1	
	lw	$s0,0($sp)
	addi	$sp,$sp,4
	jr 	$ra
leftBitGot:
	move 	$v0,$s0
	lw	$s0,0($sp)
	addi	$sp,$sp,4
	jr 	$ra

printv0:
	addi	$sp,$sp,-4
	sw	$ra,0($sp)
	add	$a0,$v0,$0
	li	$v0,1
	syscall
	li	$v0,11
	li	$a0,'\n'
	syscall
	lw	$ra,0($sp)
	addi	$sp,$sp,4
	jr	$ra
