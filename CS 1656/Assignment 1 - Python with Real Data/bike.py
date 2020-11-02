import argparse
import collections
import csv
import json
import glob
import math
import os
import pandas
import re
import requests
import string
import sys
import time
import xml
from requests import get

#Lu, Gordon: CS 1656 Fall 2020 Assignment I -> Passes Gradescope!
class Bike():
    def __init__(self, baseURL, station_info, station_status):
        self.baseURL = baseURL
        self.station_info = baseURL + station_info
        self.station_status = baseURL + station_status

        # The program conatins a class, Bike, that has three arguments in its constructor, 
        # baseURL, station_info and station_status. 
        
        # These arguments are used to define URLs for specific data feeds, 
        # namely information about individual stations and the status of every station. 
        # You can create an instance of the Bike class by calling its constructor with appropriate URL fragments, 
        # and call its methods to run the different parts of the assignment.
        # initialize the instance
        pass

    def total_bikes(self):
        unparsed_status = get(self.station_status, verify=False)
        status = json.loads(unparsed_status.content)
        adder = 0
        for k,v in status.items(): 
            if type(v) is dict:
                for(k2, v2) in v.items():
                    for lsts in v2:
                        for (k3, v3) in lsts.items():
                            if k3 == 'num_bikes_available': #Everytime we encounter a key with 'num_bikes_available'
                                adder += v3 #add it to the running sum
        #unpack result from json 
        ###Station Status: station_statusURL = baseURL+'/station_status.json', 
        # return the total number of bikes available
        return adder

    def total_docks(self):
        # return the total number of docks available
        #same as total_bikes but with total_docks
        unparsed_status = get(self.station_status, verify=False)
        status = json.loads(unparsed_status.content)
        adder = 0
        for k,v in status.items():
            if type(v) is dict:
                for(k2, v2) in v.items():
                    for lsts in v2:
                        for (k3, v3) in lsts.items():
                            if k3 == 'num_docks_available':
                                adder += v3
        return adder

    def percent_avail(self, station_id):
        # return the percentage of available docks

        # The method percent_avail will compute and return how many docks are 
        # currently available for the specified station as a percentage over 
        # the total number of bikes and docks available. 
        
        # In this case, the station_id is given as a parameter.

        matched_stations = []
        unparsed_status = get(self.station_status, verify=False)
        status = json.loads(unparsed_status.content)
        found = 0
        for k,v in status.items():
            if type(v) is dict:
                for(k2, v2) in v.items():
                    for lsts in v2:
                        for (k3, v3) in lsts.items():
                            if k3 == 'station_id':
                                if int(v3) == station_id:
                                    matched_stations.append(lsts)
        bikes_avail = 0.0
        docks_avail = 0.0
        for i in matched_stations:
            for (k4, v4) in i.items():
                if k4 == 'num_bikes_available':
                    bikes_avail = int(v4)
                elif k4 == 'num_docks_available':
                    docks_avail = int(v4)        
        if bikes_avail == 0 and docks_avail == 0:
            return '' #both zero -> OOF
        s1 = math.floor(float(docks_avail / (docks_avail + bikes_avail)) * 100)
        # for dictionary in lsts:
        return '' + str(s1) + '%'
    def closest_stations(self, latitude, longitude):
        # return the stations closest to the given coordinates
        distances = []
        tups = []
        unparsed_status = get(self.station_info, verify=False)
        status = json.loads(unparsed_status.content)
        for k,v in status.items():
            if type(v) is dict:
                for(k2, v2) in v.items():
                    for lsts in v2:                            
                        num_items = 0
                        lst = []                             
                        for (k3, v3) in lsts.items():
                            if k3 == 'lat':
                                lst.append(float(v3))
                                num_items += 1
                            elif k3 == 'lon':
                                lst.append(float(v3))
                                num_items += 1
                            else:
                                if num_items == 2:
                                    lst.append(lsts)
                                    # print(lst)
                                    tups.append(tuple(lst))
                                    break
        #enumerate through tuples:
        # The variable result should have a dictionary value of strings mapped to integers like
        #  {342885: 'Schenley Dr at Schenley Plaza (Carnegie Library Main)', 342887: 'Fifth Ave & S Dithridge St', 
        #  342882: 'Fifth Ave & S Bouquet St'}.

        # Note that in order to compute distances between two points, you must use the provided distance() 
        # method of the class. The distance method takes four arguments, lat1, 
        # lon1, lat2 and lon2 that correspond to two latitude-longitude coordinate pairs, 
        # and returns the distance between the two points.

        for tup in tups:
            lat2 = float(tup[0])
            lon2 = float(tup[1])
            #calculate distance
            temp_lst = []
            temp_lst.append(self.distance(latitude, longitude, lat2, lon2))
            temp_lst.append(tup[2])
            # print(tup[2])
            distances.append(tuple(temp_lst))
        distances.sort(key=lambda x: x[0]) #sort by second value in tuple:
        shortest_three = {}
        max_three = []
        for tup in distances[0:3]:
            max_three.append(tup[1])
        #sort distances 
        tup_three = tuple(max_three)
        # print(tup_three)
        for dicts in tup_three: #enumerate through top three shortest distances
            curr_name = ''
            curr_station = 0
            for (k, v) in dicts.items():
                if k == 'station_id':
                    station_id = v
                elif k == 'name':
                    name = v
            shortest_three[station_id] = name #add to dictionary
        return shortest_three

    def closest_bike(self, latitude, longitude):
        # return the station with available bikes closest to the given coordinates

        distances = []
        tups = []
        unparsed_status = get(self.station_info, verify=False)
        status = json.loads(unparsed_status.content)
        for k,v in status.items():
            if type(v) is dict:
                for(k2, v2) in v.items():
                    for lsts in v2:                            
                        num_items = 0
                        lst = []                             
                        for (k3, v3) in lsts.items():
                            if k3 == 'lat':
                                lst.append(float(v3))
                                num_items += 1
                            elif k3 == 'lon':
                                lst.append(float(v3))
                                num_items += 1
                            else:
                                if num_items == 2:
                                    lst.append(lsts)
                                    # print(lst)
                                    tups.append(tuple(lst))
                                    break
        #enumerate through tuples:
        # The variable result should have a dictionary value of strings mapped to integers like
        #  {342885: 'Schenley Dr at Schenley Plaza (Carnegie Library Main)', 342887: 'Fifth Ave & S Dithridge St', 
        #  342882: 'Fifth Ave & S Bouquet St'}.

        # Note that in order to compute distances between two points, you must use the provided distance() 
        # method of the class. The distance method takes four arguments, lat1, 
        # lon1, lat2 and lon2 that correspond to two latitude-longitude coordinate pairs, 
        # and returns the distance between the two points.
        id_lst = []
        for tup in tups:
            lat2 = float(tup[0])
            lon2 = float(tup[1])
            #calculate distance
            temp_lst = []
            temp_lst.append(self.distance(latitude, longitude, lat2, lon2))
            temp_lst.append(tup[2])

            id_tuple = [] #list of ids, probably unnecessary
            id_tuple.append(self.distance(latitude, longitude, lat2, lon2))
            id_tuple.append(tup[2]['station_id'])

            id_lst.append(tuple(id_tuple))
            distances.append(tuple(temp_lst))
        distances.sort(key=lambda x: x[0]) #sort the distances tuple based on distance:
        id_lst.sort(key=lambda x: x[0]) #sort the id tuples based on distance
        #also make a mapping between ids and distances, so we can retrieve the dictionary based on distance -> inadvertently implying
        #we have a indirect pointer from ids to dictionaries

        # The method closest_bike will return the station_id and the name of the closest HealthyRidePGH station that has 
        # available bikes, given a specific latitude and longitude. The first parameter is the latitude and 
        # the second parameter is the longitude.
        closest = {}
        
        unparsed_info = get(self.station_status, verify=False)
        info = json.loads(unparsed_info.content)
        max_indx = 0 #need to be able to wrap around the array
        bikes_avail = 0 
        found = {}
        found_station = False
        current_station_max = id_lst[max_indx][1] #need to increment the index each time
        for k,v in info.items():
            if type(v) is dict:
                for(k2, v2) in v.items():
                        for lsts in v2:
                            for (k3, v3) in lsts.items():
                                if(bikes_avail == 0):
                                    ###CASE I: Found matching station id:
                                    if k3 == 'station_id' and v3 == current_station_max and found_station == False:
                                        found_station = True
                                        station_id = current_station_max
                                    ###CASE II: station found, so look for corresponding number of bikes!!
                                    elif found_station == True:
                                        for (k4, v4) in lsts.items():
                                            ##Find the number of bikes available
                                            if(k4 == 'num_bikes_available' and int(v4) > 0):
                                                bikes_avail = int(v4)
                                                break
                                        if bikes_avail == 0: ##couldn't find bike so dive further and use next shortest distance!!
                                            max_indx += 1
                                            current_station_max = id_lst[max_indx][1] #update current_station max
                                else:
                                    break #buh bye
        for tup in distances: #enumerate through all tuples in the distances list 
            for val in tup: 
                if type(val) == dict: #find matching station id and name
                    if(val['station_id'] == current_station_max):
                        good_boi = val
        closest[current_station_max] = good_boi['name'] #issa good boi!
        return closest

        #look for closest, make sure that station id's match and 

        # return None
        
    def station_bike_avail(self, latitude, longitude):
        # return the station id and available bikes that correspond to the station with the given coordinates

        # The method station_bike_avail will return the station_id and the number of bikes available at the station, given a specific latitude and longitude. 
        
        # The first parameter is the latitude and the second parameter is the longitude.

        # Sample invocation:

        # result = instance.station_bike_avail(40.444618, -79.954707)
        # The variable result should have a dictionary value of integers mapped to integers like {342887: 4}. 
        # The dictionary must have a single item that corresponds to the station with the exact coordinates. 
        coords = []
        tups = []
        unparsed_status = get(self.station_info, verify=False)
        status = json.loads(unparsed_status.content)
        for k,v in status.items():
            if type(v) is dict:
                for(k2, v2) in v.items():
                    for lsts in v2:                            
                        num_items = 0
                        lst = []                             
                        for (k3, v3) in lsts.items():
                            if k3 == 'lat':
                                lst.append(float(v3))
                                num_items += 1
                            elif k3 == 'lon':
                                lst.append(float(v3))
                                num_items += 1
                            else:
                                if num_items == 2:
                                    lst.append(lsts)
                                    # print(lst)
                                    tups.append(tuple(lst))
                                    break
        to_find = ''
        for tup in tups:
            lat2 = float(tup[0])
            lon2 = float(tup[1])
            if lat2 == latitude and longitude == lon2:
                to_find = tup[2]['station_id'] #add station id
        if to_find == '': #couldn't find it so return empty dictionary
            return {}
        found_station = {}
        unparsed_info = get(self.station_status, verify=False)
        info = json.loads(unparsed_info.content)
        lst_found = dict()
        for k,v in info.items():
            if type(v) is dict:
                for(k2, v2) in v.items():
                    for lsts in v2:
                        for (k3, v3) in lsts.items():
                            if k3 == 'station_id' and v3 == to_find:
                                lst_found = lsts
        for (k4, v4) in lst_found.items():
            if k4 == 'num_bikes_available':
                found_station[to_find] = int(v4)
        # In this example, the result is the station with ID 342887 that has 4 bikes available. 
        
        # Also, if a station with the exact given coordinates doesn't exist, you must return an empty dictionary.
        
        return found_station
        

    def distance(self, lat1, lon1, lat2, lon2):
        p = 0.017453292519943295
        a = 0.5 - math.cos((lat2-lat1)*p)/2 + math.cos(lat1*p)*math.cos(lat2*p) * (1-math.cos((lon2-lon1)*p)) / 2
        return 12742 * math.asin(math.sqrt(a))
# testing and debugging the Bike class

if __name__ == '__main__':
    instance = Bike('https://api.nextbike.net/maps/gbfs/v1/nextbike_pp/en', '/station_information.json', '/station_status.json')
    print('------------------total_bikes()-------------------')
    t_bikes = instance.total_bikes()
    print(type(t_bikes))
    print(t_bikes)
    print()

    print('------------------total_docks()-------------------')
    t_docks = instance.total_docks()
    print(type(t_docks))
    print(t_docks)
    print()

    print('-----------------percent_avail()------------------')
    p_avail = instance.percent_avail(342849) # replace with station ID
    print(type(p_avail))
    print(p_avail)
    print()

    print('----------------closest_stations()----------------')
    c_stations = instance.closest_stations(40.444618, -79.954707) # replace with latitude and longitude
    print(type(c_stations))
    print(c_stations)
    print()

    print('-----------------closest_bike()-------------------')
    c_bike = instance.closest_bike(40.444618, -79.954707) # replace with latitude and longitude
    print(type(c_bike))
    print(c_bike)
    print()

    print('---------------station_bike_avail()---------------')
    s_bike_avail = instance.station_bike_avail(40.445834, -80.008882) # replace with exact latitude and longitude of station
    print(type(s_bike_avail))
    print(s_bike_avail)