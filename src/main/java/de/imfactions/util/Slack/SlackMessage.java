package de.imfactions.util.Slack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SlackMessage {
    private static final String CHANNEL = "channel";
    private static final String USERNAME = "username";
    private static final String HTTP = "http";
    private static final String ICON_URL = "icon_url";
    private static final String ICON_EMOJI = "icon_emoji";
    private static final String UNFURL_MEDIA = "unfurl_media";
    private static final String UNFURL_LINKS = "unfurl_links";
    private static final String TEXT = "text";
    private static final String ATTACHMENTS = "attachments";
    private List<SlackAttachment> attach;
    private String channel;
    private String icon;
    private final JsonObject slackMessage;
    private String text;
    private String username;
    private boolean unfurlMedia;
    private boolean unfurlLinks;

    public SlackMessage() {
        this.attach = new ArrayList();
        this.channel = null;
        this.icon = null;
        this.slackMessage = new JsonObject();
        this.text = null;
        this.username = null;
        this.unfurlMedia = false;
        this.unfurlLinks = false;
    }

    public SlackMessage(String text) {
        this(null, null, text);
    }

    public SlackMessage(String username, String text) {
        this(null, username, text);
    }

    public SlackMessage(String channel, String username, String text) {
        this.attach = new ArrayList();
        this.channel = null;
        this.icon = null;
        this.slackMessage = new JsonObject();
        this.text = null;
        this.username = null;
        this.unfurlMedia = false;
        this.unfurlLinks = false;
        if (channel != null) {
            this.channel = channel;
        }

        if (username != null) {
            this.username = username;
        }

        this.text = text;
    }

    public SlackMessage addAttachments(SlackAttachment attach) {
        this.attach.add(attach);
        return this;
    }

    public JsonObject prepare() {
        if (this.channel != null) {
            this.slackMessage.addProperty("channel", this.channel);
        }

        if (this.username != null) {
            this.slackMessage.addProperty("username", this.username);
        }

        if (this.icon != null) {
            if (this.icon.contains("http")) {
                this.slackMessage.addProperty("icon_url", this.icon);
            } else {
                this.slackMessage.addProperty("icon_emoji", this.icon);
            }
        }

        this.slackMessage.addProperty("unfurl_media", Boolean.valueOf(this.unfurlMedia));
        this.slackMessage.addProperty("unfurl_links", Boolean.valueOf(this.unfurlLinks));
        if (this.text == null) {
            throw new IllegalArgumentException("Missing Text field @ SlackMessage");
        } else {
            this.slackMessage.addProperty("text", this.text);
            if (!this.attach.isEmpty()) {
                this.slackMessage.add("attachments", this.prepareAttach());
            }

            return this.slackMessage;
        }
    }

    private JsonArray prepareAttach() {
        JsonArray attachs = new JsonArray();
        Iterator var3 = this.attach.iterator();

        while (var3.hasNext()) {
            SlackAttachment attach = (SlackAttachment) var3.next();
            attachs.add(attach.toJson());
        }

        return attachs;
    }

    public SlackMessage removeAttachment(int index) {
        this.attach.remove(index);
        return this;
    }

    public SlackMessage setAttachments(List<SlackAttachment> attach) {
        this.attach = attach;
        return this;
    }

    public SlackMessage setChannel(String channel) {
        if (channel != null) {
            this.channel = channel;
        }

        return this;
    }

    public SlackMessage setIcon(String icon) {
        if (icon != null) {
            this.icon = icon;
        }

        return this;
    }

    public SlackMessage setText(String message) {
        if (message != null) {
            this.text = message;
        }

        return this;
    }

    public SlackMessage setUsername(String username) {
        if (username != null) {
            this.username = username;
        }

        return this;
    }

    public SlackMessage setUnfurlMedia(boolean unfurlMedia) {
        this.unfurlMedia = unfurlMedia;
        return this;
    }

    public SlackMessage setUnfurlLinks(boolean unfurlLinks) {
        this.unfurlLinks = unfurlLinks;
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            SlackMessage that = (SlackMessage) o;
            if (this.unfurlMedia != that.unfurlMedia) {
                return false;
            } else if (this.unfurlLinks != that.unfurlLinks) {
                return false;
            } else {
                label76:
                {
                    if (this.attach != null) {
                        if (this.attach.equals(that.attach)) {
                            break label76;
                        }
                    } else if (that.attach == null) {
                        break label76;
                    }

                    return false;
                }

                if (this.channel != null) {
                    if (!this.channel.equals(that.channel)) {
                        return false;
                    }
                } else if (that.channel != null) {
                    return false;
                }

                label62:
                {
                    if (this.icon != null) {
                        if (this.icon.equals(that.icon)) {
                            break label62;
                        }
                    } else if (that.icon == null) {
                        break label62;
                    }

                    return false;
                }

                label55:
                {
                    if (this.text != null) {
                        if (this.text.equals(that.text)) {
                            break label55;
                        }
                    } else if (that.text == null) {
                        break label55;
                    }

                    return false;
                }

                boolean var10000;
                label99:
                {
                    if (this.username != null) {
                        if (!this.username.equals(that.username)) {
                            break label99;
                        }
                    } else if (that.username != null) {
                        break label99;
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
        int result = this.attach != null ? this.attach.hashCode() : 0;
        result = 31 * result + (this.channel != null ? this.channel.hashCode() : 0);
        result = 31 * result + (this.icon != null ? this.icon.hashCode() : 0);
        result = 31 * result + (this.text != null ? this.text.hashCode() : 0);
        result = 31 * result + (this.username != null ? this.username.hashCode() : 0);
        result = 31 * result + (this.unfurlMedia ? 1 : 0);
        result = 31 * result + (this.unfurlLinks ? 1 : 0);
        return result;
    }

    public String toString() {
        return "SlackMessage{attach=" + this.attach + ", channel='" + this.channel + '\'' + ", icon='" + this.icon + '\'' + ", slackMessage=" + this.slackMessage + ", text='" + this.text + '\'' + ", username='" + this.username + '\'' + ", unfurlMedia=" + this.unfurlMedia + ", unfurlLinks=" + this.unfurlLinks + '}';
    }
}
