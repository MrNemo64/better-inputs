package me.nemo_64.betterinputs.bukkit.message.component;

import java.awt.Color;
import java.util.Arrays;

import me.lauriichan.laylib.localization.IMessage;
import me.lauriichan.laylib.logger.util.StringUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Entity;

public final class Component extends SendableComponent {

    public static Component of(String message) {
        return new Component(message);
    }

    private final String message;

    private ClickEvent click;
    private HoverEvent hover;

    private Color defaultColor = ComponentParser.DEFAULT_COLOR;

    private boolean changed = true;

    private BaseComponent[] componentMessage = EMPTY;

    public Component(final String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }

    public Component defaultColor(Color defaultColor) {
        if (defaultColor == null) {
            defaultColor = ComponentParser.DEFAULT_COLOR;
        }
        if (this.defaultColor == defaultColor) {
            return this;
        }
        this.defaultColor = defaultColor;
        return this;
    }

    public Component clickUrl(String url, Object... format) {
        return clickUrl(StringUtil.format(url, format));
    }

    public Component clickFile(String file, Object... format) {
        return clickFile(StringUtil.format(file, format));
    }

    public Component clickCopy(String copy, Object... format) {
        return clickCopy(StringUtil.format(copy, format));
    }

    public Component clickSuggest(String suggest, Object... format) {
        return clickSuggest(StringUtil.format(suggest, format));
    }

    public Component clickRun(String run, Object... format) {
        return clickRun(StringUtil.format(run, format));
    }

    public Component clickUrl(String url) {
        return click(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
    }

    public Component clickFile(String file) {
        return click(new ClickEvent(ClickEvent.Action.OPEN_FILE, file));
    }

    public Component clickCopy(String copy) {
        return click(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, copy));
    }

    public Component clickSuggest(String suggest) {
        return click(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest));
    }

    public Component clickRun(String run) {
        return click(new ClickEvent(ClickEvent.Action.RUN_COMMAND, run));
    }

    public Component click(final ClickEvent click) {
        if (this.click == click) {
            return this;
        }
        this.click = click;
        this.changed = true;
        return this;
    }

    public Component hoverEntity(final org.bukkit.entity.Entity entity) {
        if (entity == null) {
            return this;
        }
        TextComponent component = new TextComponent();
        component
            .setExtra(Arrays.asList(ComponentParser.parse(entity.getCustomName() == null ? entity.getName() : entity.getCustomName())));
        return hover(new HoverEvent(HoverEvent.Action.SHOW_ENTITY,
            new Entity(entity.getType().getKey().toString(), entity.getUniqueId().toString(), component)));
    }

    public Component hoverText(final Component component) {
        if (component == null) {
            return this;
        }
        return hover(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.hover.content.Text(component.build())));
    }
    
    public Component hoverText(final IMessage message) {
        
        return this;
    }

    public Component hoverText(final String string) {
        if (string == null) {
            return this;
        }
        return hover(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
            new net.md_5.bungee.api.chat.hover.content.Text(ComponentParser.parse(string, defaultColor))));
    }

    public Component hover(HoverEvent hover) {
        if (this.hover == hover) {
            return this;
        }
        this.hover = hover;
        this.changed = true;
        return this;
    }

    public Component reset() {
        componentMessage = EMPTY;
        changed = true;
        return this;
    }

    @Override
    public BaseComponent[] build() {
        if (!changed) {
            return componentMessage;
        }
        if (componentMessage == EMPTY) {
            return (componentMessage = ComponentParser.parse(message, defaultColor, click, hover));
        }
        for (BaseComponent component : componentMessage) {
            component.setClickEvent(click);
            component.setHoverEvent(hover);
        }
        return componentMessage;
    }

}