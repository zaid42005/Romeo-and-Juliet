


import java.io.*;
import java.net.Socket;
import java.net.InetAddress;
import javafx.util.Pair;
public class PlayWriter {
    private Romeo  myRomeo  = null;
    private InetAddress RomeoAddress = null;
    private int RomeoPort = 0;
    private Socket RomeoMailbox = null;
    private Juliet myJuliet = null;
    private InetAddress JulietAddress = null;
    private int JulietPort = 0;
    private Socket JulietMailbox = null;
    double[][] theNovel = null;
    int novelLength = 0;
    public PlayWriter()
    {
        novelLength = 500; //Number of verses
        theNovel = new double[novelLength][2];
        theNovel[0][0] = 0;
        theNovel[0][1] = 1;
    }
    public void createCharacters() {
        System.out.println("PlayWriter: Romeo enters the stage.");
        myRomeo = new Romeo(0);
        System.out.println("PlayWriter: Juliet enters the stage.");
        myJuliet = new Juliet(1);
        myJuliet.start();
        myRomeo.start();


    }
    //Meet the lovers and start letter communication
    public void charactersMakeAcquaintances() {
        Pair<InetAddress, Integer> R = myRomeo.getAcquaintance();
        RomeoAddress = R.getKey();
        RomeoPort = R.getValue();
        System.out.println("PlayWriter: I've made acquaintance with Romeo");
        Pair<InetAddress,Integer> J = myJuliet.getAcquaintance();
        JulietAddress = J.getKey();
        JulietPort = J.getValue();
        System.out.println("PlayWriter: I've made acquaintance with Juliet");
    }
    //Request next verse: Send letters to lovers communicating the partner's love in previous verse
    public void requestVerseFromRomeo(int verse) {
        try {
            System.out.println("PlayWriter: Requesting verse " + verse + " from Romeo. -> (" + theNovel[verse-1][1] + ")");
            RomeoMailbox = new Socket(RomeoAddress,RomeoPort);
            PrintWriter versesRomeo = new PrintWriter(RomeoMailbox.getOutputStream(), true);
            versesRomeo.println(String.valueOf(theNovel[verse-1][1]) + "J");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //Request next verse: Send letters to lovers communicating the partner's love in previous verse
    public void requestVerseFromJuliet(int verse) {
        try {
            System.out.println("PlayWriter: Requesting verse " + verse + " from Juliet. -> (" + theNovel[verse-1][0] + ")");
            JulietMailbox = new Socket(JulietAddress,JulietPort);
            PrintWriter versesJuli = new PrintWriter(JulietMailbox.getOutputStream(), true);
            versesJuli.println(String.valueOf(theNovel[verse-1][0]) + "R");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //Receive letter from Romeo with renovated love for current verse
    public void receiveLetterFromRomeo(int verse) {
        try {
            BufferedReader letterRomeo = new BufferedReader (new InputStreamReader(RomeoMailbox.getInputStream()));
            String tmp = letterRomeo.readLine();
            double d = Double.parseDouble(tmp.substring(0, tmp.length() - 1));
            theNovel[verse][0] = d;
            RomeoMailbox.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("PlayWriter: Romeo's verse " + verse + " -> " + theNovel[verse][0]);
    }

    //Receive letter from Juliet with renovated love fro current verse
    public void receiveLetterFromJuliet(int verse) {
        try {
            BufferedReader letterJuli = new BufferedReader (new InputStreamReader(JulietMailbox.getInputStream()));
            String tmp = letterJuli.readLine();
            double d = Double.parseDouble(tmp.substring(0, tmp.length() - 1));
            theNovel[verse][1] = d;
            JulietMailbox.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("PlayWriter: Juliet's verse " + verse + " -> " + theNovel[verse][1]);
    }
    //Let the story unfold
    public void storyClimax() {
        for (int verse = 1; verse < novelLength; verse++) {
            //Write verse
            System.out.println("PlayWriter: Writing verse " + verse + ".");

            //TO BE COMPLETED
            requestVerseFromRomeo(verse);
            receiveLetterFromRomeo(verse);
            requestVerseFromJuliet(verse);
            receiveLetterFromJuliet(verse);

            System.out.println("PlayWriter: Verse " + verse + " finished.");
        }
    }
    //Character's death
    public void charactersDeath() {
        myJuliet.interrupt();
        myRomeo.interrupt();
    }
    //A novel consists of introduction, conflict, climax and denouement
    public void writeNovel() {
        System.out.println("PlayWriter: The Most Excellent and Lamentable Tragedy of Romeo and Juliet.");
        System.out.println("PlayWriter: A play in IV acts.");
        //Introduction,
        System.out.println("PlayWriter: Act I. Introduction.");
        this.createCharacters();
        //Conflict
        System.out.println("PlayWriter: Act II. Conflict.");
        this.charactersMakeAcquaintances();
        //Climax
        System.out.println("PlayWriter: Act III. Climax.");
        this.storyClimax();
        //Denouement
        System.out.println("PlayWriter: Act IV. Denouement.");
        this.charactersDeath();

    }
    //Dump novel to file
    public void dumpNovel() {
        FileWriter Fw = null;
        try {
            Fw = new FileWriter("RomeoAndJuliet.csv");
        } catch (IOException e) {
            System.out.println("PlayWriter: Unable to open novel file. " + e);
        }

        System.out.println("PlayWriter: Dumping novel. ");
        StringBuilder sb = new StringBuilder();
        for (int act = 0; act < novelLength; act++) {
            String tmp = theNovel[act][0] + ", " + theNovel[act][1] + "\n";
            sb.append(tmp);
            //System.out.print("PlayWriter [" + act + "]: " + tmp);
        }

        try {
            BufferedWriter br = new BufferedWriter(Fw);
            br.write(sb.toString());
            br.close();
        } catch (Exception e) {
            System.out.println("PlayWriter: Unable to dump novel. " + e);
        }
    }

    public static void main (String[] args) {
        PlayWriter Shakespeare = new PlayWriter();
        Shakespeare.writeNovel();
        Shakespeare.dumpNovel();
        System.exit(0);
    }
}