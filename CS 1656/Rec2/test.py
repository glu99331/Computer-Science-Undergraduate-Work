r = io.StringIO(requests.get('http://data.cs1656.org/KPIT_Aug17.csv', verify=False).content.decode('utf-8'))
df = pd.read_csv(r,\
                    sep=',', engine='python', parse_dates=['EST'])
print(df.head())
print(df.dtypes)

print(df['EST'].head())
#alternatively
print(df.EST.head())

#access multiple columns
print(df[['EST', 'Mean TemperatureF']].head())

#Plotting
p1 = plt.plot(df['EST'], df['MeanDew PointF'])
p2 = plt.plot(df['EST'], df['Mean TemperatureF'])
plt.legend([p1[0], p2[0]], ['Mean Dew Point', 'Mean Temperature'])
plt.show()
# Initializing a larger figure
fig = plt.figure(figsize=(10, 6))

# PLotting
p1 = plt.plot(df['EST'],df['MeanDew PointF'])
p2 = plt.plot(df['EST'],df['Mean TemperatureF'])
plt.legend([p1[0],p2[0]], ['Mean Dew Point', 'Mean Temperature'])

# Formatting graph
plt.xticks(rotation = 90, fontsize = 8)
plt.xlabel('Date')
plt.ylabel('Mean Temperature')
plt.title('Mean Tempertaures for August 2017')

# Are we ready to show the formatted graph now? Not yet. Because we want
# to save our graph figure this time.In order to use the save command, it
# is important to save before the show command because the show command
# clears the axis of the figure after displaying.

plt.savefig("basic_plot.png")
plt.show()

fig = plt.figure(figsize=(10, 6))
plt.bar(range(len(df['EST'])),df['Mean Humidity'], align = 'center')

# Formatting graph
plt.xticks(range(len(df['EST'])), df['EST'].dt.strftime('%Y-%m-%d'),\
               rotation = 90, fontsize = 8)
plt.xlabel('Date')
plt.ylabel('Mean Humidity')
plt.title('Mean Humidity for August 2017')

plt.savefig("bar_plot.png")
plt.show()