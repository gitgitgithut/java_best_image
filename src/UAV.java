/**
 * CMPE 365 Lab 3
 * @author Xiaofeng Lin, 10138176
 * --------------------------------------------------------------------------------------------------------------------------------------------------
 * this code uses three array list to track the bits of the images, the Hamming Distances of the images, and the position of the images respectively
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class UAV {
	public final static int BEST_NUM = 500; //The number of best images we want
	public final static String FILE_PATH = "images.csv";
	public final static String COORDS_PATH = "coords.csv";
	public final static String OUTPUT_PATH = "abc.csv";
	
	public static void main(String[] args) {
		int i,j,currentDistance;//i,j: counter; currentDistance: store the Hamming Distance of the current read image
		String currentStr[]; //store the current read image
		String idealImg[] = "1,1,1,1,1,1,1,1,1,1".split(",");//best image we could possibly get. Every images read from the file should compare themselves with this one
		String content[] = readFile(FILE_PATH).split("\n");//store all read images
		ArrayList<String[]> bestImg = new ArrayList<>();//store the first "BEST_NUM" image with smallest Hamming Distance
		ArrayList<Integer> hdList = new ArrayList<>();// store the Hamming Distance of the images in bestImg[]
		ArrayList<Integer> imgPos = new ArrayList<>();//store the position of the image in bestImg
		//add the first 10 strings into the list
		//here add the first string
		currentStr = content[0].split(",");
		bestImg.add(currentStr);
		hdList.add(hammingDistance(idealImg,currentStr));
		imgPos.add(1);
		//add the remaining ones
		for (i = 1; i < BEST_NUM; i++) {
			currentStr = content[i].split(",");
			currentDistance = hammingDistance(idealImg,currentStr);
			if (!ifExist(bestImg,currentStr)) {
				//add it to the list, place it at the position where its Hamming distance is smaller than that of the string on the right hand side
				for (j = 0; j < bestImg.size(); j++) {
					if (currentDistance < hdList.get(j)) {
						bestImg.add(j,currentStr);
						hdList.add(j,currentDistance);
						imgPos.add(j,i+1);
						break;
					}
					else if (j == (bestImg.size()-1)) {// if the counter reaches the end of the list, add it to the tail of the list
						bestImg.add(j,currentStr);
						hdList.add(j,currentDistance);
						imgPos.add(j,i+1);
						break;
					}
				}
			}
		}
		//add the string afterwards into the array list only if it has smaller Hamming Distance than that of any one of the string in the array list
		while (i < content.length) {
			currentStr = content[i].split(",");
			currentDistance = hammingDistance(idealImg,currentStr);
			for (j = 0; j < BEST_NUM; j++) {
				if (currentDistance < hdList.get(j)) {
					//update the lists: add in the new one and pop out the last one so that the size of the array list is preserved
					bestImg.add(j,currentStr);
					hdList.add(j,currentDistance);
					imgPos.add(j,i+1);
					bestImg.remove(BEST_NUM);
					hdList.remove(BEST_NUM);
					imgPos.remove(BEST_NUM);
					break;
				}
			}
			i++;
		}
		System.out.print("The best images are at position ");
		for (j = 0; j < BEST_NUM; j++)
			System.out.print(imgPos.get(j) + ", ");
		System.out.print("sorted in the order of their Hamming Distance (from smallest to the largest)");
		
		//output the corresponding coordinate
		ArrayList<String> coords = new ArrayList<>();
		String temp;
		content = readFile(COORDS_PATH).split("\n");
		for (i = 0; i < BEST_NUM; i++) {
			temp = content[imgPos.get(i)] + "\n";
			coords.add(temp);
		}
        try {
        	FileWriter writer = new FileWriter(OUTPUT_PATH);
        	CSVUtils.writeLine(writer, coords);
        	writer.flush();
            writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * Calculate the Hamming Distance between two images
	 * @param A Image A
	 * @param B Image B
	 * @return The Hamming Distance between A and B
	 */
	public static int hammingDistance(String[] A, String[] B) {
		int i,count = 0;
		int size = A.length;
		for (i = 0; i < size; i++) {
			if (!A[i].equals(B[i]))
				count++;
		}
		return count;
	}
	
	/**
	 * Read the .csv file
	 * @param filePath
	 * @return contents in .csv file as string
	 */
	private static String readFile(String filePath) {
		File file = new File(filePath);
		String content = "Data was not read successfully";
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();
			content = new String(data, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	/**
	 * Check if the array list already has the same image
	 * @param list the array list
	 * @param str the current read image
	 * @return true if exists, otherwise false
	 */
	private static boolean ifExist(ArrayList<String[]> list, String[] str) {
		int i,j;
		int length = str.length;
		for (i = 0; i < list.size(); i++) {
			for (j = 0; j < length; j++) {
				//check if the current bit matches or not
				if (list.get(i)[j] != str[j]) //current bit does not match
					break;//still  not found
				else if (j == (length - 1))//current bit match and it is the last bit of the string
					return true;//found it
			}
		}
		return false;//all string in list are checked and there is no match
	}

}
