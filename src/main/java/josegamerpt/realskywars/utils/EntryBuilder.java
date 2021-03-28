package josegamerpt.realskywars.utils;

import java.util.LinkedList;
import java.util.List;

public class EntryBuilder {

    private LinkedList<Entry> entries = new LinkedList<>();

    public EntryBuilder blank() {
        return next("");
    }

    public EntryBuilder next(String string) {
        entries.add(new Entry(string, entries.size()));
        return this;
    }

    public List<Entry> build() {
        for (Entry entry : entries) {
            entry.setPosition(entries.size() - entry.getPosition());
        }
        return entries;
    }
}