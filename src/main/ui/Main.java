package ui;


import java.util.Scanner;

public class Main {
    private static boolean startOver = false;

    public static void main(String[] args) {
        new SchedulerApp();
        showOptions();

        while (startOver) {
            new SchedulerApp();
            startOver = false;
            showOptions();
        }
    }

    public static void showOptions() {
        Scanner input = new Scanner(System.in);

        System.out.println("~~~~~~~~~~~~~~~~ OPTIONS ~~~~~~~~~~~~~~~~");
        System.out.println("TYPE (not case sensitive):");
        System.out.println("EXIT (or e) - to exit.");
        System.out.println("NEW  (or n) - to make a new Timetable.");

        String inputStr = input.nextLine();
        if (inputStr.equalsIgnoreCase("EXIT") || inputStr.equalsIgnoreCase("E")) {
            System.exit(0);
        }
        if (inputStr.equalsIgnoreCase("NEW") || inputStr.equalsIgnoreCase("N")) {
            startOver = true;
        }
    }
}
