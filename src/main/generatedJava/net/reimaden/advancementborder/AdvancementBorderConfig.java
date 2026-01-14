package net.reimaden.advancementborder;

import blue.endless.jankson.Jankson;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.ConfigWrapper.BuilderConsumer;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AdvancementBorderConfig extends ConfigWrapper<net.reimaden.advancementborder.AdvancementBorderConfigModel> {

    public final Keys keys = new Keys();

    private final Option<java.lang.Boolean> detailedNotifications = this.optionForKey(this.keys.detailedNotifications);
    private final Option<net.reimaden.advancementborder.AdvancementBorderConfigModel.NotificationStyle> notificationStyle = this.optionForKey(this.keys.notificationStyle);
    private final Option<java.lang.Boolean> perPlayerAdvancements = this.optionForKey(this.keys.perPlayerAdvancements);
    private final Option<java.util.List<net.minecraft.resources.Identifier>> blacklist = this.optionForKey(this.keys.blacklist);

    private AdvancementBorderConfig() {
        super(net.reimaden.advancementborder.AdvancementBorderConfigModel.class);
    }

    private AdvancementBorderConfig(BuilderConsumer consumer) {
        super(net.reimaden.advancementborder.AdvancementBorderConfigModel.class, consumer);
    }

    public static AdvancementBorderConfig createAndLoad() {
        var wrapper = new AdvancementBorderConfig();
        wrapper.load();
        return wrapper;
    }

    public static AdvancementBorderConfig createAndLoad(BuilderConsumer consumer) {
        var wrapper = new AdvancementBorderConfig(consumer);
        wrapper.load();
        return wrapper;
    }

    public boolean detailedNotifications() {
        return detailedNotifications.value();
    }

    public void detailedNotifications(boolean value) {
        detailedNotifications.set(value);
    }

    public net.reimaden.advancementborder.AdvancementBorderConfigModel.NotificationStyle notificationStyle() {
        return notificationStyle.value();
    }

    public void notificationStyle(net.reimaden.advancementborder.AdvancementBorderConfigModel.NotificationStyle value) {
        notificationStyle.set(value);
    }

    public boolean perPlayerAdvancements() {
        return perPlayerAdvancements.value();
    }

    public void perPlayerAdvancements(boolean value) {
        perPlayerAdvancements.set(value);
    }

    public java.util.List<net.minecraft.resources.Identifier> blacklist() {
        return blacklist.value();
    }

    public void blacklist(java.util.List<net.minecraft.resources.Identifier> value) {
        blacklist.set(value);
    }


    public static class Keys {
        public final Option.Key detailedNotifications = new Option.Key("detailedNotifications");
        public final Option.Key notificationStyle = new Option.Key("notificationStyle");
        public final Option.Key perPlayerAdvancements = new Option.Key("perPlayerAdvancements");
        public final Option.Key blacklist = new Option.Key("blacklist");
    }
}

