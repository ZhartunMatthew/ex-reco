package demo.nlp;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import demo.Main;
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
    private static final Logger LOG = LoggerFactory.getLogger(IntentRecognizer.class);

    public void train(String trainingDirectory) {
        this.training(trainingDirectory, 0, 50);
    }

    public void train(String trainingDirectory, int c, int iterations) {
        this.training(trainingDirectory, c, iterations);
    }

    private void training(String trainingDirectory, int c, int iterations) {
        try (InputStream inputStream = new FileInputStream(trainingDirectory + "/dataset.txt")) {
            ObjectStream<String> lineStream = new PlainTextByLineStream(inputStream, "UTF-8");
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
            model = DocumentCategorizerME.train("en", sampleStream, c, iterations);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.validate(trainingDirectory + "/validate.txt");
    }

    public Pair<Integer, String> parse(String sentence) {
        DocumentCategorizerME classifier = new DocumentCategorizerME(model);
        double[] outcomes = classifier.categorize(sentence);
        String category = classifier.getBestCategory(outcomes);

        String[] intentNames = {"hello", "bye", "book_room"};
        Pair<Integer, String> intent;
        Integer num = -1;

        switch (category) {
            case "0":
                num = new Integer(category);
                intent = new Pair<>(num, intentNames[num]);
                break;

            case "1":
                num = new Integer(category);
                intent = new Pair<>(num, intentNames[num]);
                break;

            case "2":
                num = new Integer(category);
                intent = new Pair<>(num, intentNames[num]);
                break;

            default:
                intent = new Pair<>(num, "N/A");
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

        Pair<Integer, String> expected = null;
        Pair<Integer, String> actual = null;

        for (int i = 1; i < entries.length; i++) {
            expected = new Pair<>(new Integer(entries[1]), entries[2]);
        }
        actual = this.parse(entries[0]);

        LOG.info("Query:    {}", entries[0]);
        LOG.info("Actual:   {}", actual);
        LOG.info("Expected: {}", expected);
        LOG.info("=============================");

        return Objects.equals(actual, expected);
    }
}