package bowlingScores;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class BowlingScores {
	
	private static String tenthFrameRegex = "(([X]{3})|([X]{2}[0-9])|([X]([0-9][/]|[0-9]{2}))|([0-9][/]([X]|[0-9]))|([0-9]{2}))";
	private static String firstNineRegex = "((([X]|[0-9][0-9/])-){9})";
	private static int[] bowl = new int[2];
	
	public BowlingScores() throws FileNotFoundException {
		String frameScore = null;
		String[] scoresByFrame = null;
		frameScore = grabFrameValues();
		validateInput(frameScore);
		scoresByFrame = populateScoresArray(frameScore);
		calculateFinalScore(scoresByFrame);
	}
	
	private static boolean isFile(String userInput) { // Checks if the input is a file
		return userInput.contains(".txt");
	}
	
	private static String grabFrameValues() throws FileNotFoundException { //grabs values that the user has provided in the input
		Scanner s = new Scanner(System.in);
		String frameInputs = s.nextLine();
		if(isFile(frameInputs)) {
			Scanner fileScanner = new Scanner(new File(frameInputs));	
			frameInputs = fileScanner.nextLine();
			fileScanner.close();
			System.out.println("Scores from file: " + frameInputs);
		}
		else
			System.out.println("Scores from input: " + frameInputs);
		s.close();
		return frameInputs;
	}
	
	private static void validateInput(String s) { // Confirms that the input is valid for a final bowling score sheet
		if(!Pattern.matches(firstNineRegex+tenthFrameRegex, s))
			throw new RuntimeException("\n*Frame score inputs are invalid*\n");
	}
	
	private static String[] populateScoresArray(String s) { // Breaks the scores down into frames
		String[] scoresArr = new String[10];
		scoresArr = s.split("-");
		System.out.print("Frames: ");
		for(int i=0;i<scoresArr.length;i++) {
			System.out.print("[ " + scoresArr[i] + " ]");
		}
		return scoresArr;
	}
	
	private static void calculateFinalScore(String[] frames) { // calls various methods to calculate a final score
		int total = 0;
		for(int i=0;i<frames.length;i++) {
			if(isFinalFrame(frames, i))
				total = calculateFinalFrame(frames, i, total);
			else if(isNinthFrame(frames, i))
				total = calculateNinthFrame(frames, i, total);
			else if(isEigthFrame(frames, i))
				total = calculateEigthFrame(frames, i, total);
			else {
				if(bowledAStrike(frames[i])) // [X][?][?]
					total = calculateAStrike(frames, i, total);
				else if(bowledASpare(frames[i]))
					total = calculateASpare(frames, i, total);
				else
					total = calculateRegFrame(frames, i, total);
			}
		}
		System.out.print("\nFinal Score: " + total);
	}
	
	private static boolean bowledAStrike(String s) {
		return s.equals("X");
	}
	
	private static boolean bowledASpare(String s) {
		return Pattern.matches("[0-9][/]", s);
	}
	
	private static boolean isFinalFrame(String[] frames, int i) {
		return frames[i].length() == 3;
	}
	
	private static boolean isNinthFrame(String[] frames, int i) {
		return i<9 && frames[i+1].length() == 3 && (bowledAStrike(frames[i]) || bowledASpare(frames[i]));
	}
	
	private static boolean isEigthFrame(String[] frames, int i) {
		return i<8 && frames[i+2].length() == 3 && bowledAStrike(frames[i]);
	}
	
	private static int calculateFinalFrame(String[] frames, int i, int total) {
		String[] finalFrameBowls = frames[i].split("(?!^)");
		if(bowledAStrike(finalFrameBowls[0])) { // [X??]
			if(bowledAStrike(finalFrameBowls[1])) { // [XX?]
				if(bowledAStrike(finalFrameBowls[2])) // [X X X]
					total += 10 + 10 + 10;
				else
					total += 10 + 10 + Integer.parseInt(finalFrameBowls[2]); // [XX9]
			}
			else if(bowledASpare(finalFrameBowls[1]+finalFrameBowls[2])) // [X9/]
				total += 10 + 10;
			else // [X54]
				total += 10 + Integer.parseInt(finalFrameBowls[1]) + Integer.parseInt(finalFrameBowls[2]);
		}
		else if(bowledASpare(finalFrameBowls[0]+finalFrameBowls[1])) {
			if(bowledAStrike(finalFrameBowls[2]))
				total += 10 + 10;
			else
				total += 10 + Integer.parseInt(finalFrameBowls[2]);
		}
		return total;
	}
	
	private static int calculateNinthFrame(String[] frames, int i, int total) {
		String[] finalFrameBowls = frames[i+1].split("(?!^)");
		if(bowledAStrike(frames[i])) { //[X][???]
			if(bowledAStrike(finalFrameBowls[0])) { //[X][X??]
				if(bowledAStrike(finalFrameBowls[1])) //[X][XX?]
					total += 10 + 10 + 10;
				else //[X][X5?]
					total += 10 + 10 + Integer.parseInt(finalFrameBowls[1]);	
			}
			else if(bowledASpare(finalFrameBowls[0]+finalFrameBowls[1])) //[X][5/?]
				total += 10 + 10;
		}
		else if(bowledASpare(frames[i])) { //[5/][???]
			if(bowledAStrike(finalFrameBowls[0])) //[5/][X??]
				total += 10 + 10;
			else //[5/][5??]
				total += 10 + Integer.parseInt(finalFrameBowls[0]);
		}
		return total;
	}
	
	private static int calculateEigthFrame(String[] frames, int i, int total) {
		String[] finalFrameBowls = frames[i+2].split("(?!^)");
		if(bowledAStrike(frames[i+1])) { //[X][X][???]
			if(bowledAStrike(finalFrameBowls[0])) //[X][X][X??]
				total += 10 + 10 + 10;
			else //[X][X][5??]
				total += 10 + 10 + Integer.parseInt(finalFrameBowls[0]);
		}
		return total;
	}
	
	private static int calculateAStrike(String[] frames, int i, int total) {
		if(bowledAStrike(frames[i+1])) { // [X][X][?]
			if(bowledAStrike(frames[i+2])) // [X][X][X]
				total += 10 + 10 + 10;
			else { //[X][X][5?]
				String[] bowlString = frames[i+2].split("(?<=[0-9])");
				bowl[0] = Integer.parseInt(bowlString[0]);
				bowl[1] = Integer.parseInt(bowlString[1]);
				total += 10 + 10 + bowl[0];
			}
		}
		else if(bowledASpare(frames[i+1])) //[X][5/]
			total += 10 + 10;
		else { //[X][45]
			String[] bowlString = frames[i+1].split("(?<=[0-9])");
			bowl[0] = Integer.parseInt(bowlString[0]);
			bowl[1] = Integer.parseInt(bowlString[1]);
			total += 10 + bowl[0] + bowl[1];
		}
		return total;
	}
	
	private static int calculateASpare(String[] frames, int i, int total) {
		if(bowledAStrike(frames[i+1]))
			total += 10 + 10;
		else {
			String[] bowlString = frames[i+1].split("(?<=[0-9])");
			total += 10 + Integer.parseInt(bowlString[0]);
		}
		return total;
	}
	
	private static int calculateRegFrame(String[] frames, int i, int total) {
		String[] bowlString = frames[i].split("(?<=[0-9])");
		bowl[0] = Integer.parseInt(bowlString[0]);
		bowl[1] = Integer.parseInt(bowlString[1]);
		if(bowl[0] + bowl[1] < 10)
			total += bowl[0] + bowl[1];
		else
			throw new RuntimeException("\n*Frame score input [ " + frames[i] + " ] is invalid, bowl sum is larger than 9*\n");
		return total;
	}
	
	public static void main(String []args) throws FileNotFoundException {
		new BowlingScores();
	}
}