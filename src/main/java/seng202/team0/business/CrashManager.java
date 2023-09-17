package seng202.team0.business;

import seng202.team0.io.CrashCSVImporter;
import seng202.team0.models.Crash;
import seng202.team0.repository.CrashDAO;
import seng202.team0.repository.SQLiteQueryBuilder;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class CrashManager {

    private final CrashDAO crashDAO;

    public CrashManager() { crashDAO = new CrashDAO(); }

    /**
     * Saves a file of sales to the repository layer using the specified crash csv importer functionality
     * TODO: handle errors gracefully
     * @param importer crash csv importer object to use
     * @param file file to be imported
     */
    public void addAllCrashesFromFile(CrashCSVImporter importer, File file) throws SQLException {
        List<Crash> sales = importer.crashListFromFile(file);
        crashDAO.addMultiple(sales);
    }

    public Crash getCrash(int id) throws SQLException {
        return crashDAO.getOne(id);
    }

    public List getCrashes() throws SQLException {
        FilterManager filters = FilterManager.getInstance();

        // TODO do not hard code for severities, make it flexible to all filters
        // TODO could instead do a * for columns
        // TODO although * does not work for query builder weirdly
        if (filters.getSeveritiesSelected().size() == 0) {
            return crashDAO.getAll();
        } else {
            return SQLiteQueryBuilder
                    .create()
                    .select("longitude, latitude")
                    .from("crashes")
                    .where("severity IN (" + filters.getSeveritiesSelected().stream().map(Object::toString).collect(Collectors.joining(", ")) + ")")
                    .build();
        }

    }

}
