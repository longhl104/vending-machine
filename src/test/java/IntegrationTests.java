import exceptions.CancellationException;
import exceptions.TimeoutException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;

/**
 *  Tests to confirm correct implementation of the user stories.
 */
public class IntegrationTests {

    private Main instance;

    @Rule public final TextFromStandardInputStream systemIn = emptyStandardInputStream();
    @Rule public final SystemOutRule systemOut = new SystemOutRule().enableLog();
    @Rule public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    /**
     * Creating an instance of the main class, and setting the default timeout value.
     */
    @Before
    public void setup() {
        this.instance = new Main();
        Main.setTimeout(30);
    }


    @Test
    public void simpleTest() {
        String correctOut = "==================\n" +
                "\n" +
                "Welcome to the Vending Machine!\n" +
                "\n" +
                "Available selections:\n" +
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
                "[ID 14] Sneakers - $1.00 (10 item(s) in stock)\n" +
                "\n" +
                "Please select a product. Type 'END' to proceed to payment. Type 'CANCEL' to cancel transaction. Type 'HELP' for instructions.\n" +
                "\n" +
                "> \n" +
                "You have selected Original. There are 2 item(s) in stock. How many would you like to purchase (Type a number)?\n" +
                "\n" +
                "> \n" +
                "You have selected:\n" +
                "[ID 0] Original - quantity 1 @ $5.00 each = total $5.00\n" +
                "\n" +
                "Available selections:\n" +
                "[ID 0] Original - $5.00 (1 item(s) in stock)\n" +
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
                "[ID 14] Sneakers - $1.00 (10 item(s) in stock)\n" +
                "\n" +
                "Please select a product. Type 'END' to proceed to payment. Type 'CANCEL' to cancel transaction. Type 'HELP' for instructions.\n" +
                "\n" +
                "> \n" +
                "Grand total is $5.00 - Please insert money:\n" +
                "\n" +
                "> \n" +
                "Payment successful.\n" +
                "\n" +
                "You have purchased:\n" +
                "[ID 0] Original - quantity 1 @ $5.00 each = total $5.00\n" +
                "\n" +
                "Thank you for your purchase!\n" +
                "\n" +
                "==================\n" +
                "\n" +
                "Welcome to the Vending Machine!\n" +
                "\n" +
                "Available selections:\n" +
                "[ID 0] Original - $5.00 (1 item(s) in stock)\n" +
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
                "[ID 14] Sneakers - $1.00 (10 item(s) in stock)\n" +
                "\n" +
                "Please select a product. Type 'END' to proceed to payment. Type 'CANCEL' to cancel transaction. Type 'HELP' for instructions.\n" +
                "\n" +
                "> \n" +
                "Exiting system. Have a nice day! :)\n";

        exit.expectSystemExit();
        exit.checkAssertionAfterwards(new Assertion() {
            @Override
            public void checkAssertion() throws Exception {
                    assertEquals(correctOut, normalize(systemOut.getLog()));
                }
            });

        systemIn.provideLines("0", "1", "end", "5", "quit");


       instance.main(null);

    }

    /**
     * Removes carriage return characters (\r) from a String.
     * Used to remove unwanted invisible characters when testing console output.
     *
     * @param toNormalize the String object to normalize.
     * @return a new String object with all \r characters removed.
     */
    private String normalize(String toNormalize) {
        return toNormalize.replaceAll("\r", "");
    }

}
