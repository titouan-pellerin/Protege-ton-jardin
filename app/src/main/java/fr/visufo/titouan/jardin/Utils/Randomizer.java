package fr.visufo.titouan.jardin.Utils;

public class Randomizer {
    public static int generate(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }
}
