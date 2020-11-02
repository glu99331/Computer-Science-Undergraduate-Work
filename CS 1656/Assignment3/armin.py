from collections import defaultdict
from pandas import Series, DataFrame
import itertools as it
import pandas as pd
import math
import csv
import sys
import argparse
import collections
import glob
import os
import re
import requests
import string
import sys
#Lu, Gordon: CS 1656 Fall 2020 Assignment 3 -> Passes Gradescope!
class Armin():
    # Item names could consist of either numbers (0-9) or characters (a-zA-z) or 
    # combinations of numbers and characters. 
    
    # No spaces or puncuation characters are allowed in item names.
    
    # The CSV files may or may not contain whitespace between values.

    # This process starts with CFI(1) being all individual items and terminates on Step k, 
    # when CFI(k + 1) is empty.
    # The above process generates all the frequent itemsets, i.e., VFI(i), for 1 <= i <= k. 
    # For every frequent itemset we need to generate all possible association rules and keep 
    # only the rules whose support is greater or equal to the min support percentage and their 
    # confidence is greater or equal to the min confidence. 
    
    # To generate all possible rules from a frequent itemset, we generate all possible 2-partitions 
    # of the itemset (one will be the left-hand-side of the association rule and the other will be 
    # the right-hand-side), where neither partition is empty. 
    
    # For example, if {A,B,C} is a frequent itemset, then we should check the following association rules:
    def apriori(self, input_filename, output_filename, min_support_percentage, min_confidence):
        """
        Implement the Apriori algorithm, and write the result to an output file
        PARAMS
        ------
        input_filename: String, the name of the input file
        output_filename: String, the name of the output file
        min_support_percentage: float, minimum support percentage for an itemset
        min_confidence: float, minimum confidence for an association rule to be significant
        """
        # • Consider all the candidate frequent itemsets of size i. Let’s name them CFI(i).
        # • Count how many times each itemset in CFI(i) appears in our input data. This is the support count, which is turned into the support percentage by dividing with the total number of transactions.
        # • The itemsets in CFI(i) whose support percentage is at least as much as the min support percentage become the verified frequent itemsets, or VFI(i).
        # • Using itemsets in VFI(i) generate all plausible candidate itemsets of size +1, i.e., CFI(i + 1). This makes use of the subset property. For example, for ABC to be in CFI(3), all of AB, BC, and AB need to be in VFI(2).
        content = []
        freq_alphabet = dict.fromkeys(string.ascii_uppercase, 0) #Hash Map of ASCII KEYS
        punctuation = '''!()-[]{};:'"\,<>./?@#$%^&*_~''' #TO BE DONE LATER
        with open(input_filename) as fhand:
            content = fhand.readlines()
        item_list = []
        for elem in content: 
            #find first instance of ',', start retrieving from element after:
            item_list.append(elem[elem.index(',')+1:])
        filter_newlines = list(map(lambda x:x.strip(),item_list))
        for s in filter_newlines:
            for c in s:
                if c in freq_alphabet: #check if in key set
                    freq_alphabet[c] += 1
        tids = []
        for lst in content:
            tids.append(lst[:lst.index(',')]) #add all tids:
        #print(tids) #SANITY CHECK
        attributes = []
        merger = []
        for s in filter_newlines:
            attributes.append(list(filter(lambda x: x != ',', s))) #filter commas out:
        for num_arrs in range(len(tids)): #merge two arrays for tids and attributes together
            merger.append(list(tids[num_arrs]) + attributes[num_arrs])
        #print(merger) #SANITY CHECK
        #print(item_set)
        relevant_chars = []
        for key,val in freq_alphabet.items():
            if val >= 1: #occurs at least one time:
                relevant_chars.append(key)
        #print(relevant_chars)
        alphabet_as_char = ''.join(relevant_chars)
        permList = it.permutations(alphabet_as_char) 
        # print all permutations 
        # for perm in list(permList): 
        #     print (''.join(perm)) 
        combination_string_list = []
        # for i in list(permList):
        for j in range(len(alphabet_as_char)+1): #produce all possibilities 
            for combination in it.combinations(alphabet_as_char, j): 
                temp_str = '' 
                for k in combination:
                    temp_str += k #form combination string
                if(j >= 1):
                    combination_string_list.append(temp_str)    
        #then for each list in merger, create all possible combinations as strings. then use a map
        #to count the number of instances.
        combination_string_list
        support_counts = dict.fromkeys(combination_string_list, 0) #Hash Map of combo strings!!
        merger_no_tids = []
        for i in range(len(merger)):
            merger_no_tids.append(list(x for x in merger[i] if not x.isdigit())) #filter tids out
        #now form all lists into a single string
        mergers_as_strings = []
        for i in range(len(merger_no_tids)):
            mergers_as_strings.append(''.join(merger_no_tids[i]))
        #now form all possible combinations per string:
        for curr_s in mergers_as_strings:
            for i in range(len(curr_s)+1): #produce all possibilities 
                for combination in it.combinations(curr_s, i): 
                    temp_str = '' 
                    for j in combination:
                        temp_str += j #form combination string
                    if(i >= 1):
                        support_counts[temp_str] += 1 #increase frequency if combination appears
        total_count = float(len(tids)) #tids list contains all possibles tids
        support_perc = {}
        for key, val in support_counts.items(): #compute support percentages
            support_perc[key] = val/total_count
        #print(support_perc)    
        frequent_itemsets = {}
        for key, val in support_perc.items():
            if val >= min_support_percentage:

                frequent_itemsets[key] = val
        # print(frequent_itemsets)
        # generate all possible candidate itemsets of size + 1:
        # build map, mapping size to counts:
        # size_map = {}
        # #print(len(relevant_chars))
        # for i  in range(len(relevant_chars)):
        #     lst = []
        #     for k,v in frequent_itemsets.items():
        #         if len(k) == i:
        #             lst.append(k)
        #     size_map[i] = lst
        #print(size_map)
        candidate_frequent_sets = []
        # print(frequent_itemsets)
        #now generate all plausible candidate itemsets of size + 1
        #enumerate through the possible alphabet, to find candidate itemsets:
        #print(combination_string_list)
        #start_from_two = list(x for x in combination_string_list if len(x) >= 2) #start with size 2 strings
        permute_frequent_sets = []
        # for k,v in frequent_itemsets.items():
        #     permList = it.permutations(k)
        #     for perm in list(permList): 
        #         permute_frequent_sets.append(''.join(perm))
        permuter = list(dict.fromkeys(combination_string_list + permute_frequent_sets)) #filter out dupes when adding two lists together
        start_from_two = list(x for x in permuter if len(x) >= 2) #start with size 2 strings
        # print(start_from_two)
        combo_to_two = {} #get counts for size-1 strings to start_from_two
        # #but we have to do this for the frequent itemsets:
        for i in range(len(start_from_two)):
            actual = []
            for j in range(len(combination_string_list)):
                count = 0
                if len(combination_string_list[j]) == len(start_from_two[i]) - 1 and combination_string_list[j] != start_from_two[i]:
                    temp_str = ''
                    for c in combination_string_list[j]:
                        if c in start_from_two[i]:
                            temp_str += c
                    # print(temp_str)        
                    for char in temp_str:
                        if char in start_from_two[i]:
                            count += 1
                    #print(combination_string_list[j], start_from_two[i])
                    if count == len(combination_string_list[j]):
                        actual.append(temp_str)
                        #print(temp_str)
            combo_to_two[start_from_two[i]] = actual
        #print(combo_to_two)
        #print(frequent_itemsets)
        #now iterate through map:
        for k,v in combo_to_two.items():
            #for all the values in the map:
            count = 0
            for i in range(len(v)):
                #look at all verified frequent itemsets
                for s in frequent_itemsets:
                    if v[i] == s:
                        count += 1
                if count == len(v):
                    candidate_frequent_sets.append(k)
        #print(frequent_itemsets)
        #print(candidate_frequent_sets)    
        candidate_association_rules = {}
        # permList = it.permutations('ABD') 
        # # print all permutations 
        # for perm in list(permList): 
        #     print (''.join(perm))
        

        #BA => C is same as AB => C because the left is a permutation of the other one
        # for i in range(len(candidate_frequent_sets)):
        #     curr_left = #start with singletons
        #     curr_right
        #How to generate association rules:
        #12 combinations, 6 permutations
        #compute size i on left, with size k - i on the right, where k is the total number of items for the itemset
        #and continue...
        #put a label around the size of the sets:
        size_map = {}
        for candidate in candidate_frequent_sets:
            #if certain size append for that key:
            if len(candidate) in size_map:
                lst = size_map.get(len(candidate))
                lst.append(candidate)
                size_map[len(candidate)] = lst
            else:
                lst = []
                lst.append(candidate)
                size_map[len(candidate)] = lst
        #print(candidate_frequent_sets)
        #print(candidate_frequent_sets)
        unfiltered_candidate_association_rules = {}
        for candidate in candidate_frequent_sets:
            permList = it.permutations(candidate)
            #print("The permutations of", candidate, 'are:')
            arrow_pos = 1
            arrow = '=>'
            #retrieve size:
            size = len(candidate)
            for perm in permList:
                #arrow loc is i+1
                size = len(perm)
                # lst = []
                for i in range(len(perm)):
                    #print(unfiltered_candidate_association_rules)
                    if candidate in unfiltered_candidate_association_rules:
                        lst = unfiltered_candidate_association_rules.get(candidate)
                        lst.append(''.join(perm[:i]) + arrow + ''.join(perm[i:])) 
                        unfiltered_candidate_association_rules[candidate] = lst
                    else:                           
                        lst = [] 
                        lst.append(''.join(perm[:i]) + arrow + ''.join(perm[i:]))
                        unfiltered_candidate_association_rules[candidate] = lst
        # look at unfiltered rules, get rid of generated rules with empty left side
        # print(unfiltered_candidate_association_rules)
        for k,v in unfiltered_candidate_association_rules.items():
            for val in v:
                if val[:val.index('=>')] == '':
                    v.remove(val)

        deletion_map = {}
        # print('BEFORE:\n', unfiltered_candidate_association_rules)

        #Handles case when the lhs is permutation of another of '=>'
        for k,v in unfiltered_candidate_association_rules.items():
            for val in v:
                substr = val[:val.index('=>')]
                temp = ''.join(sorted(val[:val.index('=>')])) + val[val.index('=>'):]
                #search in parallel:
                for val2 in v:
                    substr2 = val2[:val2.index('=>')]
                    temp2 = ''.join(sorted(val2[:val2.index('=>')])) + val2[val2.index('=>'):]
                    if temp == temp2 and substr != substr2:
                        v.remove(val2)
        # print('AFTER:\n', unfiltered_candidate_association_rules)
        #Now try to handle the case when the rhs is permutation of another, and lhs are the same:
        for k,v in unfiltered_candidate_association_rules.items():
            for val in v:
                l_substr = val[:val.index('=>')]
                r_substr = val[val.index('=>')+2:]
                temp = l_substr + val[val.index('=>'):val.index('=>')+2] + ''.join(sorted(r_substr))
                #search in parallel:
                for val2 in v:
                    l_substr2 = val2[:val2.index('=>')]
                    r_substr2 = val2[val2.index('=>')+2:]
                    temp2 = l_substr2 + val2[val2.index('=>'):val2.index('=>')+2] + ''.join(sorted(r_substr2))
                    #if lhs match, and rhs are permutations of one another
                    if l_substr == l_substr2 and temp == temp2 and r_substr != r_substr2: 
                        v.remove(val2)
        # print('AFTER ANOTHER FILTER:\n', unfiltered_candidate_association_rules)
        #now filter out any keys in the candidate association rules, if it is not a frequent item set:
        for k in list(unfiltered_candidate_association_rules.keys()):
                if k not in frequent_itemsets:
                    #remove key
                    try:
                        del(unfiltered_candidate_association_rules[k])
                    except KeyError:
                        pass
        #compute the confidence of each of the association rules:
        association_confidence_map = {}
        # print('support percs:\n', support_perc)
        # print('FULLY FILTERED:\n', unfiltered_candidate_association_rules)
        #dictionary of strings -> list of dictionaries:
        confidence_map = {}

        for k,v in unfiltered_candidate_association_rules.items():
            for val in v:
                #compute conf as: sup(I U J)/I, where I => J
                l_substr = val[:val.index('=>')]
                r_substr = val[val.index('=>')+2:]
                union = l_substr + r_substr
                # union_support_perc = 0.0
                # lhs_support_perc = 0.0
                #check if perm is in list:
                union_perms = it.permutations(union)
                for perm in union_perms:
                    if ''.join(perm) in support_perc:
                        #perm matches one of the support counts:
                        union_support_perc = support_perc.get(''.join(perm))
                l_substr_perms = it.permutations(l_substr)
                for perm in l_substr_perms:
                    if ''.join(perm) in support_perc:
                        #perm matches one of the support counts:
                        lhs_support_perc = support_perc.get(''.join(perm))
                #compute confidence:
                conf = union_support_perc/lhs_support_perc
                #want something like this:
                #{'AB': [{'AB' -> conf}, {'ABD' -> conf}]}
                if(conf >= min_confidence):
                    if k not in confidence_map: #key not in confidence map, so add it:
                        #if the key is not in the map, we must add the first dictionary to the tuple:
                        lst = []
                        vals = {} 
                        vals[val] = float("{:.4f}".format(conf))
                        lst.append(vals)
                        confidence_map[k] = lst
                    else: #key is in map, so add to the pre-existing list:
                        lst = confidence_map.get(k)
                        vals = {}
                        vals[val] = float("{:.4f}".format(conf))
                        lst.append(vals)
                        confidence_map[k] = lst
                        #append to inner dictionary
                        #want to add list of dictionaries as value to key
                        #check if value is in dictionary:
        support_out = dict(sorted(frequent_itemsets.items(), key = lambda k: len(k)))
        tmp = []
        for k,v in confidence_map.items():
            for val in v:
                tmp.append(val)
        # print(tmp)
        isolated_rules = {k:v for element in tmp for k,v in element.items()}
        #now we have to sort lexicographically:
        # lexicographic_isolated_rules = dict(sorted(isolated_rules.items(), key = lambda k: (k[:k.index('=>')], len(k[:k.index('=>')]))))
        # print(isolated_rules) #now we've isolated the association rule:
        # for k,v in isolated_rules.items():
        #     print(len(k.split('=>')[0]), k)
        # for i, (k,v) in enumerate(sorted(isolated_rules.items())):
        #     print(isolated_rules[i])
        res = dict(sorted(isolated_rules.items(), key = lambda k: (k[0].split('=>')[0])))
        # print(dict(sorted(isolated_rules.items(), key = lambda k: (k[0].split('=>')[0]))))
        confidence_prelim = dict(sorted(res.items(), key = lambda k: (len(k[0].split('=>')[0]))))
        with open(output_filename, "w+") as file1:
            #write out frequent item sets
            for k,v in support_out.items():
                if len(k) > 1: #add commas
                    test = ','.join(k)
                    file1.write("S,{:.4f},{}\n".format(v, test))
                else: #length of string is 1
                    file1.write("S,{:.4f},{}\n".format(v, k))
            for k,v in confidence_prelim.items():
                #now look inside of unfileted_candidate_association_rules:
                for k2,v2 in unfiltered_candidate_association_rules.items():
                    #look inside each respective list:
                    for vals in v2:
                        if k == vals:
                            test = ','.join(vals[:vals.index('=>')])
                            test += ",'=>'," 
                            test += ','.join(vals[vals.index('=>')+2:])
                            s = "R,{:.4f},{:.4f},{}".format(frequent_itemsets[k2], isolated_rules[vals], test)
                            file1.write(s+'\n')                            
        
        pass
   
if __name__ == "__main__":
    armin = Armin()
    armin.apriori('input.csv', 'output.sup=0.5,conf=0.7.csv', 0.5, 0.7)
    armin.apriori('input.csv', 'output.sup=0.5,conf=0.8.csv', 0.5, 0.8)
    armin.apriori('input.csv', 'output.sup=0.6,conf=0.8.csv', 0.6, 0.8)