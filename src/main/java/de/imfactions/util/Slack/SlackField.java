package de.imfactions.util.Slack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Yannick who could get really angry if somebody steal his code!
 * ~Yannick on 18.11.2017 at 12:13 o´ clock
 */
public class SlackField {
    private static final String TITLE = "title";
    private static final String VALUE = "value";
    private static final String SHORT = "short";
    private static final String MRKDWN_IN = "mrkdwn_in";
    private static final String FIELD_ALLOWS_MARKDOWN_REGEX = "^(pretext|text|title|fields|fallback)$";
    private List<String> allowMarkdown = null;
    private boolean shorten = false;
    private String title = null;
    private String value = null;

    public SlackField() {
    }

    public void addAllowedMarkdown(String field) {
        if (this.allowMarkdown == null) {
            this.allowMarkdown = new ArrayList();
        }

        if (field.matches("^(pretext|text|title|fields|fallback)$")) {
            this.allowMarkdown.add(field);
        } else {
            throw new IllegalArgumentException(field + " is not allowed. Allowed: pretext, text, title, fields and fallback");
        }
    }

    public boolean isShorten() {
        return this.shorten;
    }

    private JsonArray prepareMarkdown() {
        JsonArray data = new JsonArray();
        Iterator var3 = this.allowMarkdown.iterator();

        while (var3.hasNext()) {
            String item = (String) var3.next();
            data.add(new JsonPrimitive(item));
        }

        return data;
    }

    public void setAllowedMarkdown(ArrayList<String> allowMarkdown) {
        if (allowMarkdown != null) {
            this.allowMarkdown = allowMarkdown;
        }

    }

    public SlackField setShorten(boolean shorten) {
        this.shorten = shorten;
        return this;
    }

    public SlackField setTitle(String title) {
        this.title = title;
        return this;
    }

    public SlackField setValue(String value) {
        this.value = value;
        return this;
    }

    public JsonObject toJson() {
        JsonObject data = new JsonObject();
        data.addProperty("title", this.title);
        data.addProperty("value", this.value);
        data.addProperty("short", Boolean.valueOf(this.shorten));
        if (this.allowMarkdown != null && this.allowMarkdown.size() > 0) {
            data.add("mrkdwn_in", this.prepareMarkdown());
        }

        return data;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            SlackField that = (SlackField) o;
            if (this.shorten != that.shorten) {
                return false;
            } else {
                if (this.allowMarkdown != null) {
                    if (!this.allowMarkdown.equals(that.allowMarkdown)) {
                        return false;
                    }
                } else if (that.allowMarkdown != null) {
                    return false;
                }

                if (this.title != null) {
                    if (!this.title.equals(that.title)) {
                        return false;
                    }
                } else if (that.title != null) {
                    return false;
                }

                boolean var10000;
                label75:
                {
                    if (this.value != null) {
                        if (!this.value.equals(that.value)) {
                            break label75;
                        }
                    } else if (that.value != null) {
                        break label75;
                    }

                    var10000 = true;
                    return var10000;
                }

                var10000 = false;
                return var10000;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.allowMarkdown != null ? this.allowMarkdown.hashCode() : 0;
        result = 31 * result + (this.shorten ? 1 : 0);
        result = 31 * result + (this.title != null ? this.title.hashCode() : 0);
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "SlackField{allowMarkdown=" + this.allowMarkdown + ", shorten=" + this.shorten + ", title='" + this.title + '\'' + ", value='" + this.value + '\'' + '}';
    }
}
