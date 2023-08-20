package seng202.team0.io;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import seng202.team0.models.CrashSeverity;
import seng202.team0.models.Crash;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class handling the importing of crash data from CSV files.
 * @author Neil Alombro
 * @author Zipporah Price
 * @author Angelica Silva
 */

public class CrashCSVImporter {

    /**
     * List of all the crashes as Point objects from the given file object
     * @param file
     * @return points list of all crashes from the given file
     * @throws IOException
     */
    public List<Crash> crashListFromFile(File file) {
        List<Crash> pointList = new ArrayList<Crash>();
        try (FileReader reader = new FileReader(file)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                csvReader.skip(1);
                String[] line = null;
                while ((line = csvReader.readNext()) != null) {
                    Crash currentPoint = crashFromString(line);
                    if (currentPoint != null) {
                        pointList.add(currentPoint);

                        // TODO manual testing to see if it works
                        System.out.println(currentPoint.getWeather());
                    }
                }
                return pointList;
            } catch (CsvValidationException e) {
                System.out.println(e.getMessage());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Takes a list of strings representing variables
     * from the crash data CSV file and returns a Point object
     * @param crashVariables
     * @return Point object initialised with given crashVariables
     */
    public Crash crashFromString(String[] crashVariables) {
        // TODO think about numbers not existing, ie empty string instead of check 0

        try {
            int objectId = Integer.parseInt(crashVariables[0]);
            boolean bicycleInvolved = Integer.parseInt(crashVariables[2]) > 0;
            boolean busInvolved = Integer.parseInt(crashVariables[4]) > 0;

            // TODO look at different type of car variables
            boolean carInvolved = Integer.parseInt(crashVariables[5]) > 0;
            int crashYear = Integer.parseInt(crashVariables[14]);
            String crashLocation1 = crashVariables[9];
            String crashLocation2 = crashVariables[10];
            CrashSeverity severity = CrashSeverity.stringToCrashSeverity(crashVariables[12]);
            boolean holiday = !Objects.equals(crashVariables[22], "");
            boolean mopedInvolved = Integer.parseInt(crashVariables[28]) > 0;
            boolean motorcycleInvolved = Integer.parseInt(crashVariables[29]) > 0;
            boolean parkedVehicleInvolved = Integer.parseInt(crashVariables[35]) > 0;
            boolean pedestrianInvolved = Integer.parseInt(crashVariables[36]) > 0;

            // TODO create enum with region list
            String region = crashVariables[39];
            boolean schoolBusInvolved = Integer.parseInt(crashVariables[44]) > 0;
            int speedLimit = Integer.parseInt(crashVariables[47]);
            boolean trainInvolved = Integer.parseInt(crashVariables[57]) > 0;
            boolean truckInvolved = Integer.parseInt(crashVariables[59]) > 0;

            // TODO think about weatherA vs weatherB
            String weather = crashVariables[65];
            float longitude = Float.parseFloat(crashVariables[68]);
            float latitude = Float.parseFloat(crashVariables[67]);

            return new Crash(objectId, speedLimit, crashYear, crashLocation1, crashLocation2, region, weather,
                    longitude, latitude, bicycleInvolved, busInvolved, carInvolved, holiday, mopedInvolved,
                    motorcycleInvolved, parkedVehicleInvolved, pedestrianInvolved, schoolBusInvolved, trainInvolved, truckInvolved);
        } catch (NumberFormatException e) {
            // TODO replace with something actually useful like a log
            System.out.println(e);
            throw new RuntimeException(e);

        }
        // TODO uncomment once logging done on catch
//        return null;
    }

}
