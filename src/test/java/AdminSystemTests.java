import exceptions.CancellationException;
import exceptions.TimeoutException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;

public class AdminSystemTests {

    private Main instance;
    private final InputStream stdin = System.in;
    private String availableOutput;

    @Rule public final TextFromStandardInputStream systemIn = emptyStandardInputStream();
    @Rule public final SystemOutRule systemOut = new SystemOutRule().enableLog();

    /**
     * Creating an instance of the main class, and setting the default timeout value.
     */
    @Before
    public void setup() {
        this.instance = new Main();
        Main.setTimeout(30);
        this.availableOutput = "\nProducts:\n" +
                        "[ID 0] Original - $5.00 (2 item(s) in stock)\n" +
                        "[ID 1] Chicken - $3.50 (10 item(s) in stock)\n" +
                        "[ID 2] BBQ - $3.50 (10 item(s) in stock)\n" +
                        "[ID 3] Sweet Chillies - $3.50 (10 item(s) in stock)\n" +
                        "[ID 4] Sour Worms - $3.00 (10 item(s) in stock)\n" +
                        "[ID 5] Jellybeans - $3.00 (10 item(s) in stock)\n" +
                        "[ID 6] Little Bears - $3.00 (10 item(s) in stock)\n" +
                        "[ID 7] Part Mix - $3.50 (10 item(s) in stock)\n" +
                        "[ID 8] Water - $2.50 (10 item(s) in stock)\n" +
                        "[ID 9] Soft Drink - $3.00 (10 item(s) in stock)\n" +
                        "[ID 10] Juice - $3.50 (10 item(s) in stock)\n" +
                        "[ID 11] M&M - $1.00 (10 item(s) in stock)\n" +
                        "[ID 12] Bounty - $1.00 (10 item(s) in stock)\n" +
                        "[ID 13] Mars - $1.00 (10 item(s) in stock)\n" +
                        "[ID 14] Sneakers - $1.00 (10 item(s) in stock)\n";
    }

    /**
     * Testing if admin can enter admin mode.
     * We expect admin to be able to enter as "admin" is the default admin id.
     * Test passes because of the above.
     */
    @Test
    public void Main_handleInput_enterAdminModeTest() {

        systemIn.provideLines("ADMIN admin");

        try {
            instance.run();
        } catch (TimeoutException | CancellationException ignored) {
        }

        assertTrue(systemOut.getLog().contains("Welcome Admin \"admin\" to the admin system!\n"));

    }

    /**
     * Testing that admin mode is not entered with an invalid admin id
     * We do not expect user to be able to enter admin mode.
     * Test passes because "bill" is not a valid admin id.
     */
    @Test
    public void Main_handleInput_invalidAdminIdTest() {

        systemIn.provideLines("ADMIN bill");

        try {
            instance.run();
        } catch (TimeoutException | CancellationException ignored) {
        }

        assertTrue(systemOut.getLog().contains("Admin id \"bill\" does not exist in the system!"));

    }

    /**
     * Testing that admin mode is not entered when no admin id provided.
     * We do not expect user to be able to enter admin mode.
     * Test passes because no admin id was provided, so admin mode was not entered.
     */
    @Test
    public void Main_handleInput_invalidSyntaxTest() {

        systemIn.provideLines("ADMIN");

        try {
            instance.run();
        } catch (TimeoutException | CancellationException ignored) {
        }

        assertTrue(systemOut.getLog().contains("Invalid input. Type HELP for instructions."));

    }

    /**
     * Testing that available products are listed in admin mode.
     * We expect all products in the vending machine to be listed.
     * Test passes because all products are listed.
     */
    @Test
    public void handleInput_availableTest() {

        systemIn.provideLines("ADMIN admin", "AVAILABLE");

        try {
            instance.run();
        } catch (TimeoutException | CancellationException ignored) {
        }

        assertTrue(systemOut.getLog().contains(availableOutput));

    }

    /**
     * Testing if available products displayed in admin mode when command entered in lower case.
     * We expect products to be listed as commands are case insensitive
     * Test passes because commands are read with ".equalsIgnoreCase"
     */
    @Test
    public void handleInput_availableLowercaseTest() {

        systemIn.provideLines("ADMIN admin", "available");

        try {
            instance.run();
        } catch (TimeoutException | CancellationException ignored) {
        }

        assertTrue(systemOut.getLog().contains(availableOutput));

    }

    /**
     * Testing that available products do not show when any command other than "available" entered in
     * admin mode.
     * We do not expect available products to be displayed.
     * Test passes because the only valid command to display products is "available" (case insensitive)
     */
    @Test
    public void handleInput_availableInvalidTest() {

        systemIn.provideLines("ADMIN admin", "available products");

        try {
            instance.run();
        } catch (TimeoutException | CancellationException ignored) {
        }

        assertFalse(systemOut.getLog().contains(availableOutput));

    }

    /**
     * Testing that admin is able to add another admin id.
     * We expect the id "admin2" to be added.
     */
    @Test
    public void addAdminIdTest_Success() {

        AdminSystem adminSystem = new AdminSystem();
        adminSystem.addAdminId("admin2");
        Set<String> actual = adminSystem.getAdminIDs();
        Set<String> expected = new HashSet<>(Arrays.asList("admin", "admin2"));
        assertEquals(expected, actual);

        assertTrue(systemOut.getLog().contains("\nAdmin id \"admin2\" has been successfully added to the system!\n"));
    }

    /**
     * Testing that a duplicate admin id will not be added.
     * We do not expect the id "admin2" to be added a second time.
     * Test passes as system checks if an admin id already exists before adding it.
     */
    @Test
    public void addAdminIdTest_Duplicate() {

        AdminSystem adminSystem = new AdminSystem();
        adminSystem.addAdminId("admin2");
        adminSystem.addAdminId("admin2");
        Set<String> actual = adminSystem.getAdminIDs();
        Set<String> expected = new HashSet<>(Arrays.asList("admin", "admin2"));
        assertEquals(expected, actual);

        assertTrue(systemOut.getLog().contains("\nAdmin id \"admin2\" has been successfully added to the system!\n"));
        assertTrue(systemOut.getLog().contains("\nAdmin id \"admin2\" has been already stored in the system!\n"));

    }

    /**
     * Testing if an admin id can be removed.
     * We expect "admin2" to be removed.
     * Test passes as "admin2" was one of the admin ids before removal, so it is now removed.
     */
    @Test
    public void removeAdminIdTest_Success() {

        AdminSystem adminSystem = new AdminSystem();
        adminSystem.addAdminId("admin2");
        adminSystem.removeAdminId("admin2");
        Set<String> actual = adminSystem.getAdminIDs();
        Set<String> expected = new HashSet<>(Arrays.asList("admin"));
        assertEquals(expected, actual);

        assertTrue(systemOut.getLog().contains("\nAdmin id \"admin2\" has been successfully removed from the system!\n"));

    }

    /**
     * Testing that nothing is changed if admin attempts to remove a non-existent id.
     * We do not expect any changes to the valid admin ids.
     * Test passes as the request to remove admin id "invalid" from the system is rejected.
     */
    @Test
    public void removeAdminIdTest_Invalid() {

        AdminSystem adminSystem = new AdminSystem();
        adminSystem.removeAdminId("invalid");
        Set<String> actual = adminSystem.getAdminIDs();
        Set<String> expected = new HashSet<>(Arrays.asList("admin"));
        assertEquals(expected, actual);

        assertTrue(systemOut.getLog().contains("\nAdmin id \"invalid\" does not exist in the system!\n"));
    }

}
