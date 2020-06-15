//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package de.imfactions.util.Slack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.*;

public class SlackAttachment {
    private static final String HEX_REGEX = "^([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
    private static final String FALLBACK = "fallback";
    private static final String TEXT = "text";
    private static final String PRETEXT = "pretext";
    private static final String COLOR = "color";
    private static final String FIELDS = "fields";
    private static final String AUTHOR_NAME = "author_name";
    private static final String AUTHOR_LINK = "author_link";
    private static final String AUTHOR_ICON = "author_icon";
    private static final String TITLE = "title";
    private static final String TITLE_LINK = "title_link";
    private static final String IMAGE_URL = "image_url";
    private static final String THUMB_URL = "thumb_url";
    private static final String MRKDWN_IN = "mrkdwn_in";
    private String fallback;
    private String text;
    private String pretext;
    private String color;
    private String authorName;
    private String authorLink;
    private String authorIcon;
    private String title;
    private String titleLink;
    private String imageUrl;
    private String thumbUrl;
    private Set<String> markdownAttributes = new HashSet();
    private List<SlackField> fields = new ArrayList();

    public SlackAttachment() {
    }

    public SlackAttachment addFields(SlackField field) {
        this.fields.add(field);
        return this;
    }

    public SlackAttachment addMarkdownAttribute(String attr) {
        this.markdownAttributes.add(attr);
        return this;
    }

    private boolean isHex(String pair) {
        return pair.matches("^([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    }

    private JsonArray prepareFields() {
        JsonArray data = new JsonArray();
        Iterator var3 = this.fields.iterator();

        while (var3.hasNext()) {
            SlackField field = (SlackField) var3.next();
            data.add(field.toJson());
        }

        return data;
    }

    public SlackAttachment removeFields(int index) {
        this.fields.remove(index);
        return this;
    }

    private JsonArray prepareMarkdownAttributes() {
        JsonArray data = new JsonArray();
        Iterator var3 = this.markdownAttributes.iterator();

        while (var3.hasNext()) {
            String attr = (String) var3.next();
            data.add(new JsonPrimitive(attr));
        }

        return data;
    }

    public SlackAttachment removeMarkdownAttribute(String attr) {
        this.markdownAttributes.remove(attr);
        return this;
    }

    public SlackAttachment setColor(String color) {
        if (color != null) {
            if (color.charAt(0) == 35) {
                if (!this.isHex(color.substring(1))) {
                    throw new IllegalArgumentException("Invalid Hex Color @ SlackAttachment");
                }
            } else if (!color.matches("^(good|warning|danger)$")) {
                throw new IllegalArgumentException("Invalid PreDefined Color @ SlackAttachment");
            }
        }

        this.color = color;
        return this;
    }

    public SlackAttachment setFallback(String fallback) {
        this.fallback = fallback;
        return this;
    }

    public SlackAttachment setFields(List<SlackField> fields) {
        this.fields = fields;
        return this;
    }

    public SlackAttachment setPretext(String pretext) {
        this.pretext = pretext;
        return this;
    }

    public SlackAttachment setText(String text) {
        this.text = text;
        return this;
    }

    public SlackAttachment setAuthorName(String authorName) {
        this.authorName = authorName;
        return this;
    }

    public SlackAttachment setAuthorLink(String authorLink) {
        this.authorLink = authorLink;
        return this;
    }

    public SlackAttachment setAuthorIcon(String authorIcon) {
        this.authorIcon = authorIcon;
        return this;
    }

    public SlackAttachment setTitle(String title) {
        this.title = title;
        return this;
    }

    public SlackAttachment setTitleLink(String titleLink) {
        this.titleLink = titleLink;
        return this;
    }

    public SlackAttachment setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public SlackAttachment setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
        return this;
    }

    public JsonObject toJson() {
        JsonObject data = new JsonObject();
        if (this.fallback == null) {
            throw new IllegalArgumentException("Missing Fallback @ SlackAttachment");
        } else {
            data.addProperty("fallback", this.fallback);
            if (this.text != null) {
                data.addProperty("text", this.text);
            }

            if (this.pretext != null) {
                data.addProperty("pretext", this.pretext);
            }

            if (this.color != null) {
                data.addProperty("color", this.color);
            }

            if (this.authorName != null) {
                data.addProperty("author_name", this.authorName);
            }

            if (this.authorLink != null) {
                data.addProperty("author_link", this.authorLink);
            }

            if (this.authorIcon != null) {
                data.addProperty("author_icon", this.authorIcon);
            }

            if (this.title != null) {
                data.addProperty("title", this.title);
            }

            if (this.titleLink != null) {
                data.addProperty("title_link", this.titleLink);
            }

            if (this.imageUrl != null) {
                data.addProperty("image_url", this.imageUrl);
            }

            if (this.thumbUrl != null) {
                data.addProperty("thumb_url", this.thumbUrl);
            }

            if (this.markdownAttributes != null) {
                data.add("mrkdwn_in", this.prepareMarkdownAttributes());
            }

            if (this.fields != null && this.fields.size() > 0) {
                data.add("fields", this.prepareFields());
            }

            return data;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            SlackAttachment that;
            label167:
            {
                that = (SlackAttachment) o;
                if (this.fallback != null) {
                    if (this.fallback.equals(that.fallback)) {
                        break label167;
                    }
                } else if (that.fallback == null) {
                    break label167;
                }

                return false;
            }

            label160:
            {
                if (this.text != null) {
                    if (this.text.equals(that.text)) {
                        break label160;
                    }
                } else if (that.text == null) {
                    break label160;
                }

                return false;
            }

            label153:
            {
                if (this.pretext != null) {
                    if (this.pretext.equals(that.pretext)) {
                        break label153;
                    }
                } else if (that.pretext == null) {
                    break label153;
                }

                return false;
            }

            if (this.color != null) {
                if (!this.color.equals(that.color)) {
                    return false;
                }
            } else if (that.color != null) {
                return false;
            }

            label139:
            {
                if (this.authorName != null) {
                    if (this.authorName.equals(that.authorName)) {
                        break label139;
                    }
                } else if (that.authorName == null) {
                    break label139;
                }

                return false;
            }

            if (this.authorLink != null) {
                if (!this.authorLink.equals(that.authorLink)) {
                    return false;
                }
            } else if (that.authorLink != null) {
                return false;
            }

            label125:
            {
                if (this.authorIcon != null) {
                    if (this.authorIcon.equals(that.authorIcon)) {
                        break label125;
                    }
                } else if (that.authorIcon == null) {
                    break label125;
                }

                return false;
            }

            label118:
            {
                if (this.title != null) {
                    if (this.title.equals(that.title)) {
                        break label118;
                    }
                } else if (that.title == null) {
                    break label118;
                }

                return false;
            }

            if (this.titleLink != null) {
                if (!this.titleLink.equals(that.titleLink)) {
                    return false;
                }
            } else if (that.titleLink != null) {
                return false;
            }

            label104:
            {
                if (this.imageUrl != null) {
                    if (this.imageUrl.equals(that.imageUrl)) {
                        break label104;
                    }
                } else if (that.imageUrl == null) {
                    break label104;
                }

                return false;
            }

            label97:
            {
                if (this.thumbUrl != null) {
                    if (this.thumbUrl.equals(that.thumbUrl)) {
                        break label97;
                    }
                } else if (that.thumbUrl == null) {
                    break label97;
                }

                return false;
            }

            if (this.markdownAttributes != null) {
                if (!this.markdownAttributes.equals(that.markdownAttributes)) {
                    return false;
                }
            } else if (that.markdownAttributes != null) {
                return false;
            }

            boolean var10000;
            label200:
            {
                if (this.fields != null) {
                    if (!this.fields.equals(that.fields)) {
                        break label200;
                    }
                } else if (that.fields != null) {
                    break label200;
                }

                var10000 = true;
                return var10000;
            }

            var10000 = false;
            return var10000;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.fallback != null ? this.fallback.hashCode() : 0;
        result = 31 * result + (this.text != null ? this.text.hashCode() : 0);
        result = 31 * result + (this.pretext != null ? this.pretext.hashCode() : 0);
        result = 31 * result + (this.color != null ? this.color.hashCode() : 0);
        result = 31 * result + (this.authorName != null ? this.authorName.hashCode() : 0);
        result = 31 * result + (this.authorLink != null ? this.authorLink.hashCode() : 0);
        result = 31 * result + (this.authorIcon != null ? this.authorIcon.hashCode() : 0);
        result = 31 * result + (this.title != null ? this.title.hashCode() : 0);
        result = 31 * result + (this.titleLink != null ? this.titleLink.hashCode() : 0);
        result = 31 * result + (this.imageUrl != null ? this.imageUrl.hashCode() : 0);
        result = 31 * result + (this.thumbUrl != null ? this.thumbUrl.hashCode() : 0);
        result = 31 * result + (this.markdownAttributes != null ? this.markdownAttributes.hashCode() : 0);
        result = 31 * result + (this.fields != null ? this.fields.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "SlackAttachment{fallback='" + this.fallback + '\'' + ", text='" + this.text + '\'' + ", pretext='" + this.pretext + '\'' + ", color='" + this.color + '\'' + ", authorName='" + this.authorName + '\'' + ", authorLink='" + this.authorLink + '\'' + ", authorIcon='" + this.authorIcon + '\'' + ", title='" + this.title + '\'' + ", titleLink='" + this.titleLink + '\'' + ", imageUrl='" + this.imageUrl + '\'' + ", thumbUrl='" + this.thumbUrl + '\'' + ", markdownAttributes=" + this.markdownAttributes + ", fields=" + this.fields + '}';
    }
}
