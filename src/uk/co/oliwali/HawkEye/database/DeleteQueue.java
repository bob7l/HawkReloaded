package uk.co.oliwali.HawkEye.database;

import uk.co.oliwali.HawkEye.entry.DataEntry;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author bob7l
 */
public class DeleteQueue {

    private LinkedBlockingQueue<Integer> entries = new LinkedBlockingQueue<Integer>();

    private int key;

    /**
     * Construct a new DeleteQueue and add all entries into the queue
     * Uses first List DataEntry's DataId as a key
     * @param entries The list of entries to be removed from the database
     */
    public DeleteQueue(List<DataEntry> entries) {

        this.entries = new LinkedBlockingQueue<Integer>(entries.size());

        for (DataEntry entry : entries) {
            this.entries.add(entry.getDataId());
        }

        this.key = entries.get(0).getDataId();
    }

    public LinkedBlockingQueue<Integer> getQueue() {
        return entries;
    }

    public int getSize() {
        return entries.size();
    }

    public Integer poll() {
        return entries.poll();
    }

    public boolean isFinished() {
        return entries.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == hashCode();
    }

    @Override
    public int hashCode() {
        return key;
    }
}
