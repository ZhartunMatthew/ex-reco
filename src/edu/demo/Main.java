package demo;

import demo.nlp.EntityExtractor;
import demo.nlp.IntentRecognizer;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Properties;

public class Main {

    private static final Properties props = new Properties();
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            props.load(new FileInputStream("config/config.properties"));

            IntentRecognizer intentRecognizer = new IntentRecognizer(props.getProperty("INTENT_TRAIN"));
            EntityExtractor entityExtractor = new EntityExtractor();

            intentRecognizer.train(props.getProperty("INTENT_TRAIN"), props.getProperty("LANGUAGE"), true);
            entityExtractor.train(props.getProperty("ENTITY_TRAIN"), props.getProperty("LANGUAGE"),true);

            Scanner scanner = new Scanner(System.in);

            while(true) {
                System.out.print("> ");
                String line = scanner.nextLine();
                if(line.equals("quit")) {
                    break;
                }

                Pair<Integer, String> intent = intentRecognizer.parse(line);
                Map<String, String> entities = entityExtractor.extract(line);

                LOG.info("Intent:   {}", intent.getValue());
                LOG.info("Entities: {}", entities.toString());
                LOG.info("==============================================");

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
