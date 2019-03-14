package fr.paug.androidmakers.ui.adapter;

import androidx.annotation.NonNull;

import fr.paug.androidmakers.model.ScheduleSlot;
import fr.paug.androidmakers.util.EmojiUtils;

public class ScheduleSession implements Comparable<ScheduleSession> {

    private ScheduleSlot mScheduleSlot;
    private String mTitle;
    private String mLanguage;

    public ScheduleSession(ScheduleSlot scheduleSlot, String title, String language) {
        mScheduleSlot = scheduleSlot;
        mTitle = title;
        mLanguage = language;
    }

    public long getStartTimestamp() {
        return mScheduleSlot.getStartDate();
    }

    public long getEndTimestamp() {
        return mScheduleSlot.getEndDate();
    }

    public String getTitle() {
        return mTitle;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public String getLanguageInEmoji() {
        return EmojiUtils.getLanguageInEmoji(mLanguage);
    }

    public int getRoomId() {
        return mScheduleSlot.getRoom();
    }

    public int getSessionId() {
        return mScheduleSlot.getSessionId();
    }

    @Override
    public String toString() {
        return "ScheduleSession{" +
                "mScheduleSlot=" + mScheduleSlot +
                ", mTitle='" + mTitle + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull ScheduleSession o) {
        return Long.valueOf(this.mScheduleSlot.getStartDate()).compareTo(o.mScheduleSlot.getStartDate());
    }

}