import matplotlib.pyplot as plt
import pandas as pd
import datetime
import io
import requests
import numpy as np #for side-by side box plot

r = io.StringIO(requests.get('http://data.cs1656.org/top12cities.csv', verify=False).content.decode('utf-8'))
df = pd.read_csv(r,\
                    sep=',', engine='python')
#Part I: Scatterplot                    
fig = plt.figure(figsize=(10, 6))

plt.scatter(df['2014 land area'],df['2014 estimate'])
# Formatting graph
for i, txt in enumerate(df['City']): #add labels of city to each point
    plt.annotate(txt, (df['2014 land area'][i], df['2014 estimate'][i]))
plt.ticklabel_format(style='plain') #turn off scientific notation
plt.xlabel('2014 Land Area')
plt.ylabel('2014 Estimate')
plt.title('Land Area and Population Density Estimates for 2014')

plt.savefig("scatter_plot.png")
plt.show()
#Answer: The plot exhibits a weak correlation, but is positive.
#Part II: Bar plot
fig = plt.figure(figsize=(10, 6))
plt.ticklabel_format(style='plain') #turn off scientific notation
plt.bar(range(len(df['City'])),df['2014 estimate'], align = 'center')
plt.xticks(range(len(df['City'])), df['City'],
               rotation = 30, fontsize = 8)
plt.xlabel('City')
plt.ylabel('2014 estimate')
plt.title('Population Density Estimate by City for 2014')

plt.savefig("bar_plot.png")
plt.show()
#Part III: Grouped bar plot
ypos = np.arange(len(df['City']))
p1 = plt.bar(ypos - 0.2, df['2014 estimate'], width = 0.4, color = 'blue')
p2 = plt.bar(ypos + 0.2, df['2010 Census'], width = 0.4, color = 'green')
plt.ticklabel_format(style='plain') #turn off scientific notation

#Aesthetics
plt.title('Population Density Estimate by City for 2010 and 2014')
plt.xticks(range(len(df['City'])), df['City'],
                rotation = 30, fontsize = 8)
plt.xlabel('City')
plt.ylabel('Population Density Estimate')
plt.legend([p1[0], p2[0]], ['2014', '2010'])
plt.savefig("side_by_sidebar_plot.png")
plt.show()