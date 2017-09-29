package demo;

import demo.nlp.EntityExtractor;
import demo.nlp.IntentExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final String INTENT_TRAIN_DIRECTORY = "data/train/intent";
    private static final String ENTITY_TRAIN_DIRECTORY = "data/train/entity";
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        IntentExtractor intentExtractor = new IntentExtractor();
        EntityExtractor entityExtractor = new EntityExtractor();

        intentExtractor.train(INTENT_TRAIN_DIRECTORY);
        entityExtractor.train(ENTITY_TRAIN_DIRECTORY);

        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            if(line.equals("quit")) {
                break;
            }
            String intent = intentExtractor.parse(line);
            Map<String, String> entities = entityExtractor.extract(line);

            LOG.info("Intent:   {}", intent);
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
