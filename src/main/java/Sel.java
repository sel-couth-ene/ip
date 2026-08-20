import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

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

        List<String> data = new ArrayList<>();

        while (true) {

            String command = scanner.nextLine();
            
            if (command.equals("bye")) {
                System.out.println(line_break + "\nBye see ya later alligator.\n" + line_break);
                break;
            }

            if (command.equals("list")) {
                System.out.println(line_break);
                for (int i = 0; i < data.size(); i++) {
                    System.out.println((i + 1) + ". " + data.get(i));
                }
                System.out.println(line_break);
                continue;
            }
            data.add(command);
            System.out.println(line_break + "\nadded: " + command + "\n" + line_break);
        }
        scanner.close();
    }
}