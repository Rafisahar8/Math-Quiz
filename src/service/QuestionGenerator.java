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
        if (usedQuestions.size() > 30) usedQuestions.clear();

        MathQuestion question = null;
        int attempts = 0;

        while (question == null && attempts < 10) {
            switch (level) {
                case 1: case 2: question = generateLevel1_2Question(level); break;
                case 3: case 4: question = generateLevel3_4Question(level); break;
                case 5: case 6: question = generateLevel5_6Question(level); break;
                case 7: case 8: question = generateLevel7_8Question(level); break;
                default:        question = generateLevel1_2Question(level); break;
            }
            if (question != null) {
                String q = question.getQuestion();
                if (usedQuestions.contains(q) || q.equals(lastQuestion)) {
                    question = null; attempts++;
                } else {
                    usedQuestions.add(q); lastQuestion = q;
                }
            } else {
                attempts++;
            }
        }

        if (question == null) {
            usedQuestions.clear(); lastQuestion = "";
            return createFallbackQuestion(level);
        }
        return question;
    }

    private static MathQuestion createFallbackQuestion(int level) {
        return new MathQuestion("2 + 3 = ?", 5, 10, getDifficultyByLevel(level),
                                new String[]{"3","4","5","6"});
    }

    public static void resetPatterns() {
        usedQuestions.clear(); lastQuestion = "";
    }

    private static Question.QuestionDifficulty getDifficultyByLevel(int level) {
        if (level <= 2) return Question.QuestionDifficulty.EASY;
        if (level <= 4) return Question.QuestionDifficulty.MEDIUM;
        if (level <= 6) return Question.QuestionDifficulty.HARD;
        return Question.QuestionDifficulty.EXPERT;
    }

    private static MathQuestion generateLevel1_2Question(int level) {
        int maxNum = (level == 1) ? 10 : 20;
        int a = (int)(Math.random() * maxNum) + 1;
        int b = (int)(Math.random() * maxNum) + 1;
        String question;
        double answer;

        if (Math.random() > 0.5) {
            question = a + " + " + b + " = ?";
            answer   = a + b;
        } else {
            if (a < b) { int tmp = a; a = b; b = tmp; }
            question = a + " - " + b + " = ?";
            answer   = a - b;
        }
        return createQuestionWithOptions(question, answer, level * 10, level);
    }

    private static MathQuestion generateLevel3_4Question(int level) {
        int maxNum = (level == 3) ? 15 : 25;
        int op = (int)(Math.random() * 4);
        String question;
        double answer;
        int a, b, c;

        switch (op) {
            case 0:
                a = (int)(Math.random() * maxNum) + 1;
                b = (int)(Math.random() * maxNum) + 1;
                question = a + " x " + b + " = ?";
                answer   = a * b;
                break;
            case 1:
                b = (int)(Math.random() * maxNum) + 1;
                a = b * ((int)(Math.random() * maxNum) + 1);
                question = a + " / " + b + " = ?";
                answer   = a / b;
                break;
            case 2: 
                a = (int)(Math.random() * (maxNum / 2)) + 1;
                b = (int)(Math.random() * (maxNum / 2)) + 1;
                c = (int)(Math.random() * (maxNum / 2)) + 1;
                question = a + " x " + b + " x " + c + " = ?";
                answer   = a * b * c;
                break;
            default:
                b = (int)(Math.random() * maxNum) + 1;
                a = (int)(Math.random() * (maxNum * 3)) + maxNum;
                while (a % b == 0) a = (int)(Math.random() * (maxNum * 3)) + maxNum;
                question = a + " / " + b + " = ?";
                answer   = a / (double) b;
                break;
        }
        return createQuestionWithOptions(question, answer, level * 20, level);
    }

    private static MathQuestion generateLevel5_6Question(int level) {
        int op = (int)(Math.random() * 7);
        String question;
        double answer;
        int a, b, c;

        switch (op) {
            case 0:
                a = (int)(Math.random() * 20) + 1;
                b = (int)(Math.random() * 10) + 1;
                c = (int)(Math.random() * 5)  + 1;
                question = "(" + a + " x " + b + ") / " + c + " = ?";
                answer   = (a * b) / (double) c;
                break;
            case 1: 
                int pct = (int)(Math.random() * 90) + 10;
                a = (int)(Math.random() * 200) + 50;
                question = "Berapa " + pct + "% dari " + a + "?";
                answer   = a * pct / 100.0;
                break;
            case 2:
                a = (int)(Math.random() * 20) + 1;
                b = (int)(Math.random() * 15) + 1;
                question = "Jika x = " + a + ", maka 2x + " + b + " = ?";
                answer   = 2 * a + b;
                break;
            case 3:
                a = (int)(Math.random() * 15) + 5;
                question = "Akar dari " + (a * a) + " = ?";
                answer   = a;
                break;
            case 4:
                int num  = (int)(Math.random() * 8) + 2;
                int den  = (int)(Math.random() * 8) + 3;
                a = (int)(Math.random() * 20) + 10;
                question = a + " x " + num + "/" + den + " = ?";
                answer   = a * num / (double) den;
                break;
            case 5:
                a = (int)(Math.random() * 50) + 10;
                b = (int)(Math.random() * 50) + 10;
                c = (int)(Math.random() * 50) + 10;
                question = "Rata-rata dari " + a + ", " + b + ", " + c + " = ?";
                answer   = (a + b + c) / 3.0;
                break;
            default:
                a = (int)(Math.random() * 20) + 10;
                b = (int)(Math.random() * 20) + 10;
                if (Math.random() > 0.5) {
                    question = a + " - " + (a + b) + " = ?"; answer = -b;
                } else {
                    question = "-" + a + " + " + b + " = ?"; answer = b - a;
                }
                break;
        }
        return createQuestionWithOptions(question, answer, level * 30, level);
    }

    private static MathQuestion generateLevel7_8Question(int level) {
        int op = (int)(Math.random() * 8);
        String question  = "";
        double answer    = 0;
        MathQuestion directResult = null;
        int a = 0, b = 0, c = 0;

        switch (op) {
            case 0:
                a = (int)(Math.random() * 10) + 1;
                b = (int)(Math.random() * 20) + 1;
                c = (int)(Math.random() * 10) + 1;
                question = "x^2 + " + a + "x + " + c + " = 0, berapa hasil diskriminannya?";
                answer   = (double)(a * a) - 4.0 * c;
                break;

            case 1:
                int radius = (int)(Math.random() * 15) + 5;
                question = "Luas lingkaran jari-jari " + radius + "? (pi=3.14)";
                answer   = 3.14 * radius * radius;
                break;

            case 2:
                int panjang = (int)(Math.random() * 20) + 5;
                int lebar   = (int)(Math.random() * 15) + 3;
                int tinggi  = (int)(Math.random() * 10) + 2;
                question = "Volume balok " + panjang + "x" + lebar + "x" + tinggi + " = ?";
                answer   = (double) panjang * lebar * tinggi;
                break;

            case 3:
                int[] angles = {30, 45, 60};
                int angle = angles[(int)(Math.random() * 3)];
                int fn    = (int)(Math.random() * 3); 
                if (fn == 0) {
                    if (angle == 30) { question = "sin 30 = ?"; answer = 0.5; }
                    else if (angle == 45) { question = "sin 45 = ?"; answer = Math.sqrt(2)/2; }
                    else { question = "sin 60 = ?"; answer = Math.sqrt(3)/2; }
                } else if (fn == 1) {
                    if (angle == 30) { question = "cos 30 = ?"; answer = Math.sqrt(3)/2; }
                    else if (angle == 45) { question = "cos 45 = ?"; answer = Math.sqrt(2)/2; }
                    else { question = "cos 60 = ?"; answer = 0.5; }
                } else {
                    if (angle == 30) { question = "tan 30 = ?"; answer = Math.sqrt(3)/3; }
                    else if (angle == 45) { question = "tan 45 = ?"; answer = 1.0; }
                    else { question = "tan 60 = ?"; answer = Math.sqrt(3); }
                }
                break;

            case 4:
                int expN  = (int)(Math.random() * 4) + 1;
                int value = (int) Math.pow(10, expN);
                question = "log " + value + " = ?";
                answer   = Math.log10(value);
                break;

            case 5: 
                a = (int)(Math.random() * 10) + 1;
                b = (int)(Math.random() * 10) + 1;
                int result = a * 2 + b * 3;
                question = "Jika 2x + 3y = " + result + " dan x = " + a + ", maka y = ?";
                answer   = b;
                break;

            case 6: 
                int favorable = (int)(Math.random() * 5) + 1;
                int total     = favorable + (int)(Math.random() * 5) + 2;
                question = "Peluang munculnya " + favorable + " dari " + total + " kemungkinan = ?";
                answer   = favorable / (double) total;

                String correctFrac = favorable + "/" + total;
                Set<String> fracSet = new LinkedHashSet<>();
                fracSet.add(correctFrac);

                for (int fav2 = 1; fav2 < total && fracSet.size() < 4; fav2++) {
                    if (fav2 != favorable) fracSet.add(fav2 + "/" + total);
                }
                for (int delta = -2; delta <= 2 && fracSet.size() < 4; delta++) {
                    int altTotal = total + delta;
                    if (altTotal <= 1 || altTotal == total) continue;
                    for (int fav3 = 1; fav3 < altTotal && fracSet.size() < 4; fav3++) {
                        if (Math.abs((double) fav3 / altTotal - (double) favorable / total) > 0.01)
                            fracSet.add(fav3 + "/" + altTotal);
                    }
                }
                while (fracSet.size() < 4) fracSet.add(fracSet.size() + "/" + (total + 1));

                String[] probOpts = fracSet.toArray(new String[4]);
                shuffleArray(probOpts);
                directResult = new MathQuestion(question, answer, level * 50,
                        getDifficultyByLevel(level), probOpts);
                break;

            default:
                a = (int)(Math.random() * 25) + 5;
                b = (int)(Math.random() * 20) + 5;
                c = (int)(Math.random() * 10) + 2;
                int d = (int)(Math.random() * 5) + 1;
                question = "(" + a + "^2 - " + b + ") / " + c + " + " + d + " = ?";
                answer   = (a * a - b) / (double) c + d;
                break;
        }

        if (directResult != null) return directResult;
        return createQuestionWithOptions(question, answer, level * 50, level);
    }

    private static MathQuestion createQuestionWithOptions(String question, double answer,
                                                          int points, int level) {
        Question.QuestionDifficulty difficulty = getDifficultyByLevel(level);

        if (question.contains("sin") || question.contains("cos") || question.contains("tan")) {
            String[] trigOptions = {"0", "1/2", "akar2/2", "akar3/2", "akar3/3", "akar3", "1"};
            String correctStr = formatTrigAnswer(answer);

            List<String> opts = new ArrayList<>(Arrays.asList(trigOptions));
            opts.removeIf(o -> o.equals(correctStr));
            Collections.shuffle(opts);

            List<String> sel = new ArrayList<>();
            sel.add(correctStr);
            sel.addAll(opts.subList(0, Math.min(3, opts.size())));
            while (sel.size() < 4) sel.add("?");

            String[] options = sel.toArray(new String[0]);
            shuffleArray(options);
            return new MathQuestion(question, answer, points, difficulty, options);
        }

        Set<String> optionSet = new LinkedHashSet<>();
        String correctStr = formatDecimalAnswer(answer);
        optionSet.add(correctStr);

        boolean isIntegerAnswer = (answer == Math.floor(answer) && !Double.isInfinite(answer));

        int attempts = 0;
        while (optionSet.size() < 4 && attempts < 30) {
            attempts++;
            double wrong = generateWrongAnswer(answer, level, question);
            if (isIntegerAnswer) wrong = Math.round(wrong);
            String wrongStr = formatDecimalAnswer(wrong);
            if (!optionSet.contains(wrongStr) && isValidOption(wrongStr)) {
                optionSet.add(wrongStr);
            }
        }

        if (optionSet.size() < 4) addDefaultOptions(optionSet, correctStr, answer);

        List<String> optList = new ArrayList<>(optionSet);
        while (optList.size() < 4) optList.add("?");

        String[] options = optList.toArray(new String[4]);
        shuffleArray(options);
        return new MathQuestion(question, answer, points, difficulty, options);
    }

    private static String formatTrigAnswer(double value) {
        if (Math.abs(value - 0.5)                 < 0.001) return "1/2";
        if (Math.abs(value - (Math.sqrt(2) / 2))  < 0.001) return "akar2/2";
        if (Math.abs(value - (Math.sqrt(3) / 2))  < 0.001) return "akar3/2";
        if (Math.abs(value - (Math.sqrt(3) / 3))  < 0.001) return "akar3/3";
        if (Math.abs(value - Math.sqrt(3))         < 0.001) return "akar3";
        if (Math.abs(value - 1.0)                  < 0.001) return "1";
        if (Math.abs(value - 0.0)                  < 0.001) return "0";
        return formatDecimalAnswer(value);
    }

    private static String formatDecimalAnswer(double value) {
        if (Double.isInfinite(value)) return "tak terhingga";
        if (Double.isNaN(value))      return "undefined";
        if (value == Math.floor(value) && !Double.isInfinite(value))
            return String.valueOf((long) value);

        BigDecimal bd = BigDecimal.valueOf(value).stripTrailingZeros();
        if (bd.scale() > 2) bd = bd.setScale(2, java.math.RoundingMode.DOWN);
        return bd.toPlainString().replace('.', ',');
    }

    private static double generateWrongAnswer(double correct, int level, String question) {
        double wrong;
        double minDiff = Math.max(0.5, Math.abs(correct) * 0.05);

        if (level <= 2) {
            int off = (int)(Math.random() * 3) + 1;
            wrong = correct + (Math.random() > 0.5 ? off : -off);

        } else if (level <= 4) {
            if (question.contains("x") && !question.contains("/")) {
                int off = (int)(Math.random() * 9) + 2;
                wrong = correct + (Math.random() > 0.5 ? off : -off);
                if (wrong <= 0) wrong = correct + off;
            } else if (question.contains("/")) {
                double pct = 0.10 + Math.random() * 0.10;
                wrong = correct * (1 + (Math.random() > 0.5 ? pct : -pct));
            } else {
                wrong = correct * (0.9 + Math.random() * 0.2);
            }

        } else if (level <= 6) {
            if (question.contains("Akar")) {
                wrong = correct + (Math.random() * 6 - 3);
            } else if (question.contains("%")) {
                double off = Math.max(2.0, Math.abs(correct) * 0.12) * (0.8 + Math.random() * 0.4);
                wrong = correct + (Math.random() > 0.5 ? off : -off);
            } else if (question.toLowerCase().contains("rata-rata")) {
                wrong = correct + (Math.random() * 10 - 5);
            } else {
                wrong = correct + (Math.random() * 12 - 6);
            }

        } else {
            if (question.contains("sin") || question.contains("cos") || question.contains("tan")) {
                double[] tv = {0, 0.5, 0.7071, 0.8660, 1, 0.5774, 1.7321};
                wrong = tv[(int)(Math.random() * tv.length)];
            } else if (question.toLowerCase().contains("volume")) {
                wrong = correct * (0.88 + Math.random() * 0.24);
            } else if (question.toLowerCase().contains("log")) {
                int off = (int)(Math.random() * 3) + 1;
                wrong = correct + (Math.random() > 0.5 ? off : -off);
            } else {
                boolean isSmallInt = (correct == Math.floor(correct)) && (Math.abs(correct) <= 20);
                if (isSmallInt) {
                    int off = (int)(Math.random() * 4) + 1;
                    wrong = correct + (Math.random() > 0.5 ? off : -off);
                } else {
                    double pct = 0.10 + Math.random() * 0.15;
                    wrong = correct * (1 + (Math.random() > 0.5 ? pct : -pct));
                    if (wrong <= 0 && correct > 0) wrong = correct * (1 + pct);
                }
            }
        }

        int retries = 0;
        while (Math.abs(wrong - correct) < minDiff && retries < 10) {
            if (level <= 2) {
                int off = (int)(Math.random() * 3) + 1;
                wrong = correct + (Math.random() > 0.5 ? off : -off);
            } else {
                wrong = correct + (Math.random() * 14 - 7);
            }
            retries++;
        }
        if (Math.abs(wrong - correct) < minDiff)
            wrong = correct + minDiff * (Math.random() > 0.5 ? 1 : -1);
        if (Double.isNaN(wrong) || Double.isInfinite(wrong))
            wrong = correct + minDiff;

        return wrong;
    }

    private static boolean isValidOption(String option) {
        if (option == null || option.isEmpty() || option.length() > 20) return false;
        if (option.contains("/")) return true; 
        if (option.startsWith("akar")) return true; 
        try {
            if (!option.equals("tak terhingga") && !option.equals("undefined") && !option.equals("?"))
                Double.parseDouble(option.replace(",", "."));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void addDefaultOptions(Set<String> optionSet, String correctStr, double correctVal) {
        int[] offsets = {1, -1, 2, -2, 3, -3, 5, -5, 10, -10};
        for (int off : offsets) {
            if (optionSet.size() >= 4) break;
            String candidate = formatDecimalAnswer(correctVal + off);
            if (!optionSet.contains(candidate)) optionSet.add(candidate);
        }
        while (optionSet.size() < 4) {
            String rnd = String.valueOf((int)(Math.random() * 100) + 1);
            if (!optionSet.contains(rnd)) optionSet.add(rnd);
        }
    }

    private static void shuffleArray(String[] array) {
        Random rng = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            String tmp = array[j];
            array[j]   = array[i];
            array[i]   = tmp;
        }
    }
}
