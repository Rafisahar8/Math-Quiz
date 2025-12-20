package service;

import model.MathQuestion;
import model.Question;
import java.text.DecimalFormat;
import java.math.BigDecimal;
import java.util.*;

public class QuestionGenerator {
    
    private static Set<String> usedQuestions = new HashSet<>();
    private static String lastQuestion = "";
    
    public static MathQuestion generateQuestion(int level) {
        Question.QuestionDifficulty difficulty = getDifficultyByLevel(level);
        
        if (usedQuestions.size() > 30) {
            usedQuestions.clear();
        }
        
        MathQuestion question = null;
        int maxAttempts = 10;
        int attempts = 0;
        
        while (question == null && attempts < maxAttempts) {
            switch (level) {
                case 1:
                case 2:
                    question = generateLevel1_2Question(level);
                    break;
                case 3:
                case 4:
                    question = generateLevel3_4Question(level);
                    break;
                case 5:
                case 6:
                    question = generateLevel5_6Question(level);
                    break;
                case 7:
                case 8:
                    question = generateLevel7_8Question(level);
                    break;
                default:
                    question = generateLevel1_2Question(level);
                    break;
            }
            
            if (question != null) {
                String q = question.getQuestion();
                if (usedQuestions.contains(q) || q.equals(lastQuestion)) {
                    question = null;
                    attempts++;
                    continue;
                } else {
                    usedQuestions.add(q);
                    lastQuestion = q;
                    break;
                }
            } else {
                attempts++;
            }
        }
        
        if (question == null) {
            usedQuestions.clear();
            lastQuestion = "";
            return createFallbackQuestion(level);
        }
        
        return question;
    }
    
    private static MathQuestion createFallbackQuestion(int level) {
        String question = "2 + 3 = ?";
        double answer = 5;
        int points = 10;
        String[] options = {"3", "4", "5", "6"};

        Question.QuestionDifficulty difficulty = getDifficultyByLevel(level);
        return new MathQuestion(question, answer, points, difficulty, options);
    }
    
    private static String getQuestionPattern(String question) {
        return question.replaceAll("\\d+", "#");
    }
    
    public static void resetPatterns() {
        usedQuestions.clear();
        lastQuestion = "";
    }
    
    private static Question.QuestionDifficulty getDifficultyByLevel(int level) {
        if (level <= 2) return Question.QuestionDifficulty.EASY;
        else if (level <= 4) return Question.QuestionDifficulty.MEDIUM;
        else if (level <= 6) return Question.QuestionDifficulty.HARD;
        else return Question.QuestionDifficulty.EXPERT;
    }
    
    private static MathQuestion generateLevel1_2Question(int level) {
        int operation = (int) (Math.random() * 2);
        String question;
        double answer;

        int maxNum = level == 1 ? 10 : 20;

        switch (operation) {
            case 0: // Penjumlahan
                int a = (int) (Math.random() * maxNum) + 1;
                int b = (int) (Math.random() * maxNum) + 1;
                question = a + " + " + b + " = ?";
                answer = a + b;
                break;
            default: // Pengurangan (hasil positif)
                a = (int) (Math.random() * maxNum) + 1;
                b = (int) (Math.random() * a) + 1;
                question = a + " - " + b + " = ?";
                answer = a - b;
                break;
        }


        return createQuestionWithOptions(question, answer, level * 10, level);
    }
    
    private static MathQuestion generateLevel3_4Question(int level) {
        int operation = (int) (Math.random() * 4);
        String question;
        double answer;

        // Adjust range based on level
        int maxNum = level == 3 ? 15 : 25;

        switch (operation) {
            case 0: // Perkalian
                int a = (int) (Math.random() * maxNum) + 1;
                int b = (int) (Math.random() * maxNum) + 1;
                question = a + " × " + b + " = ?";
                answer = a * b;
                break;
            case 1: // Pembagian dengan hasil integer
                b = (int) (Math.random() * maxNum) + 1;
                a = b * ((int) (Math.random() * maxNum) + 1);
                question = a + " ÷ " + b + " = ?";
                answer = a / b;
                break;
            case 2: // Perkalian tiga angka
                a = (int) (Math.random() * (maxNum / 2)) + 1;
                b = (int) (Math.random() * (maxNum / 2)) + 1;
                int c = (int) (Math.random() * (maxNum / 2)) + 1;
                question = a + " × " + b + " × " + c + " = ?";
                answer = a * b * c;
                break;
                default: // Pembagian dengan sisa
                b = (int) (Math.random() * maxNum) + 1;
                a = (int) (Math.random() * (maxNum * 3)) + maxNum;
                question = a + " ÷ " + b + " = ?";
                answer = a / (double) b;
                break;
        }


        return createQuestionWithOptions(question, answer, level * 20, level);
    }
    
    private static MathQuestion generateLevel5_6Question(int level) {
        int operation = (int) (Math.random() * 7);
        String question;
        double answer;
        
        switch (operation) {
                case 0: // Operasi bertingkat
                int a = (int) (Math.random() * 20) + 1;
                int b = (int) (Math.random() * 10) + 1;
                int c = (int) (Math.random() * 5) + 1;
                question = "(" + a + " × " + b + ") ÷ " + c + " = ?";
                answer = (a * b) / (double) c;
                break;
            case 1: // Persentase random
                int percentage = (int) (Math.random() * 90) + 10;
                a = (int) (Math.random() * 200) + 50;
                question = "Berapa " + percentage + "% dari " + a + "?";
                answer = a * percentage / 100.0;
                break;
            case 2: // Aljabar sederhana
                a = (int) (Math.random() * 20) + 1;
                b = (int) (Math.random() * 15) + 1;
                question = "Jika x = " + a + ", maka 2x + " + b + " = ?";
                answer = 2 * a + b;
                break;
            case 3: // Akar kuadrat perfect square
                a = (int) (Math.random() * 15) + 5;
                int square = a * a;
                question = "√" + square + " = ?";
                answer = a;
                break;
            case 4: // Pecahan
                int numerator = (int) (Math.random() * 8) + 2;
                int denominator = (int) (Math.random() * 8) + 3;
                a = (int) (Math.random() * 20) + 10;
                question = a + " × " + numerator + "/" + denominator + " = ?";
                answer = a * numerator / (double) denominator;
                break;
            case 5: // Rata-rata 3 angka
                a = (int) (Math.random() * 50) + 10;
                b = (int) (Math.random() * 50) + 10;
                c = (int) (Math.random() * 50) + 10;
                question = "Rata-rata dari " + a + ", " + b + ", " + c + " adalah?";
                answer = (a + b + c) / 3.0;
                break;
            default: // Bilangan negatif
                a = (int) (Math.random() * 20) + 10;
                b = (int) (Math.random() * 20) + 10;
                if (Math.random() > 0.5) {
                    question = a + " - " + (a + b) + " = ?";
                    answer = -b;
                } else {
                    question = "-" + a + " + " + b + " = ?";
                    answer = b - a;
                }
                break;
        }
        

        return createQuestionWithOptions(question, answer, level * 30, level);
    }
    
    private static MathQuestion generateLevel7_8Question(int level) {
        int operation = (int) (Math.random() * 8);
        String question;
        double answer;
        
        switch (operation) {
            case 0: // Diskriminan kuadrat
                int a = (int) (Math.random() * 10) + 1;
                int b = (int) (Math.random() * 20) + 1;
                int c = (int) (Math.random() * 10) + 1;
                question = "x² + " + a + "x + " + c + " = 0, diskriminannya?";
                answer = a * a - 4 * c;
                break;
            case 1: // Luas lingkaran
                int radius = (int) (Math.random() * 15) + 5;
                question = "Luas lingkaran dengan jari-jari " + radius + "? (π=3.14)";
                answer = 3.14 * radius * radius;
                break;
            case 2: // Volume balok
                int panjang = (int) (Math.random() * 20) + 5;
                int lebar = (int) (Math.random() * 15) + 3;
                int tinggi = (int) (Math.random() * 10) + 2;
                question = "Volume balok dengan panjang " + panjang + ", lebar " + lebar + ", tinggi " + tinggi + " = ?";


                answer = panjang * lebar * tinggi;
                break;

            case 3: // Trigonometri dasar
                int[] angles = {30, 45, 60};
                int angle = angles[(int)(Math.random() * angles.length)];
                String[] trigFunctions = {"sin", "cos", "tan"};
                String trigFunc = trigFunctions[(int)(Math.random() * trigFunctions.length)];

                if (trigFunc.equals("sin")) {
                    switch (angle) {
                        case 30: question = "sin 30° = ?"; answer = 0.5; break;
                        case 45: question = "sin 45° = ?"; answer = Math.sqrt(2)/2; break;
                        case 60: question = "sin 60° = ?"; answer = Math.sqrt(3)/2; break;
                        default: question = "sin 30° = ?"; answer = 0.5; break;
                    }
                } else if (trigFunc.equals("cos")) {
                    switch (angle) {
                        case 30: question = "cos 30° = ?"; answer = Math.sqrt(3)/2; break;
                        case 45: question = "cos 45° = ?"; answer = Math.sqrt(2)/2; break;
                        case 60: question = "cos 60° = ?"; answer = 0.5; break;
                        default: question = "cos 30° = ?"; answer = Math.sqrt(3)/2; break;
                    }
                } else { // tan
                    switch (angle) {
                        case 30: question = "tan 30° = ?"; answer = Math.sqrt(3)/3; break;
                        case 45: question = "tan 45° = ?"; answer = 1; break;
                        case 60: question = "tan 60° = ?"; answer = Math.sqrt(3); break;
                        default: question = "tan 30° = ?"; answer = Math.sqrt(3)/3; break;
                    }
                }
                break;
            case 4: // Logaritma basis 10
                int value = (int) Math.pow(10, (int)(Math.random() * 4) + 1);
                question = "log " + value + " = ?";
                answer = Math.log10(value);
                break;
            case 5: // Sistem persamaan sederhana
                a = (int) (Math.random() * 10) + 1;
                b = (int) (Math.random() * 10) + 1;
                int result = a * 2 + b * 3;
                question = "Jika 2x + 3y = " + result + " dan x = " + a + ", maka y = ?";
                answer = b;
                break;
            case 6: // Peluang
                int favorable = (int) (Math.random() * 6) + 1;
                int total = favorable + (int) (Math.random() * 6) + 1;
                question = "Peluang munculnya " + favorable + " dari " + total + " kemungkinan = ?";
                answer = favorable / (double) total;
                break;
            default: // Operasi campuran kompleks
                a = (int) (Math.random() * 25) + 5;
                b = (int) (Math.random() * 20) + 5;
                c = (int) (Math.random() * 10) + 2;
                int d = (int) (Math.random() * 5) + 1;
                question = "(" + a + "² - " + b + ") ÷ " + c + " + " + d + " = ?";
                answer = ((a * a - b) / (double) c + d);
                break;
        }
        

        return createQuestionWithOptions(question, answer, level * 50, level);
    }
    
    private static MathQuestion createQuestionWithOptions(String question, double answer, int points, int level) {
        Question.QuestionDifficulty difficulty = getDifficultyByLevel(level);

        if (question.contains("sin") || question.contains("cos") || question.contains("tan")) {
            // Special handling for trigonometry questions
            String[] trigOptions = {"0", "1/2", "√2/2", "√3/2", "√3/3", "√3", "1"};
            DecimalFormat df = getDecimalFormat(answer, level);
            String correctStr = formatAnswer(answer, df, level, question);

            List<String> optionsList = new ArrayList<>(Arrays.asList(trigOptions));
            optionsList.removeIf(opt -> opt.equals(correctStr));

            Collections.shuffle(optionsList);
            List<String> selected = new ArrayList<>();
            selected.add(correctStr);
            selected.addAll(optionsList.subList(0, Math.min(3, optionsList.size())));

            while (selected.size() < 4) {
                selected.add("?");
            }

            String[] options = selected.toArray(new String[0]);
            shuffleArray(options);

            return new MathQuestion(question, answer, points, difficulty, options);
        } else {
            // Normal logic for other questions
            Set<String> optionSet = new LinkedHashSet<>();
            DecimalFormat df = getDecimalFormat(answer, level);

            String correctStr = formatAnswer(answer, df, level, question);
            optionSet.add(correctStr);

            int attempts = 0;
            while (optionSet.size() < 4 && attempts < 30) {
                attempts++;
                double wrongAnswer = generateWrongAnswer(answer, level, question);
                String wrongStr = formatAnswer(wrongAnswer, df, level, question);

                if (!optionSet.contains(wrongStr) && isValidOption(wrongStr)) {
                    optionSet.add(wrongStr);
                }
            }

            if (optionSet.size() < 4) {
                addDefaultOptions(optionSet, correctStr, level);
            }

            List<String> optionList = new ArrayList<>(optionSet);
            while (optionList.size() < 4) {
                optionList.add("?");
            }

            String[] options = optionList.toArray(new String[4]);
            shuffleArray(options);

            return new MathQuestion(question, answer, points, difficulty, options);
        }
    }
    
    private static DecimalFormat getDecimalFormat(double answer, int level) {
        if (answer == Math.floor(answer) && !Double.isInfinite(answer) && !Double.isNaN(answer)) {
            return new DecimalFormat("#");
        } else {
            return new DecimalFormat("#.##");
        }
    }
    

    private static String formatAnswer(double value, DecimalFormat df, int level, String question) {
        if (Double.isInfinite(value)) {
            return "∞";
        } else if (Double.isNaN(value)) {
            return "undefined";
        } else if (question.contains("sin") || question.contains("cos") || question.contains("tan")) {
            // Format trigonometric answers with common root forms
            if (Math.abs(value - 0.5) < 0.001) {
                return "1/2";
            } else if (Math.abs(value - (Math.sqrt(2)/2)) < 0.001) {
                return "√2/2";
            } else if (Math.abs(value - (Math.sqrt(3)/2)) < 0.001) {
                return "√3/2";
            } else if (Math.abs(value - (Math.sqrt(3)/3)) < 0.001) {
                return "√3/3";
            } else if (Math.abs(value - Math.sqrt(3)) < 0.001) {
                return "√3";
            } else if (Math.abs(value - 1.0) < 0.001) {
                return "1";
            } else {
                // Use the new decimal formatting method
                return formatDecimalAnswer(value);
            }
        } else {
            // Use the new decimal formatting method
            return formatDecimalAnswer(value);
        }
    }


    private static String formatDecimalAnswer(double value) {
        if (Double.isInfinite(value)) return "∞";
        if (Double.isNaN(value)) return "undefined";

        if (value == Math.floor(value)) {
            long intValue = (long) value;
            return String.valueOf(intValue);
        }

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.stripTrailingZeros();
        // Set scale to 2, but do not round - truncate if necessary
        if (bd.scale() > 2) {
            bd = bd.setScale(2, java.math.RoundingMode.DOWN);
        }
        String plain = bd.toPlainString();
        // Replace dot with comma for decimal separator
        plain = plain.replace('.', ',');
        return plain;
    }
    

    private static double generateWrongAnswer(double correctAnswer, int level, String question) {
        double wrongAnswer;
        double minDifference = Math.max(1.0, Math.abs(correctAnswer) * 0.1); // At least 10% difference or 1
        
        if (level <= 2) {
            int offset = (int)(Math.random() * 5) + 1;
            if (Math.random() > 0.5) {
                wrongAnswer = correctAnswer + offset;
            } else {
                wrongAnswer = correctAnswer - offset;
            }
        } else if (level <= 4) {
            if (question.contains("×") && !question.contains("÷")) {
                // For multiplication, use common multiplication errors
                wrongAnswer = correctAnswer + (int)(Math.random() * 15) + 5;
            } else if (question.contains("÷")) {
                // For division, use division-related errors
                int multiplier = (int)(Math.random() * 3) + 2;
                wrongAnswer = correctAnswer * multiplier;
            } else if (question.contains("²")) {
                // For squares, use square root or different power
                wrongAnswer = Math.sqrt(Math.abs(correctAnswer));
            } else {
                // For other operations, use proportional differences
                double factor = 0.5 + Math.random() * 1.5; // 0.5 to 2.0
                wrongAnswer = correctAnswer * factor;
            }
        } else if (level <= 6) {
            if (question.contains("√")) {
                // For square roots, use square or wrong root
                wrongAnswer = correctAnswer * correctAnswer;
            } else if (question.contains("%")) {
                // For percentages, use different percentage calculations
                wrongAnswer = correctAnswer * (0.5 + Math.random());
            } else if (question.contains("rata-rata")) {
                // For averages, use sum instead of average
                wrongAnswer = correctAnswer * 3;
            } else {
                // For complex operations, use mathematical inverses or related operations
                wrongAnswer = correctAnswer + (Math.random() * 30 - 15);
            }
        } else {
            if (question.contains("sin") || question.contains("cos") || question.contains("tan")) {
                // For trigonometry, use common trigonometric values
                double[] trigValues = {0, 0.5, 0.7071, 0.8660, 1, 0.5774, 1.7321};
                wrongAnswer = trigValues[(int)(Math.random() * trigValues.length)];
            } else if (question.contains("log")) {
                // For logarithms, use wrong base or argument
                wrongAnswer = Math.log(Math.abs(correctAnswer) * 10) / Math.log(10);
            } else if (question.contains("volume")) {
                // For volume, use area or perimeter calculations
                wrongAnswer = correctAnswer * (0.3 + Math.random() * 0.7);
            } else {
                // For complex operations, use related mathematical concepts
                wrongAnswer = correctAnswer * (0.6 + Math.random() * 0.8);
            }
        }
        
        // Ensure wrong answer is sufficiently different from correct answer
        int attempts = 0;
        while (Math.abs(wrongAnswer - correctAnswer) < minDifference && attempts < 10) {
            if (level <= 2) {
                wrongAnswer = correctAnswer + (int)(Math.random() * 10 + 1) * (Math.random() > 0.5 ? 1 : -1);
            } else if (level <= 4) {
                wrongAnswer = correctAnswer * (0.5 + Math.random() * 2);
            } else {
                wrongAnswer = correctAnswer + (Math.random() * 50 - 25);
            }
            attempts++;
        }
        
        // Final safety check
        if (Math.abs(wrongAnswer - correctAnswer) < minDifference) {
            wrongAnswer = correctAnswer + minDifference * (Math.random() > 0.5 ? 1 : -1);
        }
        
        if (Double.isNaN(wrongAnswer) || Double.isInfinite(wrongAnswer)) {
            wrongAnswer = correctAnswer + minDifference;
        }
        
        return wrongAnswer;
    }
    
    private static boolean isValidOption(String option) {
        if (option == null || option.isEmpty()) return false;
        if (option.length() > 15) return false;

        try {
            if (!option.equals("∞") && !option.equals("undefined") &&
                !option.equalsIgnoreCase("infinity") && !option.equals("?")) {
                Double.parseDouble(option.replace(",", "."));
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private static void addDefaultOptions(Set<String> optionSet, String correctStr, int level) {
        String[][] defaultOptionsByLevel = {
            {"1", "2", "5", "10"},
            {"5", "10", "15", "20"},  
            {"10", "25", "50", "100"},
            {"0.5", "1", "2", "5"}
        };
        
        int levelIndex = Math.min((level - 1) / 2, 3);
        String[] defaultOptions = defaultOptionsByLevel[levelIndex];
        
        for (String opt : defaultOptions) {
            if (optionSet.size() < 4 && !opt.equals(correctStr)) {
                optionSet.add(opt);
            }
        }
        
        while (optionSet.size() < 4) {
            int randomNum = (int)(Math.random() * 100) + 1;
            String randomOpt = String.valueOf(randomNum);
            if (!optionSet.contains(randomOpt) && !randomOpt.equals(correctStr)) {
                optionSet.add(randomOpt);
            }
        }
    }
    
    private static void shuffleArray(String[] array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            String temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}