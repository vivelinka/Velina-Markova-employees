package com.company;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.CheckedOutputStream;

import static java.time.temporal.ChronoUnit.DAYS;

public class Main {


    public static void main(String[] args) throws IOException {

        BufferedReader bReader = new BufferedReader(new FileReader(".\\input.txt")); //open input file for reading
        List<String> inputData = new ArrayList<String>();          //create a List to store the data from the file

        /* read every line from the file until EOF and store as String */
        String lineInput;
        while ((lineInput = bReader.readLine()) != null) {
            inputData.add(lineInput);
        }
        bReader.close();

        // create four arrays for employeeID, projectId, dateFrom and dateTo
        String[] employeeID = new String[inputData.size()];
        String[] projectID = new String[inputData.size()];
        String[] dateFrom = new String[inputData.size()];
        String[] dateTo = new String[inputData.size()];

        int counter = 0; //iterate on the array indexes

        //iterate on the list with initial data and store in the arrays
        for (String line : inputData) {
            String[] splited = line.split(", ");

            employeeID[counter] = splited[0];
            projectID[counter] = splited[1];
            dateFrom[counter] = splited[2];
            dateTo[counter] = splited[3];

            counter++;
        }

        //Convert dates to milliseconds for easier comparison
        long[] beginDates = new long[dateFrom.length];
        long[] endDates = new long[dateTo.length];

        for (int i = 0; i < dateFrom.length; i++) {
            beginDates[i] = convertToMs(dateFrom[i]);
        }
        for (int i = 0; i < dateTo.length; i++) {
            endDates[i] =  convertToMs(dateTo[i]);
        }

        //find unique employee and project IDs by using the "findUniqueIDs" method
        String[] uniqueEmplID = findUniqueIDs(employeeID);
        String[] uniqueProjectID = findUniqueIDs(projectID);

        //initialize variables to store the resulting employees that worked most time together
        String bestEmplOne = "";
        String bestEmplTwo = "";
        double maxTimeTogether = 0;

        /* The following loops iterate through the unique employee IDs by couples - currentID and versusID.
         * Then it checks if the couple worked on the same current project by looping through the unique project IDs.
         * The time of the couple working on the same projects is accumulating.
         * If the time of the current couple is greater than the max time - the value is replaced
         * and the IDs of the couple employees is stored in bestEmplOne and bestEmplTwo.
         */
        for (int i = 0; i < uniqueEmplID.length; i++) {
           String currentID = uniqueEmplID[i];
            for (int j = i+1; j < uniqueEmplID.length; j++) {
                String versusID = uniqueEmplID[j];
                double tempTimeTogether = 0;

                for (int k = 0; k < uniqueProjectID.length; k++) {
                    String currentProject = uniqueProjectID[k];
                    long currentBeginTime = 0;
                    long currentEndTime = 0;
                    long versusBeginTime = 0;
                    long versusEndTime = 0;

                    for (int m = 0; m < inputData.size(); m++) {
                        if (employeeID[m].equals(currentID) && projectID[m].equals(currentProject)) {
                            currentBeginTime = beginDates[m];
                            currentEndTime = endDates[m];
                        } else if (employeeID[m].equals(versusID)&&projectID[m].equals(currentProject)) {
                            versusBeginTime = beginDates[m];
                            versusEndTime = endDates[m];
                        }

                    }

                    if (currentBeginTime > 0 && versusBeginTime > 0) {
                        //calculate the common time the employees worked together on the project
                        long soonerEndDate = Math.min(currentEndTime,versusEndTime);
                        long latterBeginDate = Math.max(currentBeginTime, versusBeginTime);
                        long intervalMs = soonerEndDate - latterBeginDate;

                        if (intervalMs > 0) {  //check if the time interval overlaps for the two employees
                            //convert time from milliseconds to days
                            double intervalDays = TimeUnit.DAYS.convert(intervalMs, TimeUnit.MILLISECONDS);
                            tempTimeTogether+=intervalDays;
                        } else {
                            break;
                        }
                    }
                }
                    if (tempTimeTogether > maxTimeTogether) {
                        maxTimeTogether = tempTimeTogether;
                        bestEmplOne = currentID;
                        bestEmplTwo = versusID;
                    }

            }
        }
        System.out.printf("The IDs of employees worked most time together are %s and %s\n", bestEmplOne, bestEmplTwo);
    }

    //method to create arrays with only the unique IDs for employees and projects
    private static String[] findUniqueIDs(String[] inputArray) {
        List<String> uniqueIDList = new ArrayList<>();
        for (int i = 0; i < inputArray.length; i++) {
            if (!uniqueIDList.contains(inputArray[i])) {
                uniqueIDList.add(inputArray[i]);
            }
        }
        String[] uniqueIDArray = new String[uniqueIDList.size()];
        int counter = 0;
        for (String listEntry : uniqueIDList) {
            uniqueIDArray[counter] = listEntry;
            counter++;
        }
        return uniqueIDArray;
    }

    // method to convert the date into milliseconds
    private static long convertToMs(String dateString) {
        Date stringToDate = new Date();
        if (dateString.equals("NULL")) {
            dateString = new SimpleDateFormat("yyy-MM-dd").format(new Date());
        }

        try {
            SimpleDateFormat myFormat = new SimpleDateFormat("yyy-MM-dd");
            stringToDate = myFormat.parse(dateString);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringToDate.getTime();
    }

}
