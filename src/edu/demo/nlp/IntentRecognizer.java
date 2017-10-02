package demo.nlp;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

import javafx.util.Pair;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IntentRecognizer {

    private DoccatModel model;
    private HashMap<String, String> intentMapper = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(IntentRecognizer.class);

    public IntentRecognizer(String trainingDirectory) {
        try (BufferedReader br = new BufferedReader(new FileReader(trainingDirectory + "/intents_mapping.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] entries = line.split("::");
                intentMapper.put(entries[0], entries[1]);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void train(String trainingDirectory, String language, boolean validate) {
        try (InputStream inputStream = new FileInputStream(trainingDirectory + "/dataset.txt")) {
            ObjectStream<String> lineStream = new PlainTextByLineStream(inputStream, "UTF-8");
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
            model = DocumentCategorizerME.train(language, sampleStream, 0, 50);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (validate) {
            this.validate(trainingDirectory + "/validate.txt");
        }
    }

    public Pair<Integer, String> parse(String sentence) {
        DocumentCategorizerME classifier = new DocumentCategorizerME(model);
        double[] outcomes = classifier.categorize(sentence);
        String category = classifier.getBestCategory(outcomes);
        Pair<Integer, String> intent;

        try {
            intent = new Pair<>(Integer.parseInt(category), intentMapper.get(category));
        } catch (Exception ex) {
            intent = new Pair<>(Integer.parseInt("-1"), "undefined");
        }

        return intent;
    }

    private void validate(String filePath) {
        LOG.info("============= VALIDATION STARTED ================");
        int amount = 0, correct = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                amount += 1;
                correct = this.validateQuery(line) ? correct + 1 : correct;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        LOG.info("Validated queries: {}, correct queries predicted: {}/{}", amount, correct, amount);
        LOG.info("============= VALIDATION FINISHED ================");
    }

    private boolean validateQuery(String query) {
        String[] entries = query.split("::");

        Pair<Integer, String> expected = new Pair<>(new Integer(entries[1]), intentMapper.get(entries[1]));
        Pair<Integer, String> actual = this.parse(entries[0]);

        LOG.info("Query:    {}", entries[0]);
        LOG.info("Actual:   {}", actual);
        LOG.info("Expected: {}", expected);
        LOG.info("=============================");

        return Objects.equals(actual, expected);
    }
}