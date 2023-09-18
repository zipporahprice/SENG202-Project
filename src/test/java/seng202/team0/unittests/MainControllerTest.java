package seng202.team0.unittests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team0.business.CrashManager;
import seng202.team0.repository.SQLiteQueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainControllerTest {

    // TODO make integration test for filtering
//    @Test
//    void testingSeveritiesSelected() {
//        // Imitating checking all the boxes
//        List<Integer> severitiesSelected = new ArrayList<Integer>();
//        severitiesSelected.add(1);
//        severitiesSelected.add(2);
//        severitiesSelected.add(4);
//        severitiesSelected.add(8);
//
//        List crashes = SQLiteQueryBuilder
//                .create()
//                .select("object_id")
//                .from("crashes")
//                .where("severity IN (" + severitiesSelected.stream().map(Object::toString).collect(Collectors.joining(", ")) + ")")
//                .build();
//
//        List expectedCrashes = null;
//
//        try {
//            CrashManager manager = new CrashManager();
//            expectedCrashes = manager.getCrashes();
//        } catch (SQLException e) {
//            System.out.println(e);
////        }
//
//        assert expectedCrashes != null;
//        Assertions.assertEquals(crashes.size(), expectedCrashes.size());
//    }
}
