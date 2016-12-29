# Task-Scheduler-using-RBT
**Problem Definition:**     
Red-black trees are very powerful data structures and find numerous applications.  
For eg, TreeMap in Java/C++ STL is usually implemented using it.  
This is an implementation of a simplified version of Linux’s scheduling algorithm - Completely Fair Scheduler(CFS).  

Here is the intuition behind the scheduler:   
The primary objective is to ensure that each task that is currently active has access to CPU as fairly as possible. So we associate an unfairness score with each task. The scheduler maintains the list of active tasks as a red-black tree and the nodes are ordered in descending order of unfairness hence the task that was treated most unfairly is the left most node. The scheduler runs a task till it is no longer the most unfairly treated. Of course, you have to insert a node for a task when it arrives and delete it once it is completed.

**Input/Output format:**  
Input is a single file of the following format.  
The first line contains the total number of tasks and the number of time periods to run the algorithm separated by a space.  
It is followed by a list of tasks. Each task will be triple.  
For eg means that task-20 arrives at 10th time unit of simulation and requires 100 seconds in the CPU to complete (not necessarily consecutively).

**The output will contain two things:**   
1. Given some time unit (say time unit 100), the snapshot of the red-black tree including the tasks, their color and their unfairness values. Do an in-order traversal so that the tasks are ordered based on their unfairness.    
2. The list of tasks that ran during the time period of the simulation. For eg, suppose the simulation ran for 5 time units means that task 1 was run by the scheduler at the start. Then task-2 ran for 1 time-unit followed by task-3 and then task-2 ran again for 2 consecutive time periods.

**Command Line input include:**   
1. File path for input file, in “.txt” format   
2. File path for output file, in “.txt” format (the file may/maynot exist)

**Input sample:**  
10 150    
1,4,10    
2,0,21    
3,0,32    
4,10,22   
5,7,11    
6,9,2   
7,5,22    
8,11,43   
9,12,27   
10,7,12   

**References:**   
http://algs4.cs.princeton.edu/33balanced/RedBlackBST.java.html 
