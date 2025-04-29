package Difficulty;

import java.util.Random;

public class DifficultyLevel {

    private Random rand = new Random();

    public String getEasyWord() {
        return Easy.easyWords[rand.nextInt(Easy.easyWords.length)];
    }

    public String getMediumWord() {
        return Medium.mediumWords[rand.nextInt(Medium.mediumWords.length)];
    }

    public String getHardWord() {
        return Hard.hardWords[rand.nextInt(Hard.hardWords.length)];
    }
}
