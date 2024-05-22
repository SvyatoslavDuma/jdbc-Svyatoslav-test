package dao;

import model.Child;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import utils.DBUtil;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChildDBTest {

    private  ChildDB childDB;
    private Connection connection;

    @BeforeEach
    public void beforeEach() throws SQLException, IOException {
        DBUtil dbUtil = new DBUtil();
        dbUtil.executeFile("init.sql");
        connection = DBUtil.getConnection();
        childDB = new ChildDB(connection);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void addNewChild() throws SQLException {
        int oldCount = DBUtil.totalCount("child");
        Child newChild = new Child(null, "John", "Doe", LocalDate.of(2010, 1, 1));
        Child addedChild = childDB.add(newChild);
        int newCount = DBUtil.totalCount("child");

        assertNotNull(addedChild.id());
        assertEquals(newCount, oldCount + 1, "The number of children should have increased by one");
    }

    @Test
    void updateExistingChild() throws SQLException {

        String verifyQuery = "SELECT COUNT(*) FROM child WHERE id = 1";
        try (PreparedStatement pst = connection.prepareStatement(verifyQuery);
             ResultSet rs = pst.executeQuery()) {
            rs.next();
            int count = rs.getInt(1);
            assertTrue(count > 0, "Record with id 1 should exist before updating");
        }

        Child updatedChild = new Child(1L, "Jane", "Doe", LocalDate.of(2012, 5, 5));
        boolean actual = childDB.update(updatedChild);

        assertTrue(actual, "Should return true if one record was updated");

        String fetchQuery = "SELECT first_name, last_name, birth_date FROM child WHERE id = 1";
        try (PreparedStatement pst = connection.prepareStatement(fetchQuery);
             ResultSet rs = pst.executeQuery()) {
            rs.next();
            assertEquals("Jane", rs.getString("first_name"));
            assertEquals("Doe", rs.getString("last_name"));
            assertEquals(Date.valueOf(LocalDate.of(2012, 5, 5)), rs.getDate("birth_date"));
        }
    }


    @Test
    void deleteExistingChild() throws SQLException {
        String verifyQuery = "SELECT COUNT(*) FROM child WHERE id = 2";
        try (PreparedStatement pst = connection.prepareStatement(verifyQuery);
             ResultSet rs = pst.executeQuery()) {
            rs.next();
            int count = rs.getInt(1);
            assertTrue(count > 0, "Record with id 2 should exist before deleting");
        }

        boolean actual = childDB.delete(2L);

        assertTrue(actual, "Should return true if one record was deleted");

        try (PreparedStatement pst = connection.prepareStatement(verifyQuery);
             ResultSet rs = pst.executeQuery()) {
            rs.next();
            int count = rs.getInt(1);
            assertEquals(0, count, "Record with id 2 should not exist after deleting");
        }
    }

    @Test
    void getAllChildrenAtLeastAge() throws SQLException {
        int age = 10;
        List<Child> children = childDB.allAtLeastAge(age);

        assertNotNull(children, "List of children should not be null");
        for (Child child : children) {
            assertTrue(LocalDate.now().getYear() - child.birthDate().getYear() >= age,
                    "Each child should be at least " + age + " years old");
        }
    }

    @Test
    void getAllChildrenWithoutBirthDate() throws SQLException {
        List<Child> children = childDB.allWithoutBirthDate();

        assertNotNull(children, "List of children should not be null");
        for (Child child : children) {
            assertNull(child.birthDate(), "Each child should have null birth date");
        }
    }
}
