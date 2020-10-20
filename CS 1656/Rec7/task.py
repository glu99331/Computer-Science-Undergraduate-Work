import json
from datetime import datetime, timedelta
import requests
import pandas as pd
import ssl
ssl._create_default_https_context = ssl._create_unverified_context

class Task(object):
    def __init__(self):
        self.df = pd.read_csv('bank-data.csv')

    def get_mean(self, group):
        return group.mean()

    def t1(self):
        df_subset = self.df[['sex', 'income']]
        df_group = df_subset['income'].groupby(df_subset['sex']).apply(self.get_mean)
        return df_group

    def t2(self):
        df_crosstab = pd.crosstab(self.df["save_act"],self.df["mortgage"],margins=True)
        return df_crosstab
    # **Task 3**
    # Convert the frequencies in task 2's cross-tab to percentages. Include the margins. (Hint: You can use apply)
    def t3(self):
        df_crosstabPercentages = pd.crosstab(self.df['save_act'], self.df['mortgage'],
                                      margins=True).apply(lambda x: x/len(self.df),
                                                          axis=1)
        return df_crosstabPercentages
    
if __name__ == "__main__":
    t = Task()
    print("----T1----" + "\n")
    print(str(t.t1()) + "\n")
    print("----T2----" + "\n")
    print(str(t.t2()) + "\n")
    print("----T3----" + "\n")
    print(str(t.t3()) + "\n")
import pandas as pd


