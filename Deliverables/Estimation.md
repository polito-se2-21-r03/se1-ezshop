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

## Estimate by activity decomposition
 
|         Activity name    | Estimated effort (person hours)   |             
| ------------------------ | ------------------------------- | 
| 1. Requirements document               | 160 |
| 1.1 Stakeholders definition            | 8   |
| 1.2 Requirements analysis              | 152 |
| 2. GUI prototype                       | 96  |
| 2.1 Management application             | 64  |
| 2.2 Cash register                      | 32  |
| 3. Design                              | 192 |
| 4. Coding                              | 352 |
| 5. Unit testing                        | 256 |
| 6. Integration testing                 | 192 |
| 7. Post Mortem                         | 16  |

**Total**: 1264 person hours

### Gantt chart

```plantuml
[Requirements document completed] happens 6 days after start
[GUI prototype completed] happens 7 days after start
[Design completed] happens 13 days after start
[Coding completed] happens 27 days after start
[Unit testing completed] happens 37 days after start
[Integration testing completed] happens 38 days after start

/' requirements: 3 days x 4 people + 4 days x 2 people '/
[Requirements] lasts 7 days and is 100% completed

/' GUI: 4 days x 2 people + 1 day x 4 people '/
[GUI] lasts 5 days and starts 3 days after start

/' design: 6 days x 4 people '/
[Design] lasts 6 days and is 0% completed
[Design] starts at [GUI]'s end

/' coding: 8 days x 4 people + 6 days x 2 people '/
[Coding] lasts 14 days and is 0% completed
[Coding] starts at [Design]'s end

/' unit testing: 16 days x 2 people '/
[Unit testing] lasts 16 days and is 0% completed
[Unit testing] starts 22 days after start

/' integration testing: 10 days x 2 people + 1 day x 4 people '/
[Integration testing] lasts 11 days and is 0% completed
[Integration testing] starts 28 days after start

/' post mortem: .5 days x 4 people '/
[Post Mortem] lasts 1 day and starts after [Integration testing]'s end and is 0% completed
```