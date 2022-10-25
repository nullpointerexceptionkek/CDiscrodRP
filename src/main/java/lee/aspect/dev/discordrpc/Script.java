package lee.aspect.dev.discordrpc;

import lee.aspect.dev.discordrpc.settings.options.TimeStampMode;

import java.util.ArrayList;
import java.util.Arrays;

public class Script {
    private static ArrayList<Updates> totalupdates;

    private static TimeStampMode timestampmode = TimeStampMode.applaunch;

    public Script() {
        totalupdates = new ArrayList<>();
    }

    public static TimeStampMode getTimestampmode() {
        return timestampmode;
    }

    public static void setTimestampmode(TimeStampMode timestampmode) {
        Script.timestampmode = timestampmode;
    }

    public static ArrayList<Updates> getTotalupdates() {
        return totalupdates;
    }


    public static void setTotalupdates(ArrayList<Updates> u) {
        totalupdates = u;
    }

    public static void addUpdates(Updates... updates) {
        totalupdates.addAll(Arrays.asList(updates));

    }

    public static void setUpdates(Updates u, int index) {
        totalupdates.set(index, u);
    }

    public static Script fromTotalUpdates() {
        return new Script();
    }

    public void removeUpdates(int list) {
        totalupdates.remove(list);

    }

    public Updates getUpdates(int list) {
        return totalupdates.get(list);
    }

    public int getSize() {
        return totalupdates.size();

    }

    @Override
    public String toString() {

        StringBuilder ts = new StringBuilder();

        for (Updates u : totalupdates) {
            ts.append(u).append("-");
        }

        return ts.toString();

    }

}
