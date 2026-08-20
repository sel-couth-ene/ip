import java.util.Scanner;

public class Sel {
    public static void main(String[] args) {
        String banner = " ____  _____ _     \n"
                      + "/ ___|| ____| |    \n"
                      + "\\___ \\|  _| | |    \n"
                      + " ___) | |___| |___ \n"
                      + "|____/|_____|_____|\n";
        System.out.println(banner);

        String line_break = "-----------------------------------------";
        System.out.println("Sup, I'm Sel.");
        System.out.println(line_break);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String command = scanner.nextLine();
            if (command.equals("bye")) {
                System.out.println(line_break);
                System.out.println("Bye see ya later alligator.");
                System.out.println(line_break);
                break;
            }
            System.out.println(command);
            System.out.println(line_break);
        }
        scanner.close();
    }
}
