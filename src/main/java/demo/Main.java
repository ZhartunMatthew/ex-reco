package demo;

import demo.nlp.EntityExtractor;
import demo.nlp.IntentRecognizer;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final String INTENT_TRAIN_DIRECTORY = "data/train/intent";
    private static final String ENTITY_TRAIN_DIRECTORY = "data/train/entity";
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        IntentRecognizer intentRecognizer = new IntentRecognizer(INTENT_TRAIN_DIRECTORY + "/intents_mapping.txt");
        EntityExtractor entityExtractor = new EntityExtractor();

        intentRecognizer.train(INTENT_TRAIN_DIRECTORY, true);
        entityExtractor.train(ENTITY_TRAIN_DIRECTORY, true);

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

    }
}
