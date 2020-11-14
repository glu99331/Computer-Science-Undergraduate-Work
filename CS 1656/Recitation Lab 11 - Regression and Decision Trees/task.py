import json
from datetime import datetime, timedelta
import requests
import pandas as pd
import numpy as np
from sklearn import linear_model, tree, metrics
import ssl
ssl._create_default_https_context = ssl._create_unverified_context
#Lu, Gordon: CS 1656 Fall 2020 Recitation Lab 11 -> Passes Gradescope!
class Task(object):
    def __init__(self, bike_df, bank_df):
        np.random.seed(31415)
        self.bike_data = bike_df.sample(1000).copy()
        self.bank_data = bank_df.copy()
    # Task 1:

    # Do linear regression over a sample of 1000 rows of bike share counts, cnt, using weekday, as
    # input feature. Calculate the mean squared error by using first 900 rows for training and the rest
    # for testing. Return the mean squared error.
    def t1(self):
        train = self.bike_data.iloc[1:900]
        train_x = train[['weekday']].values
        train_y = train[['cnt']].values
        test=self.bike_data.iloc[901:]
        test_x = test[['weekday']].values
        test_y = test[['cnt']].values

        regr = linear_model.LinearRegression()
        regr.fit(train_x, train_y)
        predict_y = regr.predict(test_x)

        return np.mean((predict_y - test_y) ** 2)

    # Task 2.1:
    # Repeat Task 1 using all atttributes except instant (also, scatter plot is not required in this task).
    # Is the mean squared error higher or lower? Is it better to use all attributes?
    def t2_1(self):
        train_x = self.bike_data.iloc[1:900][['season', 'hr', 'holiday', 'weekday', 'workingday', 'weathersit', 'temp', 'temp_feels', 'hum', 'windspeed']]
        train_y = self.bike_data.iloc[1:900][['cnt']].values

        test_x = self.bike_data.iloc[901:][['season', 'hr', 'holiday', 'weekday', 'workingday', 'weathersit', 'temp', 'temp_feels', 'hum', 'windspeed']]
        test_y = self.bike_data.iloc[901:][['cnt']].values

        regr = linear_model.LinearRegression()
        regr.fit(train_x, train_y)
        predict_y = regr.predict(test_x)

        return np.mean((predict_y - test_y) ** 2)

    # Task 2.2:
    # Comparing the results of task 1 and task 2.1, is it better to use all attributes? Why?

    # Answer: Looking at the MSEs between the linear regression model using a single predictor, 'weekday' vs. the multiple regression model using
    # all attributes, the multiple regression model has a lower MSE. Therefore, we can say that the average distance between the actual and predicted 
    # values is relatively lower than that of the simple linear regression model. Although, using metrics such as scatterplots, adjusted R^2, and 
    # inspections on the correlation matrix will give us a stronger impression of the strength of the respective models, I would say using MSE 
    # as a single metric is still an indicator that the multiple regression model is stronger.

    # Task 3:    
    # You will use bank-data.csv as input for this task. Use decision trees to do binary classification
    # of mortgage{yes,no} using region, sex and married attributes as input features. Use the first 500
    # rows for training and the rest for testing. Measure the accuracy of your classification. Return the
    # accuracy
    def t3(self):
        #Arbitrary encodings for variables
        self.bank_data['sex'] = self.bank_data['sex'].replace(['FEMALE', 'MALE'], [0, 1])
        self.bank_data['married'] = self.bank_data['married'].replace(['NO', 'YES'], [0, 1])
        self.bank_data['region'] = self.bank_data['region'].replace(['INNER_CITY', 'TOWN', 'RURAL', 'SUBURBAN'], [1, 2, 3, 4])

        dt_train_x = self.bank_data.iloc[:500][['region','sex','married']].values
        dt_train_y = self.bank_data.iloc[:500][['mortgage']].values

        dt_test_x = self.bank_data.iloc[501:][['region','sex','married']].values
        dt_test_y = self.bank_data.iloc[501:][['mortgage']].values

        clf = tree.DecisionTreeClassifier()
        clf = clf.fit(dt_train_x, dt_train_y)

        dt_predict_y = clf.predict(dt_test_x)

        return metrics.accuracy_score(dt_test_y,dt_predict_y)


if __name__ == "__main__":
    t = Task(pd.read_csv('http://data.cs1656.org/bike_share.csv'), pd.read_csv('http://data.cs1656.org/bank-data.csv'))
    print("---------- Task 1 ----------")
    print(t.t1())
    print("--------- Task 2.1 ---------")
    print(t.t2_1())
    print("---------- Task 3 ----------")
    print(t.t3())