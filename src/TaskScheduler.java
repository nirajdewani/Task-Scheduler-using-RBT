import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class TaskScheduler 
{

	int numberOfTasks;
    int totalTimeOfRun;
    int currentTaskPointer;
    String outputFilePath;
    String inputFilePath;
    
	public static void main(String args[]) throws IOException
	{
		TaskScheduler t = new TaskScheduler();

		Scanner scanner = new Scanner(new InputStreamReader(System.in));
        System.out.println("Enter file path for input file");
        t.inputFilePath = scanner.nextLine();
        System.out.println("Enter file path for output file");
        t.outputFilePath = scanner.nextLine();
		scanner.close();
		
		StringBuffer executionList = new StringBuffer("<");
		
		int[][]taskParameters = t.readInput(t.inputFilePath);
		t.sortInput(taskParameters);									//sorts tasks on the basis of start time

		t.currentTaskPointer = 0;										//making task at 0th position in sorted matrix as current task	
		
		for(int currentProcessorTime = 0; currentProcessorTime < t.totalTimeOfRun; currentProcessorTime++)
		{				
			if(t.currentTaskPointer < t.numberOfTasks && taskParameters[t.currentTaskPointer][1] <= currentProcessorTime)			//start time of next task == currentProcessorTime
			{
				if(taskParameters[t.currentTaskPointer][2] > 1)			//if task requires only 1 time unit then it is not inserted in tree
				{
					t.put(taskParameters[t.currentTaskPointer][0], taskParameters[t.currentTaskPointer][1], 1, taskParameters[t.currentTaskPointer][2]);
					executionList.append(taskParameters[t.currentTaskPointer][0] + ",");
				}								
				t.currentTaskPointer++;									//incrementing currentTaskPointer to point to next task 									
			}
			else if(t.root != null)										//there is no task with startTime == currentProcessorTime hence pick task with min. PTT		
			{
				Node toBeProcessed = t.min(t.root);
				if(toBeProcessed != null)
				{
					if(toBeProcessed.TTC == 1)							//task needs 1 time unit for completion
					{	
							/*
							{											//test block																					 
							File output = new File(t.outputFilePath);
							PrintWriter writer = new PrintWriter(new FileWriter(output, true));
							writer.append("Contents of the tree (inorder traversal) just before completion of task " + toBeProcessed.taskID + ", after " + currentProcessorTime + " time units: " + System.lineSeparator());
							writer.close();
							t.printTree(t.root, t.outputFilePath);
							}
							*/
						
						executionList.append(toBeProcessed.taskID + ",");
						t.deleteMin();
													
					}
					else
					{
						int tempTaskID = toBeProcessed.taskID;
						int tempTTC = --toBeProcessed.TTC;
						int tempPTT = ++toBeProcessed.PTT;
						int tempStartTime = toBeProcessed.startTime;
						
						t.deleteMin();
						t.put(tempTaskID, tempStartTime, tempPTT, tempTTC);						
						executionList.append(toBeProcessed.taskID + ",");
					}									
				}
			}			
		}
		
		File output = new File(t.outputFilePath);
		PrintWriter writer = new PrintWriter(new FileWriter(output, true));

		if(t.root != null)
			t.printTree(t.root, t.outputFilePath);
		else
			writer.append("All tasks executed, hence the tree is empty." + System.lineSeparator());

		writer.append("Sequence of task execution: " + System.lineSeparator());
		writer.append(executionList.substring(0, executionList.length()-1) + ">");
		writer.close();			
	}
	
	
	//reads input from input.txt and gives out n*3 matrix containing task parameters
	public int[][] readInput(String inputFilePath) throws IOException
	{
	    BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
	    String everything;
	    try 
	    {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) 
	        {
	            sb.append(line);
	            sb.append(System.lineSeparator());
	            line = br.readLine();
	        }
	        everything = sb.toString();
	    } 
	    finally 
	    {
	        br.close();
	    }    
	        //writing input to matrix
	        String inputLines[] = everything.split(System.lineSeparator());
	        String runDetails[] = inputLines[0].split(" ");
	        numberOfTasks = Integer.parseInt(runDetails[0]);
	        totalTimeOfRun = Integer.parseInt(runDetails[1]);
	        int[][] taskParameters = new int[numberOfTasks][3];
	        
	        for(int i = 1; i <= numberOfTasks; i++)
	        {
	        	String temp[] = inputLines[i].split(","); 		//i+1 since first line contains run details
	        	for(int j = 0; j < 3; j++)
	        	{
	        		taskParameters[i-1][j] = Integer.parseInt(temp[j]); 
	        	}
	        }
	        return taskParameters;
	}
		
	//sorts tasks on the basis of start time i.e. column 1
	public void sortInput(int taskParameters[][])
	{
		int temp;
		for(int i = 0; i < taskParameters.length-1; i++)
		{
			for(int j =0; j < taskParameters.length-i-1; j++)
			{
				if(taskParameters[j][1] > taskParameters[j+1][1])
				{
					temp = taskParameters[j][0];
					taskParameters[j][0] = taskParameters[j+1][0];
					taskParameters[j+1][0] = temp;
					
					temp = taskParameters[j][1];
					taskParameters[j][1] = taskParameters[j+1][1];
					taskParameters[j+1][1] = temp;
					
					temp = taskParameters[j][2];
					taskParameters[j][2] = taskParameters[j+1][2];
					taskParameters[j+1][2] = temp;
				}
			}
		}
	}
	
	public void printTree(Node w, String outputFilePath) throws FileNotFoundException, UnsupportedEncodingException		//inorder traversal
	{
		if(w != null)
		{				
			File output = new File(outputFilePath);
			try
			{
				if(output.exists()==false)
				{
		            output.createNewFile();
				}
				
				printTree(w.left, outputFilePath);
				
				PrintWriter writer = new PrintWriter(new FileWriter(output, true));
				writer.append("TaskID:" + w.taskID + System.lineSeparator());
				writer.append("Processor Time Taken:" + w.PTT + System.lineSeparator());
				writer.append("Time To Completion:" + w.TTC + System.lineSeparator());
				String color = w.color?"Red":"Black";
				writer.append("Color:" + color + System.lineSeparator());
				writer.append(System.lineSeparator());
				writer.close();
		
				printTree(w.right, outputFilePath);					
			}
			catch(IOException e)
			{
		        System.out.println("Error in writing output");
			}
		}
	}
	
	
	private static final boolean RED = true;
	private static final boolean BLACK = false;
	
	private Node root;
	
	private class Node
	{
		private int taskID;
		private int PTT;			//Processor Time Taken		
		private int TTC;			//Time To Completion
		private int startTime;
		private boolean color;
		private int subtreeCount;
		private Node left, right;
		
		public Node(int taskID, int startTime, int PTT, int TTC, boolean color, int subtreeCount)
		{
			this.taskID = taskID;
			this.startTime = startTime;
			this.PTT = PTT;
			this.TTC = TTC;
			this.color = color;
			this.subtreeCount = subtreeCount;
		}
	}
		
		private boolean isRed(Node n)
		{
			if(n == null)
				return false;
			else
				return (n.color == RED);
		}
		
		// number of node in subtree rooted at x; 0 if x is null
		private int size(Node x) 
		{
			    if (x == null) 
			    	return 0;
			    
			    return x.subtreeCount;
		} 

		//Insertion
		public void put(int taskID, int startTime, int PTT, int TTC)
		{
			root = put(root, taskID, startTime, PTT, TTC);
			root.color=BLACK;			
		}

		private Node put(Node h, int taskID, int startTime, int PTT, int TTC)
		{
			if(h==null)
				return new Node(taskID, startTime, PTT, TTC, RED, 1);
			
			int cmp = PTT - h.PTT;
			if (cmp<0) 
				h.left = put(h.left, taskID, startTime, PTT, TTC);
			else 
				if(cmp>=0) 
					h.right = put(h.right, taskID, startTime, PTT, TTC);
			else 
				h.PTT = PTT;
			
			if(isRed(h.right) && !isRed(h.left)) 
				h = rotateLeft(h);
			if(isRed(h.left) && isRed(h.left.left)) 
				h=rotateRight(h);
			if(isRed(h.left) && isRed(h.right)) 
				flipColors(h);
			
			h.subtreeCount = size(h.left) + size(h.right) + 1;
			
			return h;
		}


		private Node rotateRight(Node h) 
		{
		    Node x = h.left;
		    h.left = x.right;
		    x.right = h;
		    x.color = x.right.color;
		    x.right.color = RED;
		    x.subtreeCount = h.subtreeCount;
		    h.subtreeCount = size(h.left) + size(h.right) + 1;
		    return x;
		}

		// make a right-leaning link lean to the left
		private Node rotateLeft(Node h) 
		{
		    Node x = h.right;
		    h.right = x.left;
		    x.left = h;
		    x.color = x.left.color;
		    x.left.color = RED;
		    x.subtreeCount = h.subtreeCount;
		    h.subtreeCount = size(h.left) + size(h.right) + 1;
		    return x;
		}

		private void flipColors(Node h) 
		{
		    h.color = !h.color;
		    h.left.color = !h.left.color;
		    h.right.color = !h.right.color;
		}
		
		private Node moveRedLeft(Node h) 
		{
			flipColors(h);
		    if (isRed(h.right.left)) 
	        { 
	            h.right = rotateRight(h.right);
	            h = rotateLeft(h);
		    }
		        return h;
		 }

		 // Assuming that h is red and both h.right and h.right.left
		 // are black, make h.right or one of its children red.
		 private Node moveRedRight(Node h) 
		 {
			 flipColors(h);
		     if (isRed(h.left.left)) 
		     { 
		         h = rotateRight(h);
		     }
		         return h;
		 }
		    
		    //deletion starts here
		    public void delete(Node toBeDeleted)
		    {
		        // if both children of root are black, set root to red
		        if (!isRed(root.left) && !isRed(root.right))
		            root.color = RED;

		        root = delete(root, toBeDeleted);
		        if (!isEmpty()) 
		        	root.color = BLACK;
		    }
			    
			private Node delete(Node h, Node toBeDeleted)
			{ 
		        if (h.PTT > toBeDeleted.PTT)
		        {
		            if (!isRed(h.left) && (h.left != null) && (h.left.left != null) && !isRed(h.left.left))
		                h = moveRedLeft(h);
		            h.left = delete(h.left, toBeDeleted);
		        }
		        else 
		        {
		            if (isRed(h.left))
		                h = rotateRight(h);
		            if (h.taskID == toBeDeleted.taskID && (h.right == null))
		                return null;
		            if (!isRed(h.right) && (h.right != null) && (h.right.left != null) && !isRed(h.right.left))
		                h = moveRedRight(h);
		            //if (h.PTT == toBeDeleted.PTT )
		            if(h.taskID == toBeDeleted.taskID)
		            {
		                Node x = min(h.right);
		                h.TTC = x.TTC;
		                h.PTT = x.PTT;
		                h.taskID = x.taskID;
		                h.right = deleteMin(h.right);
		            }
		            else if(h.right != null) 
		            	h.right = delete(h.right, toBeDeleted);
		        }
		        return balance(h);
		    }
			
	    // the smallest key; null if no such key
	    public Node min() 
	    {
	        if (isEmpty()) return null;
	        return min(root);
	    } 

	    // the smallest key in subtree rooted at x; null if no such key
	    private Node min(Node x) 
	    { 
	        if (x.left == null) 
	        	return x; 
	        else                
	        	return min(x.left); 
	    } 
		
	    public boolean isEmpty() 
	    {
	        return root == null;
	    }	
		
	    public void deleteMin() 
	    {
	        if (isEmpty()) throw new NoSuchElementException("BST underflow");

	        // if both children of root are black, set root to red
	        if (!isRed(root.left) && !isRed(root.right))
	            root.color = RED;

	        root = deleteMin(root);
	        if (!isEmpty()) 
	        	root.color = BLACK;
	    }

	    // delete the key-value pair with the minimum key rooted at h
	    private Node deleteMin(Node h) 
	    { 
	        if (h.left == null)
	            return null;

	        if (!isRed(h.left) && !isRed(h.left.left))
	            h = moveRedLeft(h);

	        h.left = deleteMin(h.left);
	        return balance(h);
	    }
    
	    // restore red-black tree invariant
	    private Node balance(Node h) 
	    {
	        // assert (h != null);

	        if (isRed(h.right))              
	        	h = rotateLeft(h);
	        if (isRed(h.left) && isRed(h.left.left))
	        	h = rotateRight(h);
	        if (isRed(h.left) && isRed(h.right))  
	        	flipColors(h);

	        h.subtreeCount = size(h.left) + size(h.right) + 1;
	        return h;
	    }	    
}

