import java.io.*;
import java.net.Socket;
import java.lang.*;
import java.util.ArrayList;
import java.util.Collections;

public class udp_client {

    private static DataOutputStream dataOutputStream = null; // Data output stream
    private static DataInputStream dataInputStream = null; // Data input stream

    public static void main(String[] args) {

        int port = Integer.parseInt(args[1]);
        long measurementTwoFirst = System.currentTimeMillis(); //Measuring Probe
        try (Socket socket = new Socket(args[0], port)) {
            long measurementTwoSecond = System.currentTimeMillis(); //Measuring Probe
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            ArrayList < Long > measurementOneMilliseconds = new ArrayList < Long > ();
            /*This code could easily be placed inside a loop, but I kept it this way because I was having issues
             * and I wanted to run the function individually each time. I fixed the issue in the receiveFile function,
             * documented inside that function.*/
            /*The purpose of the below code is to add each roundtrip (sending the request and receiving the file)
             * to an arraylist that will store all 10 measurements such that we can do calculations on those measurements.*/
            measurementOneMilliseconds.add(receiveFile("clientUDP/joke1.png"));
            measurementOneMilliseconds.add(receiveFile("clientUDP/joke2.png"));
            measurementOneMilliseconds.add(receiveFile("clientUDP/joke3.png"));
            measurementOneMilliseconds.add(receiveFile("clientUDP/joke4.png"));
            measurementOneMilliseconds.add(receiveFile("clientUDP/joke5.png"));
            measurementOneMilliseconds.add(receiveFile("clientUDP/joke6.png"));
            measurementOneMilliseconds.add(receiveFile("clientUDP/joke7.png"));
            measurementOneMilliseconds.add(receiveFile("clientUDP/joke8.png"));
            measurementOneMilliseconds.add(receiveFile("clientUDP/joke9.png"));
            measurementOneMilliseconds.add(receiveFile("clientUDP/joke10.png"));

            /*Prints the results of each calculation to the console. Note that it does not display the individual trip times
             * but rather the total, cumulative trip time. The individual trip times are still stored in the arraylist
             * and could be easily printed.*/
            System.out.println("");
            System.out.println("Measurement 1: ");
            System.out.println("Total Round Trip Time: " + getTotal((measurementOneMilliseconds)) + " milliseconds.");
            System.out.println("Minimum Round Trip Time: " + Collections.min(measurementOneMilliseconds) + " milliseconds.");
            System.out.println("Mean Round Trip Time: " + getAverage((measurementOneMilliseconds)) + " milliseconds.");
            System.out.println("Maximum Round Trip Time: " + Collections.max(measurementOneMilliseconds) + " milliseconds.");
            System.out.println("Standard Deviation: " + getStandardDeviation(measurementOneMilliseconds) + " milliseconds.");
            System.out.println("");
            System.out.println("Measurement 2: ");
            System.out.println("DNS Resolution Time: " + (measurementTwoSecond - measurementTwoFirst) + " milliseconds.");

            dataInputStream.close();
            dataInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static double getTotal(ArrayList < Long > experiment) { // This function just sums up the values in the parameter
        double total = 0;
        for (int m = 0; m < experiment.size(); m++) {
            total += experiment.get(m);
        }
        return total;
    }
    private static double getAverage(ArrayList < Long > experiment) {
        double average = 0;
        for (int m = 0; m < experiment.size(); m++) {
            average += experiment.get(m);
        }
        average /= experiment.size();
        return average;
    } //This function calculates the avg of parameter

    private static double getStandardDeviation(ArrayList < Long > experiment) { // This function calculates the sd of parameter
        /*Standard deviation formula: o = sqrt((sum[x-i]^2)/n)*/
        double mean = getAverage(experiment);

        double squareSum = 0;

        for (int i = 0; i < experiment.size(); i++) {

            squareSum += Math.pow(experiment.get(i) - mean, 2);

        }

        return Math.sqrt((squareSum) / (experiment.size() - 1));

    }

    private static long receiveFile(String fileName) throws Exception {
        /*This is the function for loading files from the server and writing them to the file. Note that this sends one
         * file and must be called multiple times to send multiple files.*/
        long startTime = System.currentTimeMillis(); //Measuring probe
        // System.out.println("Requested time: " + System.currentTimeMillis());
        int bytes = 0;
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);

        long size = dataInputStream.readLong(); // read file size
        byte[] buffer = new byte[4 * 1024];
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
            /*The above condition checks whether the buffer array is empty. If it is not, there must be more items to copy*/
            int arrayArea = 0;
            fileOutputStream.write(buffer, 0, bytes);
            size -= bytes; // read upto file size
        }
        long endTime = System.currentTimeMillis(); // Measuring probe
        fileOutputStream.close();
        return (endTime - startTime); // This is the round trip time for this individual file being requested and written
    }
}