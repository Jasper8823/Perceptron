import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Perceptron {
    private static double[] weights;
    private static double threshold;
    private static double learningRate;
    private static List<Instance> trainingData;
    private static List<Instance> testData;
    private String[] answers =new String[2];

    public Perceptron() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter path to file with training data: ");
        String trainingFilePath = scanner.nextLine();
        System.out.print("Enter path to file with test data: ");
        String testFilePath = scanner.nextLine();

        System.out.print("Enter learning rate: ");
        learningRate = scanner.nextDouble();
        System.out.print("Enter number of epochs: ");
        int numEpochs = scanner.nextInt();

        trainingData = loadData(trainingFilePath);
        testData = loadData(testFilePath);

        initializeWeightsAndThreshold(trainingData.get(0).getAttributes().length);

        for (int epoch = 1; epoch <= numEpochs; epoch++) {
            Collections.shuffle(trainingData);
            trainPerceptron();
            double accuracy = testPerceptron();
            System.out.println("Epoch " + epoch + ", Test Accuracy: " + accuracy);
        }

        scanner.nextLine();
        while (true) {
            System.out.println("Enter new observations (comma-separated attributes), type 'exit' to quit: ");
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                break;
            }
            double[] attributes = parseAttributes(input);
            int predictedClass = predictClass(attributes);
            System.out.println("Predicted class: " + predictedClass);
        }

        scanner.close();
    }

    private List<Instance> loadData(String filePath) {
        List<Instance> instances = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null&&line.length()>0) {
                String[] parts = line.split(",");
                double[] attributes = new double[parts.length - 1];
                for (int i = 0; i < attributes.length; i++) {
                    attributes[i] = Double.parseDouble(parts[i]);
                }
                String classLabel = parts[parts.length - 1];
                if(answers[0]==null){
                    answers[0]=classLabel;
                } else if (!answers[0].equals(classLabel)) {
                    answers[1]=classLabel;
                }
                instances.add(new Instance(attributes, classLabel));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instances;
    }

    private static void initializeWeightsAndThreshold(int numAttributes) {
        weights = new double[numAttributes];
        for (int i = 0; i < numAttributes; i++) {
            weights[i] = Math.random() * 0.1;
        }
        threshold = Math.random() * 0.1;
    }

    private void trainPerceptron() {
        for (Instance instance : trainingData) {
            double[] attributes = instance.getAttributes();
            int actualClass = instance.getClassLabel(answers[0]);
            int predictedClass = predictClass(attributes);
            if (actualClass != predictedClass) {
                for (int i = 0; i < attributes.length; i++) {
                    weights[i] += learningRate * (actualClass - predictedClass) * attributes[i];
                }
                threshold -= learningRate * (actualClass - predictedClass);
            }
        }
    }

    private double testPerceptron() {
        int correctPredictions = 0;
        for (Instance instance : testData) {
            double[] attributes = instance.getAttributes();
            int actualClass = instance.getClassLabel(answers[0]);
            int predictedClass = predictClass(attributes);
            if (actualClass == predictedClass) {
                correctPredictions++;
            }
        }
        return (double) correctPredictions / testData.size();
    }

    private static int predictClass(double[] attributes) {
        double activation = 0;
        for (int i = 0; i < attributes.length; i++) {
            activation += weights[i] * attributes[i];
        }
        activation -= threshold;
        return activation >= 0 ? 1 : 0;
    }

    private static double[] parseAttributes(String input) {
        String[] parts = input.split(",");
        double[] attributes = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            attributes[i] = Double.parseDouble(parts[i]);
        }
        return attributes;
    }

    static class Instance {
        private final double[] attributes;
        private final String classLabel;

        public Instance(double[] attributes, String  classLabel) {
            this.attributes = attributes;
            this.classLabel = classLabel;
        }

        public double[] getAttributes() {
            return attributes;
        }

        public int getClassLabel(String answer) {
            if(classLabel.equals(answer)){
                return 1;
            }
            return 0;
        }
    }
}

