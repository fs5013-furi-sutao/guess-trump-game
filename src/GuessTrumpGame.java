import java.util.Random;
import java.util.Scanner;

public class GuessTrumpGame {
    private static final Scanner STDIN = new Scanner(System.in);
    private static final Random RANDOM = new Random();

    // 正解をデバッグ表示するかの設定
    private static final boolean IS_DEBUG_MODE = true;

    private static final int ARRAY_FIRST_INDEX = 0;
    private static final int ROUND_OF_START = 1;
    private static final int ONE_LINE = 1;

    // 正解カードのインデックス設定
    private static final int SUIT_INFO = 0;
    private static final int RANK_INFO = 1;

    // 回答できる回数の設定
    private static final int MAX_CHALLENGE_TIMES_FOR_SUIT = 2;
    private static final int MAX_CHALLENGE_TIMES_FOR_RANK = 4;

    private static final String COMMA = ",";
    private static final String HALF_SPACE = " ";
    private static final String[] SUITS = { "ハート", "ダイヤ", "スペード", "クローバー", };
    private static final String[] RANKS = { "A", "2", "3", "4", "5", "6", "7",
            "8", "9", "10", "J", "Q", "K", };

    private static final String MESSAGE_FORMAT_FOR_DEBUG_CORRECTS = "[DEBUG] 正解は %s の %s %n";
    private static final String MESSAGE_FORMAT_FOR_ROUND = "%d 回目の回答：＝＝＝＝＝ %n";

    private static final String MESSAGE_FOR_FINISH_TO_GENERATE_CORRECT = "トランプを選んだよ";
    private static final String MESSAGE_FORMAT_FOR_GUESS_SUIT_DESCRIPTION = "トランプの図柄を当ててね（%d 回まで答えられるよ）%n";
    private static final String MESSAGE_FORMAT_FOR_GUESS_RANK_DESCRIPTION = "次は数字を当ててね（%d 回まで答えられるよ）%n";
    private static final String MESSAGE_FORMAT_FOR_SHOW_SELECT_MENU = "%d: %s %n";
    private static final String MESSAGE_FOR_REQUIRE_USER_INPUT = "どれだと思う？：";
    private static final String MESSAGE_FOR_INPUT_WITH_NUM = "数字で入力してください";
    private static final String MESSAGE_FORMAT_F0R_INPUT_IN_RANGE = "%d ～ %d の範囲にある数字で入力してください %n";
    private static final String MESSAGE_FORMAT_FOR_REQUIRE_INPUT_RANK_IN_RANGE = "%s のうちのどれかで入力してください %n";
    private static final String MESSAGE_FORMAT_FOR_DOUBT = "残念！%sじゃないよ %n";
    private static final String MESSAGE_FORMAT_FOR_CORRECT = "正解は%sでした %n";
    private static final String MESSAGE_FORMAT_FOR_HIT_CORRECT_SUIT = "正解！図柄は%sだよ %n";
    private static final String MESSAGE_FORMAT_FOR_HIT_CORRECT_CARD = "正解！%sの%sだよ %n";
    private static final String MESSAGE_FORMAT_FOR_CORRECT_CARD = "正解は%sの%sでした %n";

    public static void main(String[] args) {
        String[] correctCard = { generateCorrect(SUITS),
                generateCorrect(RANKS) };
        showCorrectCardForDebug(IS_DEBUG_MODE, correctCard);

        show(MESSAGE_FOR_FINISH_TO_GENERATE_CORRECT);
        showGuessSuitDescription();
        showBlankLine(ONE_LINE);

        int round = ROUND_OF_START;
        playGuessSuit(round, correctCard);
        showBlankLine(ONE_LINE);

        showGuessRankDescription();
        playGuessRank(round, correctCard);
    }

    private static void playGuessSuit(int round, String[] correctCard) {
        showRound(round++);
        showSelectMenu(SUITS);
        showWithNoBreaks(MESSAGE_FOR_REQUIRE_USER_INPUT);

        String correctSuit = correctCard[SUIT_INFO];
        int userAnswerSuitAsIndex = recieveInputtedOptionNo(SUITS);
        String userAnswerSuit = SUITS[userAnswerSuitAsIndex];

        if (isOverMaxChallengeTimes(MAX_CHALLENGE_TIMES_FOR_SUIT, round)) {
            showDoubt(userAnswerSuit);
            showCorrectSuit(correctSuit);
            return;
        }

        if (!isCorrect(correctSuit, userAnswerSuit)) {
            showDoubt(userAnswerSuit);
            playGuessSuit(round, correctCard);
            return;
        }
        runInCaseCorrectSuit(correctSuit, userAnswerSuit);
    }

    private static void runInCaseCorrectSuit(String correctSuit,
            String userAnserSuit) {
        if (!isCorrect(correctSuit, userAnserSuit)) {
            return;
        }
        showHitCorrectSuit(userAnserSuit);
    }

    private static void playGuessRank(int round, String[] correctCard) {
        showRound(round++);
        showWithNoBreaks(MESSAGE_FOR_REQUIRE_USER_INPUT);

        String correctRank = correctCard[RANK_INFO];
        String userAnswerRank = recieveInputtedOption(RANKS);

        if (isOverMaxChallengeTimes(MAX_CHALLENGE_TIMES_FOR_RANK, round)) {
            showDoubt(userAnswerRank);
            showCorrectCard(correctCard);
            return;
        }

        if (!isCorrect(correctRank, userAnswerRank)) {
            showDoubt(userAnswerRank);
            playGuessRank(round, correctCard);
            return;
        }
        runInCaseCorrectRank(correctCard, userAnswerRank);
    }

    private static void runInCaseCorrectRank(String[] correctCard,
            String userAnswerRank) {
        String correctSuit = correctCard[SUIT_INFO];
        String correctRank = correctCard[RANK_INFO];

        if (!isCorrect(correctRank, userAnswerRank)) {
            return;
        }
        showHitCorrect(correctSuit, userAnswerRank);
    }

    private static String recieveInputtedOption(String[] options) {
        String inputtedNumStr = recieveInputtedNumStr();
        inputtedNumStr = validateInputtedOption(inputtedNumStr, options);
        return inputtedNumStr;
    }

    private static String validateInputtedOption(String inputtedNumStr,
            String[] options) {
        if (!isInRange(inputtedNumStr, options)) {
            showRequireRankInRange();
            return recieveInputtedOption(options);
        }
        return inputtedNumStr;
    }

    private static void showRequireRankInRange() {
        String allRanksSparatedComma = getAllRanksSeparratedComma();
        System.out.format(MESSAGE_FORMAT_FOR_REQUIRE_INPUT_RANK_IN_RANGE,
                allRanksSparatedComma);
    }

    private static String getAllRanksSeparratedComma() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < RANKS.length; i++) {
            sb.append(RANKS[i]);

            if (i != RANKS.length - 1) {
                sb.append(COMMA);
            }
            sb.append(HALF_SPACE);
        }
        return sb.toString();
    }

    private static boolean isInRange(String str, String[] options) {
        for (String option : options) {

            if (str.equals(option)) {
                return true;
            }
        }
        return false;
    }

    private static void showGuessSuitDescription() {
        System.out.format(MESSAGE_FORMAT_FOR_GUESS_SUIT_DESCRIPTION,
                MAX_CHALLENGE_TIMES_FOR_SUIT);
    }

    private static void showGuessRankDescription() {
        System.out.format(MESSAGE_FORMAT_FOR_GUESS_RANK_DESCRIPTION,
                MAX_CHALLENGE_TIMES_FOR_RANK);
    }

    private static void showCorrectCardForDebug(boolean isDebugMode,
            String[] correctCard) {
        System.out.format(MESSAGE_FORMAT_FOR_DEBUG_CORRECTS,
                correctCard[SUIT_INFO], correctCard[RANK_INFO]);
    }

    private static void showCorrectCard(String[] correctCard) {
        System.out.format(MESSAGE_FORMAT_FOR_CORRECT_CARD,
                correctCard[SUIT_INFO], correctCard[RANK_INFO]);
    }

    private static void showHitCorrect(String correctSuit,
            String userAnswerRank) {
        System.out.format(MESSAGE_FORMAT_FOR_HIT_CORRECT_CARD, correctSuit,
                userAnswerRank);
    }

    private static String generateCorrect(String[] options) {
        int randomIndex = generateCorrectAsIndex(options);
        return options[randomIndex];
    }

    private static int generateCorrectAsIndex(String[] options) {
        return RANDOM.nextInt(options.length);
    }

    private static boolean isCorrect(String correct, String answer) {
        return correct.equals(answer);
    }

    private static boolean isOverMaxChallengeTimes(int maxChallengeTimes,
            int round) {
        return round > maxChallengeTimes;
    }

    private static void showRound(int round) {
        System.out.format(MESSAGE_FORMAT_FOR_ROUND, round);
    }

    private static void showCorrectSuit(String correct) {
        System.out.format(MESSAGE_FORMAT_FOR_CORRECT, correct);
    }

    private static void showHitCorrectSuit(String userAnserSuit) {
        System.out.format(MESSAGE_FORMAT_FOR_HIT_CORRECT_SUIT, userAnserSuit);
    }

    private static void showDoubt(String userAnswer) {
        System.out.format(MESSAGE_FORMAT_FOR_DOUBT, userAnswer);
    }

    private static int recieveInputtedOptionNo(String[] options) {
        String inputtedNumStr = recieveInputtedNumStr();
        int inputtedNum = validateInputtedOptionNo(inputtedNumStr, options);
        return inputtedNum;
    }

    private static int validateInputtedOptionNo(String inputtedNumStr,
            String[] options) {
        if (!isNum(inputtedNumStr)) {
            show(MESSAGE_FOR_INPUT_WITH_NUM);
            return recieveInputtedOptionNo(options);
        }

        int inputtedNum = parseToInt(inputtedNumStr);

        int min = ARRAY_FIRST_INDEX;
        int max = options.length - 1;
        if (!isInRange(min, max, inputtedNum)) {
            showRequireInputInRange(min, max);
            return recieveInputtedOptionNo(options);
        }
        return inputtedNum;
    }

    private static void showRequireInputInRange(int min, int max) {
        System.out.format(MESSAGE_FORMAT_F0R_INPUT_IN_RANGE, min, max);
    }

    private static boolean isNum(String str) {
        try {
            parseToInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static boolean isInRange(int min, int max, int num) {
        return num >= min && num <= max;
    }

    private static int parseToInt(String str) {
        return Integer.parseInt(str);
    }

    private static String recieveInputtedNumStr() {
        return STDIN.nextLine();
    }

    private static void showSelectMenu(String[] options) {
        int index = 0;
        for (String option : options) {
            System.out.format(MESSAGE_FORMAT_FOR_SHOW_SELECT_MENU, index++,
                    option);
        }
    }

    private static void show(String message) {
        System.out.println(message);
    }

    private static void showWithNoBreaks(String message) {
        System.out.print(message);
    }

    private static void showBlankLine(int numOfLine) {
        for (int i = 0; i < numOfLine; i++) {
            System.out.println();
        }
    }
}
