import subprocess
#raw input is deprecated in python3...
fHand = raw_input("Enter the name of the Java File: ")
args = raw_input("Enter any desired arguments: ")
num_iterations = int(raw_input("Enter the number of simulations: "))
substr = fHand.index('.')

for i in range(num_iterations):
    if args is "0":
        cmd = "javac " + fHand + " && java " + fHand[0:substr] #splicing be cool!
    else:
        cmd = "javac " + fHand + " && java " + fHand[0:substr] + " " + args
    returned_value = subprocess.call(cmd, shell=True)  # returns the exit code in unix
print("Successfully executed " + cmd)

