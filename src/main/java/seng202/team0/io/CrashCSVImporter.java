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
                String[] line;
                while ((line = csvReader.readNext()) != null) {
                    if (!Objects.equals(line[0], "")) {
                        Crash currentPoint = crashFromString(line);
                        if (currentPoint != null) {
                            pointList.add(currentPoint);
                        }
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

    private int changeEmptyToZero(String string) {
        if (string != "" && string != null) {
            return Integer.parseInt(string);
        } else {
            return 0;
        }
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
            int objectId = changeEmptyToZero(crashVariables[0]);
            boolean bicycleInvolved = changeEmptyToZero(crashVariables[2]) > 0;
            boolean busInvolved = changeEmptyToZero(crashVariables[4]) > 0;

            // TODO look at different type of car variables
            boolean carInvolved = changeEmptyToZero(crashVariables[5]) > 0;
            int crashYear = changeEmptyToZero(crashVariables[14]);
            String crashLocation1 = crashVariables[9];
            String crashLocation2 = crashVariables[10];

            String severity = crashVariables[12];

            boolean holiday = !Objects.equals(crashVariables[22], "");
            boolean mopedInvolved = changeEmptyToZero(crashVariables[28]) > 0;
            boolean motorcycleInvolved = changeEmptyToZero(crashVariables[29]) > 0;
            boolean parkedVehicleInvolved = changeEmptyToZero(crashVariables[35]) > 0;
            boolean pedestrianInvolved = changeEmptyToZero(crashVariables[36]) > 0;

            // TODO create enum with region list
            String region = crashVariables[39];
            boolean schoolBusInvolved = changeEmptyToZero(crashVariables[44]) > 0;
            int speedLimit = changeEmptyToZero(crashVariables[47]);
            boolean trainInvolved = changeEmptyToZero(crashVariables[57]) > 0;
            boolean truckInvolved = changeEmptyToZero(crashVariables[59]) > 0;

            // TODO think about weatherA vs weatherB
            String weather = crashVariables[65];
            float longitude = Float.parseFloat(crashVariables[68]);
            float latitude = Float.parseFloat(crashVariables[67]);

            return new Crash(objectId, speedLimit, crashYear, crashLocation1, crashLocation2, severity, region, weather,
                    longitude, latitude, bicycleInvolved, busInvolved, carInvolved, holiday, mopedInvolved,
                    motorcycleInvolved, parkedVehicleInvolved, pedestrianInvolved, schoolBusInvolved, trainInvolved, truckInvolved);
        } catch (NumberFormatException e) {
            // TODO replace with something actually useful like a log
            System.out.println(e);
        }
        // TODO uncomment once logging done on catch
        return null;
    }

}
