import java.io.*;
import java.lang.*;
import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;

public class tcp_server {
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]); // This is to get the port number from the only argument.
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("listening to port:" + port);
            Socket clientSocket = serverSocket.accept(); // TCP must make a connection and accept the connection
            System.out.println(clientSocket + " connected.");
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

            List < Integer > lstIndexes = new ArrayList < Integer > ();
            for (int i = 1; i <= 10; i++) {
                lstIndexes.add(i);
            }
            Collections.shuffle(lstIndexes); // This is for randomizing the sending of memes. The array is shuffled
            // so that the items are randomly ordered. Then I can call the items in that random order to randomly
            //call things
            ArrayList < Long > measurementThreeMilliseconds = new ArrayList < Long > ();
            for (Integer index: lstIndexes) {
                switch (index) {
                    case 1:
                        measurementThreeMilliseconds.add(sendFile("server/joke1.png"));
                        break;
                    case 2:
                        measurementThreeMilliseconds.add(sendFile("server/joke2.png"));
                        break;
                    case 3:
                        measurementThreeMilliseconds.add(sendFile("server/joke3.png"));
                        break;
                    case 4:
                        measurementThreeMilliseconds.add(sendFile("server/joke4.png"));
                        break;
                    case 5:
                        measurementThreeMilliseconds.add(sendFile("server/joke5.png"));
                        break;
                    case 6:
                        measurementThreeMilliseconds.add(sendFile("server/joke6.png"));
                        break;
                    case 7:
                        measurementThreeMilliseconds.add(sendFile("server/joke7.png"));
                        break;
                    case 8:
                        measurementThreeMilliseconds.add(sendFile("server/joke8.png"));
                        break;
                    case 9:
                        measurementThreeMilliseconds.add(sendFile("server/joke9.png"));
                        break;
                    case 10:
                        measurementThreeMilliseconds.add(sendFile("server/joke10.png"));
                        break;
                }
            }

            System.out.println("");

            /*Prints the results of each calculation to the console. Note that it does not display the individual trip times
             * but rather the total, cumulative trip time. The individual trip times are still stored in the arraylist
             * and could be easily printed.*/
            System.out.println("Measurement 3: ");
            System.out.println("Total Round Trip Time: " + getTotal((measurementThreeMilliseconds)) + " milliseconds.");
            System.out.println("Minimum Round Trip Time: " + Collections.min(measurementThreeMilliseconds) + " milliseconds.");
            System.out.println("Mean Round Trip Time: " + getAverage((measurementThreeMilliseconds)) + " milliseconds.");
            System.out.println("Maximum Round Trip Time: " + Collections.max(measurementThreeMilliseconds) + " milliseconds.");
            System.out.println("Standard Deviation: " + getStandardDeviation(measurementThreeMilliseconds) + " milliseconds.");
            dataInputStream.close();
            dataOutputStream.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long sendFile(String path) throws Exception {
        int bytes = 0;
        long requestTime = System.currentTimeMillis(); // Measurement Probe
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        // send file size
        dataOutputStream.writeLong(file.length());
        // break file into chunks
        byte[] buffer = new byte[4 * 1024];
        while ((bytes = fileInputStream.read(buffer)) != -1) {
            dataOutputStream.write(buffer, 0, bytes);
            dataOutputStream.flush();
        }
        long finishTime = System.currentTimeMillis(); // Measurement Probe
        fileInputStream.close();
        return (finishTime - requestTime); // This is the round trip time for the file being requested and sent
    }

    private static double getTotal(ArrayList < Long > experiment) {
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
    }

    private static double getStandardDeviation(ArrayList < Long > experiment) {
        /*Standard deviation formula: o = sqrt((sum[x-i]^2)/n)*/
        double mean = getAverage(experiment);

        double squareSum = 0;

        for (int i = 0; i < experiment.size(); i++) {

            squareSum += Math.pow(experiment.get(i) - mean, 2);

        }

        return Math.sqrt((squareSum) / (experiment.size() - 1));

    }

}