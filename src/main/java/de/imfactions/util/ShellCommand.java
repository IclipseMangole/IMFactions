package de.imfactions.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Diese Klasse wurde von feuerwehr45 am 14.10.2016 erstellt.
 * Der Inhalt ist Geistiges Eigentum von feuerwehr45 und ist damit Rechtlich geschützt.
 * Nutzung ohne das schriftliche Einverständnis ist untersagt!
 */
public class ShellCommand {

    public ShellCommand(String ShellCommand) {
        doshellcommand(ShellCommand);
    }

    public ShellCommand(ArrayList<String> ShellCommand) {
        for (String shellcommand : ShellCommand) {
            doshellcommand(shellcommand);
        }
    }

    private String doshellcommand(String shellcommand) {
        System.out.println("[Shell] " + shellcommand);
        StringBuffer output = new StringBuffer();
        Process p;
        try {

            p = Runtime.getRuntime().exec(shellcommand);
            p.waitFor(1, TimeUnit.MINUTES);
            InputStreamReader inreader = new InputStreamReader(p.getInputStream());
            @SuppressWarnings("resource")
            BufferedReader reader = new BufferedReader(inreader);
            String line = "";

            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }
}
