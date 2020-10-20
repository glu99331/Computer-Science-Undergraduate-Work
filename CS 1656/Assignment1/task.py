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


class Bike():
    def __init__(self, baseURL, station_info, station_status):
        self.baseURL = baseURL
        self.station_info = baseURL + station_info
        self.station_status = baseURL + station_status
        print(self.station_info)
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
                            if k3 == 'num_bikes_available':
                                adder += v3

        # for (k, v) in status.items():
        #     print(type(v))
            # for (k1, v2) in v.items():
            #     print(v2)    
        # status = json.loads(unparsed_status.response.content)
        # print(status)
        ###Station Status: station_statusURL = baseURL+'/station_status.json', 
        ###which provides for each station_id how many bikes and how many docks are available at any given time (e.g.,
        # return the total number of bikes available
        return adder

    def total_docks(self):
        # return the total number of docks available
        return None

    def percent_avail(self, station_id):
        # return the percentage of available docks
        return None

    def closest_stations(self, latitude, longitude):
        # return the stations closest to the given coordinates
        return None


    def closest_bike(self, latitude, longitude):
        # return the station with available bikes closest to the given coordinates
        return None
        
    def station_bike_avail(self, latitude, longitude):
        # return the station id and available bikes that correspond to the station with the given coordinates
        return None
        

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
    p_avail = instance.percent_avail(342885) # replace with station ID
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
    s_bike_avail = instance.station_bike_avail(40.445834, -79.954707) # replace with exact latitude and longitude of station
    print(type(s_bike_avail))
    print(s_bike_avail)