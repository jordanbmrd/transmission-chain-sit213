package sources;

import information.Information;

public class SourceFixe extends Source<Boolean> {
    /**
     * Une source qui envoie toujours le même message
     */
    public SourceFixe (String messageString) {
        super();

        informationGeneree = new Information<Boolean>();

        for (int i = 0; i < messageString.length(); ++i) {
            boolean value = messageString.charAt(i) != '0';
            informationGeneree.add(value);
        }

        informationEmise = informationGeneree;
    }
}
