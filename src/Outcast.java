import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordNet;
    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;
    }  // constructor takes a WordNet object

    public String outcast(String[] nouns) {

        String outcast = null;
        int maxDist = 0;
        for (int i = 0; i < nouns.length; i++) {
            int tempDist = 0;
            for (int j = 0; j < nouns.length; j++) {
                tempDist += wordNet.distance(nouns[i], nouns[j]);
            }
            if(tempDist > maxDist) {
                maxDist = tempDist;
                outcast = nouns[i];
            }
        }
        return outcast;
    } // given an array of WordNet nouns, return an outcast

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}