import java.util.*;
import java.util.stream.Collectors;

public class BoardImpl implements Board {


    private Map<String, Card> decksByName = new HashMap<>();
    private  List<Card> deckList = new LinkedList<>();

    public BoardImpl() {

    }

    @Override
    public void draw(Card card) {
        String name = card.getName();
        // Name
        if (this.decksByName.containsKey(name)) {
            throw new IllegalArgumentException();
        }
        this.decksByName.put(name, card);
        //List
        this.deckList.add(card);
    }


    @Override
    public Boolean contains(String name) {
        return this.decksByName.containsKey(name);
    }

    @Override
    public int count() {
        return this.decksByName.size();
    }


    @Override
    public void play(String attackerCardName, String attackedCardName) {
        if (attackerCardName.equals(attackedCardName)) {
            throw new IllegalArgumentException();
        }
        Card attackeRCard = this.decksByName.get(attackerCardName);
        Card attackeDCard = this.decksByName.get(attackedCardName);

        if (attackeRCard == null || attackeDCard == null) {
            throw new IllegalArgumentException();
        }

        if (attackeRCard.getLevel() != attackeDCard.getLevel()) {
            throw new IllegalArgumentException();
        }
        int attackedHealth = attackeDCard.getHealth();
        if (attackedHealth > 0) {
            int attackHealth = attackedHealth - attackeRCard.getDamage();
            attackeDCard.setHealth(attackHealth);

            if (attackHealth <= 0) {
                attackeRCard.setScore(attackeRCard.getScore() + attackeDCard.getLevel());
            }
        }
    }

    @Override
    public void remove(String name) {
        Card card = this.decksByName.get(name);
        if (card == null) {
            throw new IllegalArgumentException();
        }
        //remove from map
        this.decksByName.remove(name);
        // Remove from List
        this.deckList.remove(card);
    }

    @Override
    public void removeDeath() {

        Iterator<Card> listIter = this.deckList.iterator();
        while (listIter.hasNext()) {
            Card currentCard = listIter.next();
            String name = currentCard.getName();
            if (currentCard.getHealth() <= 0) {
                // remove from map
                decksByName.remove(name);
                // remove from list
                listIter.remove();
            }
        }

    }

    @Override
    public Iterable<Card> getBestInRange(int start, int end) {
        Comparator<Card> comparatorLevelDesending = Comparator.comparing(Card::getLevel).reversed();
        return this.deckList
                .stream()
                .filter(card -> card.getScore() >= start && card.getScore() <= end)
                .sorted(comparatorLevelDesending)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Card> listCardsByPrefix(String prefix) {

        Map<String, Card> resultMap = new TreeMap<>();
        this.decksByName
                .entrySet()
                .stream()
                .filter(card -> card.getKey().startsWith(prefix))
                .forEach(c -> resultMap.put(reverseString(c.getKey()), c.getValue()));

        return new ArrayList<>(resultMap.values());

    }

    public static String reverseString(String str) {
        StringBuilder sb = new StringBuilder(str);
        sb.reverse();
        return sb.toString();
    }

    @Override
    public Iterable<Card> searchByLevel(int level) {

        Comparator<Card> comparatorScoreDesending = Comparator.comparing(Card::getScore).reversed();

        return this.deckList
                .stream()
                .filter(card -> card.getLevel() == level)
                .sorted(comparatorScoreDesending)
                .collect(Collectors.toList());

    }

    @Override
    public void heal(int health) {
        Optional<Card> minCard = this.deckList.stream().min(Comparator.comparing(Card::getHealth));
        if (minCard.isPresent()) {
            Card card = minCard.get();
            card.setHealth(card.getHealth() + health);
        }

    }
}
