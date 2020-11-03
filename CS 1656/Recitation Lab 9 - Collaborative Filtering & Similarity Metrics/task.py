import json
from datetime import datetime, timedelta
import requests
import pandas as pd
import numpy as np
from scipy.spatial.distance import euclidean, cityblock, cosine
import ssl
ssl._create_default_https_context = ssl._create_unverified_context

#Lu, Gordon: CS 1656 Fall 2020 Lab 9 -> Passes Gradescope!
class Task(object):
    def __init__(self, data):
        self.df = pd.read_csv(data)
    def t1(self, name):
        sim_weights = {}

        for user in self.df.columns[~self.df.columns.isin([name, 'Alias'])]: #for all columns except name and movie titles
            df_subset = self.df[[name,user]][self.df[name].notnull() & self.df[user].notnull()] #subset with current user and name
            #what if two users have weights that are NaN -> doesn't occur in BabyKangaroo example
            dist = cosine(df_subset[name], df_subset[user]) #compute distance
            sim_weights[user] = 1.0 / (1.0 + dist) #add to weight
        lst = []
        for i in range(len(self.df['Alias'])): #for every movie
            curr_alias = self.df['Alias'][i]
            predicted_rating = 0.0
            weights_sum = 0.0
            ratings = self.df.iloc[i][self.df.columns[~self.df.columns.isin([name, 'Alias'])]]  #retrieve rating
        # ratings = df.iloc[0][1:-1]
            for user in self.df.columns[~self.df.columns.isin([name, 'Alias'])]: #for all users except one we want to predict
                if (not np.isnan(ratings[user])): #if nan, then predict
                    predicted_rating += ratings[user] * sim_weights[user] 
                    weights_sum += sim_weights[user]
            predicted_rating /= weights_sum
            dumb = self.df.iloc[i][self.df.columns[self.df.columns.isin([name])]]
            if(np.isnan(dumb[name])): #now output 
                tlst = []
                tlst.append(curr_alias)
                tlst.append(predicted_rating)
                tup = tuple(tlst)
                lst.append(tup)
        # predicted_rating /= weights_sum
        return lst

    def t2(self, name):
        sim_weights = {}
        #Make sure who rated goodmovie1 also rated badmovie1
        bad_movies = self.df.loc[(self.df[name].isnull())]['Alias'] 
        #for every missing movie
        for bad in bad_movies:
            sim_movie = {}
            for movie in self.df['Alias'][self.df['Alias'] != bad]:
                subset = self.df.loc[(self.df['Alias'] == bad) | (self.df['Alias'] == movie)].dropna(1)
                sim_value = cosine(subset.iloc[0][1:].tolist(), subset.iloc[1][1:].tolist())
                dist = 1.0 / (1.0 + sim_value)
                sim_movie[movie] = dist
            sim_weights[bad] = sim_movie
     
        #now try to do the predictions:
        predicted_rating = 0
        weights_sum = 0.0
        missing_ratings = self.df[['Alias', name]][self.df[name].isnull()]['Alias'] #have missing
        non_missing_ratings = self.df[['Alias', name]][self.df[name].notnull()] #good ones without missing
        
        lst = []
        for miss in missing_ratings:
            # print(miss)
            predicted_rating = 0.0
            weights_sum = 0.0
            #b is column with ratings, and a is column with movie titles
            for a, b in non_missing_ratings.itertuples(index=False): #retrieve the good ratings, and use it to predict the bad ones
                predicted_rating += b * sim_weights[miss][a]
                weights_sum += sim_weights[miss][a]

            predicted_rating /= weights_sum
            tlst = [] #append tup to lst
            tlst.append(miss)
            tlst.append(predicted_rating)
            tup = tuple(tlst)
            lst.append(tup)

        return lst
    def t3(self, name):
        sim_weights = {}
        
        for user in self.df.columns[~self.df.columns.isin([name, 'Alias'])]:
            df_subset = self.df[[name,user]][self.df[name].notnull() & self.df[user].notnull()]
            #what if two users have weights that are NaN -> doesn't occur in BabyKangaroo example
            dist = cosine(df_subset[name], df_subset[user])
            sim_weights[user] = 1.0 / (1.0 + dist)
        temp = {k: v for k, v in sorted(sim_weights.items(), key=lambda item: item[1], reverse = True)}
        actual_top_10= dict(list(temp.items())[:10]) #get top 10 similar users
        # test = sorted(sim_weights, key=sim_weights.get, reverse=True)[:10]
        # print(dict(test)) 
        lst = []
        for i in range(len(self.df['Alias'])):
            curr_alias = self.df['Alias'][i]
            predicted_rating = 0.0
            weights_sum = 0.0
            ratings = self.df.iloc[i][self.df.columns[~self.df.columns.isin([name, 'Alias'])]]

            for user in self.df.columns[~self.df.columns.isin([name, 'Alias'])]:
                if (not np.isnan(ratings[user]) and user in actual_top_10): #only add if in the top 10
                    predicted_rating += ratings[user] * actual_top_10[user]
                    weights_sum += actual_top_10[user]
            predicted_rating /= weights_sum
            dumb = self.df.iloc[i][self.df.columns[self.df.columns.isin([name])]]
            if(np.isnan(dumb[name])):
                tlst = []
                tlst.append(curr_alias)
                tlst.append(predicted_rating)
                tup = tuple(tlst)
                lst.append(tup)
        return lst
if __name__ == "__main__":
    # using the class movie ratings data we collected in http://data.cs1656.org/movie_class_responses.csv
    t = Task('http://data.cs1656.org/movie_class_responses.csv')
    print(t.t1('BabyKangaroo'))
    print('------------------------------------')
    print(t.t2('BabyKangaroo'))
    print('------------------------------------')
    print(t.t3('BabyKangaroo'))
    print('------------------------------------')