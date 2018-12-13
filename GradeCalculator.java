import java.util.Random;
import java.util.Scanner;

public class GradeCalculator {
	//initializing program
	static int classSize;
	static Student classList[] ;
	static int assignment1Weight;
	static int assignment2Weight;
	static int finalExamWeight;
	static Random generator =  new Random(System.currentTimeMillis());
	static Scanner  myScanner = new Scanner(System.in);
	static boolean simulationRun = false;//if this is false, then user will get a message that ask them to run option 1 when they try to run option 2 or 3

	//main function, control the whole program flow
	public static void main (String [] args) {
		//menu selection
		boolean again = true;//true: go to menu when an operation is finished, otherwise end program (as option 9)
		while (again == true) {
			System.out.println("Grade Calculator (Version 1.2 Author: [Yifei Yin])");
			System.out.println("1 - Simulate Course Marks");
			System.out.println("2 – View/Update Student Marks");
			System.out.println("3 – Run Mark Statistics");
			System.out.println("Select Option [1, 2 or 3] (9 to Quit)");
			//get user input
			String choice =myScanner.next();
			int userChoice = Integer.parseInt(choice);
			System.out.println(userChoice);
			//first selection promoted
			switch(userChoice) {
				//Option 1 - ask for class size and weight of each assignment
				case 1:
					System.out.println("Enter course enrollment size:");
					String Size = myScanner.next();
					int classSize = Integer.parseInt(Size);
					do{
						System.out.println("Enter weight assignment 1 (20-30):");
						String A1 = myScanner.next();
						assignment1Weight = Integer.parseInt(A1);
					}while(assignment1Weight>30 || assignment1Weight <20);
					do {
						System.out.println("Enter weight assignment 2 (20-30):");
						String A2 = myScanner.next();
						assignment2Weight = Integer.parseInt(A2);
					}while(assignment2Weight>30 || assignment2Weight <20);
					do {
						System.out.println("Enter weight final exam (40-60):");
						String FE= myScanner.next();
						finalExamWeight = Integer.parseInt(FE);
					}while(finalExamWeight>60 || finalExamWeight <40);
					if (assignment1Weight + assignment2Weight + finalExamWeight != 100) {
						System.out.println("<< Error: weights do not add up to 100% >>");
						break;
					}
					generateClass(classSize);//this generate a whole class
					simulationRun = true;
					break;
					//Option 2 - View or Update
				case 2:
					if (simulationRun == false) {
						System.out.println("Please run option 1 - Simulate Couse Marks first\n");
						break;
					}
					System.out.println("Enter Student Number:");
					String enterNum = myScanner.next();
					int num = Integer.parseInt(enterNum);
					int studentIndex = findStudentPostition(num);
					if (studentIndex == -1||classList[studentIndex]==null||classList[studentIndex].getStatus()==false) {
						System.out.println(num+" is invalid");
						break;
					}
					boolean wrongViewUpdateInput = true;//this make sure the program will only promote after a valid operation type has been entered
					while (wrongViewUpdateInput) {
						System.out.println("View or Update? (V/U):");
						String type =myScanner.next();
						switch (type) {
							case "V":
								wrongViewUpdateInput = false;
								printIndividual(classList[studentIndex]);
								break;
							case "U":
								wrongViewUpdateInput = false;
								boolean wrongMarkInput = true;
								while (wrongMarkInput) {
									System.out.println("Mark Type? (A1, A2 or FE):");
									String markType = myScanner.next();
									if (markType.equals("A1")|markType .equals("A2")|markType.equals("FE")) {
										markUpdater(classList[studentIndex],markType);
										wrongMarkInput = false;
									}
								}
								break;
							default:
								System.out.println("Please Re-enter U or V");
						}
					}
					break;
				case 3:
					if (simulationRun == false) {
						System.out.println("Please run option 1 - Simulate Couse Marks first\n");
						break;
					}
					printClassReport(classList);
					System.out.println(" Press any key to continue");
					String enteredKey = myScanner.next();
					break;
				case 9:
					again = false;
					break;
				case 0://this is for debug purpose
					System.out.println("classSize is" + classList.length);

			}
		}
	}

	static Student generateStudentMarks (int studentNumber){
		Student student  = new Student(studentNumber);
		student.updateMark("A1",numGenerator(1));
		student.updateMark("A2",numGenerator(1));
		student.updateMark("FE",numGenerator(1));
		return student;
	}

	static double computeStudentGrade (Student student){
		return (student.getMark("A1")*assignment1Weight
				+student.getMark("A2")*assignment2Weight
				+student.getMark("FE")*finalExamWeight)/100;
	}


	static void printClassReport (Student[] classList){
		//print title and item names
		System.out.println("Student Number\tA1 ("+assignment1Weight
						+"%)\t\t\tA2 ("+assignment2Weight+"%)\t\t\tFE ("
						+finalExamWeight+"%)\t\t\tFinal Mark\n"
						+"**************\t**************\t**************"
						+ "\t**************\t**************\t");

		//print individual information
		short [] A1Arr = new short [classList.length];
		short [] A2Arr = new short [classList.length];
		short [] FEArr = new short [classList.length];
		int k = 0;
		for(Student student: classList) {
			if (student!=null && student.getStatus()==true){
				System.out.println(student.getStudentNumber()+"\t\t"+student.getMark("A1")+"\t\t\t\t"
								+student.getMark("A2")+"\t\t\t\t"+student.getMark("FE")
								+"\t\t\t\t"+(double)(student.getMark("A1")*assignment1Weight
								+student.getMark("A2")*assignment2Weight
								+student.getMark("FE")*finalExamWeight)/100);
				A1Arr[k] = (short) student.getMark("A1");
				A2Arr[k] = (short) student.getMark("A2");
				FEArr[k] = (short) student.getMark("FE");
				k++;
			}
		}

		//print bottom and overall class information
		System.out.println("**************\t**************\t**************"
						+ "\t**************\t**************\t");
		System.out.println("AVERAGES"+"\t\t"+calculateAvg(A1Arr)
						+"\t\t\t\t"+calculateAvg(A2Arr)+"\t\t\t\t"+calculateAvg(FEArr)
						+"\t\t\t\t"+(double)(calculateAvg(A1Arr)*assignment1Weight
						+calculateAvg(A2Arr)*assignment2Weight
						+calculateAvg(FEArr)*finalExamWeight)/100);
		System.out.println("Highest final mark is" + "["+getMax(FEArr)+"]");
		System.out.println("Lowest final mark is" + "["+getMin(FEArr)+"]");
		System.out.print("Press any key to continue");
		return;
	}


	//-----------------------my own methods------------------------

	//generate mark when type is 1, generate student number when type is other integer, like 2
	static int numGenerator(int type) {
		if (type == 1){
			return generator.nextInt(100);
		}
		else {
			return generator.nextInt(89999999)+10000000;
		}
	}

	//this find the index of the student object in the classList array
	static int findStudentPostition(int studentNumber) {
		if (classList.length == 0) {
			return -1;
		}
		int index = 0;
		int nowStudentNumber = -1;
		boolean targetNotFound = true;
		while (targetNotFound) {
			if (classList[index].getStudentNumber()!=0) {
				nowStudentNumber = classList[index].getStudentNumber();
			}

			if (nowStudentNumber== studentNumber) {
				targetNotFound = false;
			}
			else {
				if (index ==classList.length-1) {
					return -1;
				}
				else {
				index++;
				}
			}
		}
		return index;
	}

	//update student mark, used in option 2 - Update
	static void markUpdater(Student student, String markType) {
			System.out.println(markType + " is "+ student.getMark(markType));
			System.out.println("Updated Mark (0 – 100): ");
			String mark = myScanner.next();
			int newMark = Integer.parseInt(mark);
			if (newMark<0 || newMark >100) {
				System.out.println(newMark+" is an invalid mark");
				return;
			}
			student.updateMark(markType, newMark);
			System.out.println("Mark has been successfully updated!\n");
			return;
		}

	//this is called in option 2 - View one student
	static void printIndividual(Student student) {
		System.out.println(student.getStudentNumber()
				+"                   "+student.getMark("A1")
		                +"                  "+student.getMark("A2")
		                +"                  "+student.getMark("FE")
		                +"                  "+computeStudentGrade(student)+"\n");
	}

	//add a student object to an array, the array could be empty
	static void addStudent(Student newStudent) {
		if (classList == null||classList.length==0) {
			Student [] newList = {newStudent};
			classList = newList;
			return;
		}
		int studentPosition = findStudentPostition(newStudent.getStudentNumber());
		if (studentPosition == -1){//student doesn't exist, create new student
			Student [] newList = new Student [classList.length+1];
			for (int i=0; i<classList.length;i++) {
				newList[i]=classList[i];
			}
			newList[classList.length] = newStudent;
			classList = newList;
		}
		else {//update old student information
			classList[studentPosition]=newStudent;
		}

	}

	static void generateClass(int classSize) {
		for (;classSize>0;classSize--) {
			int studentID = numGenerator(2);
			addStudent(generateStudentMarks(studentID));
		}
	}

	static double calculateAvg(short[] a1Arr) {
		int sum=0;
		for(int num: a1Arr) {
			sum = sum + num;
		}
		int number = a1Arr.length;

		return sum/number;
	}

	static int getMin(short[] fEArr){
		int minValue = fEArr[0];
		for(int i=1;i<fEArr.length;i++){
			if(fEArr[i] < minValue){
				minValue = fEArr[i];
			}
		}
		return minValue;
	}

	static int getMax(short[] fEArr){
			int maxValue = fEArr[0];
			for(int i=1;i < fEArr.length;i++){
				if(fEArr[i] > maxValue){
					maxValue = fEArr[i];
				}
			}
			return maxValue;
	}
}


class Student {
	private int studentNumber;
	private boolean status = true;
	private int assignment1Mark;
	private int assignment2Mark;
	private int finalExamMark;

	public Student (int num) {
		setStudentNumber(num);
	}

	void updateMark (String markType, int mark) {
		switch (markType) {
			case "A1":
				this.assignment1Mark = mark;
				return;
			case "A2":
				this.assignment2Mark = mark;
				return;
			case "FE":
				this.finalExamMark = mark;
				return;
		}
		return;

	}

	int getMark (String markType) {
		switch (markType) {
			case "A1":
				return assignment1Mark;
			case "A2":
				return assignment2Mark;
			case "FE":
				return finalExamMark;
			default:
				return -1;
		}
	}

	int getStudentNumber() {
		return studentNumber;
	}

	void setStudentNumber(int num) {
		if ((num>99999999 || num<10000000)) {
			return;
		}
		studentNumber = num;
	}

	void setStatus(boolean newStatus) {
		status = newStatus;
	}

	boolean getStatus() {
		return status;
	}
}
