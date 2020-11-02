import json
from datetime import datetime, timedelta, date
import requests
import matplotlib.pyplot as plt
import pandas as pd

class Task(object):
    def __init__(self, start_date, end_date):
        self.wprdc_api_endpoint = "https://data.wprdc.org/api/3/action/datastore_search_sql"
        self.resource_id = "1a1329e2-418c-4bd3-af2c-cc334e7559af"
        self.start_str = start_date.strftime("%Y-%m-%d")
        self.end_str = end_date.strftime("%Y-%m-%d")        

    #T1) Find top 30 restaurants in Pittsburgh with maximum number of violations (facility name[facility], number of violations[count]).
    def t1(self):
       
        query = """
        SELECT "facility_name" as facility, COUNT("description_new") as count
        FROM "{}"
        WHERE "inspect_dt" BETWEEN '{}' and '{}' AND "city" = '{}' AND "rating" = '{}'
        GROUP BY "facility_name"
        ORDER BY count DESC
        LIMIT 30""".format(self.resource_id, self.start_str, self.end_str, "Pittsburgh", "V")

        response = requests.get(self.wprdc_api_endpoint, {'sql': query})

        df = pd.DataFrame.from_dict(json.loads(response.text)['result']['records'])

        return df
    #T2) Find the category descriptions and their high, medium, low risk ratings for all violations at 
    #facilities that start with 'Pitt' over the past nine months (facility name[facility], 
    #violation description[violation], rating[rating], high[high], medium[medium], low[low]).
    def t2(self):
        query = """
        SELECT "facility_name" as facility, "description_new" as violation, "rating" as rating,
        "high" as high, "medium" as medium, "low" as low
        FROM "{}"
        WHERE "facility_name" LIKE 'Pitt%' AND "inspect_dt" BETWEEN '{}' and '{}' AND "city" = '{}' 
        """.format(self.resource_id, self.start_str, self.end_str, "Pittsburgh")

        response = requests.get(self.wprdc_api_endpoint, {'sql': query})

        df = pd.DataFrame.from_dict(json.loads(response.text)['result']['records'])

        return df
    #T3) Find the category descriptions and their high, medium, low risk ratings for all violations at all 
    #facilities that have word 'Pitt' in their name. Note that results that contain word 'Pitt' as part of 
    #another word (e.g. 'Pittsburgh') should not be included (facility name[facility], violation description[violation], 
    #rating[rating], high[high], medium[medium], low[low])
    def t3(self):
        query = """
        SELECT "facility_name" as facility, "description_new" as violation, "rating" as rating,
        "high" as high, "medium" as medium, "low" as low
        FROM "{}"
        WHERE "inspect_dt" BETWEEN '{}' AND '{}' AND ("facility_name" LIKE '% Pitt %' OR "facility_name" LIKE 'Pitt %' OR
        "facility_name" LIKE '% Pitt' OR "facility_name" = 'Pitt') AND "city" = '{}' 
        """.format(self.resource_id, self.start_str, self.end_str, "Pittsburgh")

        response = requests.get(self.wprdc_api_endpoint, {'sql': query})

        df = pd.DataFrame.from_dict(json.loads(response.text)['result']['records'])
        
        return df
    #T4) Find top 20 facilities that have word 'Pitt' in their name and have the highest counts of violations 
    #(facility name[facility], number of violations[count]).

    def t4(self):
        query = """
        SELECT "facility_name" as facility, COUNT("description_new") as count
        FROM "{}"
        WHERE ("facility_name" LIKE '% Pitt %' OR "facility_name" LIKE 'Pitt %' OR
        "facility_name" LIKE '% Pitt' OR "facility_name" = 'Pitt') 
        AND "inspect_dt" BETWEEN '{}' and '{}' AND "city" = '{}' AND "rating" = '{}'
        GROUP BY "facility_name"
        ORDER BY count DESC
        LIMIT 20""".format(self.resource_id, self.start_str, self.end_str, "Pittsburgh", "V")

        response = requests.get(self.wprdc_api_endpoint, {'sql': query})

        df = pd.DataFrame.from_dict(json.loads(response.text)['result']['records'])

        print(df)

        fig = plt.figure(figsize=(10,6))
        print(type(df['count']))
        df['count'] = df['count'].astype(float)
        count_legend = ['Count'] #for legend
        plt.bar(range(len(df['facility'])), df['count'], align = 'center')
        plt.xticks(range(len(df['facility'])), df['facility'], rotation = 90, fontsize = 8)
        plt.xlabel('Pitt facilities')
        plt.ylabel('Number of violations')
        plt.title('Violations plot')
        plt.tight_layout() #display facilities fully
        plt.legend(count_legend) #plot legend
        plt.show()

        return df

if __name__ == "__main__":
    t = Task(date(2018, 9, 1), date(2019, 6, 1))
    print("----T1----" + "\n")
    print(str(t.t1()) + "\n")
    print("----T2----" + "\n")
    print(str(t.t2()) + "\n")
    print("----T3----" + "\n")
    print(str(t.t3()) + "\n")
    print("----T4----" + "\n")
    print(str(t.t4()) + "\n")