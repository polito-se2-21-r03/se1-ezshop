1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
# Project Estimation  
Authors: Can Karacomak (s287864), Alessandro Loconsolo (s244961), Julian Neubert (s288423), Simone Alberto Peirone (s286886)

Date: 30/04/2021

Version: 1.0
# Contents
- [Estimate by product decomposition]
- [Estimate by activity decomposition ]
# Estimation approach
<Considering EZShop Project to be independent of the deadlines given by the course, it would last 2 months>
# Estimate by product decomposition
### 
|             | Estimate                        |             
| ----------- | ------------------------------- |  
| NC = 130    | Model Classes = 20, View = 85, Controller = 25 |             
| A =  100    |                            | 
| S =  13000  | |
| E = 1300    | Assuming 10 LOC per person hour |   
| C = 39000   | Assuming 30 euro per person hour | 
| 8 weeks | Assuming team of 4 people, 8 hours per day, 5 days per week |  

# Estimate by activity decomposition
### 
|         Activity name    | Estimated effort (person hours)   |             
| ----------- | ------------------------------- | 
| Requirements analysis | 184 |
| GUI prototype | 96 |
| Design | 184 |
| Coding | 352 |
| Unit testing | 256 |
| Integration testing | 184 |

Total: 1240

###
Insert here Gantt chart with above activities

```plantuml
[Requirements] lasts 6 days
[GUI] lasts 3 days and starts after [Requirements]'s end
[Design] lasts 6 days
[Design] starts at [GUI]'s end
[Coding] lasts 11 days
[Coding] starts at [Design]'s end
[Unit tests] lasts 8 days
[Unit tests] starts after [Coding]'s end
[Integration] lasts 6 days
[Integration] starts after [Unit tests]'s end
```