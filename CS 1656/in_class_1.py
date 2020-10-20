print("Hello, world!")

a = 3
b = 2*a
print(type(a))
b #on an interpreter, like a print statement ----> just like in R

print(b)
a*b

b = 'hello'
type(b)

print(b + b) #just a concatenation

print(5*b) #EW

3/2

print(3//2)

3/2.

####Lists####
#just an array!!!
l = ['red', 'white', 'blue', 'gold', 'black', 'pink']
type(l)

print(l[-1]) #from index 0, go back 1
print(l[-2])
# print(l[15]) #cardinal sin >:(, out of range!
# print(l[-7]) #ALSO BAD

#SLICING
#l[start:stop] -> range from start to i-1
print(l[0:3]) #prints elems 0 to 2
print(l[2:]) #print all elements starting from index 2
# print(l[::-1]) #reverse

#lists are mutable
print(l)
l[3] = 'yellow'
print(l)

l[2:4] = ['light blue', 'gold'] #change elements 2 and 3, modifying a slice!
print(l)

l[1] = '42'
print(l)

l.append('green') #add, lists are dynamic? so a list is just an array list
print(l)

print(l.pop()) #print elem at end of list
print(l)

l.extend(['one', 'two']) #can add an array!
print(l)

l = l[:-2] #delete last two elements
print(l)

m = ['alice', 'bob', 'cathy', 'dre']
print(m)

n = l + m
print(n) #add two lists together!

####STRINGS####
s = 'Hello, how are you?'
s = "Hi, what's up?"
#tripling quotes allows the string to span more than one line
s = '''Hello,
how are you'''

astr = 'abcdefghij.def'
print(astr)
print(astr[3:6])
print(astr[3:])

#Strings are immutable
#astr[2] = 'z' #RUNTIME ERROR
print(astr)

print(astr)
print(astr.replace('d', "#", 1)) #replace 1 instance of d with '#', by the order you see it appears in the string
print(astr)

print(astr.replace('d', '#')) #replace ALL instances of d in the string with '#'

fs = 'An integer: %i; a float: %f; another string: %s' % (42, 0.01, 'alice') #format specifier!
print(fs)

i = 102
filename = 'processing_of_dataset_%d.txt' % i 
filename

#####DICTIONARIES#####
#Hash Maps!
courses = {'cs1555':'intro to database systems', 'cs1520':'web programming', 'cs1501':'algorithm implementation'}

courses['cs1656'] = 'intro to data science'
print(courses)
print(courses['cs1555'])

print(courses.keys())
print(courses.values())

#Existence
print('cs1656' in courses)

#mixed types in dictionaries:
mixed = {'a':1, 'b':2, 3:'hello'}
print(mixed)

tweet = {
    'user' : 'alex',
    'text' : 'cs1656 today feasures an intro to python',
    'retweet_count' : 3,
    'hashtags' : ["#datascience", "#python", "#pitt"]
}
print(tweet)
print(tweet["text"])
print(tweet["hashtags"])
print(tweet["hashtags"][1])

####TUPLES####
#Immutable Lists!!!
t = 12345, 54321, 'hello', 'there!'
print(t[0])
print(t)
u = (0,2)
print(u)

#u[1] = 6 #<--- not allowed!!!
#print(u)

my_list = [1,2]
my_tuple = (3,4)
also_tup = 5,6
my_list[1] = 12
try:
    my_tuple[1] = 13
except TypeError:
    print("cannot modify a tuple")


####SETS####
#Unordered, unique
s = set(('a', 'b', 'c', 'a'))
print(s)
print(s.difference(('a', 'b'))) #A set minus B


####ASSIGNMENTS#####
a = 'banana'
b = 'banana'
print(a is b) #YAY

c = [1,2,3]
d = c
print(c is d)
d[0] = 'z'
print(d)
print(c)
#not a copy, a POINTER
d = ['z', 2, 3]
print(c is d) #now not pointing at same memory location
print(c == d) #same value, but not same location in memory

d = c
d[:] = [1,2,3,4,5]
print(id(c))
print(id(d))

####CONTROL FLOW####
if 2**2 == 4: #2^2 <--> Math.pow(2,2)
    print("TADA!")

a = 10
if a==1:
    print(1)
elif a==2:
    print(2)
else:
    print('more than 2')
print(range(6))

for idx in range(4): #print from 0 to 3
    print(idx)
for word in ['alice', 'bob', 'cathy', 'dolores', 'emily']:
    print(word)
for word in ('alice', 'bob', 'cathy', 'dolores', 'emily'):
    print(word)


ar = {'a':1, 'b':2.2, 'c': 'third'}

#treat pairs of things as a data structure using tuples!
for key, val in ar.items():
    print('Key: %s has value: %s' %(key, val))
#Above enumerates through all (k,v) pairs in dictionary
#enumerate sorted
for key, val in sorted(ar.items()):
    print('Key: %s has value: %s' %(key, val))

print([i**2 for i in range(5)]) 
print([x for x in range(25) if x%2 == 0]) #even nums between 0 and 24 

###FIVE WORD COUNTING EXAMPLES###
doc_string = """Alma Mater, wise and glorious,
Child of Light and Bride of Truth,
Over fate and foe victorious, 
Dowered with eternal tough,
Crowned with love of son and daughter,
Thou shalt conquer as of yore,
Dear old Pittsburgh, Alma Mater,
God preserve Thee everymore!"""
doc_words = doc_string.split() #split by spaces
print(doc_words)

##Print data structures a little cleaner!!
import pprint
pp = pprint.PrettyPrinter(indent=4)

#version 1
word_counts = {}
for word in doc_words:
    if word in word_counts: #check for existence
        word_counts[word] += 1
    else:
        word_counts[word] = 1 #code is a bit too verbose, too long!!!
pp.pprint(word_counts)

#version 2
word_counts = {}
for word in doc_words:
    try:
        word_counts[word] += 1
    except KeyError:
        print("EXCEPTION")
        word_counts[word] = 1
pp.pprint(word_counts)

#version 3
word_counts = {}
for word in doc_words:
    previous_count = word_counts.get(word, 0)
    word_counts[word] = previous_count + 1
pp.pprint(word_counts)

#version 4
from collections import defaultdict
word_counts = defaultdict(int)
for word in doc_words:
    word_counts[word] += 1
pp.pprint(word_counts)

#version 5
from collections import Counter
word_counts = Counter(doc_words)
pp.pprint(word_counts)

###FUNCTIONS###
def squared(x=5):
    '''returns square of the input parameter, with default value 5 for parameter
    NOTE: Default values are evaluated when function is defined, NOT when called'''

    return x*x
print(squared(12))
print(squared())

#using tuples to return multiple values
def sum_and_product(x,y):
    return (x+y), (x*y)

sp = sum_and_product(1, 10)
print(sp)
s,p = sum_and_product(1, 10) #s initialized to first res, second res for p
print(s)
print(p)
print(s,p)

###Pass by value
def try_to_modify(x, y, z):
    x = 23
    y.append(42)
    z = [992] #new ref
    print('x is', x)
    print('y is', y)
    print('z is', z)

a = 77 #immutable
b = [99] #mutable
c = [28] #mutable

print("BEFORE")
print('a is', a)
print('b is', b)
print('c is', c)

print("\nINSIDE")
try_to_modify(a,b,c)

print("\nAFTER")
print('a is', a)
print('b is', b) #append fine
print('c is', c) #but keep original data from c if we try to assign it to another memory reference

###Global Variable###
##Scope Rules
x = 5
def addx(y):
    return x+y

print(addx(10))

#SCOPE (cont)
x = 5
def setx(y):
    x = y #assignment
    print('x is %d' % x)
print(x)
setx(12)
print(x)

#SCOPE (cont 2)
x = [5]
def setxx(y):
    x[0] = y
    print('x is %d' % x[0])
    print('x is %d' % x)
print(x[0])
setxx([10])
print(x[0])

#SCOPE (cont 2)
x = 5
def sety(y):
    print
    print('x is %d' % x[0])
    print('x is %d' % x)
print(x[0])
setxx([10])
print(x[0])
