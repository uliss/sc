GuidoGuiLibrary {
    classvar instance;
    var dict;

    *new {
        if(instance.isNil) {
            instance = super.new.init;
        };

        ^instance;
    }

    init {
        dict = Dictionary.new;
        this.initFreeverb;
        this.initMonitor;
    }

    initFreeverb {
        var box, mix, room;

        box = NodeJS_VBox.new.title_("viola reverb").hidden_(true).borderColor_("#AAA").align_("left");
        mix = NodeJS_Knob.new(0.5, 0, 1).size_(70).label_("mix").labelSize_(20).hidden_(true).layout_(box);
        room = NodeJS_Knob.new(0.7, 0, 1).size_(70).label_("room").labelSize_(20).hidden_(true).layout_(box);

        this.addGroup(\freeverb, [\box, box, \mix, mix, \room, room]);
    }

    initMonitor {
        var box, mute, vol;
        box = NodeJS_VBox.new.title_("monitor").borderColor_("#AAA").align_("center").titleIcon_("headphones");
        mute = NodeJS_Toggle.new(0).label_("on").labelSize_(16).size_(40).layout_(box);
        vol = NodeJS_Slider.new(0, 0, 1, 150).label_("amp").labelSize_(20).layout_(box);

        this.addGroup(\monitor, [\box, box, \mute, mute, \vol, vol]);
    }

    groupWidgets {
        arg name;
        name = name.asSymbol;
        if(dict[name].isNil) { ^nil };
        ^dict[name].reject({|i| i.isKindOf(Symbol)});
    }

    groupWidgetNames {
        arg name;
        name = name.asSymbol;
        if(dict[name].isNil) { ^nil };
        ^dict[name].select({|i| i.isKindOf(Symbol)});
    }

    addGroup {
        arg name, widgets;
        name = name.asSymbol;
        if(dict[name].isNil) {
            dict[name] = List.new;
        };

        dict[name].addAll(widgets);
    }

    removeGroup {
        arg name;
        name = name.asSymbol;
        if(dict[name].notNil) {
            dict[name] = nil;
        };
    }

    groups {
        ^dict.keys.asArray.sort;
    }
}
