import apis.Lifx;

import java.util.Scanner;

/**
 * Created by haydenchristensen on 2/11/17.
 */
public class MainClass {

    public static void main(String[] args) {

        askForUserInput();

    }

    private static void askForUserInput() {
        while (true) {
            Scanner reader = new Scanner(System.in);
            System.out.println("Enter a command: ");
            String input = reader.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            if (!Lifx.processPossibleCommand(input)) {
                System.out.println("Sorry that isn't a valid command");
            }

        }

    }

}
